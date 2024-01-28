package org.gameservice.leaderboard.model;

public class ApiErrorResponse {
    private int statusCode;
    private String errorMessage;
    private String url;

    public ApiErrorResponse() {

    }
    public ApiErrorResponse(int statusCode, String errorMessage, String url) {
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
        this.url = url;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getUrl() {
        return url;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
