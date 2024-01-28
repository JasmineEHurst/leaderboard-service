package org.gameservice.leaderboard.model;

import java.util.List;
// Could've used records here but ran into a deserialzation issue
public class Leaderboard {
    private String leaderboardId;
    private List<Rank> ranks;
    private String name;
    private long timestamp;
    public Leaderboard() {

    }
    public Leaderboard(String leaderboardId, List<Rank> ranks, String name, long timestamp) {
        this.leaderboardId = leaderboardId;
        this.ranks = ranks;
        this.name = name;
        this.timestamp = timestamp;
    }

    public String getLeaderboardId() {
        return leaderboardId;
    }

    public List<Rank> getRanks() {
        return ranks;
    }

    public String getName() {
        return name;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

