package foodsave.com.foodsave.repository;

import foodsave.com.foodsave.model.Role;
import foodsave.com.foodsave.model.Role.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}