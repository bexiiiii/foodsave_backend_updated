package foodsave.com.foodsave.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

    private String category;
    private String description;
    private String imageUrl;  // Путь к изображению

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(name = "sold_quantity")
    private Integer soldQuantity = 0;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne
    @JoinColumn(name = "discount_id")
    private Discount discount;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "favorites")
    private Set<User> favoritedBy;

    @PrePersist
    protected void onCreate() {
        this.createdAt = java.time.LocalDateTime.now();
        this.updatedAt = java.time.LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = java.time.LocalDateTime.now();
    }

    public boolean isLowStock() {
        return stockQuantity != null && stockQuantity < 10;
    }

    public boolean isOutOfStock() {
        return stockQuantity != null && stockQuantity <= 0;
    }

    public Double getDiscountedPrice() {
        if (discount != null && discount.isActive()) {
            return price * (1 - discount.getPercentage() / 100.0);
        }
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    public Set<User> getFavoritedBy() {
        return favoritedBy;
    }

    public void setFavoritedBy(Set<User> favoritedBy) {
        this.favoritedBy = favoritedBy;
    }
}
