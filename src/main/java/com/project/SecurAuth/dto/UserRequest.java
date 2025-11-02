package com.project.SecurAuth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO pour la requête de mise à jour d'un utilisateur
 * Tous les champs sont optionnels - seuls les champs fournis seront mis à jour
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {
    
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    private String firstName;
    
    @Size(min = 2, max = 50, message = "Le nom de famille doit contenir entre 2 et 50 caractères")
    private String lastName;
    
    @Email(message = "L'email doit être une adresse email valide", 
          regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    @Size(max = 100, message = "L'email ne peut pas dépasser 100 caractères")
    private String email;
    
    private Set<Long> roleIds; // IDs des rôles à attribuer
}

