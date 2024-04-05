package edu.java.service.jdbc;

import edu.java.domain.ChatRepository;
import edu.java.domain.model.Chat;
import edu.java.service.ChatService;
import edu.java.service.exceptions.ChatAlreadyRegisteredException;
import edu.java.service.exceptions.ChatIdNotExistsException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JdbcChatService implements ChatService {
    private final ChatRepository chatRepository;

    @Override
    public void registerChat(Long chatId) {
        Optional<Chat> foundChat = chatRepository.findById(chatId);
        if (foundChat.isPresent()) {
            throw new ChatAlreadyRegisteredException(chatId);
        }
        chatRepository.add(chatId);
    }

    @Override
    public void unregisterChat(Long chatId) {
        checkThatChatExists(chatId);
        chatRepository.delete(chatId);
    }

    @Override
    public void checkThatChatExists(Long chatId) {
        chatRepository.findById(chatId)
            .orElseThrow(() -> new ChatIdNotExistsException(chatId));
    }


    @Override
    public Optional<Chat> findChat(Long chatId) {
        return chatRepository.findById(chatId);
    }

    @Override
    public List<Long> findAllChatsIdsWithLink(Long linkId) {
        return chatRepository.findAllChatIdsWithLink(linkId);
    }

}
