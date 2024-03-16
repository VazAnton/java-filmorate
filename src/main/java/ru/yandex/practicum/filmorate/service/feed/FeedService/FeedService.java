package ru.yandex.practicum.filmorate.service.feed.FeedService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage.FeedStorage;

import java.util.List;

@Service
public class FeedService {
    FeedStorage feedStorage;

    @Autowired
    public FeedService(FeedStorage feedStorage) {
        this.feedStorage = feedStorage;
    }

    public List<Feed> getFeedByUserId(int id) {
        return feedStorage.getFeedByUserId(id);
    }
}
