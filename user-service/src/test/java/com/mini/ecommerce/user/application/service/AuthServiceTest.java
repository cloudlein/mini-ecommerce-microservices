package com.mini.ecommerce.user.application.service;

import com.mini.ecommerce.user.application.dto.auth.LoginRequest;
import com.mini.ecommerce.user.application.dto.auth.TokenResponse;
import com.mini.ecommerce.user.application.dto.user.CreateUserRequest;
import com.mini.ecommerce.user.application.exception.BusinessException;
import com.mini.ecommerce.user.application.port.in.CreateUserUseCase;
import com.mini.ecommerce.user.application.port.out.TokenProviderPort;
import com.mini.ecommerce.user.domain.model.User;
import com.mini.ecommerce.user.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenProviderPort tokenProviderPort;

    @Mock
    private CreateUserUseCase createUserUseCase;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_success() {
        LoginRequest request = new LoginRequest("john@example.com", "secret");
        User user = User.builder()
                .id(UUID.randomUUID())
                .email(request.getEmail())
                .passwordHash("hashed")
                .fullName("John Doe")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(null);
        given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.of(user));
        given(tokenProviderPort.generateToken(user)).willReturn("token");

        TokenResponse result = authService.login(request);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("token");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(request.getEmail());
        verify(tokenProviderPort).generateToken(user);
    }

    @Test
    void login_badCredentials_throwsBusinessException() {
        LoginRequest request = new LoginRequest("john@example.com", "wrong");

        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willThrow(new BadCredentialsException("bad"));

        assertThrows(BusinessException.class, () -> authService.login(request));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, never()).findByEmail(any());
        verify(tokenProviderPort, never()).generateToken(any());
    }

    @Test
    void login_userNotFound_throwsBusinessException() {
        LoginRequest request = new LoginRequest("john@example.com", "secret");

        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(null);
        given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> authService.login(request));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(request.getEmail());
        verify(tokenProviderPort, never()).generateToken(any());
    }

    @Test
    void register_success() {
        CreateUserRequest request = new CreateUserRequest("john@example.com", "secret", "John Doe");
        User user = User.builder()
                .id(UUID.randomUUID())
                .email(request.getEmail())
                .passwordHash("hashed")
                .fullName(request.getFullName())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.of(user));
        given(tokenProviderPort.generateToken(user)).willReturn("token");

        TokenResponse result = authService.register(request);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("token");
        verify(createUserUseCase).createUser(request);
        verify(userRepository).findByEmail(request.getEmail());
        verify(tokenProviderPort).generateToken(user);
    }

    @Test
    void register_userNotFound_throwsBusinessException() {
        CreateUserRequest request = new CreateUserRequest("john@example.com", "secret", "John Doe");

        given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> authService.register(request));
        verify(createUserUseCase).createUser(request);
        verify(userRepository).findByEmail(request.getEmail());
        verify(tokenProviderPort, never()).generateToken(any());
    }
}
