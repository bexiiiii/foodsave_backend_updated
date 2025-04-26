package foodsave.com.foodsave.service;

import foodsave.com.foodsave.model.Discount;
import foodsave.com.foodsave.model.Product;
import foodsave.com.foodsave.model.Review;
import foodsave.com.foodsave.repository.DiscountRepository;
import foodsave.com.foodsave.repository.ProductRepository;
import foodsave.com.foodsave.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    // Сохранение нового продукта
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    // Получение продукта по названию
    public Product findByName(String name) {
        return productRepository.findByName(name);
    }

    // Получение всех продуктов
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Поиск продукта по ID
    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    // Обновление продукта
    public Product updateProduct(Long id, Product updatedProduct) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Обновление данных продукта
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

    // Поиск продуктов по ключевому слову
    public List<Product> searchProducts(String query) {
        return productRepository.findByNameContaining(query); // Поищет по имени
    }

    // Получение отзывов для продукта
    public List<Review> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProductId(productId); // Получить отзывы по ID продукта
    }
    // Метод для обновления изображения для продукта
    public Product updateProductImage(Long productId, String imageUrl) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setImageUrl(imageUrl);  // Обновляем поле imageUrl

        return productRepository.save(product);  // Сохраняем изменения
    }




    @Autowired
    private DiscountRepository discountRepository;

    // Применение скидки к продукту
    public Product applyDiscount(Long id, Discount discount) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        product.setDiscount(discount); // Устанавливаем скидку на продукт
        return productRepository.save(product);
    }

}
