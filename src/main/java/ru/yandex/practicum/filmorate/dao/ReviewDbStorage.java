package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.enums.EventTypes;
import ru.yandex.practicum.filmorate.enums.Operations;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage.UserStorage;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    private RowMapper<Review> getReviewMapper() {
        return ((rs, rowNum) -> new Review(rs.getInt("review_id"), rs.getString("content"),
                rs.getBoolean("is_positive"), rs.getInt("user_id"), rs.getInt("film_id"),
                rs.getInt("useful")));
    }

    @Override
    public Review addReview(Review review) {
        FilmStorage filmStorage = new FilmDbStorage(jdbcTemplate);
        UserStorage userStorage = new UserDbStorage(jdbcTemplate);
        FeedDbStorage feedDbStorage = new FeedDbStorage(jdbcTemplate);
        if (review != null && userStorage.getUser(review.getUserId()) != null &&
                filmStorage.getFilm(review.getFilmId()) != null) {
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
            feedDbStorage.createFeed(new Feed(0, System.currentTimeMillis(), review.getUserId(), EventTypes.REVIEW.toString(), Operations.ADD.toString(), review.getReviewId()));
        }
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        FilmStorage filmStorage = new FilmDbStorage(jdbcTemplate);
        UserStorage userStorage = new UserDbStorage(jdbcTemplate);
        FeedDbStorage feedDbStorage = new FeedDbStorage(jdbcTemplate);
        if (review != null && getReview(review.getReviewId()) != null && filmStorage.getFilm(review.getFilmId()) != null
                && userStorage.getUser(review.getUserId()) != null) {
            jdbcTemplate.update("UPDATE reviews set content = ?, is_positive = ? WHERE review_id = ?;",
                    review.getContent(), review.getIsPositive(), review.getReviewId());
            Review review1 = getReview(review.getReviewId());
            feedDbStorage.createFeed(new Feed(0, System.currentTimeMillis(), review1.getUserId(), EventTypes.REVIEW.toString(), Operations.UPDATE.toString(), review1.getReviewId()));
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
        FeedDbStorage feedDbStorage = new FeedDbStorage(jdbcTemplate);
        if (getReview(id) != null) {
            feedDbStorage.createFeed(new Feed(0, System.currentTimeMillis(), getReview(id).getUserId(), EventTypes.REVIEW.toString(), Operations.REMOVE.toString(), getReview(id).getFilmId()));
            jdbcTemplate.update("DELETE reviews WHERE review_id = ?;", id);
            return true;
        }
        return false;
    }

    @Override
    public boolean addLikeOfReview(int id, int userId) {
        jdbcTemplate.update("UPDATE REVIEWS SET USEFUL = USEFUL + 1 WHERE REVIEW_ID = ?", id);
        log.info("Пользователь с ID = {} добавил лайк для отзыва ID = {}.", userId, id);
        return true;
    }

    @Override
    public boolean deleteLikeOfReview(int id, int userId) {
        jdbcTemplate.update("UPDATE REVIEWS SET USEFUL = USEFUL - 1 WHERE REVIEW_ID = ?", id);
        log.info("Пользователь удалил лайк для отзыва");
        return true;
    }

    @Override
    public boolean addDislikeOfReview(int id, int userId) {
        jdbcTemplate.update("UPDATE REVIEWS SET USEFUL = USEFUL - 1 WHERE REVIEW_ID = ?", id);
        log.info("Пользователь с ID = {} добавил дизлайк для отзыва ID = {}.", userId, id);
        return true;
    }

    @Override
    public boolean deleteDislikeOfReview(int id, int userId) {
        jdbcTemplate.update("UPDATE REVIEWS SET USEFUL = USEFUL + 1 WHERE REVIEW_ID = ?", id);
        log.info("Пользователь с ID = {} удалил дизлайк для отзыва ID = {}.", userId, id);
        return true;

    }
}
