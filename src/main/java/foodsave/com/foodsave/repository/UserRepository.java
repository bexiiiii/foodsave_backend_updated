package foodsave.com.foodsave.repository;

import foodsave.com.foodsave.model.User;
import foodsave.com.foodsave.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    
    Optional<User> findByUsername(String username);
    
    boolean existsByEmail(String email);
    
    boolean existsByUsername(String username);
    
    List<User> findByStoreId(Long storeId);
    
    List<User> findByLastLoginAfter(LocalDateTime date);
    
    List<User> findByCreatedAtAfter(LocalDateTime date);
    
    List<User> findByCreatedAtBefore(LocalDateTime date);
    
    long countByLastLoginAfter(LocalDateTime date);
    
    long countByCreatedAtAfter(LocalDateTime date);
    
    long countByCreatedAtBefore(LocalDateTime date);
    
    List<User> findByFirstNameContainingOrLastNameContainingOrEmailContaining(String firstName, String lastName, String email);
    
    @Query("SELECT o FROM Order o WHERE o.userId = :userId")
    List<Map<String, Object>> findUserOrders(@Param("userId") Long userId);

    // Получить избранные продукты пользователя
    @Query("SELECT u.favorites FROM User u WHERE u.id = :userId")
    Set<Product> findUserFavorites(@Param("userId") Long userId);
    

    
    @Query("SELECT COUNT(u) FROM User u WHERE u.storeId = :storeId AND u.lastLogin >= :date")
    long countByStoreIdAndLastLoginAfter(@Param("storeId") Long storeId, @Param("date") LocalDateTime date);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.storeId = :storeId AND u.createdAt <= :date")
    long countByStoreIdAndCreatedAtBefore(@Param("storeId") Long storeId, @Param("date") LocalDateTime date);
}

