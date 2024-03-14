package ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {

    Review addReview(Review review);

    Review updateReview(Review review);

    Review getReview(int id);

    List<Review> getReviewOfFilm(Integer filmId, Integer count);

    boolean deleteReview(int id);

    boolean addLikeOfReview(int id, int userId);

    boolean deleteLikeOfReview(int id, int userId);

    boolean addDislikeOfReview(int id, int userId);

    boolean deleteDislikeOfReview(int id, int userId);
}
