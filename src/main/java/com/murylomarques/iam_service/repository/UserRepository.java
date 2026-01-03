package com.murylomarques.iam_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.murylomarques.iam_service.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, java.util.UUID> {
    // O Optional evita NullPointerException se o usuário não existir
    Optional<User> findByEmail(String email);
    
    // Verifica se existe para não deixar cadastrar duplicado
    boolean existsByEmail(String email);
}