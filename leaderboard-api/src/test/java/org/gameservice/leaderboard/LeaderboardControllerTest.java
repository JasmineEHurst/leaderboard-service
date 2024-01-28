package org.gameservice.leaderboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gameservice.leaderboard.controller.LeaderboardController;
import org.gameservice.leaderboard.model.Leaderboard;
import org.gameservice.leaderboard.model.Rank;
import org.gameservice.leaderboard.service.LeaderboardServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LeaderboardController.class)
@ContextConfiguration(classes = LeaderboardController.class)
public class LeaderboardControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private LeaderboardServiceImpl leaderboardService;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void getLeaderboardTest() throws Exception {
        List<Rank> ranks = List.of(new Rank("testId","entityId", 100));
        Leaderboard leaderboard = new Leaderboard("testId",
                ranks,
                "myLeaderboard",
                new Date().getTime());
        when(leaderboardService.getLeaderboardById(anyString())).thenReturn(leaderboard);

        MvcResult result = mockMvc.perform(get("/leaderboards/{leaderboardId}","testId")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        MockHttpServletResponse response = result.getResponse();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getHeader(HttpHeaders.CONTENT_TYPE));
        Leaderboard leaderboardResult = objectMapper.readValue(response.getContentAsString(), Leaderboard.class);

        Assertions.assertEquals(leaderboard.getLeaderboardId(), leaderboardResult.getLeaderboardId());
        Assertions.assertEquals(leaderboard.getRanks().size(), leaderboardResult.getRanks().size());
        Assertions.assertEquals(leaderboard.getName(), leaderboardResult.getName());
        Assertions.assertEquals(leaderboard.getTimestamp(), leaderboardResult.getTimestamp());
        // TODO verify that the correct content was returned for ranks
        // Assertions.assertTrue(leaderboardResult.getRanks().containsAll(leaderboard.getRanks()));
    }
    /* TODO: Add more cest Cases
        * Leaderboard not found
        * Invalid LeaderboardId
        * Internal Service Error
    * */
}
