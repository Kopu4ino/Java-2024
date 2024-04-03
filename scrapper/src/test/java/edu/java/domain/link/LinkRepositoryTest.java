package edu.java.domain.link;

import edu.java.domain.ChatRepository;
import edu.java.domain.LinkRepository;
import edu.java.domain.model.Link;
import edu.java.scrapper.IntegrationTest;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class LinkRepositoryTest extends IntegrationTest {
    private static LinkRepository linkRepository;
    private static ChatRepository chatRepository;
    private static List<Long> chatIds;

    private static List<Link> links;

    @BeforeAll
    static void setUp() {
        linkRepository = new LinkRepository(jdbcTemplate);
        chatRepository = new ChatRepository(jdbcTemplate);

        chatIds = new ArrayList<>();
        chatIds.add(100L);
        chatIds.add(200L);
        chatIds.add(300L);
        chatIds.add(400L);
        chatIds.add(500L);
        chatIds.add(600L);
        chatIds.add(700L);

        for (Long chatId : chatIds) {
            chatRepository.add(chatId);
        }

        links = List.of(
            new Link("https://github.com/Kopu4ino/java"),
            new Link("https://github.com/sanyarnd/java-course-2023-backend-template"),
            new Link("https://stackoverflow.com/questions/28295625/mockito-spy-vs-mock"),
            new Link("https://github.com/Kopu4ino/client"),
            new Link("https://github.com/Kopu4ino/yobit-wraper"),
            new Link("https://github.com/Kopu4ino/project")
        );
    }

    @Transactional
    @Test
    public void addAndFindTest() {
        // Arrange
        Long chatId = chatIds.getFirst();
        Link link = links.getFirst();

        // Act
        linkRepository.add(chatId, link);
        Optional<Link> linkByUrl = linkRepository.findLinkByUrl(link.getUrl());
        Optional<Link> chatLinkByUrl = linkRepository.findLinkByChatIdAndUrl(chatId, link.getUrl());
        List<Link> chatLinks = linkRepository.findChatLinks(chatId);

        // Assert
        assertThat(linkByUrl).isPresent();
        assertThat(linkByUrl.get().getUrl()).isEqualTo(link.getUrl());

        assertThat(chatLinkByUrl).isPresent();
        assertThat(chatLinkByUrl.get().getUrl()).isEqualTo(link.getUrl());

        assertThat(chatLinks.size()).isEqualTo(1);
        assertThat(chatLinks.getFirst().getUrl()).isEqualTo(link.getUrl());
    }

    @Test
    public void testGetLinkByUrl() {
        // Arrange
        Long chatId = chatIds.get(1);
        Link link = links.get(1);

        // Act
        linkRepository.add(chatId, link);
        Optional<Link> chatLink = linkRepository.findLinkByChatIdAndUrl(chatId, link.getUrl());

        // Assert
        assertThat(chatLink).isPresent();
        assertThat(chatLink.get().getUrl()).isEqualTo(link.getUrl());
    }

    @Test
    public void testSaveLinkWithManyToManyRelationship() {
        // Arrange
        Long chatId1 = chatIds.get(2);
        Long chatId2 = chatIds.get(3);
        Link link = links.get(2);
        linkRepository.add(chatId1, link);
        linkRepository.add(chatId2, link);

        // Act
        Optional<Link> firstChatLink = linkRepository.findLinkByChatIdAndUrl(chatId1, link.getUrl());
        Optional<Link> secondChatLink = linkRepository.findLinkByChatIdAndUrl(chatId2, link.getUrl());

        // Assert
        assertThat(firstChatLink).isPresent();
        assertThat(secondChatLink).isPresent();
        assertThat(firstChatLink.get()).isEqualTo(secondChatLink.get());
    }

    @Test
    public void testDeleteLink() {
        //Arrange
        Long chatId = chatIds.get(4);
        Link link = links.get(3);
        linkRepository.add(chatId, link);

        //Act
        Optional<Link> chatLink = linkRepository.findLinkByChatIdAndUrl(chatId, link.getUrl());
        linkRepository.delete(chatId, chatLink.get().getId());
        chatLink = linkRepository.findLinkByChatIdAndUrl(chatId, link.getUrl());

        //Assert
        assertThat(chatLink).isEmpty();
    }

    @Test
    public void testFindAllOutdatedLinks() {
        // Arrange
        Long chatId = chatIds.get(5);
        Link firstLink = links.get(3);
        Link secondLink = links.get(4);
        firstLink.setLastUpdate(OffsetDateTime.now());
        secondLink.setLastUpdate(OffsetDateTime.now());

        linkRepository.add(chatId, firstLink);
        linkRepository.add(chatId, secondLink);

        //Act
        List<Link> foundLinks1 = linkRepository.findAllOutdatedLinks(2, 60L);
        List<Link> foundLinks2 = linkRepository.findAllOutdatedLinks(2, 0L);

        //Arrange
        assertThat(foundLinks1.size()).isEqualTo(0);

        assertThat(foundLinks2.size()).isGreaterThan(0);
        assertThat(foundLinks2.size()).isEqualTo(2);
    }

    @Test
    public void testSetUpdateAndCheckTime() {
        // Arrange
        Long chatId = chatIds.get(6);
        Link link = links.get(5);
        linkRepository.add(chatId, link);

        Optional<Link> chatLink = linkRepository.findLinkByUrl(link.getUrl());

        OffsetDateTime lastUpdateTime = OffsetDateTime.now();
        OffsetDateTime lastCheckTime = OffsetDateTime.now();

        //Act
        linkRepository.setUpdateAndCheckTime(chatLink.get(), lastUpdateTime, lastCheckTime);
        chatLink = linkRepository.findLinkByUrl(link.getUrl());

        //Assert
        assertThat(chatLink.get().getLastCheck().toEpochSecond()).isEqualTo(lastCheckTime.toEpochSecond());
        assertThat(chatLink.get().getLastUpdate().toEpochSecond()).isEqualTo(lastUpdateTime.toEpochSecond());
    }
}
