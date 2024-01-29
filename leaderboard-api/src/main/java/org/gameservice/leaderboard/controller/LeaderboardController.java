package org.gameservice.leaderboard.controller;

import org.gameservice.leaderboard.exception.LeaderboardServiceException;
import org.gameservice.leaderboard.model.ApiErrorResponse;
import org.gameservice.leaderboard.model.Leaderboard;
import org.gameservice.leaderboard.model.Rank;
import org.gameservice.leaderboard.service.LeaderboardServiceImpl;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/leaderboards")
@Validated
public class LeaderboardController {
    Logger logger = LoggerFactory.getLogger(LeaderboardController.class);

    private final LeaderboardServiceImpl leaderboardServiceImpl;
    public LeaderboardController(LeaderboardServiceImpl leaderboardServiceImpl) {
        this.leaderboardServiceImpl = leaderboardServiceImpl;
    }
    // Note on response type: Return responseEntity<Class> whenever there is a need to
    // have access to the full HTTP response such as changing the status code
    @RequestMapping(value = "/{leaderboardId}",
    produces = MediaType.APPLICATION_JSON_VALUE,
    method = RequestMethod.GET)
    public Leaderboard getLeaderboard(@PathVariable String leaderboardId) {
            Leaderboard response = leaderboardServiceImpl.getLeaderboardById(leaderboardId);
            if(response == null) {
                // Log error details
                throw new LeaderboardServiceException(
                        new ApiErrorResponse(HttpStatus.NOT_FOUND.value(),"Leaderboard with id {} not found", "/leaderboard")
                );
            }
            return response;
    }

    @RequestMapping(value ="/{leaderboardId}/ranks/entities/{entityId}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public Rank getRankByEntityId(@PathVariable String leaderboardId, @PathVariable String entityId) {
            return new Rank(leaderboardId, entityId, 93);
    }

    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public List<Leaderboard> getLeaderboards() {
        Leaderboard l1 = new Leaderboard("123", List.of(new Rank("123", "ABC", 45)), "firstLeaderboard", new Date().getTime());
        Leaderboard l2 = new Leaderboard("456", List.of(new Rank("456", "ABC", 60)), "secondLeaderboard", new Date().getTime());
        return List.of(l1, l2);
    }
}

