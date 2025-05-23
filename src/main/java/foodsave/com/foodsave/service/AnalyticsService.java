package foodsave.com.foodsave.service;

import foodsave.com.foodsave.model.Order;
import foodsave.com.foodsave.model.Product;
import foodsave.com.foodsave.model.User;
import foodsave.com.foodsave.repository.OrderRepository;
import foodsave.com.foodsave.repository.ProductRepository;
import foodsave.com.foodsave.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Cacheable(value = "salesAnalytics", key = "#storeId + '-' + #period")
    public Map<String, Object> getSalesAnalytics(Long storeId, String period) {
        LocalDateTime startDate = getStartDate(period);
        List<Order> orders = orderRepository.findByStoreIdAndCreatedAtAfter(storeId, startDate);

        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalSales", calculateTotalSales(orders));
        analytics.put("averageOrderValue", calculateAverageOrderValue(orders));
        analytics.put("salesByDay", getSalesByDay(orders));
        analytics.put("topProducts", getTopProducts(storeId, startDate));
        analytics.put("salesTrend", getSalesTrend(orders));

        return analytics;
    }

    @Cacheable(value = "productAnalytics", key = "#storeId")
    public Map<String, Object> getProductAnalytics(Long storeId) {
        List<Product> products = productRepository.findByStoreId(storeId);

        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalProducts", products.size());
        analytics.put("topSellingProducts", getTopSellingProducts(storeId));
        analytics.put("lowStockProducts", getLowStockProducts(storeId));
        analytics.put("productCategories", getProductCategories(products));

        return analytics;
    }

    @Cacheable(value = "discountAnalytics", key = "#storeId")
    public Map<String, Object> getDiscountAnalytics(Long storeId) {
        List<Order> orders = orderRepository.findByStoreId(storeId);

        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalDiscounts", calculateTotalDiscounts(orders));
        analytics.put("discountEffectiveness", calculateDiscountEffectiveness(orders));
        analytics.put("popularDiscounts", getPopularDiscounts(storeId));
        analytics.put("discountImpact", getDiscountImpact(orders));

        return analytics;
    }

    private Object getPopularDiscounts(Long storeId) {
        return new ArrayList<>();
    }

    @Cacheable(value = "userAnalytics", key = "#storeId")
    public Map<String, Object> getUserAnalytics(Long storeId) {
        List<User> users = userRepository.findByStoreId(storeId);

        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalUsers", users.size());
        analytics.put("activeUsers", getActiveUsers(storeId));
        analytics.put("userSegments", getUserSegments(users));
        analytics.put("userRetention", calculateUserRetention(storeId));

        return analytics;
    }

    @Cacheable(value = "generalAnalytics")
    public Map<String, Object> getGeneralAnalytics() {
        Map<String, Object> analytics = new HashMap<>();

        // Get metrics
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalUsers", userRepository.count());
        metrics.put("activeUsers", getActiveUsers(null));
        metrics.put("totalOrders", orderRepository.count());
        metrics.put("revenue", calculateTotalSales(orderRepository.findAll()));
        analytics.put("metrics", metrics);

        // Get trends
        List<Map<String, Object>> trends = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 6; i >= 0; i--) {
            LocalDateTime date = now.minusDays(i);
            Map<String, Object> trend = new HashMap<>();
            trend.put("date", date.toLocalDate().toString());
            trend.put("value", calculateTotalSales(orderRepository.findByCreatedAtBetween(
                    date.toLocalDate().atStartOfDay(),
                    date.toLocalDate().atTime(23, 59, 59)
            )));
            trends.add(trend);
        }
        analytics.put("trends", trends);

        return analytics;
    }

    @Cacheable(value = "dashboardMetrics")
    public Map<String, Object> getDashboardMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        // Get current metrics
        long currentCustomers = userRepository.count();
        long currentOrders = orderRepository.count();

        // Get previous period metrics (last month)
        LocalDateTime lastMonth = LocalDateTime.now().minusMonths(1);
        long previousCustomers = userRepository.countByCreatedAtBefore(lastMonth);
        long previousOrders = orderRepository.countByCreatedAtBefore(lastMonth);

        // Calculate changes
        double customerChange = previousCustomers > 0
                ? ((double) (currentCustomers - previousCustomers) / previousCustomers) * 100
                : 0;
        double orderChange = previousOrders > 0
                ? ((double) (currentOrders - previousOrders) / previousOrders) * 100
                : 0;

        metrics.put("customers", Map.of(
                "total", currentCustomers,
                "change", customerChange
        ));

        metrics.put("orders", Map.of(
                "total", currentOrders,
                "change", orderChange
        ));

        return metrics;
    }

    @Cacheable(value = "monthlySales", key = "#year")
    public Map<String, Object> getMonthlySales(int year) {
        Map<String, Object> sales = new HashMap<>();
        List<Map<String, Object>> monthlyData = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
            LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);

            List<Order> monthOrders = orderRepository.findByCreatedAtBetween(startDate, endDate);
            double totalSales = calculateTotalSales(monthOrders);

            Map<String, Object> data = new HashMap<>();
            data.put("month", startDate.getMonth().toString());
            data.put("sales", totalSales);
            monthlyData.add(data);

        }

        sales.put("year", year);
        sales.put("data", monthlyData);

        return sales;
    }

    @Cacheable(value = "recentOrders")
    public List<Map<String, Object>> getRecentOrders(int limit) {
        return orderRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, limit))
                .stream()
                .map(order -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("id", order.getId());
                    data.put("customerName", order.getUser().getFullName());
                    data.put("total", order.getTotalAmount());
                    data.put("status", order.getStatus());
                    data.put("date", order.getCreatedAt());
                    return data;
                })
                .collect(Collectors.toList());
    }

    @Cacheable(value = "demographics")
    public Map<String, Object> getDemographics() {
        Map<String, Object> demographics = new HashMap<>();

        // Get user age distribution
        Map<String, Long> ageGroups = userRepository.findAll().stream()
                .collect(groupingBy(
                        user -> {
                            int age = user.getAge();
                            if (age < 18) return "Under 18";
                            if (age < 25) return "18-24";
                            if (age < 35) return "25-34";
                            if (age < 45) return "35-44";
                            if (age < 55) return "45-54";
                            return "55+";
                        },
                        Collectors.counting()
                ));

        // Get user gender distribution
        Map<String, Long> genderDistribution = userRepository.findAll().stream()
                .collect(groupingBy(
                        User::getGender,
                        Collectors.counting()
                ));

        demographics.put("ageGroups", ageGroups);
        demographics.put("genderDistribution", genderDistribution);

        return demographics;
    }

    private LocalDateTime getStartDate(String period) {
        LocalDateTime now = LocalDateTime.now();
        return switch (period) {
            case "week" -> now.minusWeeks(1);
            case "month" -> now.minusMonths(1);
            case "year" -> now.minusYears(1);
            default -> now.minusDays(7);
        };
    }

    private double calculateTotalSales(List<Order> orders) {
        return orders.stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();
    }

    private double calculateAverageOrderValue(List<Order> orders) {
        if (orders.isEmpty()) return 0;
        return calculateTotalSales(orders) / orders.size();
    }

    private Map<String, Double> getSalesByDay(List<Order> orders) {
        return orders.stream()
                .collect(groupingBy(
                        order -> order.getCreatedAt().toLocalDate().toString(),
                        Collectors.summingDouble(Order::getTotalAmount)
                ));
    }

    private List<Map<String, Object>> getTopProducts(Long storeId, LocalDateTime startDate) {
        List<Map<String, Object>> results = orderRepository.findTopProductsByStoreAndDate(storeId, startDate);
        return results.stream()
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("productId", row.get("productId"));
                    map.put("productName", row.get("productName"));
                    map.put("totalSales", row.get("totalQuantity"));
                    return map;
                })
                .collect(Collectors.toList());
    }

    private Map<String, Double> getSalesTrend(List<Order> orders) {
        return orders.stream()
                .collect(groupingBy(
                        order -> order.getCreatedAt().toLocalDate().toString(),
                        Collectors.summingDouble(Order::getTotalAmount)
                ));
    }

    public List<Map<String, Object>> getTopSellingProducts(Long storeId) {
        List<Object[]> results = productRepository.findTopSellingProducts(storeId);
        return results.stream()
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("productId", row[0]);
                    map.put("productName", row[1]);
                    map.put("totalQuantity", row[2]);
                    return map;
                })
                .collect(Collectors.toList());
    }

    public List<Product> getLowStockProducts(Long storeId) {
        return productRepository.findByStoreIdAndStockQuantityLessThan(storeId, 10);
    }

    private Map<String, Long> getProductCategories(List<Product> products) {
        return products.stream()
                .collect(groupingBy(
                        Product::getCategory,
                        Collectors.counting()
                ));
    }

    private double calculateTotalDiscounts(List<Order> orders) {
        return orders.stream()
                .mapToDouble(Order::getDiscountAmount)
                .sum();
    }

    private Map<String, Double> calculateDiscountEffectiveness(List<Order> orders) {
        return orders.stream()
                .collect(groupingBy(
                        Order::getDiscountType,
                        Collectors.averagingDouble(order ->
                                order.getDiscountAmount() / order.getTotalAmount() * 100
                        )
                ));
    }

    public List<Map<String, Object>> getTopProductsByRevenue() {
        List<Object[]> results = productRepository.findTopProductsByRevenue();
        return results.stream()
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("productId", row[0]);
                    map.put("productName", row[1]);
                    map.put("totalRevenue", row[2]);
                    return map;
                })
                .collect(Collectors.toList());
    }

    private Map<String, Double> getDiscountImpact(List<Order> orders) {
        return orders.stream()
                .collect(groupingBy(
                        Order::getDiscountType,
                        Collectors.summingDouble(Order::getDiscountAmount)
                ));
    }

    private long getActiveUsers(Long storeId) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        if (storeId != null) {
            return userRepository.countByStoreIdAndLastLoginAfter(storeId, thirtyDaysAgo);
        } else {
            return userRepository.countByLastLoginAfter(thirtyDaysAgo);
        }
    }

    private Map<String, Long> getUserSegments(List<User> users) {
        return users.stream()
                .collect(Collectors.groupingBy(
                        user -> user.getRole().getName().toString(), // Убедитесь, что getRole() и getName() существуют
                        Collectors.counting()
                ));
    }

    private double calculateUserRetention(Long storeId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyDaysAgo = now.minusDays(30);
        LocalDateTime sixtyDaysAgo = now.minusDays(60);

        long activeUsers = userRepository.countByStoreIdAndLastLoginAfter(storeId, thirtyDaysAgo);
        long totalUsers = userRepository.countByStoreIdAndCreatedAtBefore(storeId, sixtyDaysAgo);

        return totalUsers > 0 ? (double) activeUsers / totalUsers * 100 : 0;
    }

    public Map<String, Double> getStoreRevenue(Long storeId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = orderRepository.findByStoreIdAndCreatedAtBetween(storeId, startDate, endDate);
        
        return orders.stream()
            .collect(groupingBy(
                order -> order.getCreatedAt().toLocalDate().toString(),
                Collectors.summingDouble(Order::getTotalAmount)
            ));
    }

    public Map<String, Double> getStoreDiscounts(Long storeId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = orderRepository.findByStoreIdAndCreatedAtBetween(storeId, startDate, endDate);
        
        return orders.stream()
            .collect(groupingBy(
                order -> order.getCreatedAt().toLocalDate().toString(),
                Collectors.summingDouble(Order::getDiscountAmount)
            ));
    }

    public Map<String, Long> getStoreOrders(Long storeId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = orderRepository.findByStoreIdAndCreatedAtBetween(storeId, startDate, endDate);
        
        return orders.stream()
            .collect(groupingBy(
                order -> order.getCreatedAt().toLocalDate().toString(),
                Collectors.counting()
            ));
    }

    public Map<String, Long> getStoreCustomers(Long storeId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = orderRepository.findByStoreIdAndCreatedAtBetween(storeId, startDate, endDate);
        
        return orders.stream()
            .collect(groupingBy(
                order -> order.getCreatedAt().toLocalDate().toString(),
                Collectors.mapping(
                    Order::getUserId,
                        Collectors.collectingAndThen(Collectors.toSet(), set -> (long) set.size())

                )
            ));
    }

    public Map<String, Long> getStoreActiveUsers(Long storeId, LocalDateTime startDate, LocalDateTime endDate) {
        return userRepository.findByStoreId(storeId).stream()
            .filter(user -> user.getLastLogin() != null && 
                          user.getLastLogin().isAfter(startDate) && 
                          user.getLastLogin().isBefore(endDate))
            .collect(groupingBy(
                user -> user.getLastLogin().toLocalDate().toString(),
                Collectors.counting()
            ));
    }

    public Map<String, Long> getStoreNewUsers(Long storeId, LocalDateTime startDate, LocalDateTime endDate) {
        return userRepository.findByStoreId(storeId).stream()
            .filter(user -> user.getCreatedAt() != null && 
                          user.getCreatedAt().isAfter(startDate) && 
                          user.getCreatedAt().isBefore(endDate))
            .collect(groupingBy(
                user -> user.getCreatedAt().toLocalDate().toString(),
                Collectors.counting()
            ));
    }

    public List<Map<String, Object>> getTopProductsByQuantity() {
        List<Object[]> results = productRepository.findTopProductsByQuantity();
        return results.stream()
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("productId", row[0]);
                    map.put("productName", row[1]);
                    map.put("totalQuantity", row[2]);
                    return map;
                })
                .collect(Collectors.toList());
    }
}