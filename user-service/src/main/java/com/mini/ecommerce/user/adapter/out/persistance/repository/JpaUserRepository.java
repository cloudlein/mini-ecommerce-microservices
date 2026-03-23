package com.mini.ecommerce.user.adapter.out.persistance.repository;

import com.mini.ecommerce.user.adapter.out.persistance.entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<UserEntity, UUID> {

    @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
    Optional<UserEntity> findByEmail(@Param("email") String email);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM users WHERE email = :email)", nativeQuery = true)
    boolean existsByEmail(@Param("email") String email);

    @Query(value = "SELECT * FROM users WHERE id = :id", nativeQuery = true)
    Optional<UserEntity> findById(@Param("id") UUID id);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM users WHERE id = :id)", nativeQuery = true)
    boolean existsById(@Param("id") UUID id);

    @Query(value = "SELECT * FROM users WHERE LOWER(full_name) LIKE LOWER(CONCAT('%', :fullName, '%')) ORDER BY id ASC", nativeQuery = true)
    Slice<UserEntity> findAllByFullNameContainingIgnoreCaseOrderByIdAsc(@Param("fullName") String fullName, Pageable pageable);

    @Query(value = "SELECT * FROM users WHERE LOWER(full_name) LIKE LOWER(CONCAT('%', :fullName, '%')) AND id > :lastId ORDER BY id ASC", nativeQuery = true)
    Slice<UserEntity> findAllByFullNameContainingIgnoreCaseAndIdGreaterThanOrderByIdAsc(@Param("fullName") String fullName, @Param("lastId") UUID lastId, Pageable pageable);

    @Modifying
    @Query(value = "DELETE FROM users WHERE id = :id", nativeQuery = true)
    void deleteById(@Param("id") UUID id);

}
