package foodsave.com.foodsave.service;



import foodsave.com.foodsave.model.Review;
import foodsave.com.foodsave.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    // Сохранение отзыва
    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }

    // Получение отзывов о продукте
    public List<Review> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProductId(productId); // Получаем отзывы по продукту
    }

    // Получение отзывов о магазине
    public List<Review> getReviewsByStoreId(Long storeId) {
        return reviewRepository.findByProductId(storeId); // предполагаем, что можно искать отзывы о магазинах
    }
    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new RuntimeException("Review not found");
        }
        reviewRepository.deleteById(id);
    }

}
