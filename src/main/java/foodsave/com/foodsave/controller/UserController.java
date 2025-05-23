package foodsave.com.foodsave.controller;

import foodsave.com.foodsave.config.JwtTokenProvider;
import foodsave.com.foodsave.model.User;
import foodsave.com.foodsave.repository.UserRepository;
import foodsave.com.foodsave.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins =  "http://localhost:3000/")

@RestController
@RequestMapping("/api/users")
@Tag(name = "Пользователи", description = "Операции с пользователями (регистрация, логин)")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "Регистрация нового пользователя")

    // Register new user
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            // Log incoming request
            System.out.println("Received registration request for email: " + user.getEmail());
            System.out.println("Request body: " + user.toString());

            // Validate required fields
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                System.out.println("Email is missing");
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email is required"));
            }
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                System.out.println("Password is missing");
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Password is required"));
            }
            if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
                System.out.println("First name is missing");
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "First name is required"));
            }
            if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
                System.out.println("Last name is missing");
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Last name is required"));
            }

            // Check if user already exists
            if (userService.findByEmail(user.getEmail()).isPresent()) {
                System.out.println("User with email " + user.getEmail() + " already exists");
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "User with this email already exists"));
            }

            // Set default values for optional fields
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                user.setUsername(user.getEmail().split("@")[0]);
                System.out.println("Setting default username: " + user.getUsername());
            }

            // Log user details before saving
            System.out.println("Saving user with details:");
            System.out.println("Email: " + user.getEmail());
            System.out.println("Username: " + user.getUsername());
            System.out.println("First Name: " + user.getFirstName());
            System.out.println("Last Name: " + user.getLastName());
            System.out.println("Role: " + (user.getRole() != null ? user.getRole().getName() : "null"));

            // Save user
            User savedUser = userService.saveUser(user);
            System.out.println("User saved successfully with ID: " + savedUser.getId());

            return ResponseEntity.ok(Map.of(
                "message", "User registered successfully",
                "user", savedUser
            ));
        } catch (Exception e) {
            System.err.println("Error during user registration: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to register user: " + e.getMessage()));
        }
    }

    @Component
    @RequiredArgsConstructor
    public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

        private final JwtTokenProvider jwtTokenProvider;
        private final UserService userService;

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
            DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();

            String email = oauthUser.getAttribute("email");
            String name = oauthUser.getAttribute("name");

            User user = userService.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setUsername(email.split("@")[0]);
                    return userService.saveUser(newUser);
                });

            String token = jwtTokenProvider.generateToken(user.getUsername());
            response.sendRedirect("http://localhost:5173?token=" + token);
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        return userService.createUser(user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.findById(id)
                .map(existingUser -> {
                    user.setId(id);
                    return ResponseEntity.ok(userService.update(user));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> {
                    userService.delete(id);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        return userService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Login user and generate JWT token
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody User user) {
        return userService.loginUser(user.getUsername(), user.getPassword())
                .map(authenticatedUser -> {
                    String token = jwtTokenProvider.generateToken(authenticatedUser.getUsername());
                    Map<String, Object> response = new HashMap<>();
                    response.put("token", token);
                    response.put("user", authenticatedUser);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.status(401).body(Map.of("error", "Invalid username or password")));
    }

    // Log out user (Clear session or token if needed)
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // Implement logout logic (e.g., clearing token or session)
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        return userService.getCurrentUser()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(@RequestBody User user) {
        return userService.getCurrentUser()
                .map(currentUser -> {
                    user.setId(currentUser.getId());
                    return ResponseEntity.ok(userService.update(user));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<User>> getUsersByStore(@PathVariable Long storeId) {
        return ResponseEntity.ok(userService.findByStoreId(storeId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<User>> getActiveUsers() {
        return ResponseEntity.ok(userService.findActiveUsers());
    }

    @GetMapping("/new")
    public ResponseEntity<List<User>> getNewUsers() {
        return ResponseEntity.ok(userService.findNewUsers());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        return ResponseEntity.ok(userService.getUserStats());
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<?> activateUser(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> {
                    userService.activateUser(id);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> {
                    userService.deactivateUser(id);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String query) {
        return ResponseEntity.ok(userService.searchUsers(query));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<User>> filterUsers(@RequestParam Map<String, String> filters) {
        return ResponseEntity.ok(userService.filterUsers(filters));
    }

    @GetMapping("/{id}/orders")
    public ResponseEntity<?> getUserOrders(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(userService.getUserOrders(id)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/favorites")
    public ResponseEntity<?> getUserFavorites(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(userService.getUserFavorites(id)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/favorites/{productId}")
    public ResponseEntity<?> addToFavorites(@PathVariable Long id, @PathVariable Long productId) {
        return userService.findById(id)
                .map(user -> {
                    userService.addToFavorites(id, productId);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/favorites/{productId}")
    public ResponseEntity<?> removeFromFavorites(@PathVariable Long id, @PathVariable Long productId) {
        return userService.findById(id)
                .map(user -> {
                    userService.removeFromFavorites(id, productId);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
