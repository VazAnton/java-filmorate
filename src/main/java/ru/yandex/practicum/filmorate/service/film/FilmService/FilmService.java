package ru.yandex.practicum.filmorate.service.film.FilmService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage.FilmStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage inMemoryFilmStorage;

    @Autowired
    public FilmService(FilmStorage inMemoryFilmStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    public Film like(int id, int userId) {
        Film chosenFilm = inMemoryFilmStorage.getFilm(id);
        Set<Integer> usersWhoLikeFilm = chosenFilm.getUsersWhoLikeFilm();
        usersWhoLikeFilm.add(userId);
        chosenFilm.setUsersWhoLikeFilm(usersWhoLikeFilm);
        return chosenFilm;
    }

    public void deleteLike(int id, int userId) {
        Film chosenFilm = inMemoryFilmStorage.getFilm(id);
        Set<Integer> likedFilmsOfUsers = chosenFilm.getUsersWhoLikeFilm();
        likedFilmsOfUsers.remove(userId);
        chosenFilm.setUsersWhoLikeFilm(likedFilmsOfUsers);
    }

    public Set<Film> getTopFilms(Integer count) {
        Set<Film> topFilms = new HashSet<>();
        if (!inMemoryFilmStorage.getFilms().isEmpty()) {
            List<Film> allFilms = inMemoryFilmStorage.getFilms().stream()
                    .sorted((film1, film2) -> {
                        if (film1.getUsersWhoLikeFilm().size() > film2.getUsersWhoLikeFilm().size()) {
                            return 1;
                        } else if (film1.getUsersWhoLikeFilm().size() < film2.getUsersWhoLikeFilm().size()) {
                            return -1;
                        }
                        return 0;
                    })
                    .collect(Collectors.toList());
            if (count == null) {
                count = 10;
            } else if (count < 0) {
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
}
