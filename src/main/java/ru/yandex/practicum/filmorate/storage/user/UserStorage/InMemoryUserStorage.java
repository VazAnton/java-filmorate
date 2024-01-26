package ru.yandex.practicum.filmorate.storage.user.UserStorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private int nextId = 1;

    @Override
    public User addUser(@RequestBody User user) {
        if (user != null) {
            if (user.getEmail().isEmpty()) {
                log.error("Был передан пустой адрес электронной почты.");
                throw new ValidationException("Адрес алектронной почты не может быть пустым!");
            } else if (!user.getEmail().contains("@")) {
                log.error("Было передан неверный адрес электронной почты.");
                throw new ValidationException("Адрес алектронной почты должен содержать символ @!");
            }
            if (user.getLogin().isEmpty()) {
                log.error("Был передан пустой логин.");
                throw new ValidationException("Логин пользователя не может быть пустым!");
            } else if (user.getLogin().contains(" ")) {
                log.error("Логин пользователя содержит пробелы.");
                throw new ValidationException("Логин пользователя не может содержать пробелы!");
            }
            try {
                if (user.getName().isEmpty()) {
                    log.error("Было передано пустое имя пользователя.");
                    user.setName(user.getLogin());
                }
            } catch (NullPointerException nullPointerException) {
                log.error("При создании пользователя не было указано его имя.");
                user.setName(user.getLogin());
            }
            if (user.getBirthday().isAfter(LocalDate.now())) {
                log.error("Передана некорректная дата рождения пользователя.");
                throw new ValidationException("День рождения пользователя не может быть больше текущей даты!");
            }
            if (user.getFriendsOfUser() == null) {
                Set<Integer> friendsOfUser = new HashSet<>();
                user.setFriendsOfUser(friendsOfUser);
            }
            user.setId(nextId++);
            users.put(user.getId(), user);
        } else {
            log.error("Был передан пустой объект.");
            throw new NullPointerException("Объект не может быть пустым");
        }
        return user;
    }

    @Override
    public User updateUser(@RequestBody User user) {
        if (user != null) {
            User chosenUser = users.get(user.getId());
            if (user.getEmail().isEmpty()) {
                log.error("Был передан пустой адрес электронной почты.");
                throw new ValidationException("Адрес алектронной почты не может быть пустым!");
            } else if (!user.getEmail().contains("@")) {
                log.error("Было передан неверный адрес электронной почты.");
                throw new ValidationException("Адрес алектронной почты должен содержать символ @!");
            }
            if (user.getLogin().isEmpty()) {
                log.error("Был передан пустой логин.");
                throw new ValidationException("Логин пользователя не может быть пустым!");
            } else if (user.getLogin().contains(" ")) {
                log.error("Логин пользователя содержит пробелы.");
                throw new ValidationException("Логин пользователя не может содержать пробелы!");
            }
            try {
                if (user.getName().isEmpty()) {
                    log.error("Было передано пустое имя пользователя.");
                    user.setName(user.getLogin());
                }
            } catch (NullPointerException nullPointerException) {
                log.error("При создании пользователя не было указано его имя.");
                user.setName(user.getLogin());
            }
            if (user.getBirthday().isAfter(LocalDate.now())) {
                log.error("Передана некорректная дата рождения пользователя.");
                throw new ValidationException("День рождения пользователя не может быть больше текущей даты!");
            }
            chosenUser.setName(user.getName());
            chosenUser.setEmail(user.getEmail());
            chosenUser.setLogin(user.getLogin());
            chosenUser.setBirthday(user.getBirthday());
            users.put(user.getId(), chosenUser);
        } else {
            log.error("Был передан пустой объект.");
            throw new NullPointerException("Объект не может быть пустым");
        }
        return user;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(@PathVariable int id) {
        if (!users.containsKey(id)) {
            throw new NullPointerException("Внимание пользователя с таким номером не существует!");
        }
        return users.get(id);
    }

    @Override
    public Set<User> getFriendsOfUser(@PathVariable int id) {
        Set<User> friendsOfUser = new HashSet<>();
        if (!users.containsKey(id)) {
            throw new NullPointerException("Внимание пользователя с таким номером не существует!");
        }
        User chosenUser = users.get(id);
        Set<Integer> numbersOfFriends = chosenUser.getFriendsOfUser();
        for (int friendId : numbersOfFriends) {
            friendsOfUser.add(users.get(friendId));
        }
        return friendsOfUser;
    }
}
