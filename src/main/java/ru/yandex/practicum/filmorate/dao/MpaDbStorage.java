package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage.MpaStorage;

import java.util.List;

@AllArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    private RowMapper<Rating> getRatingMapper() {
        return ((rs, rowNum) -> new Rating(rs.getInt("rating_id"), rs.getString("name")));
    }

    @Override
    public Rating getRating(int id) {
        return jdbcTemplate.queryForObject("SELECT* " +
                        "FROM ratings WHERE rating_id = ?;",
                getRatingMapper(), id);
    }

    @Override
    public List<Rating> getRatings() {
        return jdbcTemplate.query("SELECT* FROM ratings;", getRatingMapper());
    }
}
