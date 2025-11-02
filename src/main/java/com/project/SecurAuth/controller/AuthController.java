package com.project.SecurAuth.controller;

import com.project.SecurAuth.dto.RegisterRequest;
import com.project.SecurAuth.dto.RegisterResponse;
import com.project.SecurAuth.service.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur pour l'authentification et l'enregistrement des utilisateurs
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * Endpoint pour l'enregistrement d'un nouvel utilisateur
     * 
     * @param request Les informations de l'utilisateur à enregistrer
     * @return RegisterResponse avec les informations de l'utilisateur créé
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Tentative d'enregistrement pour l'email: {}", request.getEmail());
        
        RegisterResponse response = userService.createUser(request);
        
        log.info("Utilisateur créé avec succès - ID: {}, Email: {}", response.getId(), response.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
