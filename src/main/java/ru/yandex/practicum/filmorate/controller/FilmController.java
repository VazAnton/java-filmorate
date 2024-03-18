package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService.FilmService;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        return filmService.updateFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable int id) {
        return filmService.getFilm(id);
    }

    @GetMapping
    public List<Film> getFilms() {
        return filmService.getFilms();
    }

    @PutMapping("/{id}/like/{userId}")
    public boolean like(@PathVariable int id,
                        @PathVariable int userId) {
        return filmService.like(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public boolean deleteLike(@PathVariable int id,
                              @PathVariable int userId) {
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(defaultValue = "2") Integer count,
                                  @RequestParam(defaultValue = "0") Integer genreId,
                                  @RequestParam(defaultValue = "0") Integer year) {
        return filmService.getTopFilms(count, genreId, year);
    }

    @DeleteMapping("/{id}")
    public boolean deleteFilmById(@PathVariable int id) {
        return filmService.deleteFilmById(id);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam int userId, @RequestParam int friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsOfDirector(@PathVariable int directorId,
                                         @RequestParam(required = false) String sortBy) {
        return filmService.getFilmsOfDirector(directorId, sortBy);
    }

    @GetMapping("/search")
    public List<Film> getFilmsByNameOrNameAndDirector(@RequestParam(value = "query") String query,
                                                      @RequestParam(value = "by") String by) {
        return filmService.getFilmsByNameOrNameAndDirector(query, by);
    }
}
