package ru.yandex.practicum.filmorate.service.film.FilmService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage.FilmStorage;

import java.util.List;

@Service
public class FilmService {

    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public boolean like(int id, int userId) {
        return filmStorage.like(id, userId);
    }

    public boolean deleteLike(int id, int userId) {
        return filmStorage.deleteLike(id, userId);
    }

    public List<Film> getTopFilms(Integer count, Integer rat, Integer year) {
        return filmStorage.getTopFilms(count, rat, year);
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilmOutStorage(int id) {
        return filmStorage.getFilm(id);
    }

    public List<Film> getFilmsOutStorage() {
        return filmStorage.getFilms();
    }

    public Genre getGenre(int id) {
        return filmStorage.getGenre(id);
    }

    public List<Genre> getGenres() {
        return filmStorage.getGenres();
    }

    public Rating getRating(int id) {
        return filmStorage.getRating(id);
    }

    public List<Rating> getRatings() {
        return filmStorage.getRatings();
    }

    public boolean deleteFilmById(int id) {
        return filmStorage.deleteFilmById(id);
    }

    public List<Film> getCommonFilms(int userID, int friendId) {
        return filmStorage.getCommonFilms(userID, friendId);
    }

    public Director addDirector(Director director) {
        return filmStorage.addDirector(director);
    }

    public Director updateDirector(Director director) {
        return filmStorage.updateDirector(director);
    }

    public Director getDirector(int id) {
        return filmStorage.getDirector(id);
    }

    public List<Director> getDirectors() {
        return filmStorage.getDirectors();
    }

    public boolean deleteDirector(int id) {
        return filmStorage.deleteDirector(id);
    }

    public List<Film> getFilmsOfDirector(int directorId, String sortBy) {
        return filmStorage.getFilmsOfDirector(directorId, sortBy);
    }
}
