package ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private int nextId = 1;

    @Override
    public Film addFilm(@RequestBody Film film) {
        if (film != null) {
            if (film.getName().isEmpty()) {
                log.error("Было передано пустое название фильмаю");
                throw new ValidationException("Название фильма не может быть пустым!");
            }
            if (film.getDescription().length() > 200) {
                log.error("Было передано слишком длинное описание фильма.");
                throw new ValidationException("Описание к фильму не может содержать более 200 символов!");
            }
            if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                log.error("Введена некорректная дата релиза");
                throw new ValidationException("Дата релиза не может быть раньше даты релиза первого фильма");
            }
            if (film.getDuration() < 0) {
                log.error("Была передана неверная продолжительность фильма");
                throw new ValidationException("Продолжительность фильма не может быть отрицаетльной");
            }
            //film.setLiked(false);
            //film.setLikeCount(0);
            film.setId(nextId++);
            films.put(film.getId(), film);
        } else {
            log.error("Был передан пустой объект.");
            throw new NullPointerException("Объект не может быть пустым");
        }
        return film;
    }

    @Override
    public Film updateFilm(@RequestBody Film film) {
        if (film != null && films.containsKey(film.getId())) {
            Film chosenFilm = films.get(film.getId());
            if (film.getName().isEmpty()) {
                log.error("Было передано пустое название фильмаю");
                throw new ValidationException("Название фильма не может быть пустым!");
            }
            if (film.getDescription().length() > 200) {
                log.error("Было передано слишком длинное описание фильма.");
                throw new ValidationException("Описание к фильму не может содержать более 200 символов!");
            }
            if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                log.error("Введена некорректная дата релиза");
                throw new ValidationException("Дата релиза не может быть раньше даты релиза первого фильма");
            }
            if (film.getDuration() < 0) {
                log.error("Была передана неверная продолжительность фильма");
                throw new ValidationException("Продолжительность фильма не может быть отрицаетльной");
            }
            chosenFilm.setName(film.getName());
            chosenFilm.setDescription(film.getDescription());
            chosenFilm.setReleaseDate(film.getReleaseDate());
            chosenFilm.setDuration(film.getDuration());
            films.put(film.getId(), chosenFilm);
        } else {
            log.error("Был передан пустой объект.");
            throw new NullPointerException("Объект не может быть пустым");
        }
        return film;
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilm(@PathVariable int id) {
        if (!films.containsKey(id)) {
            return null;
        }
        return films.get(id);
    }
}
