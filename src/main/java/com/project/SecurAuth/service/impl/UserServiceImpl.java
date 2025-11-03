package com.project.SecurAuth.service.impl;

import com.project.SecurAuth.Exception.EmailAlreadyExistsException;
import com.project.SecurAuth.Exception.WeakPasswordException;
import com.project.SecurAuth.dto.RegisterRequest;
import com.project.SecurAuth.entity.Enum.RoleType;
import com.project.SecurAuth.entity.User;
import com.project.SecurAuth.repository.UserRepository;
import com.project.SecurAuth.service.interfaces.UserService;
import com.project.SecurAuth.validation.StrongPasswordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * <h4>UserServiceImpl.java</h4>
 * <hr>
 * <p>Implémentation concrète de l'interface {@link com.project.SecurAuth.service.interfaces.UserService}.</p>
 * <p>Ce service contient la logique métier liée aux utilisateurs :</p>
 * <ul>
 *  <li>Création d'un utilisateur (vérification d'email unique et validation de la robustesse du mot de passe),</li>
 *  <li>Génération d'un nom d'utilisateur lisible et unique basé sur le nom de famille,</li>
 *  <li>récupération d'un utilisateur par username ou email,</li>
 *  <li>Activation d'un compte, gestion des tentatives d'échec et verrouillage de compte (méthodes de gestion partiellement implémentées).</li>
 *</ul>
 * <hr>
 * <p>Détails techniques :</p>
 * <ul>
 *     <li>Utilise {@link com.project.SecurAuth.repository.UserRepository} pour la persistance.</li>
 *     <li>Utilise {@link com.project.SecurAuth.validation.StrongPasswordValidator} pour valider la solidité des mots de passe.</li>
 *     <li>Lance des exceptions métiers {@link com.project.SecurAuth.Exception.EmailAlreadyExistsException}</li>
 *     <li>{@link com.project.SecurAuth.Exception.WeakPasswordException} en cas d'erreur de validation.</li>
 * </ul>
 *
 *
 * @author Mohamed Azzam
 * @since 2025-11-02
 */

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final StrongPasswordValidator strongPasswordValidator;



    @Override
    public User createUser(RegisterRequest user) {
        if(userRepository.existsByEmail(user.getEmail())){
            throw new EmailAlreadyExistsException("Email déjà utilisé");
        }

        if(user.getPassword().isBlank() || !strongPasswordValidator.isValid(user.getPassword(), null)){
            throw new WeakPasswordException("Le mot de passe est trop faible : il doit contenir au moins 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial.");
        }

        User userCreate = new User();
        userCreate.setFirstName(user.getFirstName());
        userCreate.setLastName(user.getLastName());
        userCreate.setEmail(user.getEmail());
        userCreate.setPassword(user.getPassword());
        userCreate.setUsername(generateUsername(user.getFirstName(), user.getLastName()));
        userCreate.setEnabled(true);
        userCreate.setFailedAttempts(0);
        userCreate.setRole(RoleType.UTILISATEUR);
        userCreate.setCreatedAt(LocalDateTime.now());
        userCreate.setUpdatedAt(LocalDateTime.now());

        userRepository.save(userCreate);

        return userCreate;
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



    /**<h6>Generate Username</h6>
     * <hr>
     * <p>Génère automatiquement un identifiant unique basé sur
     *      le nom de famille de l’utilisateur et un nombre aléatoire.
     *      Si le nom de famille est vide, on utilise le prénom comme base.</p>
     * <p>
     * Cette méthode est utilisée lors de la création d’un compte
     * afin d’attribuer un identifiant lisible et distinct à chaque utilisateur.
     * <p>
     * Exemple :
     * <pre>{@code
     * generateUsername("Mohamed", "Azzam");
     * // Résultat : "azzam.472918"
     * }</pre>
     *
     * @param firstname prénom de l’utilisateur (utilisé comme repli si le nom est vide)
     * @param lastname  nom de famille de l’utilisateur (préféré pour la génération)
     * @return un identifiant généré au format <b>nom.XXXXXX</b>
     * @author Mohamed Azzam
     * @since 2025-11
     */


    private String generateUsername(String firstname, String lastname) {

        String base = (lastname != null && !lastname.isBlank()) ? lastname : firstname;
        if (base == null || base.isBlank()) {
            base = "user";
        }

        String cleanName = base
                .trim()
                .toLowerCase()
                .replaceAll("\\s+", ".")
                .replaceAll("[^a-z]", ""); // garde uniquement les lettres

        int random = (int) (Math.random() * 900000) + 100000;

        return cleanName + "." + random;
    }
}
