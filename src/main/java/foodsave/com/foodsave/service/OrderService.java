package foodsave.com.foodsave.service;



import foodsave.com.foodsave.model.Order;
import foodsave.com.foodsave.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    // Сохранение нового заказа
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    // Получение всех заказов для пользователя
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    // Поиск заказа по ID
    public Order findById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    // Обновление статуса заказа
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }
    public Order updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setOrderStatus(status); // Use setOrderStatus, which corresponds to the orderStatus field in the entity

        return orderRepository.save(order);
    }


}
