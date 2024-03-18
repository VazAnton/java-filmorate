package ru.yandex.practicum.filmorate.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JdbcTest
public class MpaDbStorageTest {

    final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorageTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Test
    public void checkGetRatings() {
        MpaDbStorage mpaDbStorage = new MpaDbStorage(jdbcTemplate);
        assertNotNull(mpaDbStorage.getRatings());
        assertEquals(5, mpaDbStorage.getRatings().size());
    }

    @Test
    public void checkGetRatingById() {
        MpaDbStorage mpaDbStorage = new MpaDbStorage(jdbcTemplate);
        assertNotNull(mpaDbStorage.getRating(1));
        assertEquals("G", mpaDbStorage.getRating(1).getName());
    }
}
