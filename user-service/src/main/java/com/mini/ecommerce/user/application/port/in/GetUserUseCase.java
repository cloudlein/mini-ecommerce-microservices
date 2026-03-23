package com.mini.ecommerce.user.application.port.in;

import com.mini.ecommerce.user.application.dto.user.PaginationResponse;
import com.mini.ecommerce.user.application.dto.user.UserResponse;

import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface GetUserUseCase {
    UserResponse getUserById(UUID userId);
    PaginationResponse<UserResponse> getAllUser(String search, UUID lastId, Pageable pageable);
}
