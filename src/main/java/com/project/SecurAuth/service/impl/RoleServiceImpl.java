package com.project.SecurAuth.service.impl;

import com.project.SecurAuth.dto.RoleRequest;
import com.project.SecurAuth.entity.Role;
import com.project.SecurAuth.service.interfaces.RoleService;

import java.util.List;

public class RoleServiceImpl implements RoleService {
    @Override
    public Role createRole(RoleRequest request) {
        return null;
    }

    @Override
    public void deleteRole(Long id) {

    }

    @Override
    public Role getRoleByName(String name) {
        return null;
    }

    @Override
    public List<Role> getAllRoles() {
        return List.of();
    }

    @Override
    public void assignRoleToUser(Long userId, Long roleId) {

    }

    @Override
    public void removeRoleFromUser(Long userId, Long roleId) {

    }
}
