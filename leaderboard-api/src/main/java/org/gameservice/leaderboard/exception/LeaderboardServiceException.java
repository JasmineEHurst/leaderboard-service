package org.gameservice.leaderboard.exception;

import org.gameservice.leaderboard.model.ApiErrorResponse;

public class LeaderboardServiceException extends RuntimeException {

    /* The underlying error response indicating why this exception happened */
    private ApiErrorResponse downstreamError;

    private String statusCode;
    public LeaderboardServiceException(ApiErrorResponse downstreamError) {
        this.downstreamError = downstreamError;
    }


    public ApiErrorResponse getDownstreamError() {
        return downstreamError;
    }
}
