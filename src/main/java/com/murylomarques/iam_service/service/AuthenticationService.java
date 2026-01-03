package com.murylomarques.iam_service.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.murylomarques.iam_service.dto.AuthenticationRequest;
import com.murylomarques.iam_service.dto.AuthenticationResponse;
import com.murylomarques.iam_service.dto.RegisterRequest;
import com.murylomarques.iam_service.entity.Role;
import com.murylomarques.iam_service.entity.User;
import com.murylomarques.iam_service.repository.RoleRepository;
import com.murylomarques.iam_service.repository.UserRepository;
import com.murylomarques.iam_service.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final RoleRepository roleRepository; // Injetamos o repo de roles
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        // Busca a role padrão USER
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .companyId(request.getCompanyId())
                .roles(roles) // Adicionamos a role ao usuário
                .build();
        
        repository.save(user);
        
        // Agora usamos o userDetailsServiceImpl para gerar o token com as roles corretas
        // Mas para simplificar aqui, vamos recriar o objeto User do Spring manualmente de novo
        // (Nota: Em produção, seria melhor extrair isso para um método auxiliar)
        var authorities = user.getRoles().stream()
                .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority(role.getName()))
                .toList();

        var jwtToken = jwtService.generateToken(new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), authorities
        ));
        
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail()).orElseThrow();
        
        // Conversão das roles para o token
        var authorities = user.getRoles().stream()
                .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority(role.getName()))
                .toList();
        
        var jwtToken = jwtService.generateToken(new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), authorities
        ));

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}