package com.project.SecurAuth.controller;

import com.project.SecurAuth.dto.RegisterRequest;
import com.project.SecurAuth.dto.RegisterResponse;
import com.project.SecurAuth.dto.UserRequest;
import com.project.SecurAuth.dto.UserResponse;
import com.project.SecurAuth.service.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur pour la gestion des utilisateurs
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Créer un nouvel utilisateur
     * POST /api/users/register
     * 
     * @param request Les informations de l'utilisateur à créer
     * @return L'utilisateur créé
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> createUser(@Valid @RequestBody RegisterRequest request) {
        log.info("Requête de création d'utilisateur reçue pour l'email: {}", request.getEmail());
        RegisterResponse createdUser = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * Mettre à jour un utilisateur existant
     * PUT /api/users/{id}
     * 
     * @param id L'ID de l'utilisateur à mettre à jour
     * @param request Les nouvelles informations de l'utilisateur (tous les champs sont optionnels)
     * @return L'utilisateur mis à jour (sans mot de passe)
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {
        log.info("Requête de mise à jour d'utilisateur reçue pour l'ID: {}", id);
        UserResponse updatedUser = userService.updateUser(id, request);
        log.info("Utilisateur ID {} mis à jour avec succès", id);
        return ResponseEntity.ok(updatedUser);
    }
}
