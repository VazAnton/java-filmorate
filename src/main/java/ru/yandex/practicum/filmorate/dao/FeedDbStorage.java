package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage.FeedStorage;

import java.util.List;
import java.util.Map;

@Repository
public class FeedDbStorage implements FeedStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FeedDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void createFeed(Feed feed) {
        if (feed != null) {
            SimpleJdbcInsert feedInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("feed")
                    .usingGeneratedKeyColumns("event_id");
            int id = feedInsert.executeAndReturnKey(
                    Map.of(
                            "timestamp", feed.getTimestamp(),
                            "user_id", feed.getUserId(),
                            "event_type", feed.getEventType(),
                            "operation", feed.getOperation(),
                            "entity_id", feed.getEntityId()
                    )).intValue();
            feed.setEventId(id);
        }
    }

    @Override
    public List<Feed> getFeedByUserId(int id) {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        userDbStorage.getUser(id);
        return jdbcTemplate.query("SELECT* FROM feed WHERE user_id = ?;",
                getFeedMapper(), id);
    }

    private RowMapper<Feed> getFeedMapper() {
        return ((rs, rowNum) ->
                new Feed(rs.getInt("event_id"),
                        rs.getLong("timestamp"),
                        rs.getInt("user_id"),
                        rs.getString("event_type"),
                        rs.getString("operation"),
                        rs.getInt("entity_id")));
    }
}
