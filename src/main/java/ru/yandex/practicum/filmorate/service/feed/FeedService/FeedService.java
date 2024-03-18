package ru.yandex.practicum.filmorate.service.feed.FeedService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage.FeedStorage;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class FeedService {

    private final FeedStorage feedStorage;

    public List<Feed> getFeedByUserId(int id) {
        log.info("Лента событий для выбранного пользователя успешно получена!");
        return feedStorage.getFeedByUserId(id);
    }
}
