package edu.java.domain.chat;

import edu.java.domain.ChatRepository;
import edu.java.domain.LinkRepository;
import edu.java.domain.model.Chat;
import edu.java.domain.model.Link;
import edu.java.scrapper.IntegrationTest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ChatRepositoryTest extends IntegrationTest {
    private static ChatRepository chatRepository;

    @BeforeAll
    static void setUp() {
        chatRepository = new ChatRepository(jdbcTemplate);
    }

    @Test
    @Transactional
    public void addTest() {
        //Arrange
        Long chatId = 1000L;

        //Act
        chatRepository.add(chatId);
        Optional<Chat> chat = chatRepository.findById(chatId);

        //Assert
        assertThat(chat).isPresent();
        assertThat(chat.get().getId()).isEqualTo(chatId);
    }

    @Test
    @Transactional
    public void deleteTest() {
        //Arrange
        Long chatId = 2000L;

        //Act
        chatRepository.add(chatId);
        chatRepository.delete(chatId);
        Optional<Chat> chat = chatRepository.findById(chatId);

        //Assert
        assertThat(chat).isEmpty();
    }

    @Test
    @Transactional
    public void testFindChatsByLinkId() {
        //Arrange
        Long chatId = 3000L;
        Link link = new Link("https://github.com/Kopu4ino/java-java");

        chatRepository.add(chatId);
        LinkRepository linkRepository = new LinkRepository(jdbcTemplate);
        linkRepository.add(chatId, link);
        Optional<Link> foundLink = linkRepository.findLinkByUrl(link.getUrl());

        //Act
        List<Long> chatIds = chatRepository.findAllChatIdsWithLink(foundLink.get().getId());

        //Assert
        assertThat(chatIds.size()).isEqualTo(1);
        assertThat(chatIds.getFirst()).isEqualTo(chatId);
    }

}
