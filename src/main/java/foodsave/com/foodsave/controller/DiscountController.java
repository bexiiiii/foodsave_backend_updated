package foodsave.com.foodsave.controller;

import foodsave.com.foodsave.model.Discount;
import foodsave.com.foodsave.model.Product;
import foodsave.com.foodsave.service.DiscountService;
import foodsave.com.foodsave.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discounts")
public class DiscountController {

    @Autowired
    private DiscountService discountService;

    @Autowired
    private ProductService productService;

    // Получение списка всех скидок
    @GetMapping
    public List<Discount> getAllDiscounts() {
        return discountService.getAllDiscounts();
    }

    // Получение скидки по ID
    @GetMapping("/{id}")
    public Discount getDiscountById(@PathVariable Long id) {
        return discountService.findById(id);
    }

    // Добавление новой скидки
    @PostMapping
    public Discount createDiscount(@RequestBody Discount discount) {
        return discountService.saveDiscount(discount);
    }

    // Обновление скидки
    @PutMapping("/{id}")
    public Discount updateDiscount(@PathVariable Long id, @RequestBody Discount discount) {
        discount.setId(id); // Устанавливаем ID для обновления
        return discountService.saveDiscount(discount);
    }

    // Удаление скидки
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiscount(@PathVariable Long id) {
        discountService.deleteDiscount(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/products/{id}/apply-discount")
    public ResponseEntity<Product> applyDiscount(@PathVariable Long id, @RequestBody Discount discount) {
        Product updatedProduct = productService.applyDiscount(id, discount);
        return ResponseEntity.ok(updatedProduct);
    }

}
