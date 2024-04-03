package edu.java.service.updater;

import edu.java.domain.model.Link;
import edu.java.domain.model.Update;
import edu.java.updateClients.StackOverflowClient;
import edu.java.updateClients.updateDto.StackOverflowQuestionResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StackOverflowLinkUpdater implements LinkUpdater {
    private String supportDomain = "stackoverflow.com";

    private final StackOverflowClient stackOverflowWebClient;

    private final int idIndex = 3;

    @Override
    public String getSupportDomain() {
        return supportDomain;
    }

    @Override
    public Optional<Update> fetchUpdate(Link link) {
        Long questionId = getQuestionId(link.getUrl());
        StackOverflowQuestionResponse response = stackOverflowWebClient.fetchQuestion(questionId);

        for (var item : response.items()) {
            if (item.lastActivityDate().isAfter(link.getLastUpdate())) {
                return Optional.of(new Update(link.getId(), link.getUrl(),
                    "произошли изменения в вопросе.", item.lastActivityDate()
                ));
            }
        }
        return Optional.empty();
    }

    @Override
    public void setLastUpdateTime(Link link) {
        long questionId = getQuestionId(link.getUrl());
        StackOverflowQuestionResponse response = stackOverflowWebClient.fetchQuestion(questionId);
        link.setLastUpdate(response.items().getLast().lastActivityDate());
    }

    private long getQuestionId(String url) {
        String[] urlParts = url.split("/+");
        return Long.parseLong(urlParts[idIndex]);
    }
}
