Leaderboard Service App [WIP]
=================================
Requirements
---
* Maven 3+
* Java 21+

Build and run locally
---
Build the application by navigating to the root directory and running the following command:
```
mvn clean install
```
Start the application by navigating to the leaderboard-api directory and running the following command:
```
mvn spring-boot:run
```

Running in Intellij
---

Example run configuration: 
![](images/run_config.png)

Try it out
---
The following endpoints currently returned mocked data as a sample response:
* /leaderboards

In a command line run:
```
curl localhost:8088/leaderboards
```

* leaderboards/{leaderboardId}/ranks/entities/{entityId}

In a command line run:
```
curl localhost:8088/leaderboards/112/ranks/entities/5
```
