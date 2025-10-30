package com.project.SecurAuth.entity;


import com.project.SecurAuth.entity.Enum.RoleType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;


@Entity
@Getter
@Data
public class User  {
    @Id
    @GeneratedValue
    private Long id;

    private String firstName;
    private String lastName;

    private String username;
    private String password;
    private String email;
    private boolean enabled;
    private int failedAttempts;


    @Enumerated(EnumType.STRING)
    private RoleType role;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;



}
