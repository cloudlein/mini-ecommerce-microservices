package com.mini.ecommerce.user.application.service;

import com.mini.ecommerce.user.adapter.out.persistance.mapper.UserMapper;
import com.mini.ecommerce.user.application.dto.user.UpdateUserRequest;
import com.mini.ecommerce.user.application.dto.user.UserResponse;
import com.mini.ecommerce.user.application.exception.ConflictException;
import com.mini.ecommerce.user.application.exception.ResourceNotFoundException;
import com.mini.ecommerce.user.application.port.in.UpdateUserUseCase;
import com.mini.ecommerce.user.domain.model.User;
import com.mini.ecommerce.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;


@RequiredArgsConstructor
@Service
public class UpdateUserService implements UpdateUserUseCase {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse updateUser(UUID userId, UpdateUserRequest request) {

        User oldUser = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user not found"));

        if (request.getEmail() != null & !request.getEmail().equals(oldUser.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ConflictException("email already exists");
            }

            oldUser = oldUser.toBuilder().email(request.getEmail()).build();
        }

        if (request.getFullName() != null){
            oldUser = oldUser.toBuilder().fullName(request.getFullName()).build();
        }

        User savedUser = userRepository.save(oldUser);
        return userMapper.toResponse(savedUser);
    }
}
