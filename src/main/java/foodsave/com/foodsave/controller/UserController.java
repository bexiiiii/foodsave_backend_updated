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
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> request) {
        try {
            log.info("Received registration request for email: {}", request.get("email"));

            // Validate required fields
            if (request.get("email") == null || request.get("email").trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
            }
            if (request.get("password") == null || request.get("password").trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Password is required"));
            }
            if (request.get("firstName") == null || request.get("firstName").trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "First name is required"));
            }
            if (request.get("lastName") == null || request.get("lastName").trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Last name is required"));
            }

            // Check if user already exists
            if (userService.findByEmail(request.get("email")).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email is already registered"));
            }

            // Create new user
            User user = new User();
            user.setEmail(request.get("email").trim());
            user.setPassword(request.get("password"));
            user.setFirstName(request.get("firstName").trim());
            user.setLastName(request.get("lastName").trim());
            user.setUsername(request.get("username") != null ? request.get("username").trim() : request.get("email").split("@")[0]);
            user.setStatus(User.EStatus.ACTIVE);

            // Handle role
            String roleName = request.get("roleName");
            if (roleName == null) {
                roleName = "ROLE_USER";
            }
            Role role = roleService.findByName(Role.ERole.valueOf(roleName))
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(Role.ERole.valueOf(roleName));
                    return roleService.save(newRole);
                });
            user.setRole(role);

            log.info("Saving user with email: {}", user.getEmail());
            User savedUser = userService.saveUser(user);
            log.info("User registered successfully with ID: {}", savedUser.getId());

            return ResponseEntity.ok(Map.of(
                "message", "User registered successfully",
                "user", Map.of(
                    "id", savedUser.getId(),
                    "email", savedUser.getEmail(),
                    "firstName", savedUser.getFirstName(),
                    "lastName", savedUser.getLastName(),
                    "username", savedUser.getUsername(),
                    "role", savedUser.getRole().getName().toString()
                )
            ));
        } catch (Exception e) {
            log.error("Error registering user: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
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
