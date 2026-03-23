package com.mini.ecommerce.user.adapter.in.web;

import com.mini.ecommerce.user.adapter.in.web.controller.UserController;
import com.mini.ecommerce.user.application.dto.user.CreateUserRequest;
import com.mini.ecommerce.user.application.dto.user.PaginationResponse;
import com.mini.ecommerce.user.application.dto.user.UpdateUserRequest;
import com.mini.ecommerce.user.application.dto.user.UserResponse;
import com.mini.ecommerce.user.application.port.in.CreateUserUseCase;
import com.mini.ecommerce.user.application.port.in.DeleteUserUseCase;
import com.mini.ecommerce.user.application.port.in.GetUserUseCase;
import com.mini.ecommerce.user.application.port.in.UpdateUserUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private CreateUserUseCase createUserUseCase;

    @Mock
    private GetUserUseCase getUserUseCase;

    @Mock
    private UpdateUserUseCase updateUserUseCase;

    @Mock
    private DeleteUserUseCase deleteUserUseCase;

    @InjectMocks
    private UserController userController;

    @Test
    void createUser_ShouldReturn201() throws Exception {
        CreateUserRequest request = new CreateUserRequest("test@example.com", "password", "Test User");
        UserResponse response = UserResponse.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .fullName("Test User")
                .build();

        given(createUserUseCase.createUser(any(CreateUserRequest.class))).willReturn(response);

        ResponseEntity<UserResponse> result = userController.createUser(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("test@example.com", result.getBody().getEmail());
        assertEquals("Test User", result.getBody().getFullName());

        verify(createUserUseCase).createUser(request);
    }

    @Test
    void getUserById_ShouldReturn200() throws Exception {
        UUID userId = UUID.randomUUID();
        UserResponse response = UserResponse.builder()
                .id(userId)
                .email("test@example.com")
                .fullName("Test User")
                .build();

        given(getUserUseCase.getUserById(userId)).willReturn(response);

        ResponseEntity<UserResponse> result = userController.getUserById(userId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(userId, result.getBody().getId());
        assertEquals("test@example.com", result.getBody().getEmail());

        verify(getUserUseCase).getUserById(userId);
    }

    @Test
    void getAllUsers_ShouldReturn200() throws Exception {
        UUID userId = UUID.randomUUID();
        UserResponse userResponse = UserResponse.builder()
                .id(userId)
                .email("test@example.com")
                .fullName("Test User")
                .build();

        PaginationResponse<UserResponse> response = PaginationResponse.<UserResponse>builder()
                .content(List.of(userResponse))
                .currentPage(0)
                .size(20)
                .last(true)
                .totalElements(1)
                .totalPages(1)
                .nextCursor(null)
                .build();

        Pageable pageable = PageRequest.of(0, 20);

        given(getUserUseCase.getAllUser("test", null, pageable)).willReturn(response);

        ResponseEntity<PaginationResponse<UserResponse>> result =
                userController.getAllUsers("test", null, pageable);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().getContent().size());
        assertEquals(userId, result.getBody().getContent().getFirst().getId());

        verify(getUserUseCase).getAllUser("test", null, pageable);
    }

    @Test
    void updateUser_ShouldReturn200() throws Exception {
        UUID userId = UUID.randomUUID();
        UpdateUserRequest request = new UpdateUserRequest("updated@example.com", null, "Updated User");
        UserResponse response = UserResponse.builder()
                .id(userId)
                .email("updated@example.com")
                .fullName("Updated User")
                .build();

        given(updateUserUseCase.updateUser(eq(userId), any(UpdateUserRequest.class))).willReturn(response);

        ResponseEntity<UserResponse> result = userController.updateUser(userId, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("updated@example.com", result.getBody().getEmail());
        assertEquals("Updated User", result.getBody().getFullName());

        verify(updateUserUseCase).updateUser(userId, request);
    }

    @Test
    void deleteUser_ShouldReturn204() throws Exception {
        UUID userId = UUID.randomUUID();

        ResponseEntity<Void> result = userController.deleteUser(userId);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());

        verify(deleteUserUseCase).deleteUser(userId);
    }
}
