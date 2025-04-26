package foodsave.com.foodsave.controller;

import foodsave.com.foodsave.config.JwtTokenProvider;
import foodsave.com.foodsave.model.User;
import foodsave.com.foodsave.repository.UserRepository;
import foodsave.com.foodsave.service.UserService;
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
import java.util.Map;
@CrossOrigin(origins = "http://localhost:9527/")


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // Register new user
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        if (userService.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.badRequest().body("User with this email already exists");
        }
        if (userService.findByUsername(user.getUsername()) != null) {
            return ResponseEntity.badRequest().body("User with this username already exists");
        }

        userService.saveUser(user); // Save new user
        return ResponseEntity.ok("User registered successfully.");
    }
    @Component
    @RequiredArgsConstructor
    public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

        private final JwtTokenProvider jwtTokenProvider;
        private final UserRepository userRepository;

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
            DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();

            String email = oauthUser.getAttribute("email");
            String name = oauthUser.getAttribute("name");

            // check or create user in DB
            User user = userRepository.findByEmail(email);
            if (user == null) {
                user = new User();
                user.setEmail(email);
                user.setName(name);
                user.setUsername(email.split("@")[0]);
                userRepository.save(user);
            }

            // generate JWT
            String token = jwtTokenProvider.generateToken(user.getUsername());

            // redirect or send response
            response.sendRedirect("http://localhost:5173?token=" + token);
        }
    }
    // Get all users
    @GetMapping("")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }



    // Login user and generate JWT token
    // UserController.java
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        // Get the authenticated user
        User authenticatedUser = userService.loginUser(user.getUsername(), user.getPassword());

        if (authenticatedUser == null) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(authenticatedUser.getUsername());

        // Create response with token and user info
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", authenticatedUser);  // Add the user information in the response

        return ResponseEntity.ok(response); // Return token and user info
    }


    // Log out user (Clear session or token if needed)
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // Implement logout logic (e.g., clearing token or session)
        return ResponseEntity.noContent().build();
    }

    // Get user by ID (example)
//    @GetMapping("/{id}")
//    public ResponseEntity<User> getUser(@PathVariable Long id) {
//        User user = userService.findById(id);
//        if (user == null) {
//            return ResponseEntity.status(404).body(null); // Not found
//        }
//        return ResponseEntity.ok(user); // Found
//    }

    // Update user details
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        User user = userService.updateUser(id, updatedUser);
        return ResponseEntity.ok(user); // Return updated user
    }

    // Delete user
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id); // Delete user by ID
        return ResponseEntity.noContent().build(); // No content response
    }
}
