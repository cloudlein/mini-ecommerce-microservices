package com.mini.ecommerce.user.adapter.out.persistance.repository;

import com.mini.ecommerce.user.adapter.out.persistance.entity.UserEntity;
import com.mini.ecommerce.user.adapter.out.persistance.mapper.UserMapper;
import com.mini.ecommerce.user.domain.model.User;
import com.mini.ecommerce.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaUserRepository;
    private final UserMapper userMapper;

    @Override
    public User save(User user) {

        UserEntity userEntity = userMapper.toEntity(user);
        UserEntity savedEntity = jpaUserRepository.save(userEntity);

        return userMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findByUserId(UUID userId) {
        return jpaUserRepository.findById(userId)
                .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email)
                .map(userMapper::toDomain);
    }


    @Override
    public Boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }

    @Override
    public void delete(UUID userId) {
        jpaUserRepository.deleteById(userId);
    }

    @Override
    public Boolean existsByUserId(UUID userId) {
        return jpaUserRepository.existsById(userId);
    }

    @Override
    public Slice<User> findAll(String search, UUID lastId, Pageable pageable) {
        if (lastId == null) {
            return jpaUserRepository.findAllByFullNameContainingIgnoreCaseOrderByIdAsc(search, pageable)
                    .map(userMapper::toDomain);
        }
        return jpaUserRepository.findAllByFullNameContainingIgnoreCaseAndIdGreaterThanOrderByIdAsc(search, lastId, pageable)
                .map(userMapper::toDomain);
    }
}
