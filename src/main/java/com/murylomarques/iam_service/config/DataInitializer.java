package com.murylomarques.iam_service.config;

import com.murylomarques.iam_service.entity.Company;
import com.murylomarques.iam_service.entity.Role;
import com.murylomarques.iam_service.entity.User;
import com.murylomarques.iam_service.repository.CompanyRepository;
import com.murylomarques.iam_service.repository.RoleRepository;
import com.murylomarques.iam_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository; // Injetar novo repo
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        
        // 1. Criar Empresa Padrão (Se não existir)
        if (companyRepository.count() == 0) {
            Company company = Company.builder()
                    .name("Tech Corp")
                    .active(true)
                    .cnpj("00000000000191")
                    .build();
            companyRepository.save(company);
        }
        
        // Pega a empresa criada
        Company defaultCompany = companyRepository.findByName("Tech Corp").orElseThrow();

        // 2. Criar Roles (Vinculadas à empresa ou globais? Vamos fazer globais null por enquanto)
        if (roleRepository.count() == 0) {
            Role adminRole = Role.builder()
                    .name("ROLE_ADMIN")
                    .description("Administrator")
                    .company(null) // Role Global
                    .build();

            Role userRole = Role.builder()
                    .name("ROLE_USER")
                    .description("Standard user")
                    .company(null) // Role Global
                    .build();

            roleRepository.save(adminRole);
            roleRepository.save(userRole);
        }

        // 3. Criar Admin vinculado à empresa
        if (userRepository.findByEmail("admin@empresa.com").isEmpty()) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();
            
            User admin = User.builder()
                    .firstName("Super")
                    .lastName("Admin")
                    .email("admin@empresa.com")
                    .password(passwordEncoder.encode("admin123"))
                    .company(defaultCompany) // <--- OBJETO REAL AGORA
                    .roles(Collections.singleton(adminRole))
                    .build();

            userRepository.save(admin);
            System.out.println("--- ADMIN CRIADO PARA EMPRESA: " + defaultCompany.getName() + " ---");
        }
    }
}