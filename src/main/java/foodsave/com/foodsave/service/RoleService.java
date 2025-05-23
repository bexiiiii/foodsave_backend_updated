package foodsave.com.foodsave.service;

import foodsave.com.foodsave.model.Role;
import foodsave.com.foodsave.model.Role.ERole;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    List<Role> getAllRoles();
    Optional<Role> getRoleById(Long id);
    Optional<Role> getRoleByName(ERole name);
    Role createRole(Role role);
    Role updateRole(Long id, Role role);
    void deleteRole(Long id);
    void initializeRoles();
} 