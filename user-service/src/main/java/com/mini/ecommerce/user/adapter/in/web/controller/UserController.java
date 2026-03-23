package com.mini.ecommerce.user.adapter.in.web.controller;

import com.mini.ecommerce.user.application.dto.user.CreateUserRequest;
import com.mini.ecommerce.user.application.dto.user.PaginationResponse;
import com.mini.ecommerce.user.application.dto.user.UpdateUserRequest;
import com.mini.ecommerce.user.application.dto.user.UserResponse;
import com.mini.ecommerce.user.application.port.in.CreateUserUseCase;
import com.mini.ecommerce.user.application.port.in.DeleteUserUseCase;
import com.mini.ecommerce.user.application.port.in.GetUserUseCase;
import com.mini.ecommerce.user.application.port.in.UpdateUserUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = createUserUseCase.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID userId) {
        UserResponse response = getUserUseCase.getUserById(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PaginationResponse<UserResponse>> getAllUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UUID lastId,
            @PageableDefault(size = 20) Pageable pageable) {
        PaginationResponse<UserResponse> response = getUserUseCase.getAllUser(search, lastId, pageable);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse response = updateUserUseCase.updateUser(userId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        deleteUserUseCase.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
