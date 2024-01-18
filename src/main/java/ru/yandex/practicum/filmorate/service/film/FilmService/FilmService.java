package ru.yandex.practicum.filmorate.service.film.FilmService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage.UserStorage;

import java.util.*;

@Service
public class FilmService {

    private final Map<User, Set<Film>> likedFilmsOfUsers = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);
    private final FilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();
    private final UserStorage inMemoryUserStorage = new InMemoryUserStorage();

    public Film like(int id, int userId) {
        Set<Film> filmsOfUser;
        Film chosenFilm = inMemoryFilmStorage.getFilm(id);
        User chosenUser = inMemoryUserStorage.getUser(userId);
        if (likedFilmsOfUsers.get(chosenUser).isEmpty()) {
            filmsOfUser = new TreeSet<>(Comparator.comparingInt(Film::getLikeCount));
            filmsOfUser.add(chosenFilm);
        }
        filmsOfUser = likedFilmsOfUsers.get(chosenUser);
        chosenFilm.setLiked(true);
        chosenFilm.setLikeCount(chosenFilm.getLikeCount() + 1);
        inMemoryFilmStorage.updateFilm(chosenFilm);
        filmsOfUser.add(chosenFilm);
        likedFilmsOfUsers.put(chosenUser, filmsOfUser);
        return chosenFilm;
    }

    public void deleteLike(int id, int userId) {
        Film chosenFilm = inMemoryFilmStorage.getFilm(id);
        User chosenUser = inMemoryUserStorage.getUser(userId);
        if (!likedFilmsOfUsers.containsValue(likedFilmsOfUsers.get(chosenUser))) {
            log.info("У данного пользователя ещё нет понравившихся фильмов.");
            System.out.println("Похоже данный пользователь ещё не поставил лайк ни одному фильму:(");
        }
        if (likedFilmsOfUsers.get(chosenUser).contains(chosenFilm)) {
            chosenFilm.setLiked(false);
            chosenFilm.setLikeCount(chosenFilm.getLikeCount() - 1);
            inMemoryFilmStorage.updateFilm(chosenFilm);
        }
        likedFilmsOfUsers.get(chosenUser).remove(chosenFilm);
    }

    public Set<Film> getTopFilms(Integer count) {
        Set<Film> topFilms = new TreeSet<>((film1, film2) -> {
            if (film1.getLikeCount() < film2.getLikeCount()) {
                return 1;
            }
            if (film1.getLikeCount() > film2.getLikeCount()) {
                return -1;
            }
            return 0;
        });
        Set<Film> allFilms = likedFilmsOfUsers.values().stream()
                .filter(films -> !films.isEmpty())
                .findAny()
                .orElseThrow(() -> new NullPointerException("Похоже ни один из пользоватлей ещё не поставил лайк ни " +
                        "одному фильму:("));
        Optional<Film> chosenFilm = allFilms.stream()
                .max(Comparator.comparingInt(Film::getLikeCount));
        if (chosenFilm.isEmpty()) {
            log.warn("Что-то пошло не так:(");
        } else {
            if (count == null) {
                count = 10;
            } else if (count < 0) {
                throw new IllegalArgumentException("Было передано отрицательное значение count");
            }
            for (int i = 0; i < count; i++) {
                topFilms.add(chosenFilm.get());
            }
        }
        return topFilms;
    }
}
