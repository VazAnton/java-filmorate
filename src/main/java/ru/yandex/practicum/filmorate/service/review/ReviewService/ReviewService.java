package ru.yandex.practicum.filmorate.service.review.ReviewService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage.ReviewStorage;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ReviewService {

    private final ReviewStorage reviewStorage;

    public Review addReview(Review review) {
        log.info("Отзыв успешно добавлен!");
        return reviewStorage.addReview(review);
    }

    public Review updateReview(Review review) {
        log.info("Отзыв успешно обновлён!");
        return reviewStorage.updateReview(review);
    }

    public Review getReview(int id) {
        log.info("Получена информация о отзыве " + id);
        return reviewStorage.getReview(id);
    }

    public List<Review> getReviewOfFilm(Integer filmId, Integer count) {
        log.info("Получена информация об отзывах на фильм " + filmId);
        return reviewStorage.getReviewOfFilm(filmId, count);
    }

    public boolean deleteReview(int id) {
        log.info("Отзыв под номером " + id + " успешно удалён!");
        return reviewStorage.deleteReview(id);
    }

    public boolean addLikeOfReview(int id, int userId) {
        log.info("Успех! Поьзователь " + userId + " оценил отзыв " + id + " как понравившийся!");
        return reviewStorage.addLikeOfReview(id, userId);
    }

    public boolean deleteLikeOfReview(int id, int userId) {
        log.info("Успех! Поьзователь " + userId + " удалил отзыв " + id + " из понравившихся!");
        return reviewStorage.deleteLikeOfReview(id, userId);
    }

    public boolean addDislikeOfReview(int id, int userId) {
        log.info("Успех! Поьзователь " + userId + " оценил отзыв " + id + " как непонравившийся!");
        return reviewStorage.addDislikeOfReview(id, userId);
    }

    public boolean deleteDislikeOfReview(int id, int userId) {
        log.info("Успех! Поьзователь " + userId + " удалил отзыв " + id + " из непонравившихся!");
        return reviewStorage.deleteDislikeOfReview(id, userId);
    }
}
