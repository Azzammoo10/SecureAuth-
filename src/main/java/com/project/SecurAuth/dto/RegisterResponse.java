package com.project.SecurAuth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de réponse pour l'enregistrement d'un utilisateur
 * Ne contient pas d'informations sensibles comme le mot de passe
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private boolean enabled;
    private LocalDateTime createdAt;
    private String message;
}
