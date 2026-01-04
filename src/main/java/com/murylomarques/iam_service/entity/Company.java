package com.murylomarques.iam_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "companies")
public class Company extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(unique = true)
    private String cnpj;

    private boolean active;
}