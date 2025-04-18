package foodsave.com.foodsave.service;

import foodsave.com.foodsave.model.Discount;
import foodsave.com.foodsave.model.Product;
import foodsave.com.foodsave.repository.DiscountRepository;
import foodsave.com.foodsave.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscountService {

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private ProductRepository productRepository;

    // Сохранение новой скидки
    public Discount saveDiscount(Discount discount) {
        return discountRepository.save(discount);
    }

    // Получение скидки по ID
    public Discount findById(Long id) {
        return discountRepository.findById(id).orElse(null);
    }

    // Получение всех скидок
    public List<Discount> getAllDiscounts() {
        return discountRepository.findAll();
    }

    // Применение скидки к продукту
    public Product applyDiscount(Long id, Discount discount) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setDiscount(discount); // Устанавливаем скидку на продукт
        return productRepository.save(product);
    }

    // Удаление скидки
    public void deleteDiscount(Long id) {
        if (!discountRepository.existsById(id)) {
            throw new RuntimeException("Discount not found");
        }
        discountRepository.deleteById(id);
    }
}
