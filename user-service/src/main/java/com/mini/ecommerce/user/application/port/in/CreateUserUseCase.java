package com.mini.ecommerce.user.application.port.in;

import com.mini.ecommerce.user.application.dto.user.CreateUserRequest;
import com.mini.ecommerce.user.application.dto.user.UserResponse;


public interface CreateUserUseCase {
    UserResponse createUser(CreateUserRequest request);
}
