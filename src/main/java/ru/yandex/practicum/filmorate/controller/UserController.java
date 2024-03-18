package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.feed.FeedService.FeedService;
import ru.yandex.practicum.filmorate.service.user.UserService.UserService;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final FeedService feedService;

    @PostMapping
    public User addUser(@RequestBody @Valid User user) {
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
        return userService.updateUser(user);
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsersOutStorage();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) {
        return userService.getUser(id);
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public User addFriend(@PathVariable int id,
                          @PathVariable int friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable int id,
                             @PathVariable int friendId) {
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendsOfUser(@PathVariable int id) {
        return userService.getFriendsOfUser(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id,
                                       @PathVariable int otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @DeleteMapping("/{id}")
    public boolean deleteUserById(@PathVariable int id) {
        return userService.deleteUserById(id);
    }

    @GetMapping("/{id}/feed")
    public List<Feed> getFeedByUserId(@PathVariable int id) {
        return feedService.getFeedByUserId(id);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable int id) {
        return userService.getRecommendationsFilms(id);
    }
}
