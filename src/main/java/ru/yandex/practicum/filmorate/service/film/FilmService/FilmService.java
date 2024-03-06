package ru.yandex.practicum.filmorate.service.film.FilmService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    public List<Film> getTopFilms(Integer count) {
        return filmStorage.getTopFilms(count);
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
}
