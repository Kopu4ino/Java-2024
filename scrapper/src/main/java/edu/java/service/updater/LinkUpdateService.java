package edu.java.service.updater;

import edu.java.domain.model.Link;
import edu.java.domain.model.Update;
import edu.java.service.ChatService;
import edu.java.service.LinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shared.dto.request.LinkUpdateRequest;

@Service
@RequiredArgsConstructor
public class LinkUpdateService {
    private final LinkUpdatersHolder linkUpdatersHolder;

    private final LinkService linkService;

    private final ChatService chatService;

    public List<LinkUpdateRequest> fetchAllUpdates(Integer updatesCount, Long interval) {
        List<Link> links = linkService.findAllOutdatedLinks(updatesCount, interval);
        List<Update> updates = new ArrayList<>();

        links.forEach((link) -> {
            String host = URI.create(link.getUrl()).getHost();
            LinkUpdater updater = linkUpdatersHolder.getUpdaterByDomain(host);

            Optional<Update> update = updater.fetchUpdate(link);
            update.ifPresent((u) -> {
                updates.add(u);
                linkService.setUpdateAndCheckTime(link, u.updateTime(), OffsetDateTime.now(ZoneId.systemDefault()));
            });
        });

        return convertToLinkUpdateRequests(updates);
    }

    public List<LinkUpdateRequest> convertToLinkUpdateRequests(List<Update> updates) {
        List<LinkUpdateRequest> requests = new ArrayList<>();

        updates.forEach((update) -> {
            List<Long> chatIds = chatService.findAllChatsIdsWithLink(update.linkId());
            var linkUpdateRequest =
                new LinkUpdateRequest(update.linkId(), update.url(), update.description(), chatIds);
            requests.add(linkUpdateRequest);
        });

        return requests;
    }
}
