package foodsave.com.foodsave.repository;

import foodsave.com.foodsave.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductId(Long productId); // Метод для получения отзывов по ID продукта
}
