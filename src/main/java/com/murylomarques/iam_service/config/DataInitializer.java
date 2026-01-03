package com.murylomarques.iam_service.config;

import java.util.Collections;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.murylomarques.iam_service.entity.Role;
import com.murylomarques.iam_service.entity.User;
import com.murylomarques.iam_service.repository.RoleRepository;
import com.murylomarques.iam_service.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        
        // 1. Criar Roles se não existirem
        if (roleRepository.count() == 0) {
            Role adminRole = Role.builder()
                    .name("ROLE_ADMIN")
                    .description("Administrator with full access")
                    .companyId("system")
                    .build();

            Role userRole = Role.builder()
                    .name("ROLE_USER")
                    .description("Standard user")
                    .companyId("system")
                    .build();

            roleRepository.save(adminRole);
            roleRepository.save(userRole);
        }

        // 2. Criar Usuário ADMIN se não existir
        if (userRepository.findByEmail("admin@empresa.com").isEmpty()) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();
            
            User admin = User.builder()
                    .firstName("Super")
                    .lastName("Admin")
                    .email("admin@empresa.com")
                    .password(passwordEncoder.encode("admin123")) // Senha forte em prod, simples aqui
                    .companyId("empresa-01")
                    .roles(Collections.singleton(adminRole))
                    .build();

            userRepository.save(admin);
            System.out.println("--- USUÁRIO ADMIN CRIADO: admin@empresa.com / admin123 ---");
        }
    }
}