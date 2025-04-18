package foodsave.com.foodsave.controller;

import foodsave.com.foodsave.model.Product;
import foodsave.com.foodsave.model.Store;
import foodsave.com.foodsave.service.ProductService;
import foodsave.com.foodsave.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @Autowired
    private ProductService productService;

    // Получение списка всех заведений
    @GetMapping
    public List<Store> getAllStores() {
        return storeService.getAllStores();
    }

    // Получение заведения по ID
    @GetMapping("/{id}")
    public Store getStoreById(@PathVariable Long id) {
        return storeService.findById(id);
    }

    // Добавление нового заведения
    @PostMapping
    public Store createStore(@RequestBody Store store) {
        return storeService.saveStore(store);
    }

    // Обновление данных заведения
    @PutMapping("/{id}")
    public ResponseEntity<Store> updateStore(@PathVariable Long id, @RequestBody Store updatedStore) {
        return ResponseEntity.ok(storeService.updateStore(id, updatedStore));
    }

    // Удаление заведения
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStore(@PathVariable Long id) {
        storeService.deleteStore(id);
        return ResponseEntity.noContent().build();
    }

    // Обновление продукта в магазине
    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        return ResponseEntity.ok(productService.updateProduct(id, updatedProduct));
    }

    // Удаление продукта из магазина
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // Получение списка всех продуктов магазина
    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }
    @GetMapping("/stores/search")
    public ResponseEntity<List<Store>> searchStores(@RequestParam String query) {
        return ResponseEntity.ok(storeService.searchStores(query));
    }

}
