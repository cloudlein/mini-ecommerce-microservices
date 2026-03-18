package com.mini.ecommerce.user.application.port.in;

import com.mini.ecommerce.user.application.dto.CreateUserRequest;
import com.mini.ecommerce.user.application.dto.UserResponse;


public interface CreateUserUseCase {
    UserResponse createUser(CreateUserRequest request);
}
