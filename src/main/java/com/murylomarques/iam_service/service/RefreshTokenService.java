package com.murylomarques.iam_service.service;

import com.murylomarques.iam_service.entity.RefreshToken;
import com.murylomarques.iam_service.repository.RefreshTokenRepository;
import com.murylomarques.iam_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshToken createRefreshToken(String email) {
        var user = userRepository.findByEmail(email).orElseThrow();
        
        // --- CORREÇÃO DO ERRO 500 ---
        // Verifica se o usuário já tem um token
        var existingToken = refreshTokenRepository.findByUser(user);
        
        if (existingToken.isPresent()) {
            refreshTokenRepository.delete(existingToken.get());
            refreshTokenRepository.flush(); // FORÇA O DELETE IMEDIATO NO BANCO
        }
        // -----------------------------
        
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshExpiration))
                .build();
        
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expirado. Por favor, faça login novamente.");
        }
        return token;
    }
    
    @Transactional
    public void deleteByUserId(UUID userId) {
        // Método necessário para logout ou rotação
       // Implementaremos se necessário mudar a lógica de OneToOne
    }
}