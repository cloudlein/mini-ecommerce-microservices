package com.mini.ecommerce.user.application.service;

import com.mini.ecommerce.user.adapter.out.persistance.mapper.UserMapper;
import com.mini.ecommerce.user.application.dto.PaginationResponse;
import com.mini.ecommerce.user.application.dto.UserResponse;
import com.mini.ecommerce.user.application.exception.ResourceNotFoundException;
import com.mini.ecommerce.user.application.port.in.GetUserUseCase;
import com.mini.ecommerce.user.domain.model.User;
import com.mini.ecommerce.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GetUserService implements GetUserUseCase {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    @Override
    public UserResponse getUserById(UUID userId) {
        return userRepository.findByUserId(userId)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    @Override
    public PaginationResponse<UserResponse> getAllUser(String search, UUID lastId, Pageable pageable) {
        Slice<User> userSlice = userRepository.findAll(search, lastId, pageable);

        List<UserResponse> content = userSlice.getContent().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());

        String nextCursor = content.isEmpty() ? null : content.get(content.size() - 1).getId().toString();

        return PaginationResponse.<UserResponse>builder()
                .content(content)
                .currentPage(userSlice.getNumber())
                .size(userSlice.getSize())
                .last(userSlice.isLast())
                .totalElements(0)
                .totalPages(0)
                .nextCursor(nextCursor)
                .build();
    }
}
