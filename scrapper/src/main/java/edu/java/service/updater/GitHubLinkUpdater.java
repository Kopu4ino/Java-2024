package edu.java.service.updater;

import edu.java.domain.model.Link;
import edu.java.domain.model.Update;
import edu.java.updateClients.GitHubClient;
import edu.java.updateClients.updateDto.GitHubEventResponse;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GitHubLinkUpdater implements LinkUpdater {
    private final String supportDomain = "github.com";

    private final GitHubClient gitHubWebClient;

    private final int userIndex = 2;

    private final int repositoryIndex = 3;

    @Override
    public String getSupportDomain() {
        return supportDomain;
    }

    @Override
    public Optional<Update> fetchUpdate(Link link) {
        var repositoryData = getUserAndRepository(link.getUrl());
        List<GitHubEventResponse> response =
            gitHubWebClient.fetchRepositoryEvents(repositoryData.getKey(), repositoryData.getValue(), 1);

        Optional<Update> update = Optional.empty();

        if (response.isEmpty()) {
            return update;
        }
        GitHubEventResponse lastEvent = response.getFirst();
        if (lastEvent.createdAt().isAfter(link.getLastUpdate())) {
            update = Optional.of(
                new Update(
                    link.getId(),
                    link.getUrl(),
                    "\nNew Event!\nType: %s\nBy_User:%s".formatted(
                        lastEvent.type(),
                        lastEvent.actor().login()
                    ),
                    lastEvent.createdAt()
                )
            );
        }
        return update;
    }

    @Override
    public void setLastUpdateTime(Link link) {
        var repositoryData = getUserAndRepository(link.getUrl());
        List<GitHubEventResponse> response =
            gitHubWebClient.fetchRepositoryEvents(repositoryData.getKey(), repositoryData.getValue(), 1);

        if (response.isEmpty()) {
            link.setLastUpdate(OffsetDateTime.now());
        } else {
            link.setLastUpdate(response.getFirst().createdAt());
        }
    }

    private Pair<String, String> getUserAndRepository(String url) {
        String[] urlParts = url.split("/+");
        return Pair.of(urlParts[userIndex], urlParts[repositoryIndex]);
    }
}
