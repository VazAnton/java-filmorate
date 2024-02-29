package ru.yandex.practicum.filmorate.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private static final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);
    private final LocalDate firstFilmRelease = LocalDate.of(1895, 12, 28);

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private void validate(Film film) {
        if (film.getName() == null || film.getName().isEmpty()) {
            log.error("Не заполнено название фильма.");
            throw new ValidationException("Название фильма должно быть заполнено!");
        }
        if (film.getDescription() == null || film.getDescription().length() > 200) {
            log.error("Указана некоректная длина описания фильма.");
            throw new ValidationException("Описание к фильму должно быть заполнено и не может содержать более 200 " +
                    "символов!");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(firstFilmRelease)) {
            log.error("Дата выхода фильма введена некорректно.");
            throw new ValidationException("Дата релиза должна быть указана и не может быть раньше даты релиза первого " +
                    "фильма!");
        }
        if (film.getDuration() < 0) {
            log.error("Была передана неверная продолжительность фильма.");
            throw new ValidationException("Продолжительность фильма не может быть отрицаетльной!");
        }
    }

    private static RowMapper<Genre> getGenreMapper() {
        return ((rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("name")));
    }

    private RowMapper<Rating> getRatingMapper() {
        return ((rs, rowNum) -> new Rating(rs.getInt("rating_id"), rs.getString("name")));
    }

    private Rating createMpa(SqlRowSet ratingRow) throws SQLException {
        if (ratingRow.next()) {
            return Rating.builder()
                    .id(ratingRow.getInt("rating_id"))
                    .name(ratingRow.getString("name"))
                    .build();
        }
        throw new SQLException();
    }

    private List<Genre> createGenres(SqlRowSet genresRow) throws SQLException {
        if (genresRow.next()) {
            List<Genre> genres = new ArrayList<>();
            String genreIds = genresRow.getString("genre_id");
            String genreNames = genresRow.getString("name");
            if (genreIds != null && genreNames != null) {
                String[] ids = genreIds.split(",");
                String[] names = genreIds.split(",");
                for (int i = 0; i < ids.length; i++) {
                    genres.add(Genre.builder()
                            .id(Integer.parseInt(ids[i]))
                            .name(names[i])
                            .build());
                }
            }
            return genres;
        }
        throw new SQLException();
    }

    private Film createFilm(ResultSet rs, int rowNum) throws SQLException {
        SqlRowSet ratingRow = jdbcTemplate.queryForRowSet("SELECT*" +
                "FROM ratings");
        SqlRowSet genresRow = jdbcTemplate.queryForRowSet("SELECT f.film_id, " +
                "g.genre_id, " +
                "g.name AS genre_name " +
                "FROM films AS f " +
                "LEFT OUTER JOIN film_genre AS fg ON f.film_id=fg.film_id " +
                "LEFT OUTER JOIN genres AS g ON fg.genre_id=g.genre_id " +
                "GROUP BY f.film_id;");
        return Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getObject("release_date", LocalDate.class))
                .duration(rs.getInt("duration"))
                .genres(createGenres(genresRow))
                .mpa(createMpa(ratingRow))
                .build();
    }

    @Override
    public Film addFilm(Film film) {
        if (film != null) {
            validate(film);
            SimpleJdbcInsert simpleFilmInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("films")
                    .usingGeneratedKeyColumns("film_id");
            int id = simpleFilmInsert.executeAndReturnKey(
                    Map.of(
                            "name", film.getName(),
                            "description", film.getDescription(),
                            "release_date", film.getReleaseDate(),
                            "duration", film.getDuration(),
                            "mpa", film.getMpa().getId()
                    )).intValue();
            film.setId(id);
            if (film.getGenres() != null) {
                setGenre(film);
            }
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (getFilm(film.getId()) == null) {
            throw new ObjectNotFoundException("Внимание! Фильма с таким номером не существует!");
        }
        validate(film);
        jdbcTemplate.update("UPDATE films set name = ?, description = ?, release_date = ?, duration = ?, " +
                        "rating_id = ? WHERE film_id = ?;", film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());
        if (film.getGenres() != null) {
            setGenre(film);
        }
        return film;
    }

    @Override
    public Film getFilm(int id) {
        Film film = jdbcTemplate.queryForObject("SELECT f.film_id, " +
                "f.name AS film_name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "r.rating_id, " +
                "r.name AS rating_name, " +
                "g.genre_id, " +
                "g.name AS genre_name " +
                "FROM films AS f " +
                "LEFT OUTER JOIN film_genre AS fg ON f.film_id=fg.film_id " +
                "LEFT OUTER JOIN genres AS g ON fg.genre_id=g.genre_id " +
                "LEFT OUTER JOIN ratings AS r ON f.rating_id=r.rating_id " +
                "WHERE f.film_id = ?;", this::createFilm, id);
        if (film == null) {
            throw new ObjectNotFoundException("ВНимание! Фильма с таким номером не существует!");
        }
        return film;
    }

    @Override
    public List<Film> getFilms() {
        return jdbcTemplate.query("SELECT f.film_id, " +
                "f.name AS film_name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "r.rating_id, " +
                "r.name AS rating_name, " +
                "g.genre_id, " +
                "g.name AS genre_name " +
                "FROM films AS f " +
                "LEFT OUTER JOIN film_genre AS fg ON f.film_id=fg.film_id " +
                "LEFT OUTER JOIN genres AS g ON fg.genre_id=g.genre_id " +
                "LEFT OUTER JOIN ratings AS r ON f.rating_id=r.rating_id " +
                "GROUP BY f.film_id;", this::createFilm);
    }

    @Override
    public Rating getRating(int id) {
        Rating chosenRating = jdbcTemplate.queryForObject("SELECT* " +
                        "FROM ratings WHERE rating_id = ?",
                getRatingMapper(), id);
        if (chosenRating == null) {
            throw new ObjectNotFoundException("Внимание! Рейтинга с таким номером не существует!");
        }
        return chosenRating;
    }

    @Override
    public List<Rating> getRatings() {
        return jdbcTemplate.query("SELECT* FROM ratings", getRatingMapper());
    }

    @Override
    public Genre getGenre(int id) {
        Genre chosenGenre = jdbcTemplate.queryForObject("SELECT* FROM genres WHERE genre_id = ?",
                FilmDbStorage.getGenreMapper(), id);
        if (chosenGenre == null) {
            throw new ObjectNotFoundException("Внимание! Жанра с таким номером не существует!");
        }
        return chosenGenre;
    }

    private List<Genre> setGenre(Film film) {
        List<Genre> genres = new ArrayList<>(film.getGenres());
        genres.sort((genre1, genre2) -> genre1.getId() - genre2.getId());
        removeGenre(film.getId());
        jdbcTemplate.batchUpdate("INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?);",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, film.getId());
                        ps.setInt(2, genres.get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return genres.size();
                    }
                });
        return genres;
    }

    private void removeGenre(int filmId) {
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", filmId);
    }

    @Override
    public List<Genre> getGenres() {
        return jdbcTemplate.query("SELECT* FROM genres", FilmDbStorage.getGenreMapper());
    }

    @Override
    public boolean like(int id, int userId) {
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?);", id, userId);
        return true;
    }

    @Override
    public boolean deleteLike(int id, int userId) {
        jdbcTemplate.update("DELETE FROM likes WHERE user_id = ? AND film_id = ?;", userId, id);
        return true;
    }

    @Override
    public List<Film> getTopFilms(Integer count) {
        if (count < 0) {
            throw new IllegalArgumentException("Было передано отрицательное значение count");
        }
        List<Film> topFilms = new ArrayList<>();
        List<Film> allFilms = jdbcTemplate.query("SELECT f.film_id, " +
                "f.name AS film_name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "r.rating_id, " +
                "r.name AS rating_name, " +
                "g.genre_id, " +
                "g.name AS genre_name " +
                "FROM films AS f " +
                "LEFT OUTER JOIN film_genre AS fg ON f.film_id=fg.film_id " +
                "LEFT OUTER JOIN genres AS g ON fg.genre_id=g.genre_id " +
                "LEFT OUTER JOIN ratings AS r ON f.rating_id=r.rating_id  " +
                "WHERE f.film_id IN(SELECT film_id " +
                "FROM likes " +
                "GROUP BY film_id " +
                "ORDER BY COUNT(user_id) DESC);", this::createFilm);
        if (!allFilms.isEmpty()) {
            for (int i = 0; i < count && i < allFilms.size(); i++) {
                topFilms.add(allFilms.get(i));
            }
        }
        return topFilms;
    }
}