package com.project.SecurAuth.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 80)
    private String name; // Exemple : "USER_CREATE", "AUDIT_VIEW", "ROLE_ASSIGN"

    @Column(length = 255)
    private String description; // Explication de la permission

}
