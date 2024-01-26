package ru.yandex.practicum.filmorate.service.user.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage.UserStorage;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Service
public class UserService {

    private final UserStorage inMemoryUserStorage;

    @Autowired
    public UserService(UserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public User addFriend(int id, int friendId) {
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

    public Set<User> getCommonFriends(int id, int otherId) {
        Set<User> commonFriends = new TreeSet<>((user1, user2) -> {
            if (user1.getId() < user2.getId()) {
                return -1;
            } else if (user1.getId() > user2.getId()) {
                return 1;
            }
            return 0;
        });
        User chosenUser = inMemoryUserStorage.getUser(id);
        Set<Integer> friendsOfUser = chosenUser.getFriendsOfUser();
        try {
            User otherUser = inMemoryUserStorage.getUser(otherId);
            Set<Integer> friendsOfOtherUser = otherUser.getFriendsOfUser();
            for (int friendId : friendsOfUser) {
                if (friendsOfOtherUser.contains(friendId)) {
                    commonFriends.add(inMemoryUserStorage.getUser(friendId));
                }
            }
        } catch (NullPointerException e) {
            return commonFriends;
        }
        return commonFriends;
    }
}