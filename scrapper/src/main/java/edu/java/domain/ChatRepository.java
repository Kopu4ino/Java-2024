package edu.java.domain;

import edu.java.domain.model.Chat;
import edu.java.domain.model.Link;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatRepository {
    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public void add(Long chatId) {
        jdbcTemplate.update("INSERT INTO chat VALUES (?)", chatId);
    }

    public Optional<Chat> findById(Long chatId) {
        return jdbcTemplate.query("select * from chat where id=?", new BeanPropertyRowMapper<>(Chat.class), chatId)
            .stream().findAny();
    }

    public void delete(Long chatId) {
        jdbcTemplate.update("DELETE FROM chat WHERE id=?", chatId);
    }

    public List<Link> findAllTrackedLinksById(Long chatId) {
        return jdbcTemplate.query(
            "SELECT * FROM chat_link where chat_id=?", new BeanPropertyRowMapper<>(Link.class), chatId);
    }

    public List<Long> findAllChatIdsWithLink(Long linkId) {
        return
            jdbcTemplate.queryForList("SELECT chat_id FROM chat_link WHERE link_id = ?", Long.class, linkId);
    }

}
