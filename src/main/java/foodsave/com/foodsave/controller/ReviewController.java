package foodsave.com.foodsave.controller;

import foodsave.com.foodsave.model.Review;
import foodsave.com.foodsave.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // Получение отзывов о продукте
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getReviewsByProductId(@PathVariable Long productId) {
        List<Review> reviews = reviewService.getReviewsByProductId(productId); // Получаем отзывы
        return ResponseEntity.ok(reviews); // Возвращаем результат
    }

    // Получение отзывов о магазине
    @GetMapping("/store/{storeId}")
    public List<Review> getReviewsByStoreId(@PathVariable Long storeId) {
        return reviewService.getReviewsByStoreId(storeId);
    }

    // Создание отзыва
    @PostMapping
    public Review createReview(@RequestBody Review review) {
        return reviewService.saveReview(review);
    }
    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

}
