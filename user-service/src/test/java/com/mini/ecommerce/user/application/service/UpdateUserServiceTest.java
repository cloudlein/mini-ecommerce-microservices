package com.mini.ecommerce.user.application.service;

import com.mini.ecommerce.user.adapter.out.persistance.mapper.UserMapper;
import com.mini.ecommerce.user.application.dto.UpdateUserRequest;
import com.mini.ecommerce.user.application.dto.UserResponse;
import com.mini.ecommerce.user.application.exception.ConflictException;
import com.mini.ecommerce.user.application.exception.ResourceNotFoundException;
import com.mini.ecommerce.user.domain.model.User;
import com.mini.ecommerce.user.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class UpdateUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UpdateUserService service;

    @Test
    void updateUser_updatesEmailAndName() {
        UUID id = UUID.randomUUID();
        User old = User.builder()
                .id(id).email("old@example.com").passwordHash("x").fullName("Old")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        UpdateUserRequest req = new UpdateUserRequest("new@example.com", null, "New Name");

        User updated = old.toBuilder().email(req.getEmail()).fullName(req.getFullName()).build();
        User saved = updated;
        UserResponse resp = UserResponse.builder().id(id).email(updated.getEmail()).fullName(updated.getFullName()).build();

        given(userRepository.findByUserId(id)).willReturn(Optional.of(old));
        given(userRepository.existsByEmail(req.getEmail())).willReturn(false);
        given(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).willReturn(saved);
        given(userMapper.toResponse(saved)).willReturn(resp);

        UserResponse result = service.updateUser(id, req);

        assertThat(result.getEmail()).isEqualTo(req.getEmail());
        assertThat(result.getFullName()).isEqualTo(req.getFullName());
        verify(userRepository).findByUserId(id);
        verify(userRepository).existsByEmail(req.getEmail());
        verify(userRepository).save(any(User.class));
        verify(userMapper).toResponse(saved);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void updateUser_notFound() {
        UUID id = UUID.randomUUID();
        given(userRepository.findByUserId(id)).willReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.updateUser(id, new UpdateUserRequest(null, null, null)));
        verify(userRepository).findByUserId(id);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void updateUser_conflictOnEmail() {
        UUID id = UUID.randomUUID();
        User old = User.builder()
                .id(id).email("old@example.com").passwordHash("x").fullName("Old")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
        UpdateUserRequest req = new UpdateUserRequest("taken@example.com", null, null);

        given(userRepository.findByUserId(id)).willReturn(Optional.of(old));
        given(userRepository.existsByEmail(req.getEmail())).willReturn(true);

        assertThrows(ConflictException.class, () -> service.updateUser(id, req));

        verify(userRepository).findByUserId(id);
        verify(userRepository).existsByEmail(req.getEmail());
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void updateUser_updatesNameOnly_whenEmailUnchanged() {
        UUID id = UUID.randomUUID();
        User old = User.builder()
                .id(id).email("same@example.com").passwordHash("x").fullName("Old")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
        UpdateUserRequest req = new UpdateUserRequest("same@example.com", null, "New Name");

        User updated = old.toBuilder().fullName("New Name").build();
        User saved = updated;
        UserResponse resp = UserResponse.builder().id(id).email(updated.getEmail()).fullName(updated.getFullName()).build();

        given(userRepository.findByUserId(id)).willReturn(Optional.of(old));
        given(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).willReturn(saved);
        given(userMapper.toResponse(saved)).willReturn(resp);

        UserResponse result = service.updateUser(id, req);
        assertThat(result.getEmail()).isEqualTo(old.getEmail());
        assertThat(result.getFullName()).isEqualTo("New Name");
        verify(userRepository).findByUserId(id);
        verify(userRepository).save(any(User.class));
        verify(userMapper).toResponse(saved);
        verifyNoMoreInteractions(userRepository, userMapper);
    }
}
