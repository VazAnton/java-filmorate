package ru.yandex.practicum.filmorate.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JdbcTest
public class GenreDbStorageTest {

    final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorageTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Test
    public void checkGetGenres() {
        GenreDbStorage genreDbStorage = new GenreDbStorage(jdbcTemplate);
        assertNotNull(genreDbStorage.getGenres());
        assertEquals(6, genreDbStorage.getGenres().size());
    }

    @Test
    public void checkGetGenreById() {
        GenreDbStorage genreDbStorage = new GenreDbStorage(jdbcTemplate);
        assertNotNull(genreDbStorage.getGenre(1));
        assertEquals("Комедия", genreDbStorage.getGenre(1).getName());
    }
}
