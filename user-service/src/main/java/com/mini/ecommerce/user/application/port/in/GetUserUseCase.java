package com.mini.ecommerce.user.application.port.in;

import com.mini.ecommerce.user.application.dto.PaginationResponse;import com.mini.ecommerce.user.application.dto.UserResponse;

import java.awt.print.Pageable;

public interface GetUserUseCase {
    UserResponse getUserById(Long userId);
    PaginationResponse<UserResponse> getAllUser(String search, Pageable pageable);
}
