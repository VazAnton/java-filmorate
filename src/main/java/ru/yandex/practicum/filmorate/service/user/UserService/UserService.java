package ru.yandex.practicum.filmorate.service.user.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final UserStorage inMemoryUserStorage;

    @Autowired
    public UserService(UserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public User addFriend(int id, int friendId) {
        if (inMemoryUserStorage.getUser(id) == null || inMemoryUserStorage.getUser(friendId) == null) {
            throw new ObjectNotFoundException("Пользователя с таким номером не существует");
        }
        User user = inMemoryUserStorage.getUser(id);
        User friend = inMemoryUserStorage.getUser(friendId);
        Set<Integer> friendsOfUser = user.getFriendsOfUser();
        Set<Integer> friendsOfFriend = friend.getFriendsOfUser();
        friendsOfUser.add(friendId);
        user.setFriendsOfUser(friendsOfUser);
        friendsOfFriend.add(id);
        friend.setFriendsOfUser(friendsOfFriend);
        return user;
    }

    public User deleteFriend(int id, int friendId) {
        if (inMemoryUserStorage.getUser(id) == null || inMemoryUserStorage.getUser(friendId) == null) {
            throw new ObjectNotFoundException("Пользователя с таким номером не существует");
        }
        User chosenUser = inMemoryUserStorage.getUser(id);
        User friendOfUser = inMemoryUserStorage.getUser(friendId);
        Set<Integer> friendsOfUser = chosenUser.getFriendsOfUser();
        Set<Integer> friendsOfFriend = friendOfUser.getFriendsOfUser();
        if (friendsOfUser.contains(friendId) && friendsOfFriend.contains(id)) {
            friendsOfUser.remove(friendId);
            chosenUser.setFriendsOfUser(friendsOfUser);
            friendsOfFriend.remove(id);
            friendOfUser.setFriendsOfUser(friendsOfFriend);
        }
        return chosenUser;
    }

    public List<User> getCommonFriends(int id, int otherId) {
        List<User> commonFriends = new ArrayList<>();
        if (inMemoryUserStorage.getUser(id) == null || inMemoryUserStorage.getUser(otherId) == null) {
            throw new ObjectNotFoundException("Пользователя с таким номером не существует");
        }
        User chosenUser = inMemoryUserStorage.getUser(id);
        Set<Integer> friendsOfUser = chosenUser.getFriendsOfUser();
        User otherUser = inMemoryUserStorage.getUser(otherId);
        Set<Integer> friendsOfOtherUser = otherUser.getFriendsOfUser();
        friendsOfUser.retainAll(friendsOfOtherUser);
        for (int commonFriendId : friendsOfUser) {
            commonFriends.add(inMemoryUserStorage.getUser(commonFriendId));
        }
        return commonFriends;
    }

    public List<User> getFriendsOfUser(int id) {
        List<User> friendsOfUser = new ArrayList<>();
        if (inMemoryUserStorage.getUser(id) == null) {
            throw new ObjectNotFoundException("Внимание пользователя с таким номером не существует!");
        }
        User chosenUser = inMemoryUserStorage.getUser(id);
        if (!chosenUser.getFriendsOfUser().isEmpty()) {
            Set<Integer> numbersOfFriends = chosenUser.getFriendsOfUser();
            for (int friendId : numbersOfFriends) {
                friendsOfUser.add(inMemoryUserStorage.getUser(friendId));
            }
        }
        return friendsOfUser;
    }

    public User getUserOutStorage(int id) {
        return inMemoryUserStorage.getUser(id);
    }

    public User addUser(User user) {
        return inMemoryUserStorage.addUser(user);
    }

    public User updateUser(User user) {
        return inMemoryUserStorage.updateUser(user);
    }

    public List<User> getUsersOutStorage() {
        return inMemoryUserStorage.getUsers();
    }
}
