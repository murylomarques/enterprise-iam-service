package com.murylomarques.iam_service.service;

import com.murylomarques.iam_service.dto.AuthenticationRequest;
import com.murylomarques.iam_service.dto.AuthenticationResponse;
import com.murylomarques.iam_service.dto.RegisterRequest;
import com.murylomarques.iam_service.entity.Role;
import com.murylomarques.iam_service.entity.User;
import com.murylomarques.iam_service.repository.RoleRepository;
import com.murylomarques.iam_service.repository.UserRepository;
import com.murylomarques.iam_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        // 1. REGRA DE NEGÓCIO: Verificar se email já existe
        if (repository.existsByEmail(request.getEmail())) {
            // Esse erro será capturado pelo GlobalExceptionHandler (Error 500 ou 409 se customizar)
            throw new RuntimeException("Email já cadastrado no sistema.");
        }

        // 2. Busca a role padrão USER
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Role 'ROLE_USER' not found."));

        // 3. Cria o usuário
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .companyId(request.getCompanyId())
                .roles(new HashSet<>(Collections.singletonList(userRole))) // Adiciona a role numa lista mutável
                .build();
        
        repository.save(user);
        
        // 4. Gera o token e retorna
        var jwtToken = generateTokenForUser(user);
        
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // 1. Autentica (Se falhar, o Spring lança exceção automaticamente)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2. Busca o usuário (Nesse ponto já sabemos que existe e a senha tá certa)
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // 3. Gera o token e retorna
        var jwtToken = generateTokenForUser(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    /**
     * Método auxiliar privado para gerar o token.
     * Evita repetição de código no register e authenticate.
     */
    private String generateTokenForUser(User user) {
        var authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .toList();

        return jwtService.generateToken(new org.springframework.security.core.userdetails.User(
                user.getEmail(), 
                user.getPassword(), 
                authorities
        ));
    }
}