package com.mini.ecommerce.user.application.port.in;

import com.mini.ecommerce.user.application.dto.UpdateUserRequest;
import com.mini.ecommerce.user.application.dto.UserResponse;


public interface UpdateUserUseCase {
    UserResponse updateUser(Long userId, UpdateUserRequest request);
}
