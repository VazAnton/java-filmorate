package ru.yandex.practicum.filmorate.service.genre.GenreService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage.GenreStorage;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class GenreService {

    private final GenreStorage genreStorage;

    public Genre getGenre(int id) {
        log.info("Информация о выбранном жанре фильма успешно получена!");
        return genreStorage.getGenre(id);
    }

    public List<Genre> getGenres() {
        log.info("Информация обо всех жанрах фильмов успешно получена!");
        return genreStorage.getGenres();
    }
}
