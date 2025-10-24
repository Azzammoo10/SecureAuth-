package com.project.SecurAuth.entity;


import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // ADMIN, MANAGER, USER

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;
}
