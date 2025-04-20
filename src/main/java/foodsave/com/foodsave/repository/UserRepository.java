package foodsave.com.foodsave.repository;



import foodsave.com.foodsave.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // Метод для поиска пользователя по email
    User findByEmail(String email);

    User findByUsername(String username);
}


