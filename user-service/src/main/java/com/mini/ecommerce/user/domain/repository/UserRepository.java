package com.mini.ecommerce.user.domain.repository;

import com.mini.ecommerce.user.domain.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    User save(User user);
    Optional<User> findByUserId(UUID userId);
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    void delete(UUID userId);
    Boolean existsByUserId(UUID userId);
    Slice<User> findAll(String search, UUID lastId, Pageable pageable);

}
