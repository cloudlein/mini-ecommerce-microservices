package com.mini.ecommerce.user.application.port.in;

import com.mini.ecommerce.user.application.dto.user.UpdateUserRequest;
import com.mini.ecommerce.user.application.dto.user.UserResponse;

import java.util.UUID;


public interface UpdateUserUseCase {
    UserResponse updateUser(UUID userId, UpdateUserRequest request);
}
