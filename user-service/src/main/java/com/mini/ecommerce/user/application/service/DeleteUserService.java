package com.mini.ecommerce.user.application.service;

import com.mini.ecommerce.user.adapter.out.persistance.mapper.UserMapper;
import com.mini.ecommerce.user.application.exception.ResourceNotFoundException;import com.mini.ecommerce.user.application.port.in.DeleteUserUseCase;
import com.mini.ecommerce.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;import java.util.UUID;

@RequiredArgsConstructor
@Service
public class DeleteUserService implements DeleteUserUseCase {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public void deleteUser(UUID userId) {
        if (!userRepository.existsByUserId(userId)){
            throw new ResourceNotFoundException("User not found");
        }

        userRepository.delete(userId);
    }
}
