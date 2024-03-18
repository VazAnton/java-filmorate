package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage.GenreStorage;

import java.util.List;

@AllArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    private RowMapper<Genre> getGenreMapper() {
        return ((rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("name")));
    }

    @Override
    public Genre getGenre(int id) {
        return jdbcTemplate.queryForObject("SELECT* FROM genres WHERE genre_id = ?;",
                getGenreMapper(), id);
    }

    @Override
    public List<Genre> getGenres() {
        return jdbcTemplate.query("SELECT* FROM genres;", getGenreMapper());
    }
}
