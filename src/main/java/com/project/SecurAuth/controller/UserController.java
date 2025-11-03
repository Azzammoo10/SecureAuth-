package com.project.SecurAuth.controller;

import com.project.SecurAuth.Exception.UserNotFoundException;
import com.project.SecurAuth.dto.PagedResponse;
import com.project.SecurAuth.dto.RegisterRequest;
import com.project.SecurAuth.dto.RegisterResponse;
import com.project.SecurAuth.dto.UserRequest;
import com.project.SecurAuth.dto.UserResponse;
import com.project.SecurAuth.dto.UserSearchRequest;
import com.project.SecurAuth.entity.User;
import com.project.SecurAuth.service.interfaces.UserService;

import java.util.stream.Collectors;
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

    /**
     * Récupère tous les utilisateurs avec pagination et filtres
     * GET /api/users
     * 
     * Paramètres de requête optionnels:
     * - email: Filtre par email (recherche partielle, insensible à la casse)
     * - enabled: Filtre par statut (true=actif, false=bloqué)
     * - roleId: Filtre par ID de rôle
     * - roleName: Filtre par nom de rôle (UTILISATEUR, ADMIN, etc.)
     * - page: Numéro de page (défaut: 0)
     * - size: Taille de la page (défaut: 20, max: 100)
     * - sortBy: Champ de tri (défaut: "id")
     * - sortDirection: Direction du tri "asc" ou "desc" (défaut: "asc")
     * 
     * @param searchRequest Les paramètres de recherche et filtres
     * @return Liste paginée d'utilisateurs
     */
    @GetMapping
    public ResponseEntity<PagedResponse<UserResponse>> getAllUsers(
            @ModelAttribute UserSearchRequest searchRequest) {
        log.info("Requête de récupération des utilisateurs avec filtres: {}", searchRequest);
        PagedResponse<UserResponse> users = userService.getAllUsers(searchRequest);
        log.info("Récupération réussie: {} utilisateurs (page {}/{})", 
                users.getContent().size(), users.getPage() + 1, users.getTotalPages());
        return ResponseEntity.ok(users);
    }

    /**
     * Supprime physiquement un utilisateur
     * DELETE /api/users/{id}
     * 
     * Cette opération effectue une suppression physique (hard delete) qui supprime
     * définitivement l'utilisateur de la base de données.
     * Les relations avec les rôles sont automatiquement supprimées.
     * L'opération est journalisée dans l'audit avant la suppression.
     * 
     * @param id L'ID de l'utilisateur à supprimer
     * @return Réponse 204 No Content en cas de succès
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Requête de suppression physique d'utilisateur reçue pour l'ID: {}", id);
        userService.deleteUser(id);
        log.info("Utilisateur ID {} supprimé définitivement avec succès", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Active un utilisateur par email
     * PUT /api/users/enable
     * 
     * @param email L'email de l'utilisateur à activer
     * @return Réponse 200 OK avec message de succès
     */
    @PutMapping("/enable")
    public ResponseEntity<String> enableUser(@RequestParam String email) {
        log.info("Requête d'activation d'utilisateur reçue pour l'email: {}", email);
        userService.enableUser(email);
        return ResponseEntity.ok("Utilisateur avec l'email " + email + " activé avec succès");
    }

    /**
     * Désactive un utilisateur par email
     * PUT /api/users/disable
     * 
     * @param email L'email de l'utilisateur à désactiver
     * @return Réponse 200 OK avec message de succès
     */
    @PutMapping("/disable")
    public ResponseEntity<String> disableUser(@RequestParam String email) {
        log.info("Requête de désactivation d'utilisateur reçue pour l'email: {}", email);
        userService.disableUser(email);
        return ResponseEntity.ok("Utilisateur avec l'email " + email + " désactivé avec succès");
    }

    /**
     * Bloque un utilisateur par ID
     * PUT /api/users/{id}/lock
     * 
     * @param id L'ID de l'utilisateur à bloquer
     * @return Réponse 200 OK avec message de succès
     */
    @PutMapping("/{id}/lock")
    public ResponseEntity<String> lockUser(@PathVariable Long id) {
        log.info("Requête de blocage d'utilisateur reçue pour l'ID: {}", id);
        User user = userService.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID : " + id));
        userService.lockUser(user);
        return ResponseEntity.ok("Utilisateur ID " + id + " bloqué avec succès");
    }

    /**
     * Débloque un utilisateur par ID
     * PUT /api/users/{id}/unlock
     * 
     * @param id L'ID de l'utilisateur à débloquer
     * @return Réponse 200 OK avec message de succès
     */
    @PutMapping("/{id}/unlock")
    public ResponseEntity<String> unlockUser(@PathVariable Long id) {
        log.info("Requête de déblocage d'utilisateur reçue pour l'ID: {}", id);
        User user = userService.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID : " + id));
        userService.unlockUser(user);
        return ResponseEntity.ok("Utilisateur ID " + id + " débloqué avec succès");
    }

    /**
     * Réinitialise les tentatives échouées d'un utilisateur par ID
     * PUT /api/users/{id}/reset-attempts
     * 
     * @param id L'ID de l'utilisateur
     * @return Réponse 200 OK avec message de succès
     */
    @PutMapping("/{id}/reset-attempts")
    public ResponseEntity<String> resetFailedAttempts(@PathVariable Long id) {
        log.info("Requête de réinitialisation des tentatives échouées reçue pour l'ID: {}", id);
        User user = userService.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID : " + id));
        userService.resetFailedAttempts(user);
        return ResponseEntity.ok("Tentatives échouées réinitialisées pour l'utilisateur ID " + id);
    }

    /**
     * Incrémente les tentatives échouées d'un utilisateur par ID (pour les tests)
     * PUT /api/users/{id}/increment-attempts
     * 
     * @param id L'ID de l'utilisateur
     * @return Réponse 200 OK avec message de succès
     */
    @PutMapping("/{id}/increment-attempts")
    public ResponseEntity<String> incrementFailedAttempts(@PathVariable Long id) {
        log.info("Requête d'incrémentation des tentatives échouées reçue pour l'ID: {}", id);
        User user = userService.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID : " + id));
        userService.incrementFailedAttempts(user);
        User updatedUser = userService.getUserById(id).orElse(user);
        String message = String.format(
            "Tentative échouée incrémentée pour l'utilisateur ID %d. Tentatives actuelles: %d. Statut: %s",
            id,
            updatedUser.getFailedAttempts(),
            updatedUser.isEnabled() ? "actif" : "bloqué"
        );
        return ResponseEntity.ok(message);
    }

    /**
     * Récupère un utilisateur par ID
     * GET /api/users/{id}
     * 
     * @param id L'ID de l'utilisateur à récupérer
     * @return UserResponse avec les informations de l'utilisateur
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        log.info("Requête de récupération d'utilisateur reçue pour l'ID: {}", id);
        User user = userService.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID : " + id));
        
        // Convertir User en UserResponse
        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .username(user.getUsername())
                .enabled(user.isEnabled())
                .failedAttempts(user.getFailedAttempts())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
        
        return ResponseEntity.ok(response);
    }
}
