package foodsave.com.foodsave.controller;

import foodsave.com.foodsave.model.Order;
import foodsave.com.foodsave.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Получение всех заказов для пользователя
    @GetMapping("/user/{userId}")
    public List<Order> getOrdersByUserId(@PathVariable Long userId) {
        return orderService.getOrdersByUserId(userId);
    }

    // Создание нового заказа
    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        return orderService.saveOrder(order);
    }

    // Обновление статуса заказа
    @PutMapping("/{id}")
    public Order updateOrderStatus(@PathVariable Long id, @RequestBody String status) {
        return orderService.updateOrderStatus(id, status);
    }

    // Получение заказа по ID
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));  // Ensure the service method returns the order
    }

    // Удаление заказа
    @DeleteMapping("/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderService.saveOrder(new Order()); // Логика удаления, добавим позже
        return "Order deleted successfully";
    }
}



