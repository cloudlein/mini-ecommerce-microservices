package com.mini.ecommerce.user.application.port.in;

import com.mini.ecommerce.user.application.dto.UpdateUserRequest;
import com.mini.ecommerce.user.application.dto.UserResponse;

import java.util.UUID;


public interface UpdateUserUseCase {
    UserResponse updateUser(UUID userId, UpdateUserRequest request);
}
