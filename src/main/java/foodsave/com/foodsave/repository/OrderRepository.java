package foodsave.com.foodsave.repository;

import foodsave.com.foodsave.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStoreIdAndCreatedAtBetween(Long storeId, LocalDateTime startDate, LocalDateTime endDate);
    
    List<Order> findByStoreIdAndCreatedAtAfter(Long storeId, LocalDateTime startDate);
    
    List<Order> findByStoreId(Long storeId);
    
    List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    long countByCreatedAtBefore(LocalDateTime date);
    
    @Query("SELECT o FROM Order o WHERE o.storeId = :storeId AND o.createdAt >= :startDate")
    List<Order> findOrdersByStoreAndDate(@Param("storeId") Long storeId, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT p.id as productId, p.name as productName, SUM(oi.quantity) as totalQuantity " +
           "FROM Order o JOIN o.orderItems oi JOIN oi.product p " +
           "WHERE o.storeId = :storeId AND o.createdAt >= :startDate " +
           "GROUP BY p.id, p.name ORDER BY totalQuantity DESC")
    List<Map<String, Object>> findTopProductsByStoreAndDate(@Param("storeId") Long storeId, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT d.type as discountType, COUNT(d) as usageCount " +
           "FROM Order o JOIN o.discounts d " +
           "WHERE o.storeId = :storeId " +
           "GROUP BY d.type ORDER BY usageCount DESC")
    List<Map<String, Object>> findPopularDiscounts(@Param("storeId") Long storeId);

    List<Order> findByUserId(Long userId);
}