package com.murylomarques.iam_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass // Indica que essa classe não vira tabela, mas seus filhos herdam os campos
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // UUID é mais seguro que ID sequencial (1, 2, 3...)
    private UUID id;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}