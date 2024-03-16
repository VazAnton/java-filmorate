package ru.yandex.practicum.filmorate.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.enums.EventTypes;
import ru.yandex.practicum.filmorate.enums.Operations;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
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

    private void validateDirector(Director director) {
        if (director.getName() == null || director.getName().isBlank()) {
            log.error("Не заполнено имя режиссера.");
            throw new ValidationException("Имя режиссера должно быть указано!");
        }
    }

    private RowMapper<Director> getDirectorMapper() {
        return ((rs, rowNum) -> new Director(rs.getInt("director_id"), rs.getString("name")));
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

    @Override
    public boolean deleteFilmById(int id) {
        if (getFilm(id) != null) {
            jdbcTemplate.update("DELETE FROM films WHERE film_id = ?", id);
            return true;
        }
        return false;
    }

    private List<Genre> createGenres(SqlRowSet genreRow) throws SQLException {
        List<Genre> genres = new ArrayList<>();
        while (genreRow.next()) {
            String genreIds = genreRow.getString("genre_id");
            String genreNames = genreRow.getString("name");
            if (genreIds != null) {
                String[] ids = genreIds.split(",");
                String[] names = genreNames.split(",");
                for (int i = 0; i < ids.length; i++) {
                    genres.add(Genre.builder()
                            .id(Integer.parseInt(ids[i]))
                            .name(names[i])
                            .build());
                }
            }
        }
        return genres;
    }

    private Film createFilm(ResultSet rs, int rowNum) throws SQLException {
        SqlRowSet ratingRow = jdbcTemplate.queryForRowSet("SELECT f.rating_id AS rating_id, " +
                "r.name AS name " +
                "FROM films AS f " +
                "LEFT OUTER JOIN ratings AS r ON f.rating_id=r.rating_id " +
                "WHERE r.rating_id=f.rating_id AND f.film_id = ?;", rs.getInt("film_id"));
        SqlRowSet genresRow = jdbcTemplate.queryForRowSet("SELECT g.genre_id AS genre_id, " +
                "g.name AS name  " +
                "FROM genres AS g " +
                "WHERE g.genre_id IN(SELECT fg.genre_id " +
                "FROM film_genre AS fg " +
                "LEFT OUTER JOIN films AS f ON f.film_id=fg.film_id " +
                "LEFT OUTER JOIN genres AS g ON fg.genre_id=g.genre_id " +
                "WHERE fg.film_id=f.film_id AND f.film_id = ?);", rs.getInt("film_id"));
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
                            "rating_id", film.getMpa().getId()
                    )).intValue();
            film.setId(id);
            setGenre(film);
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (film != null && getFilm(film.getId()) != null) {
            validate(film);
            jdbcTemplate.update("UPDATE films set name = ?, description = ?, release_date = ?, duration = ?, " +
                            "rating_id = ? WHERE film_id = ?;", film.getName(), film.getDescription(), film.getReleaseDate(),
                    film.getDuration(), film.getMpa().getId(), film.getId());
            setGenre(film);
        }
        return getFilm(film.getId());
    }

    @Override
    public boolean deleteFilmById(int id) {
        if (getFilm(id) != null) {
            jdbcTemplate.update("DELETE FROM films WHERE film_id = ?", id);
            return true;
        }
        return false;
    }

    @Override
    public Film getFilm(int id) {
        Film film = jdbcTemplate.queryForObject("SELECT f.film_id, " +
                "f.name , " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "f.rating_id AS rating_id, " +
                "r.name AS rating_name , " +
                "GROUP_CONCAT(g.genre_id) AS genre_id, " +
                "GROUP_CONCAT(g.name) AS genre_name " +
                "FROM films AS f " +
                "LEFT OUTER JOIN film_genre AS fg ON f.film_id=fg.film_id " +
                "LEFT OUTER JOIN genres AS g ON fg.genre_id=g.genre_id " +
                "LEFT OUTER JOIN ratings AS r ON f.rating_id=r.rating_id " +
                "WHERE f.film_id = ?" +
                "GROUP BY f.film_id;", this::createFilm, id);
        if (film == null) {
            throw new ObjectNotFoundException("ВНимание! Фильма с таким номером не существует!");
        }
        return film;
    }

    @Override
    public List<Film> getFilms() {
        return jdbcTemplate.query("SELECT f.film_id, " +
                "f.name , " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "f.rating_id, " +
                "r.name , " +
                "GROUP_CONCAT(g.genre_id) AS genre_id, " +
                "GROUP_CONCAT(g.name) AS name " +
                "FROM films AS f " +
                "LEFT OUTER JOIN film_genre AS fg ON f.film_id=fg.film_id " +
                "LEFT OUTER JOIN genres AS g ON fg.genre_id=g.genre_id " +
                "LEFT OUTER JOIN ratings AS r ON f.rating_id=r.rating_id " +
                "GROUP BY f.film_id;", this::createFilm);
    }

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
        if (film.getGenres() == null) {
            return new ArrayList<>();
        }
        List<Genre> genres = new ArrayList<>(film.getGenres());
        genres.sort((genre1, genre2) -> genre1.getId() - genre2.getId());
        removeGenre(film.getId());
        jdbcTemplate.batchUpdate("MERGE INTO film_genre (film_id, genre_id) VALUES (?, ?);",
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
        LocalTime now = LocalTime.now();
        int millis = now.get(ChronoField.MILLI_OF_SECOND);
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        FeedDbStorage feedDbStorage = new FeedDbStorage(jdbcTemplate);
        if (getFilm(id) != null && userDbStorage.getUser(userId) != null) {
            jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?);", id, userId);
            feedDbStorage.createFeed(new Feed(0, millis, userId, EventTypes.LIKE.toString(), Operations.ADD.toString(), id));

        }
        return true;
    }

    @Override
    public boolean deleteLike(int id, int userId) {
        LocalTime now = LocalTime.now();
        int millis = now.get(ChronoField.MILLI_OF_SECOND);
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        FeedDbStorage feedDbStorage = new FeedDbStorage(jdbcTemplate);
        if (getFilm(id) != null && userDbStorage.getUser(userId) != null) {
            jdbcTemplate.update("DELETE FROM likes WHERE user_id = ? AND film_id = ?;", userId, id);
            feedDbStorage.createFeed(new Feed(0, millis, userId, EventTypes.LIKE.toString(), Operations.REMOVE.toString(), id));
        }
        return true;
    }

    @Override
    public List<Film> getTopFilms(Integer count, Integer genreId, Integer year) {
        if (count < 0) {
            throw new IllegalArgumentException("Было передано отрицательное значение count");
        }
        List<Film> topFilms = new ArrayList<>();
        List<Film> allFilms = jdbcTemplate.query("SELECT f.film_id, " +
                "f.name AS film_name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "f.rating_id, " +
                "r.name AS rating_name, " +
                "(SELECT GROUP_CONCAT(genre_id) " +
                "FROM film_genre AS fg " +
                "WHERE film_id =f.film_id) AS genre_id, " +
                "(SELECT GROUP_CONCAT(g.name) " +
                "FROM genres AS g " +
                "WHERE genre_id IN(SELECT g.genre_id " +
                "FROM film_genre AS fi_g " +
                "WHERE film_id=f.film_id)) AS genre_name " +
                "FROM films AS f " +
                "LEFT OUTER JOIN likes AS l ON f.film_id=l.film_id " +
                "LEFT OUTER JOIN ratings AS r ON f.rating_id=r.rating_id " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(l.user_id) DESC, f.film_id;", this::createFilm);
        if (!allFilms.isEmpty()) {
            for (int i = 0; i < count && i < allFilms.size(); i++) {
                topFilms.add(allFilms.get(i));
            }
        }
        return topFilms;
    }

    @Override
    public List<Film> getCommonFilms(int userID, int friendId) {
        List<Film> commonFilms = new ArrayList<>();
        List<Film> userFilms = likeFilms(userID);
        List<Film> friendFilms = likeFilms(friendId);
        for (Film films : userFilms) {
            if (friendFilms.contains(films)) {
                commonFilms.add(films);
            }
        }
        return commonFilms;
    }


    private List<Film> likeFilms(int userId) {
        String query = "SELECT f.film_id, " +
                "f.name AS film_name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "f.rating_id, " +
                "r.name AS rating_name, " +
                "(SELECT GROUP_CONCAT(genre_id) " +
                "FROM film_genre AS fg " +
                "WHERE film_id =f.film_id) AS genre_id, " +
                "(SELECT GROUP_CONCAT(g.name) " +
                "FROM genres AS g " +
                "WHERE genre_id IN(SELECT g.genre_id " +
                "FROM film_genre AS fi_g " +
                "WHERE film_id=f.film_id)) AS genre_name " +
                "FROM films AS f " +
                "LEFT OUTER JOIN likes AS l ON f.film_id=l.film_id " +
                "LEFT OUTER JOIN ratings AS r ON f.rating_id=r.rating_id " +
                "Where f.film_id IN (Select film_id from likes where user_id = ?) " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(l.user_id) DESC, f.film_id;";
        return jdbcTemplate.query(query, this::createFilm, userId);
    }

    @Override
    public Director addDirector(Director director) {
        if (director != null) {
            validateDirector(director);
            SimpleJdbcInsert simpleDirectorInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("directors")
                    .usingGeneratedKeyColumns("director_id");
            int id = simpleDirectorInsert.executeAndReturnKey(
                    Map.of("name", director.getName())).intValue();
            director.setId(id);
        }
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        if (director != null && getDirector(director.getId()) != null) {
            jdbcTemplate.update("UPDATE directors set name = ? WHERE director_id = ?;", director.getName(),
                    director.getId());
            return getDirector(director.getId());
        }
        return director;
    }

    @Override
    public Director getDirector(int id) {
        return jdbcTemplate.queryForObject("SELECT* FROM directors WHERE director_id = ?;", getDirectorMapper(), id);
    }

    @Override
    public List<Director> getDirectors() {
        return jdbcTemplate.query("SELECT* FROM directors;", getDirectorMapper());
    }

    @Override
    public boolean deleteDirector(int id) {
        if (getDirector(id) != null) {
            jdbcTemplate.update("DELETE FROM directors WHERE director_id = ?;", id);
            return true;
        }
        return false;
    }

    @Override
    public List<Film> getFilmsOfDirector(int directorId, String sortBy) {
        getDirector(directorId);
        if (sortBy != null) {
            if (sortBy.equals("year")) {
                return jdbcTemplate.query("SELECT f.film_id, " +
                        "f.name , " +
                        "f.description, " +
                        "f.release_date, " +
                        "f.duration, " +
                        "f.rating_id, " +
                        "r.name AS rating_name, " +
                        "(SELECT GROUP_CONCAT(genre_id) " +
                        "FROM film_genre AS fg " +
                        "WHERE film_id =f.film_id) AS genre_id, " +
                        "(SELECT GROUP_CONCAT(g.name) " +
                        "FROM genres AS g " +
                        "WHERE genre_id IN(SELECT g.genre_id " +
                        "FROM film_genre AS fi_g " +
                        "WHERE film_id=f.film_id)) AS genre_name, " +
                        "(SELECT GROUP_CONCAT(director_id) " +
                        "FROM film_director AS fd " +
                        "WHERE film_id=f.film_id) AS director_id, " +
                        "(SELECT GROUP_CONCAT(d.name) " +
                        "FROM directors AS d " +
                        "WHERE director_id IN(SELECT d.director_id " +
                        "FROM film_director AS fd " +
                        "WHERE film_id=f.film_id)) AS name " +
                        "FROM films AS f " +
                        "LEFT OUTER JOIN likes AS l ON f.film_id=l.film_id " +
                        "LEFT OUTER JOIN ratings AS r ON f.rating_id=r.rating_id " +
                        "LEFT OUTER JOIN film_director AS fd ON f.film_id=fd.film_id " +
                        "WHERE director_id = ? " +
                        "GROUP BY f.film_id " +
                        "ORDER BY EXTRACT(YEAR FROM f.release_date) ASC, f.film_id;", this::createFilm, directorId);
            }
            if (sortBy.equals("likes")) {
                return jdbcTemplate.query("SELECT f.film_id, " +
                        "f.name , " +
                        "f.description, " +
                        "f.release_date, " +
                        "f.duration, " +
                        "f.rating_id, " +
                        "r.name AS rating_name, " +
                        "(SELECT GROUP_CONCAT(genre_id) " +
                        "FROM film_genre AS fg " +
                        "WHERE film_id =f.film_id) AS genre_id, " +
                        "(SELECT GROUP_CONCAT(g.name) " +
                        "FROM genres AS g " +
                        "WHERE genre_id IN(SELECT g.genre_id " +
                        "FROM film_genre AS fi_g " +
                        "WHERE film_id=f.film_id)) AS genre_name, " +
                        "(SELECT GROUP_CONCAT(director_id) " +
                        "FROM film_director AS fd " +
                        "WHERE film_id=f.film_id) AS director_id, " +
                        "(SELECT GROUP_CONCAT(d.name) " +
                        "FROM directors AS d " +
                        "WHERE director_id IN(SELECT d.director_id " +
                        "FROM film_director AS fd " +
                        "WHERE film_id=f.film_id)) AS name " +
                        "FROM films AS f " +
                        "LEFT OUTER JOIN likes AS l ON f.film_id=l.film_id " +
                        "LEFT OUTER JOIN ratings AS r ON f.rating_id=r.rating_id " +
                        "LEFT OUTER JOIN film_director AS fd ON f.film_id=fd.film_id " +
                        "WHERE director_id = ? " +
                        "GROUP BY f.film_id " +
                        "ORDER BY COUNT(l.user_id) DESC, f.film_id;", this::createFilm, directorId);
            }
        }
        return jdbcTemplate.query("SELECT f.film_id, " +
                "f.name , " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "f.rating_id, " +
                "r.name AS rating_name, " +
                "(SELECT GROUP_CONCAT(genre_id) " +
                "FROM film_genre AS fg " +
                "WHERE film_id =f.film_id) AS genre_id, " +
                "(SELECT GROUP_CONCAT(g.name) " +
                "FROM genres AS g " +
                "WHERE genre_id IN(SELECT g.genre_id " +
                "FROM film_genre AS fi_g " +
                "WHERE film_id=f.film_id)) AS genre_name, " +
                "(SELECT GROUP_CONCAT(director_id) " +
                "FROM film_director AS fd " +
                "WHERE film_id=f.film_id) AS director_id, " +
                "(SELECT GROUP_CONCAT(d.name) " +
                "FROM directors AS d " +
                "WHERE director_id IN(SELECT d.director_id " +
                "FROM film_director AS fd " +
                "WHERE film_id=f.film_id)) AS name " +
                "FROM films AS f " +
                "LEFT OUTER JOIN likes AS l ON f.film_id=l.film_id " +
                "LEFT OUTER JOIN ratings AS r ON f.rating_id=r.rating_id " +
                "LEFT OUTER JOIN film_director AS fd ON f.film_id=fd.film_id " +
                "WHERE director_id = ? " +
                "GROUP BY f.film_id;", this::createFilm, directorId);
    }
}
