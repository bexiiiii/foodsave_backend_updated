package foodsave.com.foodsave.controller;

import foodsave.com.foodsave.model.User;
import foodsave.com.foodsave.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Регистрация нового пользователя
    @PostMapping("/register")
    public String registerUser(@Valid @RequestBody User user, BindingResult result) {
        if (result.hasErrors()) {
            return "Ошибка валидации: " + result.getAllErrors();
        }

        // Проверка, существует ли уже пользователь с таким email или username
        if (userService.findByEmail(user.getEmail()) != null) {
            return "Пользователь с таким email уже существует";
        }
        if (userService.findByUsername(user.getUsername()) != null) {
            return "Пользователь с таким username уже существует";
        }

        userService.saveUser(user); // Сохраняем нового пользователя
        return "Пользователь зарегистрирован успешно.";
    }

    // Логин пользователя
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User user) {
        String token = userService.loginUser(user.getUsername(), user.getPassword());

        if (token == null) {
            return ResponseEntity.status(401).body("Неверный username или пароль");
        }

        return ResponseEntity.ok("Успешный логин. Токен: " + token);
    }

    // Выход из системы
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // Логика для выхода
        return ResponseEntity.noContent().build();
    }

    // Получение пользователя по ID
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            throw new RuntimeException("Пользователь не найден");
        }
        return user;
    }

    // Обновление данных пользователя
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        User user = userService.updateUser(id, updatedUser);
        return ResponseEntity.ok(user);
    }

    // Удаление пользователя
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
