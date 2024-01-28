package org.gameservice.leaderboard.service;

import org.gameservice.leaderboard.controller.LeaderboardController;
import org.gameservice.leaderboard.exception.LeaderboardServiceException;
import org.gameservice.leaderboard.http.WebClientHelper;
import org.gameservice.leaderboard.model.ApiErrorResponse;
import org.gameservice.leaderboard.model.Leaderboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.AbstractMap;
import java.util.Map;

@Service
public class LeaderboardServiceImpl {
    /* * NOTE: IntelliJ Community edition doesn't Support fully Spring
    * so some key features are missing, such as Spring configuration properties
    * I'm unable to utilize the @Value annotation.
    * e.g @Value("${leaderboard-backend.host}")
    * */
    Logger logger = LoggerFactory.getLogger(LeaderboardController.class);
    private final String LEADERBOARD_PROCESSOR_HOST = "localhost";
    private final int LEADERBOARD_PROCESSOR_PORT = 8081;
    private final String LEADERBOARD_SCHEME = "https";
    private final String GET_LEADERBOARD_PATH_TEMPLATE = "/leaderboards/{leaderboardId}";
    private final WebClientHelper webClientHelper;

    public LeaderboardServiceImpl(WebClientHelper webClientHelper) {
        this.webClientHelper = webClientHelper;
    }

    public Leaderboard getLeaderboardById(String leaderboardId) {
        Map<String, String> pathVariables = Map.ofEntries(
                new AbstractMap.SimpleEntry<>("leaderboardId", leaderboardId)
        );
        Object response =
                leaderboardBackendGetRequest(LEADERBOARD_PROCESSOR_HOST, LEADERBOARD_PROCESSOR_PORT, LEADERBOARD_SCHEME,
                        GET_LEADERBOARD_PATH_TEMPLATE, pathVariables, Leaderboard.class);

        // Interpret the response. If an error occurred decide what exception to throw.
        validateResponse(response, Leaderboard.class);

        return response == null? null: (Leaderboard) response;

    }

    private void validateResponse(Object result, Class expectedResponseType) {
        if(result!=null) {
            if (result instanceof ApiErrorResponse) { // Assuming the downstream service returns a known error structure
                ApiErrorResponse error = (ApiErrorResponse) result;
                logger.error("Failed to {} for request {}{} with uri variables {}, body {}, and response {}", error);
                throw new LeaderboardServiceException(error);
            } if (result instanceof String && expectedResponseType != String.class) {
                logger.error("Failed to {} for request {}{} with uri variables {}, body {}, and response {}", result);
                throw new LeaderboardServiceException(new ApiErrorResponse(500, "Unknown failure:" + result, ""));

            }
        }

    }

    private <T> T leaderboardBackendGetRequest(String host, int port, String scheme, String pathTemplate, Map<String,
            String> pathVariables, Class<T> responseType) {
        URI uri = UriComponentsBuilder.newInstance()
                .scheme(scheme).host(host).port(port)
                .path(pathTemplate).buildAndExpand(pathVariables).toUri();

        return webClientHelper.performGetRequest(uri, responseType);
    }

}
