Leaderboard Service Design [WIP]
================================

A leaderboard is a board for displaying the ranking of players in a competitive event.

Requirements
------------

Functional

* Clients can create a leaderboard with a condition for sorting ranks in the leaderboard
* Clients can view leaderboards
* Clients can view rankings in a leaderboard in near real-time
* Clients can submit scores for entities on a leaderboard
* Clients can view the ranking of a specific entity on a leaderboard

Non-functional:

* Reliability
* Scalability
* Availability
* Low latency

High Level Design
--------------------------

* Host on the cloud for on-demand scaling capabilities. Ex: Compute Engine (GCP), EC2 (AWS)
* Public facing APIs that are behind Gateway for authentication, versioning, rate limiting with Open API specification
* Plan to support web and game clients with HTTP/JSON and HTTP/protobuf endpoints
* Microservice and event-driven architecture. Dockerize the services for platform agnosticity
* Infrastructure as code via Ex: Terraform (platform agnostic), Cloud Deplopyment Manager (GCP), AWS Cloud formation (AWS)

Component Diagram
---
![LeaderboardComponentDiagram.png](..%2F..%2FDownloads%2FLeaderboardComponentDiagram.png)

Services Overview
--------

**Leaderboard Service**: A microservice that exposes a public-facing API for interacting with the Leaderboard system. Can be seen as a proxy service.

* It can provide multiple styles of API, data format, and transport options, allowing the client to choose what is best for their system.

**Leaderboard Backend**: A microservice that performs the bulk of Leaderboard system opterations such as writing data to storage + pushing messages to pubsub topic, and returning requested content to the client.

* The service could be a bottleneck due to the amount of processes it runs

**Pubsub**: Pushes incoming events/leaderboard messages to subscribers

* Allows for an event-driven architecture through event streaming
* Could be replaced with Kafka or Amazon SNS if using AWS

**Redis**: Will be used to store rankings

* Provides quick access and has structures would could be useful for structuring rankings

**Flink**: Consumes events messages and performs computation based on leaderboard criteria. If the criteria are met, sends messages to another pubsub topic to update rankings

* Flink is able to save the state via checkpoints + savepoints in case of failure allowing for recovery of up-to-date leaderboard rankings

**Leaderboard Serverless Function**: Consumes messages from pubsub containing updated rankings and writes them to Redis.

* Dynamically scale up and down according to load
* Replace with Lambda if using AWS

**Cassandra**: Persistent storage for leaderboards, entities, event data and metadata

* NoSQL for higher performance
* Could be replaced with DynamoDB or Datastore if using AWS/GCP

API Definitions
---

**Base URL**: https://{api-gateway-hostname}/leaderboards/v1/

**Authentication**: OAuth 2.0/Open ID Connect

**Authorization**: OAuth 2.0

**Resource**: _Leaderboards_: Displays the ranking of players in a competitive event

| Method | Accept | Content-type | Path/Endpoint                 | Request Parameters                                                       | Response (data type:field name)                                                         |
|--------|--------|--------------|-------------------------------|--------------------------------------------------------------------------|-----------------------------------------------------------------------------------------|
| GET    |        | JSON         | /leaderboards                 |                                                                          | array< object >: leaderboards                                                           |
| GET    |        | JSON         | /leaderboards/{leaderboardId} | Query Params<br/> string: leaderboardId                                  | string: leaderboardId<br/>string: name<br/>string: createdAt<br/>array< object >: ranks |
| POST   | JSON   | JSON         | /leaderboards                 | Request Body<br/>string: titleId<br/>string: name<br/>boolean: orderDesc | string: leaderboardId                                                                   |

Example:

Get a leaderboard

```
Endpoint: GET /leaderboards/{leaderboardId}

Example Request: 
curl http://localhost:8088/leaderboards/1

Example Response:
200 OK
{
    "leaderboardId":"leaderboard1",
    "name":"myLeaderboard",
    "ranks":
	    [
			{ 
			    "leaderboardId": "lb5001",
			    "entityId": "entity3",
			    "name":"Person A",
			    "position": 1,
			    "score":100,
			    "timestamp":"2023-12-10T12:25:90.276Z"
			  },
			{ 
			    "leaderboardId": "lb5001", 
			    "entityId": "entity4",
			    "score": 72 ,
			    "name":"Person C",
			    "position": 2,
			    "score":70,
			    "timestamp":"2023-07-13T04:50:33.222Z"
             }
		],
    "createdAt":"2023-10-05T14:48:00.000Z"
}
```

**Resource** - _Ranks_: An individual entry consisting of an entity’s placement on a leaderboard based on a set condition

| Method | Accept | Content-type | Path/Endpoint                                           | Request Parameters                                          | Response (data type:field name)                                                                                                                    |
|--------|--------|--------------|---------------------------------------------------------|-------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------|
| GET    |        | JSON         | /leaderboards/{leaderboardId}/ranks                     | Query Params<br/>string: leaderboardId                      | array< object >: ranks                                                                                                                             |
| GET    |        | JSON         | /leaderboards/{leaderboardId}/ranks/entities/{entityId} | Query Params<br/>string: leaderboardId<br/>string: entityId | string: leaderboardId<br/>string: entityId<br/>string: name<br/>string: leaderboardId<br/>number: position<br/>number: score<br/>string: timestamp |

Example:

Get an entity's rank in a leaderboard
```
Endpoint: GET /leaderboards/{leaderboardId}/ranks/entities/{entityId}

Example Request: 
curl http://localhost:8088/leaderboards/1/ranks/entities/1

Example Response:
200 OK
{
    "leaderboardId":"gameleaderboard1",
    "entityId":"entity1",
    "name":"Team 1",
    "position": 1, 
    "score":93,
    "timestamp":"2023-08-10T13:15:60.286Z"  
}
```
**Resource** - _Events_: A submission of an entity’s score, which could cause an update to a leaderboard

| Method | Accept | Content-type | Path/Endpoint | Request Parameters                                                                                  | Response (data type:field name) |
|--------|--------|--------------|---------------|-----------------------------------------------------------------------------------------------------|---------------------------------|
| POST   | JSON   | JSON         | /events       | Query Params<br/>string: leaderboardId<br/>string: entityId<br/>number: score<br/>string: timestamp | empty                           |

Example:

Submit score to the leaderboard for an entity

```
Endpoint: POST /events

Example Request: 
curl -X POST http://localhost:8088/events -H "Content-Type: application/json" -d '{"leaderboardId":"leaderboard1", "entityId":"myentity3","score": 467 ,"timestamp":"2023-04-23T18:25:43.511Z"}' 

Example Response:
201 Created 
{}
```

Challenges
---
**Error handling**
* What information should be shown to the clients when there are downstream errors with the Leaderboard system? Show too much and the system becomes more vulnerable to malicious actors.
Show too little and client won't know how to act on the error. 
* How should errors propagated to the clients in terms of structure

**API Design**
* Creating endpoints with nested resources vs adding more query parameters

**Leaderboard Service to Leaderboard Backend communication**
* What would be the API contract of the Leaderboard Backend?
* What would be the Leaderboard Backend methods of transport?

**Caching**
* What should be cached if anything?
* Best method for dealing with inconsistency

Future work
---
Expand requirements
* ex: Allow for clients to specify more unique conditions for ranking order for a leaderboard

Batch events API

Support Protobuf data format

Switch to reactive programming style 
