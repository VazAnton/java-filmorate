package ru.yandex.practicum.filmorate.service.film.FilmService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage.FilmStorage;

import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;

//    @Autowired
//    public FilmService(FilmStorage filmStorage) {
//        this.filmStorage = filmStorage;
//    }

    public Film addFilm(Film film) {
        log.info("Фильм успешно добавлен!");
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        log.info("Фильм успешно обновлен!");
        return filmStorage.updateFilm(film);
    }

    public Film getFilm(int id) {
        log.info("Информация о выбранном фильме успешно получена!");
        return filmStorage.getFilm(id);
    }

    public List<Film> getFilms() {
        log.info("Информация обо всех фильмах успешно получена!");
        return filmStorage.getFilms();
    }

//    public Genre getGenre(int id) {
//        return filmStorage.getGenre(id);
//    }
//
//    public List<Genre> getGenres() {
//        return filmStorage.getGenres();
//    }
//
//    public Rating getRating(int id) {
//        return filmStorage.getRating(id);
//    }
//
//    public List<Rating> getRatings() {
//        return filmStorage.getRatings();
//    }

    public boolean deleteFilmById(int id) {
        log.info("Выбранный фильм успешно удален!");
        return filmStorage.deleteFilmById(id);
    }

    public List<Film> getCommonFilms(int userID, int friendId) {
        log.info("Получена информация об общих фильмах для пользователя " + userID + " и " + friendId);
        return filmStorage.getCommonFilms(userID, friendId);
    }

    public boolean like(int id, int userId) {
        log.info("Успех! Поьзователь " + userId + " добавил фильм " + id + " в понравившиеся!");
        return filmStorage.like(id, userId);
    }

    public boolean deleteLike(int id, int userId) {
        log.info("Успех! Поьзователь " + userId + " удалил фильм " + id + " из понравившихся!");
        return filmStorage.deleteLike(id, userId);
    }

    public List<Film> getTopFilms(Integer count, Integer rat, Integer year) {
        log.info("Получена инфрмация о самых популярных фильмах!");
        return filmStorage.getTopFilms(count, rat, year);
    }

//    public Director addDirector(Director director) {
//        return filmStorage.addDirector(director);
//    }
//
//    public Director updateDirector(Director director) {
//        return filmStorage.updateDirector(director);
//    }
//
//    public Director getDirector(int id) {
//        return filmStorage.getDirector(id);
//    }
//
//    public List<Director> getDirectors() {
//        return filmStorage.getDirectors();
//    }
//
//    public boolean deleteDirector(int id) {
//        return filmStorage.deleteDirector(id);
//    }

    public List<Film> getFilmsOfDirector(int directorId, String sortBy) {
        log.info("Получена информация о фильмах режиссера " + directorId);
        return filmStorage.getFilmsOfDirector(directorId, sortBy);
    }

    public List<Film> getFilmsByNameOrNameAndDirector(String query, String by) {
        log.info("Успешно найден фильм по переданным параметрам!");
        return filmStorage.getFilmsByNameOrNameAndDirector(query, by);
    }
}
