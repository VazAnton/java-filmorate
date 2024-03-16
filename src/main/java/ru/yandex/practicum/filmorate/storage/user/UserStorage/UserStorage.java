package ru.yandex.practicum.filmorate.storage.user.UserStorage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User addUser(User user);

    User updateUser(User user);

    List<User> getUsers();

    User getUser(int id);

    User addFriend(int id, int friendId);

    User deleteFriend(int id, int friendId);

    List<User> getCommonFriends(int id, int otherId);

    List<User> getFriendsOfUser(int id);

    boolean deleteUserById(int id);
}
