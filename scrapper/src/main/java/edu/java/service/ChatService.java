package edu.java.service;

import edu.java.domain.model.Chat;
import java.util.List;
import java.util.Optional;

public interface ChatService {
    void registerChat(Long chatId);

    void unregisterChat(Long chatId);

    void checkThatChatExists(Long chatId);

    Optional<Chat> findChat(Long chatId);

    List<Long> findAllChatsIdsWithLink(Long linkId);
}
