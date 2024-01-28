package org.gameservice.leaderboard.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.gameservice.leaderboard.model.Leaderboard;
import org.gameservice.leaderboard.model.Rank;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.web.util.UriComponentsBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class WebClientHelperTest {
    private static MockWebServer mockBackEnd;
    private final ObjectMapper objectMapper = new ObjectMapper();
    WebClientHelper webClientHelper = new WebClientHelper();

    private final String TEST_LEADERBOARD_ID = "leaderboardTestId";
    private final String TEST_ENTITY_ID = "entityTestId";
    private final int TEST_SCORE = 55;

    private final long TEST_TIMESTAMP = 123456L;
    private final String TEST_NAME = "myLeaderboard";

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    void performPost_getLeaderboardById() throws Exception {
        Leaderboard mockResponse = new Leaderboard(TEST_LEADERBOARD_ID,
                List.of(new Rank(TEST_LEADERBOARD_ID, TEST_ENTITY_ID, TEST_SCORE)), TEST_NAME, TEST_TIMESTAMP);

        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(mockResponse))
                .addHeader("Content-Type", "application/json"));

        URI uri = UriComponentsBuilder.newInstance().host(mockBackEnd.getHostName())
                .port(mockBackEnd.getPort())
                .scheme("http").path("/leaderboards/{leaderboardId}")
                .buildAndExpand(Map.of("leaderboardId","testId"))
                .toUri();

        Leaderboard result = webClientHelper.performGetRequest(uri, Leaderboard.class);

        Assertions.assertEquals(TEST_LEADERBOARD_ID, result.getLeaderboardId());
        List<Rank> rankResult = result.getRanks();
        Assertions.assertEquals(1, rankResult.size());
        Rank rank = rankResult.getFirst();
        Assertions.assertEquals(TEST_LEADERBOARD_ID, rank.getLeaderboardId());
        Assertions.assertEquals(TEST_ENTITY_ID, rank.getEntityId());
        Assertions.assertEquals(TEST_SCORE, rank.getScore());

    }
}
