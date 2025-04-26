package foodsave.com.foodsave.repository;

import foodsave.com.foodsave.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByUserId(Long userId);  // Находим корзину по пользователю
}
