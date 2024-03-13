package ru.yandex.practicum.filmorate.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
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
            throw new ValidationException("Внимание! Поле с уникальным номером пользователя должно быть заполнено!");
        }
    }

    private RowMapper<Review> getReviewMapper() {
        return ((rs, rowNum) -> new Review(rs.getInt("review_id"), rs.getString("content"),
                rs.getBoolean("is_positive"), rs.getInt("user_id"), rs.getInt("film_id"),
                rs.getInt("useful")));
    }

    private Review createReview(ResultSet rs) throws SQLException {
        return Review.builder()
                .id(rs.getInt("review_id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .userId(rs.getInt("user_id"))
                .filmId(rs.getInt("film_id"))
                .useful(rs.getInt("useful"))
                .build();
    } //Вдруг пригодится

    @Override
    public Review addReview(Review review) {
        FilmStorage filmStorage = new FilmDbStorage(jdbcTemplate);
        UserStorage userStorage = new UserDbStorage(jdbcTemplate);
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
            review.setId(id);
        }
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        FilmStorage filmStorage = new FilmDbStorage(jdbcTemplate);
        UserStorage userStorage = new UserDbStorage(jdbcTemplate);
        if (review != null && getReview(review.getId()) != null && filmStorage.getFilm(review.getFilmId()) != null &&
                userStorage.getUser(review.getUserId()) != null) {
            jdbcTemplate.update("UPDATE reviews set content = ?, is_positive = ?, useful = ? WHERE review_id = ?;",
                    review.getContent(), review.getIsPositive(), review.getUseful(), review.getId());
            return getReview(review.getId());
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
    public Review getReviewOfFilm(int filmId) {
        return null;
    }

    @Override
    public boolean deleteReview(int id) {
        if (getReview(id) != null) {
            jdbcTemplate.update("DELETE reviews WHERE review_id = ?;", id);
            return true;
        }
        return false;
    }

    @Override
    public boolean addLikeOfReview(int id, int userId) {
        return false;
    }

    @Override
    public boolean deleteLikeOfReview(int id, int userId) {
        String sqlQuery = "SELECT* FROM REVIEWS WHERE review_id = ? AND user_id = ?";
        SqlRowSet reviewRows = jdbcTemplate.queryForRowSet(sqlQuery, id, userId);
        if (reviewRows.next()) {
            int i = jdbcTemplate.update("UPDATE REVIEWS SET useful = useful-? WHERE review_id = ? AND user_id = ?",
                    1, id, userId);
            if (i != 0) {
                log.info("Удален лайк у отзыва " + id);
                return true;
            } else {
                return false;
            }
        } else {
            log.info("Лайк отзыва " + id + " не найден");
            throw new ObjectNotFoundException("Лайк отзыва " + id + " не найден");
        }
    }

    @Override
    public boolean addDislikeOfReview(int id, int userId) {
        return false;
    }

    @Override
    public boolean deleteDislikeOfReview(int id, int userId) {
        return false;
    }
}
