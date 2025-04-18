package foodsave.com.foodsave.repository;

import foodsave.com.foodsave.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {
    // Методы для работы с заведениями (поиск по типу, категории и названию)
    // Пример метода для поиска по названию:
    Store findByName(String name);

    List<Store> findByNameContaining(String query);
}
