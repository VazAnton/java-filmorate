//package ru.yandex.practicum.filmorate.storage.film.FilmStorage;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestBody;
//import ru.yandex.practicum.filmorate.controller.FilmController;
//import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
//import ru.yandex.practicum.filmorate.exception.ValidationException;
//import ru.yandex.practicum.filmorate.model.Film;
//
//import java.time.LocalDate;
//import java.util.*;
//
//@Component
//public class InMemoryFilmStorage implements FilmStorage {
//
//    private final Map<Integer, Film> films = new HashMap<>();
//    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
//    private int nextId = 1;
//    private final LocalDate firstFilmRelease = LocalDate.of(1895, 12, 28);
//
//    @Override
//    public Film addFilm(@RequestBody Film film) {
//        if (film != null) {
//            validate(film);
//            /*if (film.getLikes() == null) {
//                Set<Integer> usersWhoLikeFilm = new HashSet<>();
//                film.setLikes(usersWhoLikeFilm);
//            }*/
//            film.setId(nextId++);
//            films.put(film.getId(), film);
//        } else {
//            log.error("Был передан пустой объект.");
//            throw new NullPointerException("Объект не может быть пустым");
//        }
//        return film;
//    }
//
//    @Override
//    public Film updateFilm(@RequestBody Film film) {
//        if (film != null && films.containsKey(film.getId())) {
//            Film chosenFilm = films.get(film.getId());
//            validate(film);
//            chosenFilm.setName(film.getName());
//            chosenFilm.setDescription(film.getDescription());
//            chosenFilm.setReleaseDate(film.getReleaseDate());
//            chosenFilm.setDuration(film.getDuration());
//            films.put(film.getId(), chosenFilm);
//        } else {
//            log.error("Был передан пустой объект.");
//            throw new NullPointerException("Объект не может быть пустым");
//        }
//        return film;
//    }
//
//    private void validate(Film film) {
//        if (film.getName() == null || film.getName().isEmpty()) {
//            log.error("Не заполнено название фильма.");
//            throw new ValidationException("Название фильма должно быть заполнено!");
//        }
//        if (film.getDescription() == null || film.getDescription().length() > 200) {
//            log.error("Указана некоректная длина описания фильма.");
//            throw new ValidationException("Описание к фильму должно быть заполнено и не может содержать более 200 " +
//                    "символов!");
//        }
//        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(firstFilmRelease)) {
//            log.error("Дата выхода фильма введена некорректно.");
//            throw new ValidationException("Дата релиза должна быть указана и не может быть раньше даты релиза первого " +
//                    "фильма!");
//        }
//        if (film.getDuration() < 0) {
//            log.error("Была передана неверная продолжительность фильма.");
//            throw new ValidationException("Продолжительность фильма не может быть отрицаетльной!");
//        }
//    }
//
//    @Override
//    public List<Film> getFilms() {
//        return new ArrayList<>(films.values());
//    }
//
//    @Override
//    public Optional<Film> getFilm(@PathVariable int id) {
//        if (!films.containsKey(id)) {
//            throw new ObjectNotFoundException("Внимание фильма с таким номером не существует!");
//        }
//        //return films.get(id);
//        return Optional.empty();
//    }
//}
