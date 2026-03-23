package com.mini.ecommerce.user.application.service;

import com.mini.ecommerce.user.adapter.out.persistance.mapper.UserMapper;
import com.mini.ecommerce.user.application.dto.user.CreateUserRequest;
import com.mini.ecommerce.user.application.dto.user.UserResponse;
import com.mini.ecommerce.user.application.exception.ConflictException;
import com.mini.ecommerce.user.application.port.in.CreateUserUseCase;
import com.mini.ecommerce.user.domain.model.User;
import com.mini.ecommerce.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CreateUserService implements CreateUserUseCase {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public UserResponse createUser(CreateUserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists");
        }

        User user = userMapper.toDomain(request);
        User userSaved = userRepository.save(user);
        return userMapper.toResponse(userSaved);
    }
}
