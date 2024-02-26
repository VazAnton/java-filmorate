package ru.yandex.practicum.filmorate.service.film.FilmService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage.FilmStorage;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class FilmService {

    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void like(int id, int userId) {
        filmStorage.like(id, userId);
    }

    public void deleteLike(int id, int userId) {
        filmStorage.deleteLike(id, userId);
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

    public Optional<Film> getFilmOutStorage(int id) {
        return filmStorage.getFilm(id);
    }

    public List<Film> getFilmsOutStorage() {
        return filmStorage.getFilms();
    }

    public Optional<Genre> getGenre(int id) {
        return filmStorage.getGenre(id);
    }

    public List<Genre> getGenres() {
        return filmStorage.getGenres();
    }

    public Optional<Rating> getRating(int id) {
        return filmStorage.getRating(id);
    }
}
