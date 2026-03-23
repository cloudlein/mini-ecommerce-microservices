package com.mini.ecommerce.user.domain.model;

import lombok.AllArgsConstructor;import lombok.Builder;
import lombok.Getter;import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {

    private UUID id;
    private String email;
    private String passwordHash;
    private String fullName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
