package foodsave.com.foodsave.controller;

import foodsave.com.foodsave.model.Product;
import foodsave.com.foodsave.model.Review;
import foodsave.com.foodsave.service.ProductService;
import foodsave.com.foodsave.service.ReviewService; // Import ReviewService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ReviewService reviewService; // Inject ReviewService

    // Получение списка всех продуктов
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // Получение продукта по ID
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.findById(id);
    }

    // Добавление нового продукта
    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productService.saveProduct(product);
    }

    // Обновление данных продукта
    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id); // Устанавливаем ID для обновления
        return productService.saveProduct(product);
    }

    // Удаление продукта
    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.saveProduct(new Product()); // Логика удаления, добавим позже
        return "Product deleted successfully";
    }

    // Поиск продуктов по ключевому слову
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String query) {
        return ResponseEntity.ok(productService.searchProducts(query));
    }

    // Получение отзывов для продукта
    @GetMapping("/reviews/product/{productId}")
    public ResponseEntity<List<Review>> getReviewsByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProductId(productId)); // Use reviewService here
    }
}
