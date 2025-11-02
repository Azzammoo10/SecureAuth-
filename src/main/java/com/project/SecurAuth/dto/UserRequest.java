package com.project.SecurAuth.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {
    
    private String firstName;
    
    private String lastName;
    
    @Email(message = "Email invalide")
    private String email;
    
    private Set<Long> roleIds; // IDs des rôles à attribuer
}

