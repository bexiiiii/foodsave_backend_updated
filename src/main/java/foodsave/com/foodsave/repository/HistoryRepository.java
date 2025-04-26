package foodsave.com.foodsave.repository;

import foodsave.com.foodsave.model.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long> {
}
