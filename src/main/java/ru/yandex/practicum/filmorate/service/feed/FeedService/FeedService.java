package ru.yandex.practicum.filmorate.service.feed.FeedService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage.FeedStorage;

import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class FeedService {

    private final FeedStorage feedStorage;

//    @Autowired
//    public FeedService(FeedStorage feedStorage) {
//        this.feedStorage = feedStorage;
//    }

    public List<Feed> getFeedByUserId(int id) {
        log.info("Лента событий для выбранного пользователя успешно получена!");
        return feedStorage.getFeedByUserId(id);
    }
}
