package ru.yandex.practicum.filmorate.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.ValidationException;
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
}
