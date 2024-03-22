package ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {

    Genre getGenre(int id);

    List<Genre> getGenres();
}
