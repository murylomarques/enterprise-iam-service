package com.murylomarques.iam_service.service;

import com.murylomarques.iam_service.dto.AuthenticationRequest;
import com.murylomarques.iam_service.dto.AuthenticationResponse;
import com.murylomarques.iam_service.dto.RefreshTokenRequest;
import com.murylomarques.iam_service.dto.RegisterRequest;
import com.murylomarques.iam_service.entity.Company;
import com.murylomarques.iam_service.entity.RefreshToken;
import com.murylomarques.iam_service.entity.Role;
import com.murylomarques.iam_service.entity.User;
import com.murylomarques.iam_service.repository.CompanyRepository;
import com.murylomarques.iam_service.repository.RefreshTokenRepository;
import com.murylomarques.iam_service.repository.RoleRepository;
import com.murylomarques.iam_service.repository.UserRepository;
import com.murylomarques.iam_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final CompanyRepository companyRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * REGISTRO DE USUÁRIO (MULTI-TENANT)
     */
    public AuthenticationResponse register(RegisterRequest request) {

        if (repository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já cadastrado no sistema.");
        }

        // 🔹 Buscar ou criar empresa
        // (Aqui o DTO usa companyId como nome para simplificar)
        Company company = companyRepository.findByName(request.getCompanyId())
                .orElseGet(() -> companyRepository.save(
                        Company.builder()
                                .name(request.getCompanyId())
                                .active(true)
                                .build()
                ));

        // 🔹 Buscar role padrão
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role 'ROLE_USER' não encontrada."));

        // 🔹 Criar usuário
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .company(company)
                .roles(new HashSet<>(Collections.singletonList(userRole)))
                .build();

        User savedUser = repository.save(user);

        // 🔹 Gerar tokens
        String accessToken = generateTokenForUser(savedUser);
        var refreshToken = refreshTokenService.createRefreshToken(savedUser.getEmail());

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    /**
     * LOGIN
     */
    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String accessToken = generateTokenForUser(user);
        var refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    /**
     * REFRESH TOKEN
     */
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {

        return refreshTokenRepository.findByToken(request.getRefreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> AuthenticationResponse.builder()
                        .accessToken(generateTokenForUser(user))
                        .refreshToken(request.getRefreshToken())
                        .build()
                )
                .orElseThrow(() -> new RuntimeException("Refresh token inválido ou expirado."));
    }

    /**
     * Geração de JWT
     */
    private String generateTokenForUser(User user) {

        var authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .toList();

        return jwtService.generateToken(
                new org.springframework.security.core.userdetails.User(
                        user.getEmail(),
                        user.getPassword(),
                        authorities
                )
        );
    }
}
