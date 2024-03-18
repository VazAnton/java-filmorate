package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.enums.EventTypes;
import ru.yandex.practicum.filmorate.enums.Operations;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    private void validate(User user) {
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            log.error("При создании пользователя не было указано его имя.");
            user.setName(user.getLogin());
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
            return getUser(user.getId());
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
        FeedDbStorage feedDbStorage = new FeedDbStorage(jdbcTemplate);
        User user = getUser(id);
        User friend = getUser(friendId);
        jdbcTemplate.update("INSERT INTO friends (user_id, friend_id) VALUES (?, ?)", id, friendId);
        feedDbStorage.createFeed(new Feed(0, System.currentTimeMillis(), id, EventTypes.FRIEND.toString(),
                Operations.ADD.toString(), friendId));
        if (getFriendsOfUser(id).contains(friend) && getFriendsOfUser(friendId).contains(user)) {
            jdbcTemplate.update("UPDATE friends set status_of_friendship = TRUE WHERE user_id = ? AND user_id = ?",
                    id, friendId);
        } else {
            jdbcTemplate.update("UPDATE friends set status_of_friendship = FALSE WHERE user_id = ? AND user_id = ?",
                    id, friendId);
        }
        return user;
    }

    @Override
    public User deleteFriend(int id, int friendId) {
        FeedDbStorage feedDbStorage = new FeedDbStorage(jdbcTemplate);
        User chosenUser = getUser(id);
        getUser(friendId);
        jdbcTemplate.update("DELETE FROM friends WHERE friend_id=? AND user_id = ?;", friendId, id);
        feedDbStorage.createFeed(new Feed(0, System.currentTimeMillis(), id, EventTypes.FRIEND.toString(),
                Operations.REMOVE.toString(), friendId));
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

    @Override
    public List<Film> getRecommendationsFilmsByUser(int id) {
        List<Film> recommendationFilm = new ArrayList<>();
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        List<Integer> filmLikesByUser = getIdFilmLikes(id);
        List<Integer> commonUserId = getIdUserFromLikes(id);
        if (commonUserId.size() == 0) {
            return recommendationFilm;
        }
        List<Integer> filmId = new ArrayList<>();
        for (Integer userId : commonUserId) {
            List<Integer> filmByFriend = getIdFilmLikes(userId);
            for (Integer filmIdFriend : filmByFriend) {
                if (!filmLikesByUser.contains(filmIdFriend)) {
                    filmId.add(filmIdFriend);
                }
            }
        }
        Set<Integer> originFilmId = new HashSet<>(filmId);
        for (Integer filmIdRecommendation : originFilmId) {
            recommendationFilm.add(filmDbStorage.getFilm(filmIdRecommendation));
        }
        return recommendationFilm;
    }

    private List<Integer> getIdUserFromLikes(int id) {
        String sql = "SELECT user_id FROM LIKES " +
                "WHERE film_id IN " +
                "(SELECT film_id FROM LIKES WHERE user_id = ? );";
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> rs.getInt("user_id"),
                id);
    }

    private List<Integer> getIdFilmLikes(int userId) {
        String sql = "SELECT film_id FROM LIKES " +
                "WHERE user_id = ?";
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> rs.getInt("film_id"),
                userId);
    }
}
