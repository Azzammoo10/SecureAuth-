package com.project.SecurAuth.service.interfaces;

import com.project.SecurAuth.dto.AuthResponse;
import com.project.SecurAuth.dto.LoginRequest;
import com.project.SecurAuth.entity.User;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(String refreshToken);

    void logout(String username);

    void forceLogout(Long userId); // réservé à l’admin

    void handleFailedLogin(User user); // incrémente, bloque, journalise

    void resetLoginAttempts(User user);

    boolean validateToken(String token);
}
