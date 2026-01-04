package mysite.com.real.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mysite.com.real.role.entity.Role;
import mysite.com.real.role.repository.RoleRepository;
import mysite.com.real.user.entity.User;
import mysite.com.real.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Create a new user
    public User createUser(String firstName, String lastName, String email, String password, 
                          String phone, Set<String> roleNames) {
        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        // Assign roles
        Set<Role> roles = new HashSet<>();
        if (roleNames == null || roleNames.isEmpty()) {
            // Default role if none specified
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Default role not found"));
            roles.add(userRole);
        } else {
            for (String roleName : roleNames) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                roles.add(role);
            }
        }

        // Build user with proper field names
        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .name(firstName + " " + lastName) // Combined name
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .phone(phone)
                .isActive(true)
                .roles(roles)
                .build();

        return userRepository.save(user);
    }

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get all active users
    public List<User> getAllActiveUsers() {
        return userRepository.findAll().stream()
                .filter(User::isActive)
                .toList();
    }

    // Get user by ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Get user by email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Update user
    public User updateUser(Long id, String firstName, String lastName, String email, 
                          String phone, Set<String> roleNames) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Build updated fields
        User.UserBuilder userBuilder = User.builder()
                .id(user.getId())
                .passwordHash(user.getPasswordHash()) // Keep existing password
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .roles(user.getRoles()); // Default to existing roles

        // Update first name
        if (firstName != null) {
            userBuilder.firstName(firstName);
        } else {
            userBuilder.firstName(user.getFirstName());
        }

        // Update last name
        if (lastName != null) {
            userBuilder.lastName(lastName);
        } else {
            userBuilder.lastName(user.getLastName());
        }

        // Update combined name if either first or last name changed
        String updatedFirstName = firstName != null ? firstName : user.getFirstName();
        String updatedLastName = lastName != null ? lastName : user.getLastName();
        userBuilder.name(updatedFirstName + " " + updatedLastName);

        // Update email
        if (email != null && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) {
                throw new RuntimeException("Email already exists");
            }
            userBuilder.email(email);
        } else {
            userBuilder.email(user.getEmail());
        }

        // Update phone
        if (phone != null) {
            userBuilder.phone(phone);
        } else {
            userBuilder.phone(user.getPhone());
        }

        // Update roles
        if (roleNames != null && !roleNames.isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (String roleName : roleNames) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                roles.add(role);
            }
            userBuilder.roles(roles);
        }

        return userRepository.save(userBuilder.build());
    }

    // Change password
    public void changePassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        User updatedUser = User.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .name(user.getName())
                .email(user.getEmail())
                .passwordHash(passwordEncoder.encode(newPassword))
                .phone(user.getPhone())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .roles(user.getRoles())
                .build();
        
        userRepository.save(updatedUser);
    }

    // Deactivate user (soft delete)
    public User deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        User updatedUser = User.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .name(user.getName())
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .phone(user.getPhone())
                .isActive(false)
                .createdAt(user.getCreatedAt())
                .roles(user.getRoles())
                .build();
        
        return userRepository.save(updatedUser);
    }

    // Activate user
    public User activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        User updatedUser = User.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .name(user.getName())
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .phone(user.getPhone())
                .isActive(true)
                .createdAt(user.getCreatedAt())
                .roles(user.getRoles())
                .build();
        
        return userRepository.save(updatedUser);
    }

    // Delete user (hard delete)
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    // Check if email exists
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // Assign role to user
    public User assignRoleToUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
        
        Set<Role> updatedRoles = new HashSet<>(user.getRoles());
        updatedRoles.add(role);
        
        User updatedUser = User.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .name(user.getName())
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .phone(user.getPhone())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .roles(updatedRoles)
                .build();
        
        return userRepository.save(updatedUser);
    }

    // Remove role from user
    public User removeRoleFromUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
        
        Set<Role> updatedRoles = new HashSet<>(user.getRoles());
        updatedRoles.remove(role);
        
        User updatedUser = User.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .name(user.getName())
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .phone(user.getPhone())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .roles(updatedRoles)
                .build();
        
        return userRepository.save(updatedUser);
    }
}