package edu.java.api.client;

import edu.java.configuration.ApplicationConfig;
import edu.java.service.exceptions.ApiErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import shared.dto.request.LinkUpdateRequest;
import shared.dto.response.ApiErrorResponse;

@Service
public class BotClient {

    private final WebClient webClient;

    @Autowired
    public BotClient(WebClient.Builder webClientBuilder, ApplicationConfig config) {
        this.webClient = webClientBuilder.baseUrl(config.botBaseUrl()).build();
    }

    public BotClient(WebClient.Builder webClientBuilder, String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public String sendUpdate(LinkUpdateRequest update) {
        return webClient
            .post()
            .uri("/updates")
            .body(BodyInserters.fromValue(update))
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response
                    .bodyToMono(ApiErrorResponse.class)
                    .flatMap(errorResponse -> Mono.error(new ApiErrorException(errorResponse)))
            )
            .bodyToMono(String.class)
            .block();
    }
}
