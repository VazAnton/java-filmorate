package ru.yandex.practicum.filmorate.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.enums.EventTypes;
import ru.yandex.practicum.filmorate.enums.Operations;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage.UserStorage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Map;

@Repository
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private static final Logger log = LoggerFactory.getLogger(UserDbStorage.class);

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private void validate(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            log.error("Не заполнена электронная почта.");
            throw new ValidationException("Поле с адресом алектронной почты не может быть пустым!");
        } else if (!user.getEmail().contains("@")) {
            log.error("Было передан неверный адрес электронной почты.");
            throw new ValidationException("Адрес алектронной почты должен содержать символ @!");
        }
        if (user.getLogin() == null || user.getLogin().isEmpty()) {
            log.error("Не заполнен логин.");
            throw new ValidationException("Логин пользователя должен быть заполнен!");
        } else if (user.getLogin().contains(" ")) {
            log.error("Логин пользователя содержит пробелы.");
            throw new ValidationException("Логин пользователя не может содержать пробелы!");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            log.error("При создании пользователя не было указано его имя.");
            user.setName(user.getLogin());
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Передана некорректная дата рождения пользователя.");
            throw new ValidationException("День рождения пользователя должен быть указан и не может быть больше " +
                    "текущей даты!");
        }
    }

    @Override
    public User addUser(User user) {
        if (user != null) {
            validate(user);
            SimpleJdbcInsert userInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("users")
                    .usingGeneratedKeyColumns("user_id");
            int id = userInsert.executeAndReturnKey(
                    Map.of(
                            "email", user.getEmail(),
                            "login", user.getLogin(),
                            "name", user.getName(),
                            "birthday", user.getBirthday()
                    )).intValue();
            user.setId(id);
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (user != null && getUser(user.getId()) != null) {
            validate(user);
            jdbcTemplate.update("UPDATE users set email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?",
                    user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        }
        return user;
    }

    @Override
    public boolean deleteUserById(int id) {
        if (getUser(id) != null) {
            jdbcTemplate.update("DELETE FROM users WHERE user_id = ?", id);
            return true;
        }
        return false;
    }

    @Override
    public List<User> getUsers() {
        return jdbcTemplate.query("SELECT* FROM users", getUserMapper());
    }

    private static RowMapper<User> getUserMapper() {
        return (rs, rowNom) -> new User(
                rs.getInt("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getObject("birthday", LocalDate.class)
        );
    }

    @Override
    public User getUser(int id) {
        return jdbcTemplate.queryForObject("SELECT* FROM users WHERE user_id = ?",
                UserDbStorage.getUserMapper(), id);
    }

    @Override
    public User addFriend(int id, int friendId) {
        LocalTime now = LocalTime.now();
        int millis = now.get(ChronoField.MILLI_OF_SECOND);
        FeedDbStorage feedDbStorage = new FeedDbStorage(jdbcTemplate);
        User user = getUser(id);
        User friend = getUser(friendId);
        jdbcTemplate.update("INSERT INTO friends (user_id, friend_id) VALUES (?, ?)", id, friendId);
        if (getFriendsOfUser(id).contains(friend) && getFriendsOfUser(friendId).contains(user)) {
            jdbcTemplate.update("UPDATE friends set status_of_friendship = TRUE WHERE user_id = ? AND user_id = ?",
                    id, friendId);
            feedDbStorage.createFeed(new Feed(0, millis, id, EventTypes.FRIEND.toString(), Operations.ADD.toString(), friendId));
        } else {
            jdbcTemplate.update("UPDATE friends set status_of_friendship = FALSE WHERE user_id = ? AND user_id = ?",
                    id, friendId);
        }
        return user;
    }

    @Override
    public User deleteFriend(int id, int friendId) {
        LocalTime now = LocalTime.now();
        int millis = now.get(ChronoField.MILLI_OF_SECOND);
        FeedDbStorage feedDbStorage = new FeedDbStorage(jdbcTemplate);
        User chosenUser = getUser(id);
        User friendOfUser = getUser(friendId);
        jdbcTemplate.update("DELETE FROM friends WHERE friend_id=? AND user_id = ?;", friendId, id);
        feedDbStorage.createFeed(new Feed(0, millis, id, EventTypes.FRIEND.toString(), Operations.REMOVE.toString(), friendId));
        return chosenUser;
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        return jdbcTemplate.query("SELECT*" +
                " FROM users WHERE user_id" +
                " IN(SELECT friend_id" +
                " FROM friends WHERE user_id = ?) AND user_id" +
                " IN(SELECT friend_id" +
                " FROM friends WHERE user_id = ?);", UserDbStorage.getUserMapper(), id, otherId);
    }

    @Override
    public List<User> getFriendsOfUser(int id) {
        getUser(id);
        return jdbcTemplate.query("SELECT* " +
                "FROM users " +
                "WHERE user_id IN(SELECT friend_id " +
                "FROM friends WHERE user_id = ?);", UserDbStorage.getUserMapper(), id);
    }
}
