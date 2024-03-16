package ru.yandex.practicum.filmorate.service.feed.FeedService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage.FeedStorage;

import java.util.List;

@Service
public class FeedService {
    FeedStorage feedStorage;

    public List<Feed> getFeedByUserId(int id) {
        if (feedStorage.getFeedByUserId(id) == null) {
            throw new BadRequestException("Такого пользователя нет", HttpStatus.NOT_FOUND);
        }
        return feedStorage.getFeedByUserId(id);
    }
}
