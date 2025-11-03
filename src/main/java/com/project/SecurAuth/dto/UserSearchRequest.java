package com.project.SecurAuth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour les paramètres de recherche et filtres des utilisateurs
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchRequest {
    private String email;           // Filtre par email (contient)
    private Boolean enabled;         // Filtre par statut (true=actif, false=bloqué, null=tous)
    private Long roleId;            // Filtre par rôle spécifique
    private String roleName;         // Filtre par nom de rôle
    private Integer page;            // Numéro de page (défaut: 0)
    private Integer size;            // Taille de la page (défaut: 20)
    private String sortBy;          // Champ de tri (défaut: "id")
    private String sortDirection;    // Direction du tri: "asc" ou "desc" (défaut: "asc")
}

