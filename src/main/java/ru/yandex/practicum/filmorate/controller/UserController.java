package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage.UserStorage;

import java.util.List;
import java.util.Set;

@RestController
public class UserController {

    protected UserStorage inMemoryUserStorage;
    protected UserService userService;

    @Autowired
    public UserController(UserStorage inMemoryUserStorage, UserService userService) {
        this.inMemoryUserStorage = inMemoryUserStorage;
        this.userService = userService;
    }

    @PostMapping("/users")
    public User addUser(@RequestBody User user) {
        return inMemoryUserStorage.addUser(user);
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user) {
        return inMemoryUserStorage.updateUser(user);
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return inMemoryUserStorage.getUsers();
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable int id) {
        return inMemoryUserStorage.getUser(id);
    }

    @PutMapping(value = "/users/{id}/friends/{friendId}")
    public User addFriend(@PathVariable int id,
                          @PathVariable int friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable int id,
                             @PathVariable int friendId) {
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriendsOfUser(@PathVariable int id) {
        return inMemoryUserStorage.getFriendsOfUser(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public Set<User> getCommonFriends(@PathVariable int id,
                                      @PathVariable int otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}
