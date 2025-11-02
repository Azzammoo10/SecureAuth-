package com.project.SecurAuth.service.impl;

import com.project.SecurAuth.Exception.EmailAlreadyExistsException;
import com.project.SecurAuth.Exception.InvalidEmailException;
import com.project.SecurAuth.Exception.UserNotFoundException;
import com.project.SecurAuth.Exception.WeakPasswordException;
import com.project.SecurAuth.dto.PagedResponse;
import com.project.SecurAuth.dto.RegisterRequest;
import com.project.SecurAuth.dto.RegisterResponse;
import com.project.SecurAuth.dto.UserRequest;
import com.project.SecurAuth.dto.UserResponse;
import com.project.SecurAuth.dto.UserSearchRequest;
import com.project.SecurAuth.entity.Enum.RoleType;
import com.project.SecurAuth.entity.Enum.SeverityLevel;
import com.project.SecurAuth.entity.Role;
import com.project.SecurAuth.entity.User;
import com.project.SecurAuth.repository.RoleRepository;
import com.project.SecurAuth.repository.UserRepository;
import com.project.SecurAuth.service.interfaces.AuditService;
import com.project.SecurAuth.service.interfaces.UserService;
import com.project.SecurAuth.validation.StrongPasswordValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final StrongPasswordValidator strongPasswordValidator;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;
    private final RoleRepository roleRepository;

    private static final int MAX_USERNAME_GENERATION_ATTEMPTS = 10;

    /**
     * Crée un nouvel utilisateur avec toutes les validations nécessaires
     * 
     * @param request Les informations de l'utilisateur à créer
     * @return RegisterResponse contenant les informations de l'utilisateur créé (sans mot de passe)
     * @throws EmailAlreadyExistsException si l'email existe déjà
     * @throws WeakPasswordException si le mot de passe ne respecte pas les critères
     * @throws RuntimeException si le rôle utilisateur n'est pas trouvé
     */
    @Override
    @Transactional
    public RegisterResponse createUser(RegisterRequest request) {
        log.debug("Début de la création de l'utilisateur avec l'email: {}", request.getEmail());
        
        // Validation supplémentaire de l'email (format strict)
        String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";
        if (email.isEmpty() || !email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            log.warn("Tentative d'enregistrement avec un email invalide: {}", request.getEmail());
            throw new InvalidEmailException(
                "L'email doit être une adresse email valide au format: nom@domaine.extension"
            );
        }
        
        // Vérification CRITIQUE de l'unicité de l'email AVANT toute création
        // Cette vérification empêche la création d'un utilisateur avec un email existant
        if (userRepository.existsByEmail(email)) {
            log.warn("ÉCHEC: Tentative d'enregistrement avec un email déjà existant: {}. L'utilisateur ne sera PAS créé.", email);
            throw new EmailAlreadyExistsException(
                "Cet email est déjà utilisé dans notre système. Veuillez utiliser un autre email ou vous connecter avec cet email."
            );
        }
        
        log.debug("Email vérifié et unique: {}", email);

        // Validation du mot de passe
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            log.warn("Tentative d'enregistrement avec un mot de passe vide");
            throw new WeakPasswordException(
                "Le mot de passe est obligatoire."
            );
        }

        if (!strongPasswordValidator.isValid(request.getPassword(), null)) {
            log.warn("Tentative d'enregistrement avec un mot de passe faible pour l'email: {}", request.getEmail());
            throw new WeakPasswordException(
                "Le mot de passe est trop faible. Il doit contenir au moins 10 caractères, " +
                "une majuscule, une minuscule, un chiffre et un caractère spécial (@$!%*?&)."
            );
        }

        // Vérification que le rôle utilisateur existe
        Role userRole = roleRepository.findByName(RoleType.UTILISATEUR)
                .orElseThrow(() -> {
                    log.error("Le rôle UTILISATEUR n'existe pas dans la base de données");
                    return new RuntimeException(
                        "Erreur de configuration: Le rôle utilisateur n'a pas été trouvé. " +
                        "Veuillez contacter l'administrateur."
                    );
                });

        // Génération d'un nom d'utilisateur unique
        String username = generateUniqueUsername(request.getFirstName(), request.getLastName());

        // Création de l'utilisateur
        User userCreate = new User();
        userCreate.setFirstName(request.getFirstName().trim());
        userCreate.setLastName(request.getLastName().trim());
        userCreate.setEmail(email);
        userCreate.setPassword(passwordEncoder.encode(request.getPassword()));
        userCreate.setUsername(username);
        userCreate.setEnabled(false); // L'utilisateur doit être activé après vérification email
        userCreate.setFailedAttempts(0);
        
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        userCreate.setRoles(roles);

        // Les timestamps sont gérés automatiquement par @CreationTimestamp et @UpdateTimestamp
        // Mais on les définit explicitement pour être sûr
        userCreate.setCreatedAt(LocalDateTime.now());
        userCreate.setUpdatedAt(LocalDateTime.now());

        // Sauvegarde de l'utilisateur
        User savedUser = userRepository.save(userCreate);
        log.info("Utilisateur créé avec succès - ID: {}, Username: {}, Email: {}", 
                savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());

        // Journalisation de l'événement d'audit
        try {
            auditService.logEvent(
                savedUser, 
                "USER_CREATED", 
                String.format("Nouvel utilisateur créé: %s %s (%s)", 
                    savedUser.getFirstName(), 
                    savedUser.getLastName(), 
                    savedUser.getEmail()),
                SeverityLevel.INFO
            );
        } catch (Exception e) {
            log.error("Erreur lors de la journalisation de l'événement d'audit pour l'utilisateur: {}", 
                    savedUser.getId(), e);
            // On continue même si l'audit échoue pour ne pas bloquer la création de l'utilisateur
        }

        // Création de la réponse sans informations sensibles
        RegisterResponse response = RegisterResponse.builder()
                .id(savedUser.getId())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .email(savedUser.getEmail())
                .username(savedUser.getUsername())
                .enabled(savedUser.isEnabled())
                .createdAt(savedUser.getCreatedAt())
                .message("Compte créé avec succès. Veuillez vérifier votre email pour activer votre compte.")
                .build();

        log.debug("Création de l'utilisateur terminée avec succès pour l'ID: {}", savedUser.getId());
        return response;
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        if(username == null || username.isBlank()) return Optional.empty();

        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        if(email == null || email.isBlank()) return Optional.empty();

        return userRepository.findByEmail(email);
    }

    @Override
    public void enableUser(String email) {
        Optional<User> user = getUserByEmail(email);
        if(user.isPresent()){
            user.get().setEnabled(true);
            userRepository.save(user.get());
        }
    }

    @Override
    public void incrementFailedAttempts(User user) {

    }

    @Override
    public void resetFailedAttempts(User user) {

    }

    @Override
    public void lockUser(User user) {

    }

    @Override
    public User save(User user) {
        return null;
    }

    /**
     * Met à jour un utilisateur existant
     * 
     * @param id L'ID de l'utilisateur à mettre à jour
     * @param request Les nouvelles informations (tous les champs sont optionnels)
     * @return UserResponse avec les informations mises à jour
     * @throws RuntimeException si l'utilisateur ou un rôle n'est pas trouvé
     * @throws EmailAlreadyExistsException si le nouvel email est déjà utilisé
     * @throws InvalidEmailException si le format de l'email est invalide
     */
    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        log.debug("Début de la mise à jour de l'utilisateur avec l'ID: {}", id);
        
        // Récupérer l'utilisateur existant
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Tentative de mise à jour d'un utilisateur inexistant - ID: {}", id);
                    return new UserNotFoundException("Utilisateur non trouvé avec l'ID : " + id);
                });

        List<String> changes = new ArrayList<>();
        boolean hasChanges = false;
        
        // Vérifier et mettre à jour le prénom
        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            String newFirstName = request.getFirstName().trim();
            if (!newFirstName.equals(user.getFirstName())) {
                String oldFirstName = user.getFirstName() != null ? user.getFirstName() : "";
                changes.add("Prénom: '" + oldFirstName + "' -> '" + newFirstName + "'");
                user.setFirstName(newFirstName);
                hasChanges = true;
                log.debug("Mise à jour du prénom pour l'utilisateur ID {}: {} -> {}", 
                        id, oldFirstName, newFirstName);
            }
        }

        // Vérifier et mettre à jour le nom
        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            String newLastName = request.getLastName().trim();
            if (!newLastName.equals(user.getLastName())) {
                String oldLastName = user.getLastName() != null ? user.getLastName() : "";
                changes.add("Nom: '" + oldLastName + "' -> '" + newLastName + "'");
                user.setLastName(newLastName);
                hasChanges = true;
                log.debug("Mise à jour du nom pour l'utilisateur ID {}: {} -> {}", 
                        id, oldLastName, newLastName);
            }
        }

        // Vérifier et mettre à jour l'email (avec validation et vérification d'unicité)
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String newEmail = request.getEmail().trim().toLowerCase();
            
            // Validation du format de l'email
            if (!newEmail.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                log.warn("Tentative de mise à jour avec un email invalide pour l'utilisateur ID {}: {}", 
                        id, request.getEmail());
                throw new InvalidEmailException(
                    "L'email doit être une adresse email valide au format: nom@domaine.extension"
                );
            }
            
            String currentEmail = user.getEmail() != null ? user.getEmail().toLowerCase() : "";
            if (!newEmail.equals(currentEmail)) {
                // Vérification de l'unicité de l'email (sauf si c'est le même utilisateur)
                if (userRepository.existsByEmail(newEmail)) {
                    // Vérifier si c'est le même utilisateur qui essaie de garder son email
                    Optional<User> existingUser = userRepository.findByEmail(newEmail);
                    if (existingUser.isEmpty() || !existingUser.get().getId().equals(id)) {
                        log.warn("Tentative de mise à jour avec un email déjà utilisé pour l'utilisateur ID {}: {}", 
                                id, newEmail);
                        throw new EmailAlreadyExistsException(
                            "Cet email est déjà utilisé par un autre utilisateur. Veuillez utiliser un autre email."
                        );
                    }
                }
                
                changes.add("Email: '" + currentEmail + "' -> '" + newEmail + "'");
                user.setEmail(newEmail);
                hasChanges = true;
                log.debug("Mise à jour de l'email pour l'utilisateur ID {}: {} -> {}", 
                        id, currentEmail, newEmail);
            }
        }

        // Vérifier et mettre à jour le statut enabled (actif/bloqué)
        if (request.getEnabled() != null) {
            boolean newEnabled = request.getEnabled();
            if (newEnabled != user.isEnabled()) {
                String oldStatus = user.isEnabled() ? "actif" : "bloqué";
                String newStatus = newEnabled ? "actif" : "bloqué";
                changes.add("Statut: '" + oldStatus + "' -> '" + newStatus + "'");
                user.setEnabled(newEnabled);
                hasChanges = true;
                log.debug("Mise à jour du statut pour l'utilisateur ID {}: {} -> {}", 
                        id, oldStatus, newStatus);
                
                // Si l'utilisateur est activé, réinitialiser les tentatives échouées
                if (newEnabled && user.getFailedAttempts() > 0) {
                    user.setFailedAttempts(0);
                    log.debug("Réinitialisation des tentatives échouées pour l'utilisateur ID {}", id);
                }
            }
        }

        // Vérifier et mettre à jour les rôles
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            // Vérifier que tous les rôles existent
            List<Role> newRoles = new ArrayList<>();
            for (Long roleId : request.getRoleIds()) {
                Role role = roleRepository.findById(roleId)
                        .orElseThrow(() -> {
                            log.error("Tentative d'attribution d'un rôle inexistant - ID: {}", roleId);
                            return new RuntimeException("Rôle non trouvé avec l'ID : " + roleId);
                        });
                newRoles.add(role);
            }

            // Créer une description des changements de rôles
            String oldRoles = user.getRoles().stream()
                    .map(r -> r.getName().name())
                    .sorted()
                    .collect(Collectors.joining(", "));
            String newRolesString = newRoles.stream()
                    .map(r -> r.getName().name())
                    .sorted()
                    .collect(Collectors.joining(", "));
            
            if (!oldRoles.equals(newRolesString)) {
                changes.add("Rôles: [" + oldRoles + "] -> [" + newRolesString + "]");
                user.setRoles(new HashSet<>(newRoles));
                hasChanges = true;
                log.debug("Mise à jour des rôles pour l'utilisateur ID {}: [{}] -> [{}]", 
                        id, oldRoles, newRolesString);
            }
        }

        // Si aucune modification n'a été apportée
        if (!hasChanges) {
            log.info("Aucune modification détectée pour l'utilisateur ID {}", id);
            return buildUserResponse(user);
        }

        // Mettre à jour le timestamp
        user.setUpdatedAt(LocalDateTime.now());
        
        // Sauvegarder les modifications
        User updatedUser = userRepository.save(user);
        log.info("Utilisateur ID {} mis à jour avec succès. Modifications: {}", 
                id, String.join("; ", changes));

        // Journaliser les modifications dans l'audit
        try {
            String changeDescription = String.join("; ", changes);
            auditService.logEvent(
                updatedUser, 
                "USER_UPDATED", 
                String.format("Utilisateur %s (%s) modifié - Modifications: %s", 
                    updatedUser.getUsername(),
                    updatedUser.getEmail(),
                    changeDescription),
                SeverityLevel.INFO
            );
            log.debug("Événement d'audit enregistré pour la mise à jour de l'utilisateur ID {}", id);
        } catch (Exception e) {
            log.error("Erreur lors de la journalisation de l'événement d'audit pour l'utilisateur ID: {}", 
                    id, e);
            // On continue même si l'audit échoue pour ne pas bloquer la mise à jour
        }

        return buildUserResponse(updatedUser);
    }

    /**
     * Construit un UserResponse à partir d'un User
     */
    private UserResponse buildUserResponse(User user) {
        return UserResponse.builder()
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
    }

    /**
     * Récupère tous les utilisateurs avec pagination et filtres
     * 
     * @param searchRequest Les paramètres de recherche et filtres
     * @return PagedResponse contenant la liste paginée d'utilisateurs
     */
    @Override
    public PagedResponse<UserResponse> getAllUsers(UserSearchRequest searchRequest) {
        log.debug("Récupération des utilisateurs avec filtres: enabled={}, email={}, roleId={}, roleName={}", 
                searchRequest.getEnabled(), searchRequest.getEmail(), 
                searchRequest.getRoleId(), searchRequest.getRoleName());
        
        // Valeurs par défaut pour la pagination
        int page = searchRequest.getPage() != null && searchRequest.getPage() >= 0 ? searchRequest.getPage() : 0;
        int size = searchRequest.getSize() != null && searchRequest.getSize() > 0 ? searchRequest.getSize() : 20;
        
        // Limiter la taille maximale de la page
        if (size > 100) {
            size = 100;
            log.warn("Taille de page limitée à 100 (demandée: {})", searchRequest.getSize());
        }
        
        // Configuration du tri
        String sortBy = searchRequest.getSortBy() != null && !searchRequest.getSortBy().isBlank() 
                ? searchRequest.getSortBy() : "id";
        String sortDirection = "desc".equalsIgnoreCase(searchRequest.getSortDirection()) ? "desc" : "asc";
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<User> userPage;
        
        // Application des filtres selon les paramètres fournis
        boolean hasEmailFilter = searchRequest.getEmail() != null && !searchRequest.getEmail().isBlank();
        boolean hasEnabledFilter = searchRequest.getEnabled() != null;
        boolean hasRoleIdFilter = searchRequest.getRoleId() != null;
        boolean hasRoleNameFilter = searchRequest.getRoleName() != null && !searchRequest.getRoleName().isBlank();
        
        // Cas 1: Filtres combinés (statut + email + rôle par ID)
        if (hasEnabledFilter && hasEmailFilter && hasRoleIdFilter) {
            log.debug("Recherche avec filtres combinés: enabled={}, email={}, roleId={}", 
                    searchRequest.getEnabled(), searchRequest.getEmail(), searchRequest.getRoleId());
            userPage = userRepository.findByEnabledAndEmailContainingAndRoleId(
                    searchRequest.getEnabled(), 
                    searchRequest.getEmail(), 
                    searchRequest.getRoleId(), 
                    pageable);
        }
        // Cas 2: Statut + Rôle par ID
        else if (hasEnabledFilter && hasRoleIdFilter) {
            log.debug("Recherche avec filtres: enabled={}, roleId={}", 
                    searchRequest.getEnabled(), searchRequest.getRoleId());
            userPage = userRepository.findByEnabledAndRoleId(
                    searchRequest.getEnabled(), 
                    searchRequest.getRoleId(), 
                    pageable);
        }
        // Cas 3: Statut + Email
        else if (hasEnabledFilter && hasEmailFilter) {
            log.debug("Recherche avec filtres: enabled={}, email={}", 
                    searchRequest.getEnabled(), searchRequest.getEmail());
            userPage = userRepository.findByEnabledAndEmailContainingIgnoreCase(
                    searchRequest.getEnabled(), 
                    searchRequest.getEmail(), 
                    pageable);
        }
        // Cas 4: Rôle par ID uniquement
        else if (hasRoleIdFilter) {
            log.debug("Recherche avec filtre: roleId={}", searchRequest.getRoleId());
            userPage = userRepository.findByRoleId(searchRequest.getRoleId(), pageable);
        }
        // Cas 5: Rôle par nom
        else if (hasRoleNameFilter) {
            try {
                RoleType roleType = RoleType.valueOf(searchRequest.getRoleName().toUpperCase());
                log.debug("Recherche avec filtre: roleName={}", roleType);
                userPage = userRepository.findByRoleName(roleType, pageable);
            } catch (IllegalArgumentException e) {
                log.warn("Nom de rôle invalide: {}. Retour de tous les utilisateurs.", searchRequest.getRoleName());
                userPage = hasEnabledFilter 
                        ? userRepository.findByEnabled(searchRequest.getEnabled(), pageable)
                        : userRepository.findAll(pageable);
            }
        }
        // Cas 6: Email uniquement
        else if (hasEmailFilter) {
            log.debug("Recherche avec filtre: email={}", searchRequest.getEmail());
            userPage = userRepository.findByEmailContainingIgnoreCase(searchRequest.getEmail(), pageable);
        }
        // Cas 7: Statut uniquement
        else if (hasEnabledFilter) {
            log.debug("Recherche avec filtre: enabled={}", searchRequest.getEnabled());
            userPage = userRepository.findByEnabled(searchRequest.getEnabled(), pageable);
        }
        // Cas 8: Aucun filtre - tous les utilisateurs
        else {
            log.debug("Récupération de tous les utilisateurs sans filtre");
            userPage = userRepository.findAll(pageable);
        }
        
        // Convertir la page d'entités User en page de UserResponse
        List<UserResponse> userResponses = userPage.getContent().stream()
                .map(this::buildUserResponse)
                .collect(Collectors.toList());
        
        log.info("Récupération réussie: {} utilisateurs sur {} (page {}/{})", 
                userResponses.size(), userPage.getTotalElements(), 
                page + 1, userPage.getTotalPages());
        
        // Construire la réponse paginée
        return PagedResponse.<UserResponse>builder()
                .content(userResponses)
                .page(userPage.getNumber())
                .size(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .first(userPage.isFirst())
                .last(userPage.isLast())
                .build();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }



    /**<h6>Generate Username</h6>
     * <hr>
     * <p>Génère automatiquement un identifiant unique basé sur
     *      le nom de famille de l'utilisateur et un nombre aléatoire.</p>
     * <p>
     * Cette méthode est utilisée lors de la création d'un compte
     * afin d'attribuer un identifiant lisible et distinct à chaque utilisateur.
     * <p>
     * Exemple :
     * <pre>{@code
     * generateUsername("Mohamed", "Azzam");
     * // Résultat : "azzam.472918"
     * }</pre>
     *
     * @param firstname prénom de l'utilisateur (actuellement non utilisé)
     * @param lastname  nom de famille de l'utilisateur
     * @return un identifiant généré au format <b>nom.XXXXXX</b>
     * @author Mohamed Azzam
     * @version 1.0
     * @since 2025-11
     */


    /**
     * Génère un nom d'utilisateur unique basé sur le nom de famille
     * 
     * @param firstname prénom de l'utilisateur
     * @param lastname nom de famille de l'utilisateur
     * @return un nom d'utilisateur unique
     */
    private String generateUniqueUsername(String firstname, String lastname) {
        String baseName = lastname
                .trim()
                .toLowerCase()
                .replaceAll("\\s+", ".")
                .replaceAll("[^a-z.]", ""); // Garde uniquement les lettres et les points

        // Si le nom est vide après nettoyage, utiliser "user"
        if (baseName.isEmpty()) {
            baseName = "user";
        }

        // Limiter la longueur du nom de base
        if (baseName.length() > 20) {
            baseName = baseName.substring(0, 20);
        }

        String username;
        int attempts = 0;
        
        do {
        int random = (int) (Math.random() * 900000) + 100000;
            username = baseName + "." + random;
            attempts++;
            
            if (attempts > MAX_USERNAME_GENERATION_ATTEMPTS) {
                // Si on ne peut pas générer un nom unique après plusieurs tentatives,
                // utiliser un timestamp
                username = baseName + "." + System.currentTimeMillis();
                log.warn("Utilisation d'un timestamp pour générer le nom d'utilisateur: {}", username);
                break;
            }
        } while (userRepository.existsByUsername(username));

        return username;
    }
}

