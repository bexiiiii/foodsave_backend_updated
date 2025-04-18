package foodsave.com.foodsave.repository;

import foodsave.com.foodsave.model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountRepository extends JpaRepository<Discount, Long> {
}
