package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.review.ReviewService.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review addReview(@RequestBody Review review) {
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review updateReview(@RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @GetMapping("/{id}")
    public Review getReview(@PathVariable int id) {
        return reviewService.getReview(id);
    }

    @GetMapping
    public List<Review> getReviewOfFilm(@RequestParam int filmId,
                                       @RequestParam(defaultValue = "10") Integer count) {
        return reviewService.getReviewOfFilm(filmId);
    }

    @DeleteMapping("/{id}")
    public boolean deleteReview(@PathVariable int id) {
        return reviewService.deleteReview(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public boolean addLikeOfReview(@PathVariable int id,
                                   @PathVariable int userId) {
        return reviewService.addLikeOfReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public boolean deleteLikeOfReview(@PathVariable int id,
                                      @PathVariable int userId) {
        return reviewService.deleteLikeOfReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public boolean addDislikeOfReview(@PathVariable int id,
                                      @PathVariable int userId) {
        return reviewService.addDislikeOfReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public boolean deleteDislikeOfReview(@PathVariable int id,
                                         @PathVariable int userId) {
        return reviewService.deleteDislikeOfReview(id, userId);
    }
}
