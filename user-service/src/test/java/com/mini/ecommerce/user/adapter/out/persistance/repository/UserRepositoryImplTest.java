package com.mini.ecommerce.user.adapter.out.persistance.repository;

import com.mini.ecommerce.user.adapter.out.persistance.entity.UserEntity;
import com.mini.ecommerce.user.adapter.out.persistance.mapper.UserMapper;
import com.mini.ecommerce.user.domain.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

    @Mock
    private JpaUserRepository jpaUserRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserRepositoryImpl repository;

    @Test
    void save_success() {
        User user = User.builder().id(UUID.randomUUID()).email("test@example.com").build();
        UserEntity entity = UserEntity.builder().id(user.getId()).email(user.getEmail()).build();
        
        given(userMapper.toEntity(user)).willReturn(entity);
        given(jpaUserRepository.save(entity)).willReturn(entity);
        given(userMapper.toDomain(entity)).willReturn(user);

        User result = repository.save(user);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(user.getId());
        verify(userMapper).toEntity(user);
        verify(jpaUserRepository).save(entity);
        verify(userMapper).toDomain(entity);
        verifyNoMoreInteractions(jpaUserRepository, userMapper);
    }

    @Test
    void findByUserId_found() {
        UUID id = UUID.randomUUID();
        UserEntity entity = UserEntity.builder().id(id).email("test@example.com").build();
        User user = User.builder().id(id).email("test@example.com").build();

        given(jpaUserRepository.findById(id)).willReturn(Optional.of(entity));
        given(userMapper.toDomain(entity)).willReturn(user);

        Optional<User> result = repository.findByUserId(id);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        verify(jpaUserRepository).findById(id);
        verify(userMapper).toDomain(entity);
        verifyNoMoreInteractions(jpaUserRepository, userMapper);
    }

    @Test
    void findByUserId_notFound() {
        UUID id = UUID.randomUUID();

        given(jpaUserRepository.findById(id)).willReturn(Optional.empty());

        Optional<User> result = repository.findByUserId(id);

        assertThat(result).isEmpty();
        verify(jpaUserRepository).findById(id);
        verifyNoMoreInteractions(jpaUserRepository, userMapper);
    }

    @Test
    void delete_success() {
        UUID id = UUID.randomUUID();

        repository.delete(id);

        verify(jpaUserRepository).deleteById(id);
        verifyNoMoreInteractions(jpaUserRepository, userMapper);
    }

    @Test
    void existsByEmail_true() {
        String email = "test@example.com";
        given(jpaUserRepository.existsByEmail(email)).willReturn(true);

        Boolean result = repository.existsByEmail(email);

        assertThat(result).isTrue();
        verify(jpaUserRepository).existsByEmail(email);
        verifyNoMoreInteractions(jpaUserRepository, userMapper);
    }

    @Test
    void existsByUserId_true() {
        UUID id = UUID.randomUUID();
        given(jpaUserRepository.existsById(id)).willReturn(true);

        Boolean result = repository.existsByUserId(id);

        assertThat(result).isTrue();
        verify(jpaUserRepository).existsById(id);
        verifyNoMoreInteractions(jpaUserRepository, userMapper);
    }

    @Test
    void findAll_withoutLastId() {
        Pageable pageable = PageRequest.of(0, 10);
        UserEntity entity = UserEntity.builder().id(UUID.randomUUID()).build();
        User user = User.builder().id(entity.getId()).build();
        Slice<UserEntity> entitySlice = new SliceImpl<>(List.of(entity), pageable, false);

        given(jpaUserRepository.findAllByFullNameContainingIgnoreCaseOrderByIdAsc("john", pageable))
                .willReturn(entitySlice);
        given(userMapper.toDomain(entity)).willReturn(user);

        Slice<User> result = repository.findAll("john", null, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(user.getId());
        verify(jpaUserRepository).findAllByFullNameContainingIgnoreCaseOrderByIdAsc("john", pageable);
        verify(userMapper).toDomain(entity);
        verifyNoMoreInteractions(jpaUserRepository, userMapper);
    }

    @Test
    void findAll_withLastId() {
        Pageable pageable = PageRequest.of(0, 10);
        UUID lastId = UUID.randomUUID();
        UserEntity entity = UserEntity.builder().id(UUID.randomUUID()).build();
        User user = User.builder().id(entity.getId()).build();
        Slice<UserEntity> entitySlice = new SliceImpl<>(List.of(entity), pageable, false);

        given(jpaUserRepository.findAllByFullNameContainingIgnoreCaseAndIdGreaterThanOrderByIdAsc("john", lastId, pageable))
                .willReturn(entitySlice);
        given(userMapper.toDomain(entity)).willReturn(user);

        Slice<User> result = repository.findAll("john", lastId, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(user.getId());
        verify(jpaUserRepository).findAllByFullNameContainingIgnoreCaseAndIdGreaterThanOrderByIdAsc("john", lastId, pageable);
        verify(userMapper).toDomain(entity);
        verifyNoMoreInteractions(jpaUserRepository, userMapper);
    }
}