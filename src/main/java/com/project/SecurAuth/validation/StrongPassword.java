package com.project.SecurAuth.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Annotation personnalisée utilisée pour valider la complexité d’un mot de passe utilisateur.
 * <p>
 * Cette contrainte applique les règles suivantes :
 * <ul>
 *   <li>Contenir au moins 8 caractères</li>
 *   <li>Inclure au moins une lettre majuscule</li>
 *   <li>Inclure au moins une lettre minuscule</li>
 *   <li>Inclure au moins un chiffre</li>
 *   <li>Inclure au moins un caractère spécial</li>
 * </ul>
 * <p>
 * La validation est assurée par la classe {@link StrongPasswordValidator}.
 *
 * Exemple d’utilisation :
 * <pre>{@code
 * public class RegisterRequest {
 *     @StrongPassword
 *     private String password;
 * }
 * }</pre>
 *
 * @author Mohamed Azzam
 * @version 1.0
 * @since 2025-11
 */


@Documented
@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {

    String message() default "Le mot de passe doit contenir au minimum 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial.";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
