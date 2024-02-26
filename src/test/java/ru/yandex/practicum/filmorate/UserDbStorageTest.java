package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private UserDbStorage userDbStorage;

    @BeforeEach
    public void stepUp() {
        userDbStorage = new UserDbStorage(jdbcTemplate);
    }

    @Test
    public void checkUpdateUser() {
        User user = new User(1, "user@email.ru", "vanya123", "Ivan Petrov",
                LocalDate.of(1990, 1, 1));
        userDbStorage.addUser(user);
        User newUser = new User(1, "updated_user@email.ru", "vanya123", "Ivan Petrov",
                LocalDate.of(1990, 1, 1));

        User updatedUser = userDbStorage.updateUser(newUser);

        assertThat(updatedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser);
    }

    @Test
    public void checkGetUserByIdIfUserExist() {
        User user = new User(1, "user@email.ru", "vanya123", "Ivan Petrov",
                LocalDate.of(1990, 1, 1));
        userDbStorage.addUser(user);

        User savedUser = userDbStorage.getUser(1).get();

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(user);
    }

    @Test
    public void checkGetUsers() {
        List<User> users = new ArrayList<>();
        User user1 = new User(1, "user@email.ru", "vanya123", "Ivan Petrov",
                LocalDate.of(1990, 1, 1));
        User user2 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22));
        userDbStorage.addUser(user1);
        users.add(user1);
        userDbStorage.addUser(user2);
        users.add(user2);

        List<User> savedUsers = userDbStorage.getUsers();

        assertThat(savedUsers)
                .isNotEmpty()
                .isEqualTo(users);
    }

    @Test
    public void checkAddFriend() {
        List<User> friendsOfUser1 = new ArrayList<>();
        List<User> friendsOfUser2 = new ArrayList<>();
        User user1 = new User(1, "user@email.ru", "vanya123", "Ivan Petrov",
                LocalDate.of(1990, 1, 1));
        User user2 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22));
        userDbStorage.addUser(user1);
        userDbStorage.addUser(user2);
        userDbStorage.addFriend(1, 2);
        friendsOfUser1.add(user2);

        List<User> friends = userDbStorage.getFriendsOfUser(1);

        assertThat(friends)
                .isNotEmpty()
                .isEqualTo(friendsOfUser1);
        assertThat(friendsOfUser2)
                .isEmpty();
    }

    @Test
    public void checkDeleteFriend() {
        List<User> friendsOfUser1 = new ArrayList<>();
        List<User> friendsOfUser2 = new ArrayList<>();
        User user1 = new User(1, "user@email.ru", "vanya123", "Ivan Petrov",
                LocalDate.of(1990, 1, 1));
        User user2 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22));
        userDbStorage.addUser(user1);
        userDbStorage.addUser(user2);
        userDbStorage.addFriend(1, 2);
        friendsOfUser1.add(user2);
        userDbStorage.deleteFriend(1, 2);
        friendsOfUser1.remove(user2);

        List<User> friends = userDbStorage.getFriendsOfUser(1);

        assertThat(friends)
                .isEmpty();
        assertThat(friendsOfUser2)
                .isEmpty();
    }

    @Test
    public void checkGetCommonFriends() {
        List<User> friendsOfUser1 = new ArrayList<>();
        List<User> friendsOfUser2 = new ArrayList<>();
        List<User> friendsOfUser3 = new ArrayList<>();
        User user1 = new User(1, "user@email.ru", "vanya123", "Ivan Petrov",
                LocalDate.of(1990, 1, 1));
        User user2 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22));
        User user3 = new User(3, "Chicken.litle@yandex.ru", "ChickenLitle", "Кудах",
                LocalDate.of(2005, 11, 4));
        userDbStorage.addUser(user1);
        userDbStorage.addUser(user2);
        userDbStorage.addUser(user3);
        userDbStorage.addFriend(1, 2);
        userDbStorage.addFriend(3, 2);
        friendsOfUser1.add(user2);
        friendsOfUser3.add(user2);

        List<User> realFriendsOfUser1 = userDbStorage.getFriendsOfUser(1);
        List<User> realFriendsOfUser3 = userDbStorage.getFriendsOfUser(3);

        assertThat(realFriendsOfUser1)
                .isNotEmpty()
                .isEqualTo(friendsOfUser1);
        assertThat(realFriendsOfUser3)
                .isNotEmpty()
                .isEqualTo(friendsOfUser3);
        assertThat(friendsOfUser2)
                .isEmpty();
    }
}
