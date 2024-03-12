package ru.yandex.practicum.filmorate.service.review.ReviewService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage.ReviewStorage;

@Service
public class ReviewService {

    private final ReviewStorage reviewStorage;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage) {
        this.reviewStorage = reviewStorage;
    }

    public Review addReview(Review review) {
        return reviewStorage.addReview(review);
    }

    public Review updateReview(Review review) {
        return reviewStorage.updateReview(review);
    }

    public Review getReview(int id) {
        return reviewStorage.getReview(id);
    }

    public Review getReviewOfFilm(int filmId) {
        return reviewStorage.getReviewOfFilm(filmId);
    }

    public boolean deleteReview(int id) {
        return reviewStorage.deleteReview(id);
    }

    public boolean addLikeOfReview(int id, int userId) {
        return reviewStorage.addLikeOfReview(id, userId);
    }

    public boolean deleteLikeOfReview(int id, int userId) {
        return reviewStorage.deleteLikeOfReview(id, userId);
    }

    public boolean addDislikeOfReview(int id, int userId) {
        return reviewStorage.addDislikeOfReview(id, userId);
    }

    public boolean deleteDislikeOfReview(int id, int userId) {
        return reviewStorage.deleteDislikeOfReview(id, userId);
    }
}
