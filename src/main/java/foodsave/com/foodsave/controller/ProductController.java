package foodsave.com.foodsave.controller;

import foodsave.com.foodsave.model.Product;
import foodsave.com.foodsave.model.Review;
import foodsave.com.foodsave.service.ProductService;
import foodsave.com.foodsave.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ReviewService reviewService;

    private final String UPLOAD_DIR = "uploads/"; // Папка для хранения изображений

    // Получение списка всех продуктов
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.findAll();
    }

    // Получение продукта по ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Добавление нового продукта
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return ResponseEntity.ok(productService.saveProduct(product));
    }

    // Обновление данных продукта
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return productService.findById(id)
                .map(existingProduct -> {
                    product.setId(id);
                    return ResponseEntity.ok(productService.saveProduct(product));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Удаление продукта
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        return productService.findById(id)
                .map(product -> {
                    productService.deleteById(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Поиск продуктов по ключевому слову
    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String query) {
        return productService.findByName(query);
    }

    // Получение отзывов для продукта
    @GetMapping("/reviews/product/{productId}")
    public ResponseEntity<List<Review>> getReviewsByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProductId(productId));
    }

    // Загрузка изображения для продукта
    @PostMapping("/{productId}/uploadImage")
    public ResponseEntity<String> uploadImage(@PathVariable Long productId, @RequestParam("file") MultipartFile file) {
        try {
            // Проверка типа файла
            if (file.isEmpty()) {
                return new ResponseEntity<>("No file selected", HttpStatus.BAD_REQUEST);
            }

            // Создание директории для хранения изображений, если она не существует
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) {
                dir.mkdir();
            }

            // Сохраняем файл на сервере
            Path path = Path.of(UPLOAD_DIR + file.getOriginalFilename());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            // Сохраняем путь к изображению в базе данных
            String imageUrl = UPLOAD_DIR + file.getOriginalFilename();
            Product product = productService.findById(productId).orElse(null);
            if (product != null) {
                product.setImageUrl(imageUrl);
                productService.saveProduct(product); // Обновляем продукт с новым путем к изображению
                return ResponseEntity.ok("Image uploaded successfully");
            } else {
                return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
            }

        } catch (IOException e) {
            return new ResponseEntity<>("Error saving the file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/category/{category}")
    public List<Product> getProductsByCategory(@PathVariable String category) {
        return productService.findByCategory(category);
    }

    @GetMapping("/store/{storeId}")
    public List<Product> getProductsByStore(@PathVariable Long storeId) {
        return productService.findByStoreId(storeId);
    }

    @GetMapping("/store/{storeId}/top-selling")
    public ResponseEntity<List<Map<String, Object>>> getTopSellingProducts(@PathVariable Long storeId) {
        List<Object[]> results = productService.getTopSellingProducts(storeId);
        List<Map<String, Object>> response = results.stream()
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("productId", row[0]);
                    map.put("productName", row[1]);
                    map.put("totalQuantity", row[2]);
                    return map;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/store/{storeId}/low-stock")
    public List<Product> getLowStockProducts(@PathVariable Long storeId) {
        return productService.getLowStockProducts(storeId);
    }

    @GetMapping("/store/{storeId}/discounted")
    public List<Product> getDiscountedProducts(@PathVariable Long storeId) {
        return productService.findDiscountedProducts(storeId);
    }

    @GetMapping("/top-by-revenue")
    public ResponseEntity<List<Map<String, Object>>> getTopProductsByRevenue() {
        List<Object[]> results = productService.getTopProductsByRevenue();
        List<Map<String, Object>> response = results.stream()
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("productId", row[0]);
                    map.put("productName", row[1]);
                    map.put("totalRevenue", row[2]);
                    return map;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/top-by-quantity")
    public ResponseEntity<List<Map<String, Object>>> getTopProductsByQuantity() {
        List<Object[]> results = productService.getTopProductsByQuantity();
        List<Map<String, Object>> response = results.stream()
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("productId", row[0]);
                    map.put("productName", row[1]);
                    map.put("totalQuantity", row[2]);
                    return map;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
