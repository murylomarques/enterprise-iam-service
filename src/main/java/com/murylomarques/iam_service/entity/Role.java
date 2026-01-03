package com.murylomarques.iam_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data // Lombok gera Getters, Setters, toString, etc.
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String name; // Ex: ROLE_ADMIN, ROLE_USER

    @Column(length = 150)
    private String description;
    
    // Multi-tenancy: O cargo pertence a qual empresa? 
    // (Pode ser null se for um cargo global do sistema, mas vamos focar no tenant)
    // Usaremos String para o ID da empresa para facilitar, poderia ser UUID.
    @Column(name = "company_id", nullable = false) 
    private String companyId; 
}