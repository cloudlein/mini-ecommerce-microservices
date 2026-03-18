package com.mini.ecommerce.user.domain.repository;

import com.mini.ecommerce.user.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    User save(User user);
    Optional<User> findByUserId(UUID userId);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    void delete(UUID userId);
    Boolean existsByUserId(UUID userId);

}
