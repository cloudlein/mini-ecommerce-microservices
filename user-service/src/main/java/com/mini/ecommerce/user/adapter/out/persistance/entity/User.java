package com.mini.ecommerce.user.adapter.out.persistance.entity;

import jakarta.persistence.*;import org.hibernate.annotations.CreationTimestamp;import org.hibernate.annotations.UpdateTimestamp;import java.time.LocalDateTime;import java.util.UUID;


@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    private String fullName;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}