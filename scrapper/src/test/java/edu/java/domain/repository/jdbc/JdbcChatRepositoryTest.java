package edu.java.domain.repository.jdbc;

import edu.java.domain.model.Chat;
import edu.java.domain.model.Link;
import edu.java.scrapper.IntegrationTest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class JdbcChatRepositoryTest extends IntegrationTest {
    private static JdbcChatRepository chatRepository;

    @BeforeAll
    static void setUp() {
        chatRepository = new JdbcChatRepository(jdbcTemplate);
    }

    @Test
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
    public void testFindChatsByLinkId() {
        //Arrange
        Long chatId = 3000L;
        Link link = new Link("https://github.com/Kopu4ino/java-java");

        chatRepository.add(chatId);
        JdbcLinkRepository linkRepository = new JdbcLinkRepository(jdbcTemplate);
        linkRepository.add(chatId, link);
        Optional<Link> foundLink = linkRepository.findLinkByUrl(link.getUrl());

        //Act
        List<Long> chatIds = chatRepository.findAllChatIdsWithLink(foundLink.get().getId());

        //Assert
        assertThat(chatIds.size()).isEqualTo(1);
        assertThat(chatIds.getFirst()).isEqualTo(chatId);
    }

}
