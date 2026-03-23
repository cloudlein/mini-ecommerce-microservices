package com.mini.ecommerce.user.application.service;

import com.mini.ecommerce.user.application.dto.auth.LoginRequest;
import com.mini.ecommerce.user.application.dto.auth.TokenResponse;
import com.mini.ecommerce.user.application.dto.user.CreateUserRequest;
import com.mini.ecommerce.user.application.exception.BusinessException;
import com.mini.ecommerce.user.application.port.in.AuthUseCase;
import com.mini.ecommerce.user.application.port.in.CreateUserUseCase;
import com.mini.ecommerce.user.application.port.out.TokenProviderPort;
import com.mini.ecommerce.user.domain.model.User;
import com.mini.ecommerce.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TokenProviderPort tokenProviderPort;
    private final CreateUserUseCase createUserUseCase;

    @Override
    public TokenResponse login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            log.warn("login failed for email : {}", loginRequest.getEmail());
            throw new BusinessException("Wrong email or password");
        }

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BusinessException("User not found"));

        String token = tokenProviderPort.generateToken(user);

        return TokenResponse.builder()
                .token(token)
                .build();
    }

    @Override
    public TokenResponse register(CreateUserRequest request) {
        createUserUseCase.createUser(request);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("User not found"));

        String token = tokenProviderPort.generateToken(user);

        return TokenResponse.builder()
                .token(token)
                .build();
    }
}
