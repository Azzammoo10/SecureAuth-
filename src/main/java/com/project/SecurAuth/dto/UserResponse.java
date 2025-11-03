package com.project.SecurAuth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DTO de réponse pour les informations d'un utilisateur
 * Ne contient pas d'informations sensibles comme le mot de passe
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private boolean enabled;
    private int failedAttempts;
    private Set<String> roles; // Noms des rôles
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

