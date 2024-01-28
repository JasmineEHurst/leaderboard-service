package org.gameservice.leaderboard.model;

public class Rank {

    private String leaderboardId;
    private String entityId;
    private int score;

    public Rank() {

    }

    public Rank(String leaderboardId, String entityId, int score) {
        this.leaderboardId = leaderboardId;
        this.entityId = entityId;
        this.score = score;
    }

    public String getLeaderboardId() {
        return leaderboardId;
    }

    public String getEntityId() {
        return entityId;
    }

    public int getScore() {
        return score;
    }
}