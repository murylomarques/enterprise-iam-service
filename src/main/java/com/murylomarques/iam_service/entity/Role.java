package com.murylomarques.iam_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Role extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String name; // Ex: ROLE_ADMIN, ROLE_USER

    @Column(length = 150)
    private String description;

    // Roles podem ser:
    // - Globais (company = null)
    // - Específicas de uma empresa (multi-tenant)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id") // nullable por padrão
    private Company company;
}
