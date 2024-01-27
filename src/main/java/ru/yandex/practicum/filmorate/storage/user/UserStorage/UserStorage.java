package ru.yandex.practicum.filmorate.storage.user.UserStorage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User addUser(User user);

    User updateUser(User user);

    List<User> getUsers();

    User getUser(int id);

    List<User> getFriendsOfUser(int id);
}
