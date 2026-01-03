package com.murylomarques.iam_service.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.murylomarques.iam_service.entity.Role;
import com.murylomarques.iam_service.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Verifica se já existem roles, se não, cria as padrões
        if (roleRepository.count() == 0) {
            Role adminRole = Role.builder()
                    .name("ROLE_ADMIN")
                    .description("Administrator with full access")
                    .companyId("system") // Role global ou do sistema
                    .build();

            Role userRole = Role.builder()
                    .name("ROLE_USER")
                    .description("Standard user")
                    .companyId("system")
                    .build();

            roleRepository.save(adminRole);
            roleRepository.save(userRole);
            
            System.out.println("--- ROLES PADRÃO CRIADAS COM SUCESSO ---");
        }
    }
}