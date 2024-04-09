package edu.java.domain.repository.jpa;

import edu.java.domain.model.Chat;
import edu.java.domain.model.Link;
import edu.java.scrapper.IntegrationTest;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class JpaChatRepositoryTest extends IntegrationTest {

    private final JpaChatRepository jpaChatRepository;

    private final JpaLinkRepository jpaLinkRepository;

    private static List<Chat> chats;

    private static Link link;

    @Autowired
    public JpaChatRepositoryTest(JpaChatRepository jpaChatRepository, JpaLinkRepository jpaLinkRepository) {
        this.jpaChatRepository = jpaChatRepository;
        this.jpaLinkRepository = jpaLinkRepository;
    }

    @BeforeAll
    public static void testDataSetUp() {
        OffsetDateTime testDateTime =
            OffsetDateTime.of(2024, 3, 15, 13, 13, 0, 0, ZoneOffset.UTC);

        chats = List.of(
            new Chat(11111L),
            new Chat(22222L),
            new Chat(33333L),
            new Chat(44444L)
        );
        link = Link.builder().url("https://github.com/Kopu4ino/Link-tracker")
            .lastCheck(testDateTime).lastUpdate(testDateTime).build();
    }

    @Test
    @Transactional
    public void testSaveChat() {
        // Arrange
        Chat testChat = chats.getFirst();

        // Act
        jpaChatRepository.save(testChat);
        Optional<Chat> savedChat = jpaChatRepository.findById(testChat.getId());

        // Assert
        assertThat(savedChat).isPresent();
        assertThat(savedChat.get()).isEqualTo(testChat);
    }

    @Test
    @Transactional
    public void testDeleteChat() {
        // Arrange
        Chat testChat = chats.get(1);
        jpaChatRepository.save(testChat);

        // Act & Assert before deletion
        assertThat(jpaChatRepository.existsById(testChat.getId())).isTrue();

        // Act
        jpaChatRepository.deleteById(testChat.getId());

        // Assert after deletion
        assertThat(jpaChatRepository.existsById(testChat.getId())).isFalse();
    }

    @Test
    @Transactional
    public void testFindAllChatsIdsWithLink() {
        // Arrange
        Chat firstChat = chats.get(2);
        Chat secondChat = chats.get(3);
        jpaChatRepository.save(firstChat);
        jpaChatRepository.save(secondChat);
        Link savedLink = jpaLinkRepository.save(link);
        jpaLinkRepository.saveLinkForChat(savedLink.getId(), firstChat.getId());
        jpaLinkRepository.saveLinkForChat(savedLink.getId(), secondChat.getId());

        // Act
        List<Long> chatsWithThisLink = jpaChatRepository.findAllChatsIdsWithLink(savedLink.getId());

        // Assert
        assertThat(chatsWithThisLink).isNotEmpty().hasSize(2);
        assertThat(chatsWithThisLink).contains(firstChat.getId(), secondChat.getId());
    }
}
