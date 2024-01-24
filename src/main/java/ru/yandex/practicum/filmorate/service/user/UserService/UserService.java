package ru.yandex.practicum.filmorate.service.user.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage.UserStorage;

import java.util.HashSet;
import java.util.Set;

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
        return friend;
    }

    public void deleteFriend(int id, int friendId) {
        Set<Integer> friendsOfUser = inMemoryUserStorage.getFriendsOfUser(id);
        Set<Integer> friendsOfFriend = inMemoryUserStorage.getFriendsOfUser(friendId);
        User friendOfUser = inMemoryUserStorage.getUser(friendId);
        User friendOfFriend = inMemoryUserStorage.getUser(friendId);
        if (friendsOfUser.contains(friendId) && friendsOfFriend.contains(id)) {
            friendsOfUser.remove(friendId);
            friendOfFriend.setFriendsOfUser(friendsOfUser);
            friendsOfFriend.remove(id);
            friendOfUser.setFriendsOfUser(friendsOfFriend);
        }
    }

    public Set<Integer> getCommonFriends(int id, int otherId) {
        Set<Integer> commonFriends = new HashSet<>();
        Set<Integer> friendsOfUser = inMemoryUserStorage.getFriendsOfUser(id);
        Set<Integer> friendsOfOtherUser = inMemoryUserStorage.getFriendsOfUser(otherId);
        for (int friendId : friendsOfUser) {
            if (friendsOfOtherUser.contains(friendId)) {
                commonFriends.add(friendId);
            }
        }
        return commonFriends;
    }
}
