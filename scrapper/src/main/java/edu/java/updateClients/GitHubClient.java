package edu.java.updateClients;

import edu.java.updateClients.updateDto.GitHubEventResponse;
import java.util.List;

public interface GitHubClient {
    List<GitHubEventResponse> fetchRepositoryEvents(String owner, String repoName, Integer limit);
}
