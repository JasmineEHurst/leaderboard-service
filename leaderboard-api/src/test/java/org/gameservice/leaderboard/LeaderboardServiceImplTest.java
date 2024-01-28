package org.gameservice.leaderboard;

import org.gameservice.leaderboard.exception.LeaderboardServiceException;
import org.gameservice.leaderboard.http.WebClientHelper;
import org.gameservice.leaderboard.model.ApiErrorResponse;
import org.gameservice.leaderboard.service.LeaderboardServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LeaderboardServiceImplTest {

    @InjectMocks
    private LeaderboardServiceImpl leaderboardServiceImpl;
    @Mock
    private WebClientHelper webClientHelper;
    private final String TEST_URL = "localhost:8080/testPath";
    private final String INTERNAL_SERVICE_ERROR_MESSAGE = "Internal Service Error";
    private final int INTERNAL_SERVICE_ERROR_CODE = 500;
    @Test
    void getLeaderboardById_InternalServiceError(){
        ApiErrorResponse apiErrorResponse =
                new ApiErrorResponse(INTERNAL_SERVICE_ERROR_CODE, INTERNAL_SERVICE_ERROR_MESSAGE, TEST_URL);
        when(webClientHelper.performGetRequest(any(), any()))
                .thenReturn(apiErrorResponse);
        LeaderboardServiceException thrown = Assertions.assertThrows(LeaderboardServiceException.class, () -> {
            leaderboardServiceImpl.getLeaderboardById("testId");
        });

        //Assert exception is thrown
        Assertions.assertInstanceOf(ApiErrorResponse.class, thrown.getDownstreamError());
        ApiErrorResponse errorResponse = thrown.getDownstreamError();
        Assertions.assertEquals(INTERNAL_SERVICE_ERROR_CODE, errorResponse.getStatusCode());
        Assertions.assertEquals(INTERNAL_SERVICE_ERROR_MESSAGE, errorResponse.getErrorMessage());
        Assertions.assertEquals(TEST_URL, errorResponse.getUrl());
    }

    /** TODO Create test cases
        happy path
        4xx
     **/
}
