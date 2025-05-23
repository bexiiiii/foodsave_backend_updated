package foodsave.com.foodsave.repository;

import foodsave.com.foodsave.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByStoreId(Long storeId);

    List<Product> findByCategory(String category);

    List<Product> findByStoreIdAndCategory(Long storeId, String category);

    List<Product> findByNameContaining(String name);

    List<Product> findByStoreIdAndStockQuantityLessThan(Long storeId, int quantityThreshold); // для low stock < 10

    List<Product> findByStoreIdAndDiscountIsNotNull(Long storeId); // для продуктов со скидкой

    List<Product> findByStoreIdAndStockQuantityEquals(Long storeId, int quantity); // out of stock (0)

    List<Product> findByStoreIdAndStockQuantityGreaterThanOrderByStockQuantityAsc(Long storeId, int quantity); // в наличии

    @Query("SELECT p.id, p.name, SUM(oi.quantity * oi.unitPrice) as totalRevenue " +
           "FROM Product p " +
           "JOIN OrderItem oi ON oi.product = p " +
           "GROUP BY p.id, p.name " +
           "ORDER BY totalRevenue DESC")
    List<Object[]> findTopProductsByRevenue();

    @Query("SELECT p.id, p.name, SUM(oi.quantity) as totalQuantity " +
           "FROM Product p " +
           "JOIN OrderItem oi ON oi.product = p " +
           "GROUP BY p.id, p.name " +
           "ORDER BY totalQuantity DESC")
    List<Object[]> findTopProductsByQuantity();

    @Query("SELECT p.id, p.name, SUM(oi.quantity) as totalQuantity " +
           "FROM Product p " +
           "JOIN OrderItem oi ON oi.product = p " +
           "WHERE p.store.id = :storeId " +
           "GROUP BY p.id, p.name " +
           "ORDER BY totalQuantity DESC")
    List<Object[]> findTopSellingProducts(@Param("storeId") Long storeId);
}
