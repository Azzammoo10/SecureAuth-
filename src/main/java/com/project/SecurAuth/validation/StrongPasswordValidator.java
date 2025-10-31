package com.project.SecurAuth.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;


/**
 * <h3>Validateur de mot de passe fort — StrongPasswordValidator</h3>
 * <br>
 *
 * <h5>Description</h5>
 * <hr>
 * <p>
 * Cette classe valide la robustesse d’un mot de passe selon les règles de sécurité
 * définies par l’annotation personnalisée {@link StrongPassword}.<br>
 * Elle implémente l’interface {@link jakarta.validation.ConstraintValidator} de Jakarta Validation,
 * permettant son intégration directe dans les entités ou DTOs annotés.
 * </p>
 *
 * <h5>Règles de validation</h5>
 * <hr>
 * <p>Pour qu’un mot de passe soit considéré comme <b>fort</b>, il doit :</p>
 * <ul>
 *   <li>Contenir <b>au moins 10 caractères</b></li>
 *   <li>Inclure <b>au moins une lettre majuscule</b></li>
 *   <li>Inclure <b>au moins une lettre minuscule</b></li>
 *   <li>Contenir <b>au moins un chiffre</b></li>
 *   <li>Inclure <b>au moins un caractère spécial</b> parmi : <code>@ $ ! % * ? &</code></li>
 * </ul>
 *
 * <h5>Comportement</h5>
 * <hr>
 * <p>
 * Si le mot de passe ne respecte pas ces critères, la méthode
 * {@link #isValid(String, jakarta.validation.ConstraintValidatorContext)}
 * désactive le message de validation par défaut et génère un message d’erreur explicite
 * afin d’informer l’utilisateur des exigences de sécurité.
 * </p>
 *
 * <h5>Exemple d’utilisation</h5>
 * <pre>{@code
 * public class UtilisateurDTO {
 *       @StrongPassword
 *       private String motDePasse;
 * }
 * }</pre>
 *
 *
 *
 * <h4>Remarques techniques</h4>
 * <hr>
 * <ul>
 *   <li>Annotée avec {@link org.springframework.stereotype.Component} pour être détectée automatiquement
 *       par le conteneur Spring.</li>
 *   <li>Utilise une expression régulière optimisée pour un bon équilibre entre lisibilité et performance.</li>
 *   <li>Respecte les bonnes pratiques de validation Jakarta (validation déclarative et message personnalisé).</li>
 * </ul>
 *
 * @author Mohamed Azzam
 * @version 1.0
 * @since 2025-11
 */


@Component
public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isBlank()) return false;

        boolean valid = password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{10,}$");

        if (!valid && context != null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Le mot de passe doit contenir au moins 10 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial"
            ).addConstraintViolation();
        }

        return valid;
    }
}

