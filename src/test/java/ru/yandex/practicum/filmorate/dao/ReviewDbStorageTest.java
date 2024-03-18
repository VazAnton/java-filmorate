package ru.yandex.practicum.filmorate.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
class ReviewDbStorageTest {

    final JdbcTemplate jdbcTemplate;
    Review testedReview = Review.builder().build();
    Film testedFilm = Film.builder().build();
    User testedUser = User.builder().build();

    @Autowired
    public ReviewDbStorageTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Test
    public void checkAddReviewIfReviewCanPassValidation() {
        ReviewDbStorage reviewDbStorage = new ReviewDbStorage(jdbcTemplate);
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
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
        testedUser = User.builder()
                .name("Валерий")
                .login("Bicycle")
                .email("broken.velik@yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();
        userDbStorage.addUser(testedUser);
        testedReview = Review.builder()
                .content("Отличный фильм, ни один раз его видел.")
                .isPositive(true)
                .userId(testedUser.getId())
                .filmId(testedFilm.getId())
                .build();
        reviewDbStorage.addReview(testedReview);

        assertThat(reviewDbStorage.getReview(testedReview.getReviewId()))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(testedReview);
    }

//    @Test
//    public void checkAddReviewIfReviewContentIsNull() {
//        ReviewDbStorage reviewDbStorage = new ReviewDbStorage(jdbcTemplate);
//        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
//        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
//        testedFilm = Film.builder()
//                .name("Маска")
//                .description("Фильм Маска -  это захватывающая комедия, где " +
//                        "главный герой Стэнли Ипкисс случайно находит магическую маску, что дарует ему " +
//                        "невероятные суперсилы. " +
//                        "Фильм смешной и непременно заставит вас улыбнуться.")
//                .duration(126)
//                .releaseDate(LocalDate.of(2003, 3,
//                        26))
//                .mpa(Rating.builder().id(4).build())
//                .build();
//        filmDbStorage.addFilm(testedFilm);
//        testedUser = User.builder()
//                .name("Валерий")
//                .login("Bicycle")
//                .email("broken.velik@yandex.ru")
//                .birthday(LocalDate.of(1999, 5, 22))
//                .build();
//        userDbStorage.addUser(testedUser);
//        testedReview = Review.builder()
//                .content(null)
//                .isPositive(true)
//                .userId(testedUser.getId())
//                .filmId(testedFilm.getId())
//                .build();
//
//        assertThrows(NullPointerException.class, () -> reviewDbStorage.addReview(testedReview));
//    }

//    @Test
//    public void checkAddReviewIfReviewContentIsBlank() {
//        ReviewDbStorage reviewDbStorage = new ReviewDbStorage(jdbcTemplate);
//        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
//        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
//        testedFilm = Film.builder()
//                .name("Маска")
//                .description("Фильм Маска -  это захватывающая комедия, где " +
//                        "главный герой Стэнли Ипкисс случайно находит магическую маску, что дарует ему " +
//                        "невероятные суперсилы. " +
//                        "Фильм смешной и непременно заставит вас улыбнуться.")
//                .duration(126)
//                .releaseDate(LocalDate.of(2003, 3,
//                        26))
//                .mpa(Rating.builder().id(4).build())
//                .build();
//        filmDbStorage.addFilm(testedFilm);
//        testedUser = User.builder()
//                .name("Валерий")
//                .login("Bicycle")
//                .email("broken.velik@yandex.ru")
//                .birthday(LocalDate.of(1999, 5, 22))
//                .build();
//        userDbStorage.addUser(testedUser);
//        testedReview = Review.builder()
//                .content(" ")
//                .isPositive(true)
//                .userId(testedUser.getId())
//                .filmId(testedFilm.getId())
//                .build();
//
//        assertThrows(ValidationException.class, () -> reviewDbStorage.addReview(testedReview));
//    }

    @Test
    public void checkAddReviewIfReviewOpinionIsNull() {
        ReviewDbStorage reviewDbStorage = new ReviewDbStorage(jdbcTemplate);
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
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
        testedUser = User.builder()
                .name("Валерий")
                .login("Bicycle")
                .email("broken.velik@yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();
        userDbStorage.addUser(testedUser);
        testedReview = Review.builder()
                .content("Отличный фильм, ни один раз его видел.")
                .isPositive(null)
                .userId(testedUser.getId())
                .filmId(testedFilm.getId())
                .build();

        assertThrows(NullPointerException.class, () -> reviewDbStorage.addReview(testedReview));
    }

    @Test
    public void checkAddReviewIfUserIdIsNull() {
        ReviewDbStorage reviewDbStorage = new ReviewDbStorage(jdbcTemplate);
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
        testedReview = Review.builder()
                .content("Отличный фильм, ни один раз его видел.")
                .isPositive(true)
                .userId(null)
                .filmId(testedFilm.getId())
                .build();

        assertThrows(NullPointerException.class, () -> reviewDbStorage.addReview(testedReview));
    }

    @Test
    public void checkAddReviewIfFilmIdIsNull() {
        ReviewDbStorage reviewDbStorage = new ReviewDbStorage(jdbcTemplate);
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        testedUser = User.builder()
                .name("Валерий")
                .login("Bicycle")
                .email("broken.velik@yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();
        userDbStorage.addUser(testedUser);
        testedReview = Review.builder()
                .content("Отличный фильм, ни один раз его видел.")
                .isPositive(null)
                .userId(testedUser.getId())
                .filmId(null)
                .build();

        assertThrows(NullPointerException.class, () -> reviewDbStorage.addReview(testedReview));
    }

    @Test
    public void checkUpdateReviewIfReviewIsFine() {
        ReviewDbStorage reviewDbStorage = new ReviewDbStorage(jdbcTemplate);
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
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
        testedUser = User.builder()
                .name("Валерий")
                .login("Bicycle")
                .email("broken.velik@yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();
        userDbStorage.addUser(testedUser);
        testedReview = Review.builder()
                .content("Отличный фильм, ни один раз его видел.")
                .isPositive(true)
                .userId(testedUser.getId())
                .filmId(testedFilm.getId())
                .build();
        reviewDbStorage.addReview(testedReview);
        Review updatedReview = Review.builder()
                .reviewId(testedReview.getReviewId())
                .content("Не самый лучший фильм, что я видел.")
                .isPositive(false)
                .userId(testedUser.getId())
                .filmId(testedFilm.getId())
                .build();

        assertThat(reviewDbStorage.updateReview(updatedReview))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(updatedReview);
    }

    @Test
    public void checkDeleteReviewIfReviewNotExist() {
        ReviewDbStorage reviewDbStorage = new ReviewDbStorage(jdbcTemplate);
        assertThrows(EmptyResultDataAccessException.class, () -> reviewDbStorage.getReview(1));
    }

    @Test
    public void checkDeleteReviewIfReviewExist() {
        ReviewDbStorage reviewDbStorage = new ReviewDbStorage(jdbcTemplate);
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
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
        testedUser = User.builder()
                .name("Валерий")
                .login("Bicycle")
                .email("broken.velik@yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();
        userDbStorage.addUser(testedUser);
        testedReview = Review.builder()
                .content("Отличный фильм, ни один раз его видел.")
                .isPositive(true)
                .userId(testedUser.getId())
                .filmId(testedFilm.getId())
                .build();
        reviewDbStorage.addReview(testedReview);
        assertThat(reviewDbStorage.getReview(testedReview.getReviewId()))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(testedReview);

        reviewDbStorage.deleteReview(testedReview.getReviewId());

        assertThrows(EmptyResultDataAccessException.class, () -> reviewDbStorage.getReview(testedReview.getReviewId()));
    }


    @Test
    public void checkGetReviewOfFilm() {
        ReviewDbStorage reviewDbStorage = new ReviewDbStorage(jdbcTemplate);
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
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
        testedUser = User.builder()
                .name("Валерий")
                .login("Bicycle")
                .email("broken.velik@yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();
        userDbStorage.addUser(testedUser);
        testedReview = Review.builder()
                .content("Отличный фильм, ни один раз его видел.")
                .isPositive(true)
                .userId(testedUser.getId())
                .filmId(testedFilm.getId())
                .build();
        reviewDbStorage.addReview(testedReview);
        assertThat(reviewDbStorage.getReview(testedReview.getReviewId()))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(testedReview);
        assertEquals(testedReview, reviewDbStorage.getReviewOfFilm(testedFilm.getId(), 1).get(0));
    }

    @Test
    public void checkAddLikeOfReview() {
        ReviewDbStorage reviewDbStorage = new ReviewDbStorage(jdbcTemplate);
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
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
        testedUser = User.builder()
                .name("Валерий")
                .login("Bicycle")
                .email("broken.velik@yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();
        userDbStorage.addUser(testedUser);

        testedReview = Review.builder()
                .content("Отличный фильм, ни один раз его видел.")
                .isPositive(true)
                .userId(testedUser.getId())
                .filmId(testedFilm.getId())
                .build();
        reviewDbStorage.addReview(testedReview);
        User likedUser = User.builder()
                .name("Анатолий")
                .login("сycle")
                .email("brok@yandex.ru")
                .birthday(LocalDate.of(1989, 11, 12))
                .build();
        userDbStorage.addUser(likedUser);
        reviewDbStorage.addLikeOfReview(5, 10);
        assertEquals(1, reviewDbStorage.getReview(5).getUseful());
    }

    @Test
    public void checkDeleteLikeOfReview() {
        ReviewDbStorage reviewDbStorage = new ReviewDbStorage(jdbcTemplate);
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
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
        testedUser = User.builder()
                .name("Валерий")
                .login("Bicycle")
                .email("broken.velik@yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();
        userDbStorage.addUser(testedUser);

        testedReview = Review.builder()
                .content("Отличный фильм, ни один раз его видел.")
                .isPositive(true)
                .userId(testedUser.getId())
                .filmId(testedFilm.getId())
                .build();
        reviewDbStorage.addReview(testedReview);
        User likedUser = User.builder()
                .name("Анатолий")
                .login("сycle")
                .email("brok@yandex.ru")
                .birthday(LocalDate.of(1989, 11, 12))
                .build();
        userDbStorage.addUser(likedUser);
        reviewDbStorage.addLikeOfReview(1, 2);
        reviewDbStorage.deleteLikeOfReview(1, 2);

        assertEquals(0, reviewDbStorage.getReview(1).getUseful());
    }

    @Test
    public void checkDeleteDisLikeOfReview() {
        ReviewDbStorage reviewDbStorage = new ReviewDbStorage(jdbcTemplate);
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
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
        testedUser = User.builder()
                .name("Валерий")
                .login("Bicycle")
                .email("broken.velik@yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();
        userDbStorage.addUser(testedUser);

        testedReview = Review.builder()
                .content("Отличный фильм, ни один раз его видел.")
                .isPositive(true)
                .userId(testedUser.getId())
                .filmId(testedFilm.getId())
                .build();
        reviewDbStorage.addReview(testedReview);
        User disLikedUser = User.builder()
                .name("Анатолий")
                .login("сycle")
                .email("brok@yandex.ru")
                .birthday(LocalDate.of(1989, 11, 12))
                .build();
        userDbStorage.addUser(disLikedUser);
        reviewDbStorage.addDislikeOfReview(3, 5);
        reviewDbStorage.deleteDislikeOfReview(3, 5);
        assertEquals(0, reviewDbStorage.getReview(3).getUseful());
    }
}
