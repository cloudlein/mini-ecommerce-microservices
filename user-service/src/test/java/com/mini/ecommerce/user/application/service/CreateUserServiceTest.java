package com.mini.ecommerce.user.application.service;

import com.mini.ecommerce.user.adapter.out.persistance.mapper.UserMapper;
import com.mini.ecommerce.user.application.dto.user.CreateUserRequest;
import com.mini.ecommerce.user.application.dto.user.UserResponse;
import com.mini.ecommerce.user.application.exception.ConflictException;
import com.mini.ecommerce.user.domain.model.User;
import com.mini.ecommerce.user.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class CreateUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private CreateUserService service;

    @Test
    void createUser_success() {
        CreateUserRequest req = new CreateUserRequest("john@example.com", "secret", "John Doe");
        User domain = User.builder()
                .id(UUID.randomUUID())
                .email(req.getEmail())
                .passwordHash("hashed")
                .fullName(req.getFullName())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        User saved = domain;
        UserResponse resp = UserResponse.builder()
                .id(domain.getId())
                .email(domain.getEmail())
                .fullName(domain.getFullName())
                .build();

        given(userRepository.existsByEmail(req.getEmail())).willReturn(false);
        given(userMapper.toDomain(req)).willReturn(domain);
        given(userRepository.save(domain)).willReturn(saved);
        given(userMapper.toResponse(saved)).willReturn(resp);

        UserResponse result = service.createUser(req);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(resp.getId());
        assertThat(result.getEmail()).isEqualTo(resp.getEmail());
        assertThat(result.getFullName()).isEqualTo(resp.getFullName());
        verify(userRepository).existsByEmail(req.getEmail());
        verify(userMapper).toDomain(req);
        verify(userRepository).save(domain);
        verify(userMapper).toResponse(saved);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void createUser_conflict_whenEmailExists() {
        CreateUserRequest req = new CreateUserRequest("john@example.com", "secret", "John Doe");
        given(userRepository.existsByEmail(req.getEmail())).willReturn(true);

        assertThrows(ConflictException.class, () -> service.createUser(req));

        verify(userRepository).existsByEmail(req.getEmail());
        verifyNoMoreInteractions(userRepository, userMapper);
    }
}
