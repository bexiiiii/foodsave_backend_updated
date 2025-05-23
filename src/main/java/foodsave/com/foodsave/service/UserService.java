package foodsave.com.foodsave.service;

import foodsave.com.foodsave.config.JwtTokenProvider;
import foodsave.com.foodsave.model.User;
import foodsave.com.foodsave.model.Product;
import foodsave.com.foodsave.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import foodsave.com.foodsave.repository.ProductRepository;
import foodsave.com.foodsave.model.Role;
import foodsave.com.foodsave.repository.RoleRepository;


@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    @Autowired
    private ProductRepository productRepository;


    private final UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User saveUser(User user) {
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        // Set default role if not provided
        if (user.getRole() == null) {
            // Try to find existing ROLE_USER
            Optional<Role> userRole = roleRepository.findByName(Role.ERole.ROLE_USER);
            if (userRole.isPresent()) {
                user.setRole(userRole.get());
            } else {
                // Create new ROLE_USER if it doesn't exist
                Role role = new Role();
                role.setName(Role.ERole.ROLE_USER);
                role = roleRepository.save(role);
                user.setRole(role);
            }
        } else {
            // If role is provided, make sure it exists in the database
            Optional<Role> existingRole = roleRepository.findByName(user.getRole().getName());
            if (existingRole.isPresent()) {
                user.setRole(existingRole.get());
            } else {
                Role savedRole = roleRepository.save(user.getRole());
                user.setRole(savedRole);
            }
        }
        
        // Set username if not provided
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            user.setUsername(user.getEmail().split("@")[0]);
        }
        
        return userRepository.save(user);
    }

    public Optional<User> createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return Optional.empty();
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            return Optional.empty();
        }
        return Optional.of(saveUser(user));
    }

    public Optional<User> updateUser(Long id, User userDetails) {
        return userRepository.findById(id)
            .map(existingUser -> {
                existingUser.setFirstName(userDetails.getFirstName());
                existingUser.setLastName(userDetails.getLastName());
                existingUser.setEmail(userDetails.getEmail());
                existingUser.setUsername(userDetails.getUsername());
                existingUser.setPhone(userDetails.getPhone());
                existingUser.setBio(userDetails.getBio());
                existingUser.setCountry(userDetails.getCountry());
                existingUser.setCity(userDetails.getCity());
                existingUser.setPostalCode(userDetails.getPostalCode());
                existingUser.setTaxId(userDetails.getTaxId());
                existingUser.setFacebookLink(userDetails.getFacebookLink());
                existingUser.setTwitterLink(userDetails.getTwitterLink());
                existingUser.setLinkedinLink(userDetails.getLinkedinLink());
                existingUser.setInstagramLink(userDetails.getInstagramLink());
                
                if (userDetails.getPassword() != null) {
                    existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
                }
                return userRepository.save(existingUser);
            });
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> loginUser(String username, String password) {
        return userRepository.findByUsername(username)
            .filter(user -> passwordEncoder.matches(password, user.getPassword()));
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findByStoreId(Long storeId) {
        return userRepository.findByStoreId(storeId);
    }

    public List<User> findActiveUsers() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return userRepository.findByLastLoginAfter(thirtyDaysAgo);
    }

    public List<User> findNewUsers() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return userRepository.findByCreatedAtAfter(thirtyDaysAgo);
    }

    public Map<String, Object> getUserStats() {
        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("activeUsers", userRepository.countByLastLoginAfter(LocalDateTime.now().minusDays(30)));
        stats.put("newUsers", userRepository.countByCreatedAtAfter(LocalDateTime.now().minusDays(30)));
        return stats;
    }

    @Transactional
    public User update(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(username);
    }

    @Transactional
    public void activateUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setActive(true);
            userRepository.save(user);
        });
    }

    @Transactional
    public void deactivateUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setActive(false);
            userRepository.save(user);
        });
    }

    public List<User> searchUsers(String query) {
        return userRepository.findByFirstNameContainingOrLastNameContainingOrEmailContaining(query, query, query);
    }

    public List<User> filterUsers(Map<String, String> filters) {
        // Implement filtering logic based on the provided filters
        return userRepository.findAll();
    }

    public List<Map<String, Object>> getUserOrders(Long userId) {
        return userRepository.findUserOrders(userId);
    }

    @Transactional
    public void addToFavorites(Long userId, Long productId) {
        User user = userRepository.findById(userId).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();
        user.getFavorites().add(product);
        userRepository.save(user);
    }

    public Set<Product> getUserFavorites(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getFavorites();
    }

    @Transactional
    public void removeFromFavorites(Long userId, Long productId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        user.getFavorites().remove(product);
        userRepository.save(user);
    }


}
