package ru.yandex.practicum.filmorate.storage.feed.FeedStorage;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedStorage {

    void createFeed(Feed feed);

    List<Feed> getFeedByUserId(int id);

}
