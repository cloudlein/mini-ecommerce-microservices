# Tutorial Implementasi Auth Service (JWT + Spring Security)

Dokumen ini adalah panduan langkah demi langkah untuk mengimplementasikan fitur Authentication (Login & Register) menggunakan JWT dan Spring Security dengan pendekatan **Hexagonal Architecture**.

---

## Prasyarat (Dependencies)
Pastikan Anda sudah menambahkan *dependency* berikut di dalam `pom.xml` dari proyek `user-service`:

```xml
<!-- Spring Boot Validation (Best Practice untuk validasi input DTO) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JJWT untuk Token Generation & Validation -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>

<!-- Dependency resmi untuk dukungan Argon2 di Spring Security -->
<dependency>
    <groupId>org.bouncycastle</groupId>
    <artifactId>bcprov-jdk18on</artifactId>
    <version>1.78.1</version>
</dependency>

<!-- Lombok untuk mengurangi boilerplate code -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

---

## Langkah 1: Layer Domain & Application (Core Logic)

Pertama, kita siapkan struktur *port* dan *DTO* karena ini adalah pondasi (kontrak) yang tidak peduli pada detail implementasi (Spring/JJWT).

**1. Buat DTO (`application/dto/auth/`)**
- `LoginRequest.java`: Berisi `email` dan `password` dengan anotasi validasi (`@NotBlank`, `@Email`).
- `TokenResponse.java`: Berisi `accessToken` dan *metadata* lainnya.

Contoh `LoginRequest.java` dengan validasi:
```java
package com.mini.ecommerce.user.application.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Email tidak boleh kosong")
    @Email(message = "Format email tidak valid")
    private String email;

    @NotBlank(message = "Password tidak boleh kosong")
    private String password;
}
```

Contoh `TokenResponse.java`:
```java
package com.mini.ecommerce.user.application.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
}
```

**2. Buat Interface Port Out (`application/port/out/TokenProviderPort.java`)**
```java
package com.mini.ecommerce.user.application.port.out;

import com.mini.ecommerce.user.domain.model.User;

public interface TokenProviderPort {
    String generateToken(User user);
    boolean validateToken(String token);
    String getUsernameFromToken(String token);
}
```

**3. Buat Interface UseCase (`application/port/in/AuthUseCase.java`)**
```java
package com.mini.ecommerce.user.application.port.in;

import com.mini.ecommerce.user.application.dto.auth.LoginRequest;
import com.mini.ecommerce.user.application.dto.auth.TokenResponse;

public interface AuthUseCase {
    TokenResponse login(LoginRequest request);
}
```

---

## Langkah 2: Layer Adapter Out (Implementasi JWT)

Sekarang kita mengimplementasikan `TokenProviderPort` menggunakan *library* JJWT.

**1. Buat `JwtTokenAdapter.java` di (`adapter/out/security/`)**
```java
package com.mini.ecommerce.user.adapter.out.security;

import com.mini.ecommerce.user.application.port.out.TokenProviderPort;
import com.mini.ecommerce.user.domain.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenAdapter implements TokenProviderPort {
    
    // Best Practice: Inject secret key & expiration dari application.yml / Environment Variables
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration.ms}")
    private long jwtExpirationMs;

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            // Best practice: Log error spesifik (jangan print stack trace ke user)
            // log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
```

---

## Langkah 3: Layer Adapter In (Konfigurasi Spring Security)

Bagian ini bertanggung jawab membaca Request dari User dan mem-filter Endpoint yang boleh diakses publik dan mana yang butuh Token.

**1. Buat Filter (`adapter/in/web/security/JwtAuthenticationFilter.java`)**
```java
package com.mini.ecommerce.user.adapter.in.web.security;

import com.mini.ecommerce.user.application.port.out.TokenProviderPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProviderPort tokenProvider;
    private final UserDetailsService userDetailsService; // CustomUserDetailsService

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            if (tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
```

**2. Konfigurasi Keamanan (`adapter/in/web/security/SecurityConfig.java`)**
```java
package com.mini.ecommerce.user.adapter.in.web.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll() // Bebas diakses
                .anyRequest().authenticated() // Sisanya wajib Token
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Konfigurasi Argon2 (saltLength, hashLength, parallelism, memory, iterations)
        return new Argon2PasswordEncoder(16, 32, 1, 16384, 2);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
```

---

## Langkah 4: Implementasi Business Logic (Service)

**1. Buat `AuthService.java` di (`application/service/`)**
```java
package com.mini.ecommerce.user.application.service;

import com.mini.ecommerce.user.application.dto.auth.LoginRequest;
import com.mini.ecommerce.user.application.dto.auth.TokenResponse;
import com.mini.ecommerce.user.application.exception.BusinessException;
import com.mini.ecommerce.user.application.exception.ResourceNotFoundException;
import com.mini.ecommerce.user.application.port.in.AuthUseCase;
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
    private final TokenProviderPort tokenProvider;

    @Override
    public TokenResponse login(LoginRequest request) {
        try {
            // 1. Verifikasi kredensial via Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            log.warn("Login failed for email: {}", request.getEmail());
            throw new BusinessException("Email atau password salah"); // Custom exception handling
        }

        // 2. Ambil data User dari Database
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan"));

        // 3. Generate Token
        String token = tokenProvider.generateToken(user);
        
        return TokenResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(86400000L) // Sesuai dengan konfigurasi jwtExpirationMs
                .build();
    }
}
```

---

## Langkah 5: Layer Adapter In Web (REST API Controller)

**1. Buat `AuthController.java` di (`adapter/in/web/`)**
```java
package com.mini.ecommerce.user.adapter.in.web;

import com.mini.ecommerce.user.application.dto.auth.LoginRequest;
import com.mini.ecommerce.user.application.dto.auth.TokenResponse;
import com.mini.ecommerce.user.application.port.in.AuthUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse response = authUseCase.login(request);
        return ResponseEntity.ok(response);
    }
}
```

---

## Kesimpulan
Dengan struktur ini, logika autentikasi terisolasi dari *framework* web (Spring MVC) maupun pustaka kriptografi (JJWT), sehingga *codebase* sangat *maintainable* dan sesuai dengan *best practice* Hexagonal Architecture.
