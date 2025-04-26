package foodsave.com.foodsave.service;

import foodsave.com.foodsave.model.Cart;
import foodsave.com.foodsave.model.Product;
import foodsave.com.foodsave.model.User;
import foodsave.com.foodsave.repository.CartRepository;
import foodsave.com.foodsave.repository.ProductRepository;
import foodsave.com.foodsave.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    // Получить корзину пользователя
    public Cart getCartForUser(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    // Добавить товар в корзину
    public Cart addToCart(Long userId, Long productId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            cart = new Cart();
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                throw new RuntimeException("User not found");
            }
            cart.setUser(user); // Устанавливаем пользователя для корзины
        }

        // Находим продукт
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            throw new RuntimeException("Product not found");
        }

        // Если корзина не имеет товаров, инициализируем пустой список
        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }

        // Добавляем продукт в корзину
        cart.getItems().add(product);

        // Сохраняем корзину
        return cartRepository.save(cart);
    }

    // Удалить товар из корзины
    public Cart removeFromCart(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart != null) {
            cart.getItems().removeIf(product -> product.getId().equals(productId));
            return cartRepository.save(cart);
        } else {
            throw new RuntimeException("Cart not found");
        }
    }

    // Очистить корзину
    public Cart clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart != null) {
            cart.getItems().clear();
            return cartRepository.save(cart);
        } else {
            throw new RuntimeException("Cart not found");
        }
    }
}
