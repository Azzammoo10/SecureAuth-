package com.project.SecurAuth.dto;


import com.project.SecurAuth.validation.StrongPassword;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "Ce champ est obligatoire")
    private String firstName;

    @NotBlank(message = "Ce champ est obligatoire")
    private String lastName;

    @StrongPassword
    private String password;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Email invalide")
    private String email;
}
