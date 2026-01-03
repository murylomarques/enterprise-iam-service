package com.murylomarques.iam_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password; // Será criptografada (BCrypt)

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    // MULTI-TENANT: O usuário pertence a UMA empresa
    @Column(name = "company_id", nullable = false)
    private String companyId;

    // Um usuário pode ter vários cargos (Ex: Gestor e Editor)
    @ManyToMany(fetch = FetchType.EAGER) // EAGER carrega as roles junto com o usuário (cuidado com performance, mas para auth é necessário)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;
}