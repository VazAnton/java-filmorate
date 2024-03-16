package ru.yandex.practicum.filmorate.service.user.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage.UserStorage;

import java.util.List;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriend(int id, int friendId) {
        return userStorage.addFriend(id, friendId);
    }

    public User deleteFriend(int id, int friendId) {
        return userStorage.deleteFriend(id, friendId);
    }

    public List<User> getCommonFriends(int id, int otherId) {
        return userStorage.getCommonFriends(id, otherId);
    }

    public List<User> getFriendsOfUser(int id) {
        return userStorage.getFriendsOfUser(id);
    }

    public User getUser(int id) {
        return userStorage.getUser(id);
    }

    public User addUser(User user) {

        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> getUsersOutStorage() {
        return userStorage.getUsers();
    }

    public boolean deleteUserById(int id) {
        return userStorage.deleteUserById(id);
    }

    public List<Film> getRecommendationsFilms(int id) {
        return userStorage.getRecommendationsFilmsByUser(id);
    }
}
