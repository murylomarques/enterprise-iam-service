package com.murylomarques.iam_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "refresh_tokens")
public class RefreshToken extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;

    // Relacionamento: Um usuário tem um Refresh Token ativo
    // (Poderia ser OneToMany se permitíssemos login em vários dispositivos)
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}