package com.mini.ecommerce.user.application.service;

import com.mini.ecommerce.user.adapter.out.persistance.mapper.UserMapper;
import com.mini.ecommerce.user.application.dto.user.PaginationResponse;
import com.mini.ecommerce.user.application.dto.user.UserResponse;
import com.mini.ecommerce.user.application.exception.ResourceNotFoundException;
import com.mini.ecommerce.user.domain.model.User;
import com.mini.ecommerce.user.domain.repository.UserRepository;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class GetUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private GetUserService service;

    @Test
    void getUserById_success() {
        UUID id = UUID.randomUUID();
        User user = User.builder()
                .id(id).email("jane@example.com").passwordHash("x").fullName("Jane")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
        UserResponse response = UserResponse.builder().id(id).email(user.getEmail()).fullName(user.getFullName()).build();

        given(userRepository.findByUserId(id)).willReturn(Optional.of(user));
        given(userMapper.toResponse(user)).willReturn(response);

        UserResponse result = service.getUserById(id);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        verify(userRepository).findByUserId(id);
        verify(userMapper).toResponse(user);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void getUserById_notFound() {
        UUID id = UUID.randomUUID();
        given(userRepository.findByUserId(id)).willReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getUserById(id));
        verify(userRepository).findByUserId(id);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void getAllUser_returnsMappedPage() {
        Pageable pageable = PageRequest.of(0, 2);
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        User u1 = User.builder().id(id1).email("a@ex.com").passwordHash("x").fullName("A").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
        User u2 = User.builder().id(id2).email("b@ex.com").passwordHash("x").fullName("B").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
        Slice<User> slice = new SliceImpl<>(List.of(u1, u2), pageable, false);
        UserResponse r1 = UserResponse.builder().id(id1).email(u1.getEmail()).fullName(u1.getFullName()).build();
        UserResponse r2 = UserResponse.builder().id(id2).email(u2.getEmail()).fullName(u2.getFullName()).build();

        given(userRepository.findAll("a", null, pageable)).willReturn(slice);
        given(userMapper.toResponse(u1)).willReturn(r1);
        given(userMapper.toResponse(u2)).willReturn(r2);

        PaginationResponse<UserResponse> result = service.getAllUser("a", null, pageable);

        assertThat(result.getContent()).hasSize(2).containsExactly(r1, r2);
        assertThat(result.getCurrentPage()).isEqualTo(slice.getNumber());
        assertThat(result.getSize()).isEqualTo(slice.getSize());
        assertThat(result.isLast()).isEqualTo(slice.isLast());
        assertThat(result.getNextCursor()).isEqualTo(id2.toString());

        verify(userRepository).findAll("a", null, pageable);
        verify(userMapper).toResponse(u1);
        verify(userMapper).toResponse(u2);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void getAllUser_emptyContent_hasNullCursor() {
        Pageable pageable = PageRequest.of(0, 2);
        Slice<User> slice = new SliceImpl<>(List.of(), pageable, true);

        given(userRepository.findAll(null, null, pageable)).willReturn(slice);

        PaginationResponse<UserResponse> result = service.getAllUser(null, null, pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getNextCursor()).isNull();
        verify(userRepository).findAll(null, null, pageable);
        verifyNoMoreInteractions(userRepository, userMapper);
    }
}
