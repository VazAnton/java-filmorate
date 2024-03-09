package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.film.FilmService.FilmService;

import java.util.List;

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
    public Film getFilm(@PathVariable int id) {
        return filmService.getFilmOutStorage(id);
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        return filmService.getFilmsOutStorage();
    }

    @PutMapping("/films/{id}/like/{userId}")
    public boolean like(@PathVariable int id,
                        @PathVariable int userId) {
        return filmService.like(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public boolean deleteLike(@PathVariable int id,
                              @PathVariable int userId) {
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getTopFilms(@RequestParam(defaultValue = "2") Integer count,
                                  @RequestParam(defaultValue = "0") Integer genreId,
                                  @RequestParam(defaultValue = "0") Integer year) {
        return filmService.getTopFilms(count, genreId, year);
    }

    @GetMapping("/genres")
    public List<Genre> getGenres() {
        return filmService.getGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenre(@PathVariable int id) {
        return filmService.getGenre(id);
    }

    @GetMapping("/mpa/{id}")
    public Rating getRating(@PathVariable int id) {
        return filmService.getRating(id);
    }

    @GetMapping("/mpa")
    public List<Rating> getRatings() {
        return filmService.getRatings();
    }

    @DeleteMapping("/films/{id}")
    public boolean deleteFilmById(@PathVariable int id) {
        return filmService.deleteFilmById(id);
    }

}
