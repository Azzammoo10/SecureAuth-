package com.project.SecurAuth.repository;

import com.project.SecurAuth.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    
    // Recherche avec filtres basiques
    Page<User> findByEnabled(boolean enabled, Pageable pageable);
    
    // Recherche par email (contient)
    Page<User> findByEmailContainingIgnoreCase(String email, Pageable pageable);
    
    // Recherche par statut et email
    Page<User> findByEnabledAndEmailContainingIgnoreCase(boolean enabled, String email, Pageable pageable);
    
    // Recherche par rôle (via la table de jointure)
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r.id = :roleId")
    Page<User> findByRoleId(@Param("roleId") Long roleId, Pageable pageable);
    
    // Recherche par nom de rôle
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r.name = :roleType")
    Page<User> findByRoleName(@Param("roleType") com.project.SecurAuth.entity.Enum.RoleType roleType, Pageable pageable);
    
    // Recherche combinée: statut + rôle
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE u.enabled = :enabled AND r.id = :roleId")
    Page<User> findByEnabledAndRoleId(@Param("enabled") boolean enabled, @Param("roleId") Long roleId, Pageable pageable);
    
    // Recherche combinée: statut + email + rôle
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE u.enabled = :enabled AND LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')) AND r.id = :roleId")
    Page<User> findByEnabledAndEmailContainingAndRoleId(
            @Param("enabled") boolean enabled, 
            @Param("email") String email, 
            @Param("roleId") Long roleId, 
            Pageable pageable);
}
