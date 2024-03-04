package ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getFilms();

    Film getFilm(int id);

    boolean like(int id, int userId);

    boolean deleteLike(int id, int userId);

    List<Film> getTopFilms(Integer count);

    Genre getGenre(int id);

    List<Genre> getGenres();

    Rating getRating(int id);

    List<Rating> getRatings();
}
