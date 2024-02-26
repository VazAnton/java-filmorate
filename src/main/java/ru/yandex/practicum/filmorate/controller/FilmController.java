package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.film.FilmService.FilmService;

import java.util.List;
import java.util.Optional;

@RestController
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping("/films")
    public Film addFilm(@RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @GetMapping("/films/{id}")
    public Optional<Film> getFilm(@PathVariable int id) {
        return filmService.getFilmOutStorage(id);
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        return filmService.getFilmsOutStorage();
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void like(@PathVariable int id,
                     @PathVariable int userId) {
        filmService.like(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id,
                           @PathVariable int userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getTopFilms(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.getTopFilms(count);
    }

    @GetMapping("/films/genres")
    public List<Genre> getGenres() {
        return filmService.getGenres();
    }

    @GetMapping("/films/genres/{id}")
    public Optional<Genre> getGenre(@PathVariable int id) {
        return filmService.getGenre(id);
    }

    @GetMapping("/films/ratings/{id}")
    public Optional<Rating> getRating(@PathVariable int id) {
        return filmService.getRating(id);
    }
}
