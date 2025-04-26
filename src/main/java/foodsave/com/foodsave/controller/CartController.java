package foodsave.com.foodsave.controller;

import foodsave.com.foodsave.model.Cart;
import foodsave.com.foodsave.model.Product;
import foodsave.com.foodsave.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    @Autowired
    private CartService cartService;

    // Получить корзину пользователя
    @GetMapping("/user/{userId}")
    public ResponseEntity<Cart> getCartForUser(@PathVariable Long userId) {
        Cart cart = cartService.getCartForUser(userId);
        return ResponseEntity.ok(cart);
    }

    // Добавить товар в корзину
    @PostMapping("/user/{userId}/product/{productId}")
    public ResponseEntity<Cart> addToCart(
            @PathVariable Long userId,
            @PathVariable Long productId,
            @RequestParam int quantity) {
        Cart cart = cartService.addToCart(userId, productId, quantity);
        return ResponseEntity.ok(cart);
    }

    // Удалить товар из корзины
    @DeleteMapping("/user/{userId}/product/{productId}")
    public ResponseEntity<Cart> removeFromCart(
            @PathVariable Long userId,
            @PathVariable Long productId) {
        Cart cart = cartService.removeFromCart(userId, productId);
        return ResponseEntity.ok(cart);
    }

    // Очистить корзину
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Cart> clearCart(@PathVariable Long userId) {
        Cart cart = cartService.clearCart(userId);
        return ResponseEntity.ok(cart);
    }
}
