package com.project.SecurAuth.service.impl;

import com.project.SecurAuth.Exception.EmailAlreadyExistsException;
import com.project.SecurAuth.Exception.WeakPasswordException;
import com.project.SecurAuth.dto.RegisterRequest;
import com.project.SecurAuth.entity.Enum.RoleType;
import com.project.SecurAuth.entity.User;
import com.project.SecurAuth.repository.UserRepository;
import com.project.SecurAuth.service.UserService;
import com.project.SecurAuth.validation.StrongPasswordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

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
        return Optional.empty();
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public void enableUser(String email) {

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
     *      le nom de famille de l’utilisateur et un nombre aléatoire.</p>
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
     * @param firstname prénom de l’utilisateur (actuellement non utilisé)
     * @param lastname  nom de famille de l’utilisateur
     * @return un identifiant généré au format <b>nom.XXXXXX</b>
     * @author Mohamed Azzam
     * @version 1.0
     * @since 2025-11
     */


    private String generateUsername(String firstname, String lastname) {

        String cleanName = lastname
                .trim()
                .toLowerCase()
                .replaceAll("\\s+", ".")
                .replaceAll("[^a-z]", ""); // garde uniquement les lettres

        int random = (int) (Math.random() * 900000) + 100000;

        return cleanName + "." + random;
    }
}
