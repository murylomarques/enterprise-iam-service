package com.murylomarques.iam_service.repository;

import com.murylomarques.iam_service.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, java.util.UUID> {
    // Busca um cargo pelo nome (ex: ROLE_ADMIN)
    Optional<Role> findByName(String name);
}