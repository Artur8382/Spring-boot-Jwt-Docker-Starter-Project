package mysite.com.real.role.entity;

import jakarta.persistence.*;
import lombok.*;
import mysite.com.real.user.entity.User;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String name; // ROLE_USER, ROLE_ADMIN, ROLE_MODERATOR, etc.
    
    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

}