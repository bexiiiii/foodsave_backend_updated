package foodsave.com.foodsave.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;


@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "app_user")  // Переименовываем таблицу на "app_user" или другое имя
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Имя пользователя не может быть пустым.")
    @Size(min = 3, max = 20, message = "Имя пользователя должно быть от 3 до 20 символов.")
    private String name;
    @Email(message = "Некорректный формат email.")
    private String email;
    @NotBlank(message = "Пароль не может быть пустым.")
    @Size(min = 8, message = "Пароль должен быть хотя бы 8 символов.")
    private String password;
    private String username;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;


    public User(Long userId) {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


}
