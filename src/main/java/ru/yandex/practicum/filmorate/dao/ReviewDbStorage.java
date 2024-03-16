package ru.yandex.practicum.filmorate.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.enums.EventTypes;
import ru.yandex.practicum.filmorate.enums.Operations;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Map;

@Repository
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;
    private static final Logger log = LoggerFactory.getLogger(ReviewDbStorage.class);

    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private void validate(Review review) {
        if (review.getContent() == null || review.getContent().isBlank()) {
            log.error("Не заполне текст отзыва.");
            throw new ValidationException("Внимание! Текст отзыва должен быть заполнен!");
        }
        if (review.getIsPositive() == null) {
            log.error("Не заполнено поле с мнением о фильме.");
            throw new ValidationException("Внимание! Поле с мнением о фильме должно быть заполнено!");
        }
        if (review.getUserId() == null || review.getFilmId() == null) {
            log.error("Не заполнено поле с уникальным номером пользователя или фильма.");
            throw new ValidationException("Внимание! Поле с уникальным номером должно быть заполнено!");
        }
    }

    private RowMapper<Review> getReviewMapper() {
        return ((rs, rowNum) -> new Review(rs.getInt("review_id"), rs.getString("content"),
                rs.getBoolean("is_positive"), rs.getInt("user_id"), rs.getInt("film_id"),
                rs.getInt("useful")));
    }

    private Review createReview(ResultSet rs) throws SQLException {
        return Review.builder()
                .reviewId(rs.getInt("review_id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .userId(rs.getInt("user_id"))
                .filmId(rs.getInt("film_id"))
                .useful(rs.getInt("useful"))
                .build();
    } //Вдруг пригодится

    @Override
    public Review addReview(Review review) {
        LocalTime now = LocalTime.now();
        int millis = now.get(ChronoField.MILLI_OF_SECOND);
        FilmStorage filmStorage = new FilmDbStorage(jdbcTemplate);
        UserStorage userStorage = new UserDbStorage(jdbcTemplate);
        FeedDbStorage feedDbStorage = new FeedDbStorage(jdbcTemplate);
        if (review != null && userStorage.getUser(review.getUserId()) != null &&
                filmStorage.getFilm(review.getFilmId()) != null) {
            validate(review);
            SimpleJdbcInsert simpleReviewInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("reviews")
                    .usingGeneratedKeyColumns("review_id");
            int id = simpleReviewInsert.executeAndReturnKey(
                    Map.of("content", review.getContent(),
                            "is_positive", review.getIsPositive(),
                            "user_id", review.getUserId(),
                            "film_id", review.getFilmId(),
                            "useful", 0)
            ).intValue();
            review.setReviewId(id);
            feedDbStorage.createFeed(new Feed(0, millis, review.getUserId(), EventTypes.REVIEW.toString(), Operations.ADD.toString(), review.getReviewId()));
        }
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        LocalTime now = LocalTime.now();
        int millis = now.get(ChronoField.MILLI_OF_SECOND);
        FilmStorage filmStorage = new FilmDbStorage(jdbcTemplate);
        UserStorage userStorage = new UserDbStorage(jdbcTemplate);
        FeedDbStorage feedDbStorage = new FeedDbStorage(jdbcTemplate);
        if (review != null && getReview(review.getReviewId()) != null && filmStorage.getFilm(review.getFilmId()) != null
                && userStorage.getUser(review.getUserId()) != null) {
            jdbcTemplate.update("UPDATE reviews set content = ?, is_positive = ? WHERE review_id = ?;",
                    review.getContent(), review.getIsPositive(), review.getReviewId());
            feedDbStorage.createFeed(new Feed(0, millis, review.getUserId(), EventTypes.REVIEW.toString(), Operations.UPDATE.toString(), review.getReviewId()));
            return getReview(review.getReviewId());
        }
        return review;
    }

    @Override
    public Review getReview(int id) {
        return jdbcTemplate.queryForObject("SELECT* " +
                "FROM reviews " +
                "WHERE review_id = ?;", getReviewMapper(), id);
    }

    @Override
    public List<Review> getReviewOfFilm(Integer filmId, Integer count) {
        FilmStorage filmStorage = new FilmDbStorage(jdbcTemplate);
        if (filmId == null) {
            return jdbcTemplate.query("SELECT* " +
                    "FROM reviews " +
                    "ORDER BY useful DESC " +
                    "LIMIT ?;", getReviewMapper(), count);
        } else if (filmStorage.getFilm(filmId) != null) {
            String sql = "SELECT * FROM REVIEWS WHERE FILM_ID = ? " +
                    "ORDER BY USEFUL DESC LIMIT ?";
            return jdbcTemplate.query(sql, getReviewMapper(), filmId, count);
        } else {
            throw new ObjectNotFoundException("Фильм не найден");

        }
    }

    @Override
    public boolean deleteReview(int id) {
        LocalTime now = LocalTime.now();
        int millis = now.get(ChronoField.MILLI_OF_SECOND);
        if (getReview(id) != null) {
            FeedDbStorage feedDbStorage = new FeedDbStorage(jdbcTemplate);
            feedDbStorage.createFeed(new Feed(0, millis, getReview(id).getUserId(), EventTypes.REVIEW.toString(), Operations.REMOVE.toString(), getReview(id).getReviewId()));
            jdbcTemplate.update("DELETE reviews WHERE review_id = ?;", id);
            return true;
        }
        return false;
    }

    @Override
    public boolean addLikeOfReview(int id, int userId) {
        LocalTime now = LocalTime.now();
        int millis = now.get(ChronoField.MILLI_OF_SECOND);
        FeedDbStorage feedDbStorage = new FeedDbStorage(jdbcTemplate);
        feedDbStorage.createFeed(new Feed(0, millis, userId, EventTypes.LIKE.toString(), Operations.ADD.toString(), id));
        jdbcTemplate.update("UPDATE REVIEWS SET USEFUL = USEFUL + 1 WHERE REVIEW_ID = ?", id);
        log.info("Пользователь с ID = {} добавил лайк для отзыва ID = {}.", userId, id);
        return true;
    }

    @Override
    public boolean deleteLikeOfReview(int id, int userId) {
        LocalTime now = LocalTime.now();
        int millis = now.get(ChronoField.MILLI_OF_SECOND);
        if (jdbcTemplate.update("DELETE FROM REVIEWS WHERE REVIEW_ID = ? AND USER_ID = ?", id, userId) < 1) {
            log.info("Ошибка при удалении лайка для отзыва ID = {} от пользователя с ID = {}.", id, userId);
            throw new ValidationException("Ошибка при удалении лайка для отзыва ID = %d от пользователя с ID = %d.");
        } else {
            FeedDbStorage feedDbStorage = new FeedDbStorage(jdbcTemplate);
            feedDbStorage.createFeed(new Feed(0, millis, userId, EventTypes.LIKE.toString(), Operations.REMOVE.toString(), id));
            jdbcTemplate.update("UPDATE REVIEWS SET USEFUL = USEFUL - 1 WHERE REVIEW_ID = ?", id);
            log.info("Пользователь удалил лайк для отзыва");
            return true;
        }
    }


    @Override
    public boolean addDislikeOfReview(int id, int userId) {
        LocalTime now = LocalTime.now();
        int millis = now.get(ChronoField.MILLI_OF_SECOND);
        FeedDbStorage feedDbStorage = new FeedDbStorage(jdbcTemplate);
        feedDbStorage.createFeed(new Feed(0, millis, userId, EventTypes.LIKE.toString(), Operations.ADD.toString(), id));
        jdbcTemplate.update("UPDATE REVIEWS SET USEFUL = USEFUL - 1 WHERE REVIEW_ID = ?", id);
        log.info("Пользователь с ID = {} добавил дизлайк для отзыва ID = {}.", userId, id);
        return true;
    }

    @Override
    public boolean deleteDislikeOfReview(int id, int userId) {
        LocalTime now = LocalTime.now();
        int millis = now.get(ChronoField.MILLI_OF_SECOND);
        if (jdbcTemplate.update("DELETE FROM REVIEWS WHERE REVIEW_ID = ? AND USER_ID = ?", id, userId) < 1) {
            log.info("Ошибка при удалении дизлайка для отзыва ID = {} от пользователя с ID = {}.", id, userId);
            throw new ValidationException("Ошибка при удалении дизлайка для отзыва ID = %d от пользователя с ID = %d.");
        } else {
            FeedDbStorage feedDbStorage = new FeedDbStorage(jdbcTemplate);
            feedDbStorage.createFeed(new Feed(0, millis, userId, EventTypes.LIKE.toString(), Operations.REMOVE.toString(), id));
            jdbcTemplate.update("UPDATE REVIEWS SET USEFUL = USEFUL + 1 WHERE REVIEW_ID = ?", id);
            log.info("Пользователь с ID = {} удалил дизлайк для отзыва ID = {}.", userId, id);
            return true;
        }
    }
}
