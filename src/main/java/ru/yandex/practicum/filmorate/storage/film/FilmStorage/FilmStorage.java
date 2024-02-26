package ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getFilms();

    Optional<Film> getFilm(int id);

    void like(int id, int userId);

    void deleteLike(int id, int userId);

    List<Film> getTopFilms(Integer count);

    Optional<Genre> getGenre(int id);

    List<Genre> getGenres();

    Optional<Rating> getRating(int id);

    List<Rating> getRatings();
}
