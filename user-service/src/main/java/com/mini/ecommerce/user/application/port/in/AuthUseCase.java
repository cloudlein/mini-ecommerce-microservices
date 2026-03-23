package com.mini.ecommerce.user.application.port.in;

import com.mini.ecommerce.user.application.dto.auth.LoginRequest;
import com.mini.ecommerce.user.application.dto.auth.TokenResponse;
import com.mini.ecommerce.user.application.dto.user.CreateUserRequest;

public interface AuthUseCase {

    TokenResponse login(LoginRequest loginRequest);

    TokenResponse register(CreateUserRequest request);
}
