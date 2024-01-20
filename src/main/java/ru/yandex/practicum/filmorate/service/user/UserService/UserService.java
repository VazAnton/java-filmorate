package ru.yandex.practicum.filmorate.service.user.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
public class UserService {

    private final Map<User, Set<User>> usersFriends = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();

    public User addFriend(int id, int friendId) {
        Set<User> friends = new HashSet<>();
        User user = inMemoryUserStorage.getUser(id);
        User friend = inMemoryUserStorage.getUser(friendId);
        friends.add(friend);
        usersFriends.put(user, friends);
        return friend;
    }

    public void deleteFriend(int id, int friendId) {
        if (usersFriends.containsKey(inMemoryUserStorage.getUser(id))) {
            Set<User> friends = usersFriends.get(inMemoryUserStorage.getUser(id));
            friends.remove(inMemoryUserStorage.getUser(friendId));
        }
    }

    public Set<User> getFriends(int id) {
        Optional<User> chosenUser = usersFriends.keySet().stream()
                .filter(user -> id == user.getId())
                .findFirst();
        if (chosenUser.isPresent()) {
            return usersFriends.get(chosenUser.get());
        }
        log.warn("Введён несуществующий номер пользователя");
        return usersFriends.getOrDefault(new User(0, "new_user@yandex.ru",
                        "newUser", "Ivan",
                        LocalDate.of(2000, 2, 22)),
                Set.of(new User(0, "new_friend@yandex.ru", "newFriend", "Motvey",
                        LocalDate.of(2001, 1, 21))));
    }

    public Set<User> getCommonFriends(int id, int otherId) {
        Set<User> commonFriends = new HashSet<>();
        Optional<User> chosenUser = usersFriends.keySet().stream()
                .filter(user -> id == user.getId())
                .findFirst();
        if (chosenUser.isEmpty()) {
            log.warn("Внимание пользователя с таким номером не существует!");
            return null;
        }
        Set<User> friendsOfUser = usersFriends.get(chosenUser.get());
        Optional<User> otherUser = usersFriends.keySet().stream()
                .filter(user -> otherId == user.getId())
                .findFirst();
        if (otherUser.isEmpty()) {
            log.warn("Внимание пользователя с таким номером не существует!");
            return null;
        }
        Set<User> friendsOfOtherUser = usersFriends.get(otherUser.get());
        for (User friend : friendsOfUser) {
            if (friendsOfOtherUser.contains(friend)) {
                commonFriends.add(friend);
            }
        }
        return commonFriends;
    }
}
