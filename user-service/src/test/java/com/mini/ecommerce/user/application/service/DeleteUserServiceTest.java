package com.mini.ecommerce.user.application.service;

import com.mini.ecommerce.user.application.exception.ResourceNotFoundException;
import com.mini.ecommerce.user.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class DeleteUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DeleteUserService service;

    @Test
    void deleteUser_success() {
        UUID id = UUID.randomUUID();
        given(userRepository.existsByUserId(id)).willReturn(true);

        service.deleteUser(id);

        verify(userRepository).existsByUserId(id);
        verify(userRepository).delete(id);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteUser_notFound() {
        UUID id = UUID.randomUUID();
        given(userRepository.existsByUserId(id)).willReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.deleteUser(id));

        verify(userRepository).existsByUserId(id);
        verifyNoMoreInteractions(userRepository);
    }
}
