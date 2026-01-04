package com.murylomarques.iam_service.repository;

import com.murylomarques.iam_service.entity.RefreshToken;
import com.murylomarques.iam_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
    
    // ADICIONE ESTE MÉTODO:
    Optional<RefreshToken> findByUser(User user);
}