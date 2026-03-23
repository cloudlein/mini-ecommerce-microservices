package com.mini.ecommerce.user.adapter.out.persistance.repository;

import com.mini.ecommerce.user.adapter.out.persistance.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class JpaUserRepositoryTest {

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        @Primary
        UserDetailsService userDetailsService() {
            return new InMemoryUserDetailsManager(
                    User.withUsername("test-user")
                            .password("password")
                            .authorities("ROLE_USER")
                            .build()
            );
        }
    }

    @Autowired
    private JpaUserRepository repository;

    @Test
    void save_and_findByEmail_success() {
        UserEntity entity = UserEntity.builder()
                .email("test@example.com")
                .passwordHash("hash")
                .fullName("Test User")
                .build();

        repository.save(entity);

        Optional<UserEntity> found = repository.findByEmail("test@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void existsByEmail_returnsTrue_whenExists() {
        UserEntity entity = UserEntity.builder()
                .email("exists@example.com")
                .passwordHash("hash")
                .fullName("Test")
                .build();
        repository.save(entity);

        boolean exists = repository.existsByEmail("exists@example.com");

        assertThat(exists).isTrue();
    }

    @Test
    void findAllByFullName_returnsMatches() {
        repository.save(UserEntity.builder().email("1@ex.com").passwordHash("h").fullName("John Doe").build());
        repository.save(UserEntity.builder().email("2@ex.com").passwordHash("h").fullName("Jane Doe").build());
        repository.save(UserEntity.builder().email("3@ex.com").passwordHash("h").fullName("Jack Smith").build());

        Slice<UserEntity> slice = repository.findAllByFullNameContainingIgnoreCaseOrderByIdAsc("doe", PageRequest.of(0, 10));

        assertThat(slice.getContent()).hasSize(2);
        assertThat(slice.getContent()).extracting(UserEntity::getFullName).containsExactlyInAnyOrder("John Doe", "Jane Doe");
    }

    @Test
    void deleteById_removesEntity() {
        UserEntity entity = repository.save(UserEntity.builder()
                .email("delete@example.com")
                .passwordHash("h")
                .fullName("To Delete")
                .build());

        UUID id = entity.getId();
        assertThat(repository.existsById(id)).isTrue();

        repository.deleteById(id);
        
        Optional<UserEntity> deleted = repository.findById(id);
        assertThat(deleted).isEmpty();
    }
}
