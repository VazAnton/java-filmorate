package ru.yandex.practicum.filmorate.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Director;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
public class DirectorDbStorageTest {

    final JdbcTemplate jdbcTemplate;
    Director testedDirector = Director.builder().build();

    @Autowired
    public DirectorDbStorageTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Test
    public void checkAddDirectorIfDirectorCanPassValidation() {
        DirectorDbStorage directorDbStorage = new DirectorDbStorage(jdbcTemplate);
        testedDirector = Director.builder()
                .name("Квентин Тарантино")
                .build();

        directorDbStorage.addDirector(testedDirector);

        assertThat(directorDbStorage.getDirector(testedDirector.getId()))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(testedDirector);
    }

    @Test
    public void checkUpdateDirectorIfDirectorCanPassValidation() {
        DirectorDbStorage directorDbStorage = new DirectorDbStorage(jdbcTemplate);
        testedDirector = Director.builder()
                .name("Квентин Тарантино")
                .build();
        directorDbStorage.addDirector(testedDirector);
        Director updatedDirector = Director.builder()
                .id(testedDirector.getId())
                .name("Тарантино")
                .build();

        assertThat(directorDbStorage.updateDirector(updatedDirector))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(directorDbStorage.getDirector(testedDirector.getId()));
    }

    @Test
    public void checkGetDirectors() {
        DirectorDbStorage directorDbStorage = new DirectorDbStorage(jdbcTemplate);
        testedDirector = Director.builder()
                .name("Квентин Тарантино")
                .build();
        directorDbStorage.addDirector(testedDirector);
        Director anotherDirector = Director.builder()
                .name("Вес Крейвен")
                .build();
        directorDbStorage.addDirector(anotherDirector);

        assertEquals(2, directorDbStorage.getDirectors().size());
    }

    @Test
    public void checkDeleteDirectorIfDirectorExists() {
        DirectorDbStorage directorDbStorage = new DirectorDbStorage(jdbcTemplate);
        testedDirector = Director.builder()
                .name("Квентин Тарантино")
                .build();
        directorDbStorage.addDirector(testedDirector);

        assertTrue(directorDbStorage.deleteDirector(testedDirector.getId()));
    }

    @Test
    public void deleteDirectorShouldThrowEmptyResultExceptionIfDirectorNotExists() {
        DirectorDbStorage directorDbStorage = new DirectorDbStorage(jdbcTemplate);
        assertEquals(0, directorDbStorage.getDirectors().size());

        assertThrows(EmptyResultDataAccessException.class, () -> directorDbStorage.deleteDirector(1));
    }
}
