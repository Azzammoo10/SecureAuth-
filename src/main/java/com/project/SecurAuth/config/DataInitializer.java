package com.project.SecurAuth.config;

import com.project.SecurAuth.entity.Enum.RoleType;
import com.project.SecurAuth.entity.Role;
import com.project.SecurAuth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        initializeRoles();
    }

    private void initializeRoles() {
        List<RoleType> roleTypes = Arrays.asList(RoleType.values());
        
        for (RoleType roleType : roleTypes) {
            if (!roleRepository.findByName(roleType).isPresent()) {
                Role role = new Role();
                role.setName(roleType);
                role.setDescription("Rôle " + roleType.name());
                roleRepository.save(role);
                log.info("Rôle {} créé avec succès", roleType);
            } else {
                log.info("Rôle {} existe déjà", roleType);
            }
        }
    }
}

