package ru.yandex.practicum.filmorate.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
class FilmDbStorageTest {

    final JdbcTemplate jdbcTemplate;
    Film testedFilm = Film.builder().build();
    User testedUser = User.builder().build();

    @Autowired
    public FilmDbStorageTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Test
    public void checkAddFilmIfFilmCanPassedValidation() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        testedFilm = Film.builder()
                .name("Маска")
                .description("Фильм Маска -  это захватывающая комедия, где " +
                        "главный герой Стэнли Ипкисс случайно находит магическую маску, что дарует ему " +
                        "невероятные суперсилы. " +
                        "Фильм смешной и непременно заставит вас улыбнуться.")
                .duration(126)
                .releaseDate(LocalDate.of(2003, 3,
                        26))
                .mpa(Rating.builder().id(4).build())
                .build();

        filmDbStorage.addFilm(testedFilm);

        assertThat(filmDbStorage.getFilm(testedFilm.getId()))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(filmDbStorage.getFilm(testedFilm.getId()));
    }

    @Test
    public void checkAddFilmIfFilmCantPassedValidationBecauseNameIsNull() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        testedFilm = Film.builder()
                .name(null)
                .description("Фильм Маска -  это захватывающая комедия, где " +
                        "главный герой Стэнли Ипкисс случайно находит магическую маску, что дарует ему " +
                        "невероятные суперсилы. " +
                        "Фильм смешной и непременно заставит вас улыбнуться.")
                .duration(126)
                .releaseDate(LocalDate.of(2003, 3,
                        26))
                .mpa(Rating.builder().id(4).build())
                .build();

        assertThrows(ValidationException.class, () -> filmDbStorage.addFilm(testedFilm));
    }

    @Test
    public void checkAddFilmIfFilmCanPassedValidationBecauseNameIsEmpty() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        testedFilm = Film.builder()
                .name("")
                .description("Фильм Маска -  это захватывающая комедия, где " +
                        "главный герой Стэнли Ипкисс случайно находит магическую маску, что дарует ему " +
                        "невероятные суперсилы. " +
                        "Фильм смешной и непременно заставит вас улыбнуться.")
                .duration(126)
                .releaseDate(LocalDate.of(2003, 3,
                        26))
                .mpa(Rating.builder().id(4).build())
                .build();

        assertThrows(ValidationException.class, () -> filmDbStorage.addFilm(testedFilm));
    }

    @Test
    public void checkAddFilmIfFilmCanPassedValidationBecauseDescriptionIsNull() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        testedFilm = Film.builder()
                .name("Маска")
                .description(null)
                .duration(126)
                .releaseDate(LocalDate.of(2003, 3,
                        26))
                .mpa(Rating.builder().id(4).build())
                .build();

        assertThrows(ValidationException.class, () -> filmDbStorage.addFilm(testedFilm));
    }

    @Test
    public void checkAddFilmIfFilmCanPassedValidationBecauseInDescriptionMoreThen200Characters() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        testedFilm = Film.builder()
                .name("Маска")
                .description("Фильм Маска - это захватывающая комедийная " +
                        "приключенческая история о скромном банковском служащем по имени Стэнли Ипкисс. Однажды " +
                        "он находит " +
                        "древнюю маску, которая превращает его в эксцентричного супергероя. Стэнли начинает " +
                        "использовать свои " +
                        "новые суперспособности для борьбы с преступностью и покорения сердца красавицы. Но с " +
                        "каждым днем он " +
                        "понимает, что маска имеет свою темную сторону. Фильм полон юмора, экшена и неожиданных " +
                        "поворотов " +
                        "сюжета. Не пропустите эту захватывающую историю о супергерое-неудачнике!")
                .duration(126)
                .releaseDate(LocalDate.of(2003, 3,
                        26))
                .mpa(Rating.builder().id(4).build())
                .build();

        assertThrows(ValidationException.class, () -> filmDbStorage.addFilm(testedFilm));
    }

    @Test
    public void checkAddFilmIfFilmCanPassedValidationBecauseReleaseDateIsNull() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        testedFilm = Film.builder()
                .name("Маска")
                .description("Фильм Маска -  это захватывающая комедия, где " +
                        "главный герой Стэнли Ипкисс случайно находит магическую маску, что дарует ему " +
                        "невероятные суперсилы. " +
                        "Фильм смешной и непременно заставит вас улыбнуться.")
                .duration(126)
                .releaseDate(null)
                .mpa(Rating.builder().id(4).build())
                .build();

        assertThrows(ValidationException.class, () -> filmDbStorage.addFilm(testedFilm));
    }

    @Test
    public void checkAddFilmIfFilmCanPassedValidationBecauseDurationIsNegative() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        testedFilm = Film.builder()
                .name("Маска")
                .description("Фильм Маска -  это захватывающая комедия, где " +
                        "главный герой Стэнли Ипкисс случайно находит магическую маску, что дарует ему " +
                        "невероятные суперсилы. " +
                        "Фильм смешной и непременно заставит вас улыбнуться.")
                .duration(-1)
                .releaseDate(LocalDate.of(2003, 3,
                        26))
                .mpa(Rating.builder().id(4).build())
                .build();

        assertThrows(ValidationException.class, () -> filmDbStorage.addFilm(testedFilm));
    }

    @Test
    public void checkAddFilmIfFilmIsNull() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        filmDbStorage.addFilm(null);

        assertEquals(0, filmDbStorage.getFilms().size());
    }

    @Test
    public void checkUpdateFilmIfFilmCanPassedValidation() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        testedFilm = Film.builder()
                .name("Маска")
                .description("Достойная комедия ни на один вечер.")
                .duration(126)
                .releaseDate(LocalDate.of(2003, 3,
                        26))
                .mpa(Rating.builder().id(4).build())
                .build();
        filmDbStorage.addFilm(testedFilm);
        Film updatedFilm = Film.builder()
                .id(1)
                .name("Маска")
                .description("Фильм Маска -  это захватывающая комедия, где " +
                        "главный герой Стэнли Ипкисс случайно находит магическую маску, что дарует ему " +
                        "невероятные суперсилы. " +
                        "Фильм смешной и непременно заставит вас улыбнуться.")
                .duration(126)
                .releaseDate(LocalDate.of(2003, 3,
                        26))
                .mpa(Rating.builder().id(4).build())
                .build();

        filmDbStorage.updateFilm(updatedFilm);

        assertThat(filmDbStorage.updateFilm(updatedFilm))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(filmDbStorage.updateFilm(updatedFilm));
    }

    @Test
    public void checkLike() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        testedFilm = Film.builder()
                .name("Маска")
                .description("Фильм Маска -  это захватывающая комедия, где " +
                        "главный герой Стэнли Ипкисс случайно находит магическую маску, что дарует ему " +
                        "невероятные суперсилы. " +
                        "Фильм смешной и непременно заставит вас улыбнуться.")
                .duration(126)
                .releaseDate(LocalDate.of(2003, 3,
                        26))
                .mpa(Rating.builder().id(4).build())
                .build();
        filmDbStorage.addFilm(testedFilm);
        testedUser = User.builder()
                .name("Валерий")
                .login("Bicycle")
                .email("broken.velik@yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();
        userDbStorage.addUser(testedUser);

        assertTrue(filmDbStorage.like(testedFilm.getId(), testedUser.getId()));
    }

    @Test
    public void checkDeleteLike() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        testedFilm = Film.builder()
                .name("Маска")
                .description("Фильм Маска -  это захватывающая комедия, где " +
                        "главный герой Стэнли Ипкисс случайно находит магическую маску, что дарует ему " +
                        "невероятные суперсилы. " +
                        "Фильм смешной и непременно заставит вас улыбнуться.")
                .duration(126)
                .releaseDate(LocalDate.of(2003, 3,
                        26))
                .mpa(Rating.builder().id(4).build())
                .build();
        filmDbStorage.addFilm(testedFilm);
        testedUser = User.builder()
                .name("Валерий")
                .login("Bicycle")
                .email("broken.velik@yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();
        userDbStorage.addUser(testedUser);
        filmDbStorage.like(testedFilm.getId(), testedUser.getId());

        assertTrue(filmDbStorage.deleteLike(testedFilm.getId(), testedUser.getId()));
    }

    @Test
    public void checkGetTopFilms() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        testedFilm = Film.builder()
                .name("Маска")
                .description("Фильм Маска -  это захватывающая комедия, где " +
                        "главный герой Стэнли Ипкисс случайно находит магическую маску, что дарует ему " +
                        "невероятные суперсилы. " +
                        "Фильм смешной и непременно заставит вас улыбнуться.")
                .duration(126)
                .releaseDate(LocalDate.of(2003, 3,
                        26))
                .mpa(Rating.builder().id(4).build())
                .build();
        filmDbStorage.addFilm(testedFilm);
        Film anotherFilm = Film.builder()
                .name("Титаник")
                .description("Фильм - катастрофа")
                .duration(194)
                .releaseDate(LocalDate.of(1998, 2, 20))
                .mpa(Rating.builder().id(5).build())
                .build();
        filmDbStorage.addFilm(anotherFilm);
        testedUser = User.builder()
                .name("Валерий")
                .login("Bicycle")
                .email("broken.velik@yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();
        userDbStorage.addUser(testedUser);
        User anotherUser = User.builder()
                .name("Сергей")
                .login("Seryoga")
                .email("voyu.na_lunu@yandex.ru")
                .birthday(LocalDate.of(1997, 6, 13))
                .build();
        userDbStorage.addUser(anotherUser);
        filmDbStorage.like(testedFilm.getId(), testedUser.getId());
        filmDbStorage.like(testedFilm.getId(), anotherUser.getId());
        filmDbStorage.like(anotherFilm.getId(), testedUser.getId());

        assertNotNull(filmDbStorage.getTopFilms(2));
        assertEquals(2, filmDbStorage.getTopFilms(2).size());
    }

    @Test
    public void checkGetRatings() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        assertNotNull(filmDbStorage.getRatings());
        assertEquals(5, filmDbStorage.getRatings().size());
    }

    @Test
    public void checkGetGenres() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        assertNotNull(filmDbStorage.getGenres());
        assertEquals(5, filmDbStorage.getRatings().size());
    }
}
