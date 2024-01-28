package org.gameservice.leaderboard.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.gameservice.leaderboard.exception.LeaderboardServiceException;
import org.gameservice.leaderboard.model.ApiErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final String INTERNAL_SERVICE_ERROR_MESSAGE = "Internal Service Error";
    @ExceptionHandler
    public void handleLeaderboardServiceException(LeaderboardServiceException exception,
                                                  ServletWebRequest request) throws IOException {
        ApiErrorResponse apiErrorResponse = exception.getDownstreamError();
        request.getResponse().sendError(apiErrorResponse.getStatusCode(), apiErrorResponse.getErrorMessage());

    }

    @ExceptionHandler
    public void handleOtherException(Exception exception,
                                                  ServletWebRequest request) throws IOException {

        request.getResponse().sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, INTERNAL_SERVICE_ERROR_MESSAGE);

    }
}
