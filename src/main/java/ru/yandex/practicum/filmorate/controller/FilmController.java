package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.film.FilmService.FilmService;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@Validated
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

//    @Autowired
//    public FilmController(FilmService filmService) {
//        this.filmService = filmService;
//    }

    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        return filmService.updateFilm(film);
    }

    @GetMapping
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

//    @GetMapping("/genres")
//    public List<Genre> getGenres() {
//        return filmService.getGenres();
//    }
//
//    @GetMapping("/genres/{id}")
//    public Genre getGenre(@PathVariable int id) {
//        return filmService.getGenre(id);
//    }
//
//    @GetMapping("/mpa/{id}")
//    public Rating getRating(@PathVariable int id) {
//        return filmService.getRating(id);
//    }
//
//    @GetMapping("/mpa")
//    public List<Rating> getRatings() {
//        return filmService.getRatings();
//    }

    @DeleteMapping("/{id}")
    public boolean deleteFilmById(@PathVariable int id) {
        return filmService.deleteFilmById(id);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam int userId, @RequestParam int friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

//    @PostMapping("/directors")
//    public Director addDirector(@RequestBody @Valid Director director) {
//        return filmService.addDirector(director);
//    }
//
//    @PutMapping("/directors")
//    public Director updateDirector(@RequestBody @Valid Director director) {
//        return filmService.updateDirector(director);
//    }
//
//    @GetMapping("/directors")
//    public List<Director> getDirectors() {
//        return filmService.getDirectors();
//    }
//
//    @GetMapping("/directors/{id}")
//    public Director getDirector(@PathVariable int id) {
//        return filmService.getDirector(id);
//    }
//
//    @DeleteMapping("/directors/{id}")
//    public boolean deleteDirector(@PathVariable int id) {
//        return filmService.deleteDirector(id);
//    }

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
