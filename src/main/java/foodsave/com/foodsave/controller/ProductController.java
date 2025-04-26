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
            Product product = productService.findById(productId);
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
}
