package org.gameservice.leaderboard.http;

import org.gameservice.leaderboard.model.ApiErrorResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.net.URI;

/** Helper functions for making webclient requests to services
 ** without all the verbosity of building the
 ** request manually. */
@Component
public class WebClientHelper {
    private final WebClient webClient = WebClient.builder().build();
//    public <T> T performPostRequest(String host, int port, String scheme, String path, Object body, Class<T> responseType) {
//        URI uri = buildUri(host, path, port, scheme);
//        // Perform the API request and return the result as the
//        // expected response type
//        Object result = webClient.post()
//        .uri(uri)
//        .accept(MediaType.APPLICATION_JSON)
//        .contentType(MediaType.APPLICATION_JSON)
//        .bodyValue(body)
//        .exchangeToMono(response -> responseHandler(response, responseType))
//        .block();
//
//        return (T) result;
//    }

    public <T> T performGetRequest(URI uri, Class<T> responseType) {
        // Perform the API request and return the result as the
        // expected response type
        // Add retries
        Object result = webClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> responseHandler(response, responseType))
                .block();

        return (T) result;
    }

    private <T> Mono<Object> responseHandler(ClientResponse response, Class<T> responseType) {
        if (response.statusCode().is2xxSuccessful()) {
            return response.bodyToMono(responseType);
        } else {
            return failureResponseHandler(response);
        }
    }

    /* Handle the failure cases for the response in the case of non 200
    *  for example, parsing the response to an error
    * */
    private <T> Mono<Object> failureResponseHandler(ClientResponse response) {
        if (response.headers().contentType().isPresent()
                && response.headers().contentType().get().includes(MediaType.APPLICATION_JSON)) {
           // this means the service returned some kind of known error that we can cast straight to an ApiErrorResponse
         return response.bodyToMono(ApiErrorResponse.class);
       } else {
            // this usually means we failed to hit the service at all (for example died in the load balancer or something
            // just return the response as a string so we can at least return it back to the client and hopefully get something more useful
            // out of it
             return response.bodyToMono(String.class);
        }
    }

}
