package com.mini.ecommerce.user.adapter.out.persistance.repository;

import com.mini.ecommerce.user.adapter.out.persistance.entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    Slice<UserEntity> findAllByFullNameContainingIgnoreCaseOrderByIdAsc(String fullName, Pageable pageable);
    Slice<UserEntity> findAllByFullNameContainingIgnoreCaseAndIdGreaterThanOrderByIdAsc(String fullName, UUID lastId, Pageable pageable);

}
