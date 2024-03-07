package edu.java.clients;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import edu.java.clients.impl.DefaultStackOverflowClient;
import edu.java.dto.StackOverflowQuestionResponse;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

class DefaultStackOverflowClientTest {
    private static WireMockServer wireMockServer;

    @BeforeEach
    public void setup() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
        wireMockServer.stubFor(WireMock.get(urlPathEqualTo("/questions/1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(
                    """
                        {
                          "items": [
                            {
                              "owner": {
                                "display_name": "Kopu4ino"
                              },
                              "last_activity_date": 1705410153
                            }
                          ]
                        }
                        """
                )
            )
        );
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void fetchQuestionShouldReturnExpectedResponse() {
        //Arrange
        String baseUrl = String.format("http://localhost:%s", wireMockServer.port());
        StackOverflowClient client = new DefaultStackOverflowClient(WebClient.builder(), baseUrl);
        OffsetDateTime expectedDate = OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153), ZoneOffset.UTC);

        //Act
        List<StackOverflowQuestionResponse> responseList = client.fetchQuestion("1");

        //Assert
        assertThat(responseList).hasSize(1);
        assertThat(responseList.getFirst().items().getFirst().owner().displayName()).isEqualTo("Kopu4ino");
        assertThat(responseList.getFirst().items().getFirst().getLastActivityDateAsOffsetDateTime()).isEqualTo(expectedDate);
    }
}
