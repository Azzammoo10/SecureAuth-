package com.project.SecurAuth.service.interfaces;

import com.project.SecurAuth.dto.PagedResponse;
import com.project.SecurAuth.dto.RegisterRequest;
import com.project.SecurAuth.dto.RegisterResponse;
import com.project.SecurAuth.dto.UserRequest;
import com.project.SecurAuth.dto.UserResponse;
import com.project.SecurAuth.dto.UserSearchRequest;
import com.project.SecurAuth.entity.User;

import java.util.Optional;

public interface UserService {
    RegisterResponse createUser(RegisterRequest user);
    public Optional<User> getUserByUsername(String username);
    public Optional<User> getUserByEmail(String email);
    void enableUser(String email);
    void incrementFailedAttempts(User user); // Augmente le Nombre d'echecs d'authentification
    void resetFailedAttempts(User user);
    void lockUser(User user);
    User save(User user);
    UserResponse updateUser(Long id, UserRequest request);
    Optional<User> getUserById(Long id);
    PagedResponse<UserResponse> getAllUsers(UserSearchRequest searchRequest);

}
