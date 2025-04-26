package foodsave.com.foodsave.service;

import foodsave.com.foodsave.config.JwtTokenProvider;
import foodsave.com.foodsave.model.User;
import foodsave.com.foodsave.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Сохранение пользователя с хешированием пароля
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // хешируем пароль
        return userRepository.save(user);
    }
    // UserService.java



    public List<User> findAllUsers() {
        return userRepository.findAll();
    }


    // Поиск пользователя по email
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Поиск пользователя по username
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Аутентификация пользователя
    public User loginUser(String username, String rawPassword) {
        User user = findByUsername(username);

        if (user == null || !passwordEncoder.matches(rawPassword, user.getPassword())) {
            return null;
        }

        return user;
    }

    // Обновление пользователя (с новым паролем — тоже шифруем)
    public User updateUser(Long id, User updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setUsername(updatedUser.getUsername());

        // Шифруем новый пароль перед сохранением
        existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));

        return userRepository.save(existingUser);
    }

    // Удаление пользователя
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }
}
