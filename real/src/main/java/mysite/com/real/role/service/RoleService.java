package mysite.com.real.role.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mysite.com.real.role.entity.Role;
import mysite.com.real.role.repository.RoleRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    // Create a new role
    public Role createRole(String name) {
        // Check if role already exists
        if (roleRepository.existsByName(name)) {
            throw new RuntimeException("Role already exists: " + name);
        }

        Role role = new Role();
        role.setName(name);
        
        return roleRepository.save(role);
    }

    // Get all roles
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    // Get role by ID
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    // Get role by name
    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    // Update role name
    public Role updateRole(Long id, String newName) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Check if new name already exists
        if (!role.getName().equals(newName) && roleRepository.existsByName(newName)) {
            throw new RuntimeException("Role name already exists: " + newName);
        }

        role.setName(newName);
        return roleRepository.save(role);
    }

    // Delete role
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Check if role is assigned to any users
        if (!role.getUsers().isEmpty()) {
            throw new RuntimeException("Cannot delete role. It is assigned to " + 
                                     role.getUsers().size() + " user(s)");
        }

        roleRepository.deleteById(id);
    }

    // Delete role by name
    public void deleteRoleByName(String name) {
        Role role = roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Role not found: " + name));

        // Check if role is assigned to any users
        if (!role.getUsers().isEmpty()) {
            throw new RuntimeException("Cannot delete role. It is assigned to " + 
                                     role.getUsers().size() + " user(s)");
        }

        roleRepository.delete(role);
    }

    // Check if role exists by name
    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }

    // Get count of users with this role
    public int getUserCountForRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        
        return role.getUsers().size();
    }

    // Get count of users with this role by name
    public int getUserCountForRole(String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
        
        return role.getUsers().size();
    }

    // Initialize default roles (useful for app startup)
    public void initializeDefaultRoles() {
        String[] defaultRoles = {"ROLE_USER", "ROLE_ADMIN", "ROLE_MODERATOR"};
        
        for (String roleName : defaultRoles) {
            if (!roleRepository.existsByName(roleName)) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
            }
        }
    }
}