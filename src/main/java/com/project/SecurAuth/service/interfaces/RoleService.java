package com.project.SecurAuth.service.interfaces;

import com.project.SecurAuth.dto.RoleRequest;
import com.project.SecurAuth.entity.Role;

import java.util.List;

public interface RoleService {
    Role createRole(RoleRequest request);

    void deleteRole(Long id);

    Role getRoleByName(String name);

    List<Role> getAllRoles();

    void assignRoleToUser(Long userId, Long roleId);

    void removeRoleFromUser(Long userId, Long roleId);
}
