package edu.java.updateClients.impl;

import edu.java.configuration.ApplicationConfig;
import edu.java.updateClients.GitHubClient;
import edu.java.updateClients.updateDto.GitHubEventResponse;
import java.util.Collections;
import java.util.List;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

public class DefaultGitHubClient implements GitHubClient {
    private final WebClient webClient;

    public DefaultGitHubClient(WebClient.Builder webClientBuilder, ApplicationConfig config) {
        this.webClient = webClientBuilder.baseUrl(config.gitHubBaseUrl()).build();
    }

    public DefaultGitHubClient(WebClient.Builder webClientBuilder, String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public List<GitHubEventResponse> fetchRepositoryEvents(String owner, String repoName, Integer limit) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/repos/{owner}/{repoName}/events")
                .queryParam("per_page", limit)
                .build(owner, repoName))
            .retrieve()
            .bodyToFlux(GitHubEventResponse.class)
            .collectList()
            .onErrorResume(ex -> {
                if (ex instanceof WebClientResponseException.NotFound) {
                    return Mono.just(Collections.emptyList());
                } else {
                    return Mono.error(ex);
                }
            })
            .block();
    }

}
