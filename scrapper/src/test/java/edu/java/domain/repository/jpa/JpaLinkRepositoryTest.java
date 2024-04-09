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
class JpaLinkRepositoryTest extends IntegrationTest {
    private final JpaLinkRepository jpaLinkRepository;

    private final JpaChatRepository jpaChatRepository;

    private static List<Link> links;

    private static List<Chat> chats;

    @Autowired
    public JpaLinkRepositoryTest(JpaLinkRepository jpaLinkRepository, JpaChatRepository jpaChatRepository) {
        this.jpaLinkRepository = jpaLinkRepository;
        this.jpaChatRepository = jpaChatRepository;
    }

    @BeforeAll
    public static void testDataSetUp() {
        OffsetDateTime testDateTime =
            OffsetDateTime.of(2024, 3, 15, 13, 13, 0, 0, ZoneOffset.UTC);
        links = List.of(
            Link.builder().url("https://github.com/maximal/http-267")
                .lastCheck(testDateTime).lastUpdate(testDateTime).build(),
            Link.builder().url("https://stackoverflow.com/questions/78210424/wkwebview-js-injection")
                .lastCheck(testDateTime).lastUpdate(testDateTime).build(),
            Link.builder().url("https://github.com/pagekit/vue-resource")
                .lastCheck(testDateTime).lastUpdate(testDateTime).build(),
            Link.builder().url("https://stackoverflow.com/questions/39802264/jpa-how-to-persist-many-to-many-relation")
                .lastCheck(testDateTime).lastUpdate(testDateTime).build()
        );
        chats = List.of(
            new Chat(89898L),
            new Chat(98989L)
        );
    }

    @Test
    @Transactional
    public void testSaveAndFindChatLinks() {
        // Arrange
        Chat chat = chats.getFirst();
        Link link = links.getFirst();

        jpaChatRepository.save(chat);
        Link savedLink = jpaLinkRepository.save(link);
        jpaLinkRepository.saveLinkForChat(savedLink.getId(), chat.getId());

        // Act
        List<Link> chatLinks = jpaLinkRepository.findAllByTgChatsId(chat.getId());

        // Assert
        assertThat(chatLinks).isNotEmpty().hasSize(1);
        assertThat(chatLinks.get(0)).isEqualTo(savedLink);
    }

    @Test
    public void testSaveLink() {
        // Arrange
        Link link = links.get(1);

        // Act
        Link savedLink = jpaLinkRepository.save(link);

        // Assert
        assertThat(savedLink.getUrl()).isEqualTo(link.getUrl());
    }

    @Test
    public void testFindLinkByUrl() {
        // Arrange
        Link link = links.getLast();
        Link savedLink = jpaLinkRepository.save(link);

        // Act
        Link foundLink = jpaLinkRepository.findByUrl(link.getUrl());

        // Assert
        assertThat(savedLink).isEqualTo(foundLink);
    }

    @Test
    @Transactional
    public void testSaveLinkWithManyToManyRelationShip() {
        // Arrange
        Link link = links.get(2);
        Chat firstChat = chats.getFirst();
        Chat secondChat = chats.getLast();
        jpaChatRepository.save(firstChat);
        jpaChatRepository.save(secondChat);
        Link savedLink = jpaLinkRepository.save(link);
        jpaLinkRepository.saveLinkForChat(savedLink.getId(), firstChat.getId());
        jpaLinkRepository.saveLinkForChat(savedLink.getId(), secondChat.getId());

        // Act
        Optional<Link> firstChatLink = jpaLinkRepository.findByTgChatsIdAndUrl(firstChat.getId(), link.getUrl());
        Optional<Link> secondChatLink = jpaLinkRepository.findByTgChatsIdAndUrl(secondChat.getId(), link.getUrl());

        // Assert
        assertThat(firstChatLink).isPresent();
        assertThat(secondChatLink).isPresent();
        assertThat(firstChatLink).isEqualTo(secondChatLink);
    }

    @Test
    @Transactional
    public void testDeleteLinkForChat() {
        // Arrange
        Link link = links.getFirst();
        Chat chat = chats.getFirst();
        jpaChatRepository.save(chat);
        Link savedLink = jpaLinkRepository.save(link);
        jpaLinkRepository.saveLinkForChat(savedLink.getId(), chat.getId());

        // Act
        jpaLinkRepository.deleteForChat(chat.getId(), savedLink.getId());
        Optional<Link> foundChatLink = jpaLinkRepository.findByTgChatsIdAndUrl(chat.getId(), link.getUrl());

        // Assert
        assertThat(foundChatLink).isEmpty();
    }

    @Test
    @Transactional
    public void testFindAllOutdatedLinks() {
        // Arrange
        OffsetDateTime testDateTime = OffsetDateTime.now();
        Link firstLink = Link.builder().url(links.getFirst().getUrl()).lastCheck(testDateTime).lastUpdate(testDateTime).build();
        Link secondLink = Link.builder().url(links.get(1).getUrl()).lastCheck(testDateTime).lastUpdate(testDateTime).build();
        jpaLinkRepository.save(firstLink);
        jpaLinkRepository.save(secondLink);

        // Act & Assert
        List<Link> foundLinks = jpaLinkRepository.findAllOutdatedLinks(2, 60);
        assertThat(foundLinks).isEmpty();

        foundLinks = jpaLinkRepository.findAllOutdatedLinks(2, 0);
        assertThat(foundLinks).isNotEmpty();
        assertThat(foundLinks).hasSize(2);
    }

    @Test
    @Transactional
    public void testLinkExistenceForChat() {
        // Arrange
        Chat chat = chats.getLast();
        Link link = links.getFirst();
        jpaChatRepository.save(chat);
        Link savedLink = jpaLinkRepository.save(link);

        // Act & Assert
        assertThat(jpaLinkRepository.existsLinkByTgChatsIdAndUrl(chat.getId(), savedLink.getUrl())).isFalse();

        jpaLinkRepository.saveLinkForChat(savedLink.getId(), chat.getId());
        assertThat(jpaLinkRepository.existsLinkByTgChatsIdAndUrl(chat.getId(), savedLink.getUrl())).isTrue();
    }


}
