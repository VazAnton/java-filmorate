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
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
class UserDbStorageTest {

    final JdbcTemplate jdbcTemplate;
    User testedUser = User.builder().build();

    @Autowired
    public UserDbStorageTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Test
    public void checkAddUserIfUserCanPasseValidation() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        testedUser = User.builder()
                .name("Валерий")
                .login("Bicycle")
                .email("broken.velik@yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();

        userDbStorage.addUser(testedUser);

        assertThat(userDbStorage.getUser(testedUser.getId()))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(testedUser);
    }

    @Test
    public void checkAddUserIfUserCanPasseValidationButHisNameIsEmpty() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        testedUser = User.builder()
                .name("")
                .login("Bicycle")
                .email("broken.velik@yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();

        userDbStorage.addUser(testedUser);

        assertNotNull(testedUser);
        assertEquals("Bicycle", testedUser.getName());
    }

    @Test
    public void checkAddUserIfUserCantPasseValidationBecauseEmailIsEmpty() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        testedUser = User.builder()
                .name("Валерий")
                .login("Bicycle")
                .email("")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();

        assertThrows(ValidationException.class,
                () -> userDbStorage.addUser(testedUser));
        assertTrue(userDbStorage.getUsers().isEmpty());
    }

    @Test
    public void checkAddUserIfUserCantPasseValidationBecauseEmailIsNull() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        testedUser = User.builder()
                .name("Валерий")
                .login("Bicycle")
                .email(null)
                .birthday(LocalDate.of(1999, 5, 22))
                .build();

        assertThrows(ValidationException.class,
                () -> userDbStorage.addUser(testedUser));
        assertTrue(userDbStorage.getUsers().isEmpty());
    }

    @Test
    public void checkAddUserIfUserCantPasseValidationBecauseEmailNotContainsSpecialSymbol() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        testedUser = User.builder()
                .name("Валерий")
                .login("Bicycle")
                .email("broken.velik.yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();

        assertThrows(ValidationException.class,
                () -> userDbStorage.addUser(testedUser));
        assertTrue(userDbStorage.getUsers().isEmpty());
    }

    @Test
    public void checkAddUserIfUserCantPasseValidationBecauseLoginIsNull() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        testedUser = User.builder()
                .name("Валерий")
                .login(null)
                .email("broken.velik@yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();

        assertThrows(ValidationException.class,
                () -> userDbStorage.addUser(testedUser));
        assertTrue(userDbStorage.getUsers().isEmpty());
    }

    @Test
    public void checkAddUserIfUserCantPasseValidationBecauseLoginIsEmpty() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        testedUser = User.builder()
                .name("Валерий")
                .login("")
                .email("broken.velik.yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();

        assertThrows(ValidationException.class,
                () -> userDbStorage.addUser(testedUser));
        assertTrue(userDbStorage.getUsers().isEmpty());
    }

    @Test
    public void checkAddUserIfUserCantPasseValidationBecauseLoginContainsBlanks() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        testedUser = User.builder()
                .name("Валерий")
                .login("Bicy cle")
                .email("broken.velik.yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();

        assertThrows(ValidationException.class,
                () -> userDbStorage.addUser(testedUser));
        assertTrue(userDbStorage.getUsers().isEmpty());
    }

    @Test
    public void checkAddUserIfUserCantPasseValidationBecauseBirthdayIsNull() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        testedUser = User.builder()
                .name("Валерий")
                .login("Bicycle")
                .email("broken.velik.yandex.ru")
                .birthday(null)
                .build();

        assertThrows(ValidationException.class,
                () -> userDbStorage.addUser(testedUser));
        assertTrue(userDbStorage.getUsers().isEmpty());
    }

    @Test
    public void checkAddUserIfUserCantPasseValidationBecauseBirthdayMoreThenNow() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        testedUser = User.builder()
                .name("Валерий")
                .login("Bicycle")
                .email("broken.velik.yandex.ru")
                .birthday(LocalDate.of(2025, 5, 22))
                .build();

        assertThrows(ValidationException.class,
                () -> userDbStorage.addUser(testedUser));
        assertTrue(userDbStorage.getUsers().isEmpty());
    }

    @Test
    public void checkAddUserIfUserIsNull() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        testedUser = null;

        userDbStorage.addUser(null);

        assertTrue(userDbStorage.getUsers().isEmpty());
    }

    @Test
    public void checkUpdateUserIfUserCanPasseValidation() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        testedUser = User.builder()
                .name("Валерий")
                .login("Bicycle")
                .email("broken.velik@yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();
        userDbStorage.addUser(testedUser);
        User updatedUser = User.builder()
                .id(testedUser.getId())
                .name("Валерий")
                .login("Super_Bicycle")
                .email("broken.velik@yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();

        userDbStorage.updateUser(updatedUser);

        assertThat(userDbStorage.updateUser(updatedUser))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(updatedUser);
    }

    @Test
    public void checkUpdateUserIfUserCantPasseValidationBecauseEmailIsEmpty() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        testedUser = User.builder()
                .name("Валерий")
                .login("Bicycle")
                .email("broken.velik@yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();
        userDbStorage.addUser(testedUser);
        User updatedUser = User.builder()
                .id(testedUser.getId())
                .name("Валерий")
                .login("Bicycle")
                .email("")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();

        assertThrows(ValidationException.class,
                () -> userDbStorage.updateUser(updatedUser));
    }

    @Test
    public void checkUpdateUserIfUserCantPasseValidationBecauseEmailIsNull() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        testedUser = User.builder()
                .name("Валерий")
                .login("Bicycle")
                .email("broken.velik@yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();
        userDbStorage.addUser(testedUser);
        User updatedUser = User.builder()
                .id(testedUser.getId())
                .name("Валерий")
                .login("Bicycle")
                .email(null)
                .birthday(LocalDate.of(1999, 5, 22))
                .build();

        assertThrows(ValidationException.class,
                () -> userDbStorage.updateUser(updatedUser));
    }

    @Test
    public void checkUpdateUserIfUserCantPasseValidationBecauseEmailNotContainsSpecialSymbol() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        testedUser = User.builder()
                .name("Валерий")
                .login("Bicycle")
                .email("broken.velik@yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();
        userDbStorage.addUser(testedUser);
        User updatedUser = User.builder()
                .id(testedUser.getId())
                .name("Валерий")
                .login("Bicycle")
                .email("broken.velik.yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();

        assertThrows(ValidationException.class,
                () -> userDbStorage.updateUser(updatedUser));
    }

    @Test
    public void checkUpdateUserIfUserCantPasseValidationBecauseLoginIsNull() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        testedUser = User.builder()
                .name("Валерий")
                .login("Bicycle")
                .email("broken.velik@yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();
        userDbStorage.addUser(testedUser);
        User updatedUser = User.builder()
                .id(testedUser.getId())
                .name("Валерий")
                .login(null)
                .email("broken.velik@yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();

        assertThrows(ValidationException.class,
                () -> userDbStorage.updateUser(updatedUser));
    }

    @Test
    public void checkUpdateUserIfUserCantPasseValidationBecauseLoginIsEmpty() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        testedUser = User.builder()
                .name("Валерий")
                .login("Bicycle")
                .email("broken.velik@yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();
        userDbStorage.addUser(testedUser);
        User updatedUser = User.builder()
                .id(testedUser.getId())
                .name("Валерий")
                .login("")
                .email("broken.velik.yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();

        assertThrows(ValidationException.class,
                () -> userDbStorage.updateUser(updatedUser));
    }

    @Test
    public void checkUpdateUserIfUserCantPasseValidationBecauseLoginContainsBlanks() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        testedUser = User.builder()
                .name("Валерий")
                .login("Bicycle")
                .email("broken.velik@yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();
        userDbStorage.addUser(testedUser);
        User updatedUser = User.builder()
                .id(testedUser.getId())
                .name("Валерий")
                .login("Bicy cle")
                .email("broken.velik.yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();

        assertThrows(ValidationException.class,
                () -> userDbStorage.updateUser(updatedUser));
    }

    @Test
    public void checkUpdateUserIfUserCantPasseValidationBecauseBirthdayIsNull() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        testedUser = User.builder()
                .name("Валерий")
                .login("Bicycle")
                .email("broken.velik@yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();
        userDbStorage.addUser(testedUser);
        User updatedUser = User.builder()
                .id(testedUser.getId())
                .name("Валерий")
                .login("Bicycle")
                .email("broken.velik.yandex.ru")
                .birthday(null)
                .build();

        assertThrows(ValidationException.class,
                () -> userDbStorage.updateUser(updatedUser));
    }

    @Test
    public void checkUpdateUserIfUserCantPasseValidationBecauseBirthdayMoreThenNow() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        testedUser = User.builder()
                .name("Валерий")
                .login("Bicycle")
                .email("broken.velik@yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();
        userDbStorage.addUser(testedUser);
        User updatedUser = User.builder()
                .id(testedUser.getId())
                .name("Валерий")
                .login("Bicycle")
                .email("broken.velik.yandex.ru")
                .birthday(LocalDate.of(2025, 5, 22))
                .build();

        assertThrows(ValidationException.class,
                () -> userDbStorage.updateUser(updatedUser));
    }

    @Test
    public void checkUpdateUserIfUserIsNull() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        testedUser = User.builder()
                .name("Валерий")
                .login("Bicycle")
                .email("broken.velik@yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();
        userDbStorage.addUser(testedUser);

        userDbStorage.updateUser(null);

        assertEquals(1, userDbStorage.getUsers().size());
    }

    @Test
    public void checkGetUserIfUserIsFine() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        testedUser = User.builder()
                .name("Валерий")
                .login("Bicycle")
                .email("broken.velik@yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();
        userDbStorage.addUser(testedUser);

        userDbStorage.getUser(testedUser.getId());

        assertThat(userDbStorage.getUser(testedUser.getId()))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(testedUser);
    }

    @Test
    public void checkGetFriendsOfUserIfUserHaveNotFriends() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        List<User> friends = new ArrayList<>();
        testedUser = User.builder()
                .name("Валерий")
                .login("Bicycle")
                .email("broken.velik@yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();
        userDbStorage.addUser(testedUser);

        userDbStorage.getFriendsOfUser(testedUser.getId());

        assertThat(userDbStorage.getFriendsOfUser(testedUser.getId()))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(friends);
    }

    @Test
    public void checkGetFriendsOfUserIfUserHaveFriends() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        List<User> friends = new ArrayList<>();
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
        userDbStorage.addFriend(testedUser.getId(), anotherUser.getId());
        friends.add(anotherUser);

        userDbStorage.getFriendsOfUser(testedUser.getId());

        assertThat(userDbStorage.getFriendsOfUser(testedUser.getId()))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(friends);
    }

    @Test
    public void checkDeleteFriendsIfUserHaveFriends() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        List<User> friends = new ArrayList<>();
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
        userDbStorage.addFriend(testedUser.getId(), anotherUser.getId());
        userDbStorage.deleteFriend(testedUser.getId(), anotherUser.getId());

        userDbStorage.getFriendsOfUser(testedUser.getId());

        assertThat(userDbStorage.getFriendsOfUser(testedUser.getId()))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(friends);
    }

    @Test
    public void checkDeleteUser() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        testedUser = User.builder()
                .name("Валерий")
                .login("Bicycle")
                .email("broken.velik@yandex.ru")
                .birthday(LocalDate.of(1999, 5, 22))
                .build();
        userDbStorage.addUser(testedUser);
        assertEquals(1, userDbStorage.getUsers().size());

        assertTrue(userDbStorage.deleteUserById(testedUser.getId()));
    }

    @Test
    public void checkRecommendationFilm() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
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
        Film testedFilm = Film.builder()
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
        filmDbStorage.like(testedFilm.getId(), testedUser.getId());
        filmDbStorage.like(testedFilm.getId(), anotherUser.getId());
        filmDbStorage.like(anotherFilm.getId(), anotherUser.getId());
        assertEquals(anotherFilm.getId(), userDbStorage.getRecommendationsFilmsByUser(testedUser.getId()).get(0).getId());
        assertEquals(1, userDbStorage.getRecommendationsFilmsByUser(testedUser.getId()).size());

    }

    @Test
    public void noRecommendation() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
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
        Film testedFilm = Film.builder()
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
        filmDbStorage.like(testedFilm.getId(), testedUser.getId());
        filmDbStorage.like(anotherFilm.getId(), testedUser.getId());
        filmDbStorage.like(testedFilm.getId(), anotherUser.getId());
        filmDbStorage.like(anotherFilm.getId(), anotherUser.getId());
        assertTrue(userDbStorage.getRecommendationsFilmsByUser(testedUser.getId()).isEmpty());
        assertEquals(0, userDbStorage.getRecommendationsFilmsByUser(testedUser.getId()).size());
    }
}
