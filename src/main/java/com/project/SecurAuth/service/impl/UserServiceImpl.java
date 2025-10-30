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
        if(userRepository.findByEmail(user.getEmail())){
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



 /**
  * -----------------------------
  *   GENERATE USERNAME
  * -----------------------------
  * **/
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
