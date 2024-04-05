package edu.java.service.jdbc;

import edu.java.domain.LinkRepository;
import edu.java.domain.model.Link;
import edu.java.service.LinkService;
import edu.java.service.exceptions.ChatIdNotExistsException;
import edu.java.service.exceptions.LinkAlreadyTrackException;
import edu.java.service.exceptions.LinkNotExistsException;
import edu.java.service.updater.LinkUpdater;
import edu.java.service.updater.LinkUpdatersHolder;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JdbcLinkService implements LinkService {
    @Autowired
    private final LinkRepository linkRepository;

    @Autowired
    private final JdbcChatService chatService;

    @Autowired
    private final LinkUpdatersHolder linkUpdatersHolder;

    @Override
    public List<Link> getUserLinks(Long chatId) throws ChatIdNotExistsException {
        chatService.checkThatChatExists(chatId);
        return linkRepository.findChatLinks(chatId);
    }

    @Override
    @Transactional
    public Link addLink(Long chatId, Link link) throws ChatIdNotExistsException {
        chatService.checkThatChatExists(chatId);

        Optional<Link> foundLink = linkRepository.findLinkByChatIdAndUrl(chatId, link.getUrl());
        if (foundLink.isPresent()) {
            throw new LinkAlreadyTrackException(chatId, link.getUrl());
        }

        foundLink = linkRepository.findLinkByUrl(link.getUrl());
        if (foundLink.isEmpty()) {
            LinkUpdater updater = linkUpdatersHolder.getUpdaterByDomain(URI.create(link.getUrl()).getHost());
            updater.setLastUpdateTime(link);
        }
        return linkRepository.add(chatId, link);
    }

    @Override
    public Link deleteLink(Long chatId, Link link) throws ChatIdNotExistsException {
        chatService.checkThatChatExists(chatId);

        Link linkToDelete = linkRepository.findLinkByChatIdAndUrl(chatId, link.getUrl())
            .orElseThrow(() ->
                new LinkNotExistsException(chatId, link.getUrl()));

        linkRepository.delete(chatId, linkToDelete.getId());
        return linkToDelete;
    }

    @Override
    public List<Link> findAllOutdatedLinks(Integer count, Long interval) {
        return linkRepository.findAllOutdatedLinks(count, interval);
    }

    @Override
    public void setUpdateAndCheckTime(Link link, OffsetDateTime lastUpdateTime, OffsetDateTime lastCheckTime) {
        linkRepository.setUpdateAndCheckTime(link, lastUpdateTime, lastCheckTime);
    }
}
