package com.project.SecurAuth.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import java.util.Set;


@Entity
@Getter
@Data
public class User  {
    @Id
    @GeneratedValue
    private Long id;

    private String username;
    private String password;
    private String email;
    private boolean enabled;
    private int failedAttempts;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    private String CreatedAt;
    private String UpdatedAt;



}
