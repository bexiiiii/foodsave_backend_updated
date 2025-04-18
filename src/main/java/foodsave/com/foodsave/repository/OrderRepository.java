package foodsave.com.foodsave.repository;

import foodsave.com.foodsave.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Пример метода для получения всех заказов для пользователя
    List<Order> findByUserId(Long userId);
}