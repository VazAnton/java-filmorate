package ru.yandex.practicum.filmorate.service.film.FilmService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage inMemoryFilmStorage;
    private final UserStorage inMemoryUserStorage;

    @Autowired
    public FilmService(FilmStorage inMemoryFilmStorage, UserStorage inMemoryUserStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public Film like(int id, int userId) {
        Film chosenFilm = inMemoryFilmStorage.getFilm(id);
        Set<Integer> usersWhoLikeFilm = chosenFilm.getLikes();
        if (inMemoryUserStorage.getUser(userId) == null) {
            throw new ObjectNotFoundException("Пользователя с таким номером не существует!");
        }
        usersWhoLikeFilm.add(userId);
        return chosenFilm;
    }

    public Film deleteLike(int id, int userId) {
        Film chosenFilm = inMemoryFilmStorage.getFilm(id);
        Set<Integer> likedFilmsOfUsers = chosenFilm.getLikes();
        if (inMemoryUserStorage.getUser(userId) == null) {
            throw new ObjectNotFoundException("Пользователя с таким номером не существует!");
        }
        likedFilmsOfUsers.remove(userId);
        return chosenFilm;
    }

    public List<Film> getTopFilms(Integer count) {
        List<Film> topFilms = new ArrayList<>();
        if (!inMemoryFilmStorage.getFilms().isEmpty()) {
            List<Film> allFilms = inMemoryFilmStorage.getFilms().stream()
                    .sorted((film1, film2) -> {
                        if (film1.getLikes().size() > film2.getLikes().size()) {
                            return -1;
                        } else if (film1.getLikes().size() < film2.getLikes().size()) {
                            return 1;
                        }
                        return 0;
                    })
                    .collect(Collectors.toList());
            if (count < 0) {
                throw new IllegalArgumentException("Было передано отрицательное значение count");
            }
            if (!allFilms.isEmpty()) {
                for (int i = 0; i < count && i < allFilms.size(); i++) {
                    topFilms.add(allFilms.get(i));
                }
            }
        }
        return topFilms;
    }

    public Film addFilm(Film film) {
        return inMemoryFilmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return inMemoryFilmStorage.updateFilm(film);
    }

    public Film getFilmOutStorage(int id) {
        return inMemoryFilmStorage.getFilm(id);
    }

    public List<Film> getFilmsOutStorage() {
        return inMemoryFilmStorage.getFilms();
    }
}
