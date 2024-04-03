package edu.java.configuration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotBlank String gitHubBaseUrl,
    @NotBlank String stackoverflowBaseUrl,
    @NotBlank String botBaseUrl,
    @NotNull Integer updatesCount,
    @NotNull Long interval,

    @Bean
    @NotNull Scheduler scheduler
) {
    public record Scheduler(boolean enable, @Bean @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
    }
}
