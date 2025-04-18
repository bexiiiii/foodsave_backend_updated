package foodsave.com.foodsave.service;

import foodsave.com.foodsave.model.User;
import foodsave.com.foodsave.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Сохранение пользователя
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // Поиск пользователя по email
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Поиск пользователя по ID
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    public User updateUser(Long id, User updatedUser) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPassword(updatedUser.getPassword());
        return userRepository.save(existingUser);
    }
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }


}
