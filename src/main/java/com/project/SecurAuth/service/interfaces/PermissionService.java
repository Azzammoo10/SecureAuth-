package com.project.SecurAuth.service.interfaces;


import com.project.SecurAuth.entity.Permission;

import java.util.List;

public interface PermissionService {

    Permission createPermission(String name, String description);

    List<Permission> getAllPermissions();

    Permission getPermissionByName(String name);

    void deletePermission(Long id);

}
