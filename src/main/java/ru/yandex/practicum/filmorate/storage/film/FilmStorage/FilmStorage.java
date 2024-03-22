package ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getFilms();

    Film getFilm(int id);

    boolean like(int id, int userId);

    boolean deleteLike(int id, int userId);

    List<Film> getTopFilms(Integer count, Integer genreId, Integer year);

    boolean deleteFilmById(int id);

    List<Film> getCommonFilms(int userID, int friendId);

    List<Film> getFilmsOfDirector(int directorId, String sortBy);

    List<Film> getFilmsByNameOrNameAndDirector(String query, String by);
}
