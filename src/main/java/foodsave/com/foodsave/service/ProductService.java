package foodsave.com.foodsave.service;

import foodsave.com.foodsave.model.Discount;
import foodsave.com.foodsave.model.Product;
import foodsave.com.foodsave.model.Review;
import foodsave.com.foodsave.repository.DiscountRepository;
import foodsave.com.foodsave.repository.ProductRepository;
import foodsave.com.foodsave.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private DiscountRepository discountRepository;

    // Сохранение нового продукта
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    // Получение всех продуктов
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    // Поиск продукта по ID
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    // Поиск продуктов по названию
    public List<Product> findByName(String name) {
        return productRepository.findByNameContaining(name);
    }

    public List<Product> searchProducts(String query) {
        return productRepository.findByNameContaining(query);
    }

    // Обновление продукта
    public Product updateProduct(Long id, Product updatedProduct) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setCategory(updatedProduct.getCategory());
        existingProduct.setDescription(updatedProduct.getDescription());

        return productRepository.save(existingProduct);
    }

    // Удаление продукта
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }

    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    // Обновление изображения продукта
    public Product updateProductImage(Long productId, String imageUrl) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setImageUrl(imageUrl);
        return productRepository.save(product);
    }

    // Получение отзывов по продукту
    public List<Review> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    // Применение скидки к продукту
    public Product applyDiscount(Long id, Discount discount) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setDiscount(discount);
        return productRepository.save(product);
    }

    // Поиск по категории
    public List<Product> findByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    // Поиск по магазину
    public List<Product> findByStoreId(Long storeId) {
        return productRepository.findByStoreId(storeId);
    }

    // Продукты с низким остатком (например, < 10)
    public List<Product> findLowStockProducts(Long storeId) {
        return productRepository.findByStoreIdAndStockQuantityLessThan(storeId, 10);
    }

    // Продукты со скидкой (discount != null)
    public List<Product> findDiscountedProducts(Long storeId) {
        return productRepository.findByStoreIdAndDiscountIsNotNull(storeId);
    }

    // ⚠️ Методы, которые использовали агрегаты и JPQL — временно закомментированы или требуют отдельной реализации через EntityManager
    // public List<Object[]> getTopProductsByRevenue() { ... }
    // public List<Object[]> getTopProductsByQuantity() { ... }

    public List<Object[]> getTopSellingProducts(Long storeId) {
        return productRepository.findTopSellingProducts(storeId);
    }

    public List<Product> getLowStockProducts(Long storeId) {
        return productRepository.findByStoreIdAndStockQuantityLessThan(storeId, 10);
    }

    public List<Object[]> getTopProductsByRevenue() {
        return productRepository.findTopProductsByRevenue();
    }

    public List<Object[]> getTopProductsByQuantity() {
        return productRepository.findTopProductsByQuantity();
    }
}
