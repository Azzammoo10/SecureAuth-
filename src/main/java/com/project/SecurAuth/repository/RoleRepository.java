package com.project.SecurAuth.repository;

import com.project.SecurAuth.entity.Enum.RoleType;
import com.project.SecurAuth.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleType name);
}
