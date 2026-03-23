# Arsitektur JWT Authentication - User Service

Dokumen ini menjelaskan struktur direktori dan arsitektur untuk implementasi JWT Authentication pada `user-service` menggunakan pola **Hexagonal Architecture (Ports and Adapters)**.

## Struktur Direktori

```text
user-service/src/main/java/com/mini/ecommerce/user/
├── adapter/
│   ├── in/
│   │   └── web/
│   │       ├── AuthController.java                 <-- Endpoint REST API untuk login/register (/api/v1/auth/*)
│   │       └── security/                           <-- Konfigurasi Web Security (Spring Security)
│   │           ├── JwtAuthenticationFilter.java    <-- Filter HTTP untuk verifikasi token JWT
│   │           ├── JwtAuthenticationEntryPoint.java<-- Penanganan error 401 Unauthorized
│   │           ├── CustomUserDetails.java          <-- Implementasi UserDetails Spring Security
│   │           ├── CustomUserDetailsService.java   <-- Service untuk load user by email/username
│   │           └── SecurityConfig.java             <-- Konfigurasi utama Spring Security
│   └── out/
│       └── security/                               <-- Implementasi eksternal untuk token JWT
│           └── JwtTokenAdapter.java                <-- Implementasi teknis pembuatan & verifikasi JWT
├── application/
│   ├── dto/
│   │   └── auth/                                   <-- DTO khusus otentikasi
│   │       ├── LoginRequest.java
│   │       ├── RegisterRequest.java
│   │       └── TokenResponse.java
│   ├── port/
│   │   ├── in/
│   │   │   └── AuthUseCase.java                    <-- Interface Use Case untuk login dan register
│   │   └── out/
│   │       └── TokenProviderPort.java              <-- Interface port untuk generate/validasi token
│   └── service/
│       └── AuthService.java                        <-- Implementasi AuthUseCase (Business logic)
└── domain/
    ├── model/
    │   ├── Role.java                               <-- Enum/Value Object untuk role user (opsional)
    │   └── User.java                               <-- Entitas utama User (pastikan ada password hash)
    └── repository/
        └── UserRepository.java                     <-- Port untuk akses data User ke database
```

## Penjelasan Layer

1. **Layer Application (`application/`)**: Merupakan core business logic yang agnostik terhadap teknologi. Tidak tahu menahu soal JWT, hanya tahu konsep Token secara abstrak melalui interface `TokenProviderPort`.
2. **Layer Adapter Out (`adapter/out/security/`)**: Implementasi teknis dari port abstrak. Di sinilah library JWT (seperti `io.jsonwebtoken.jjwt`) digunakan di dalam `JwtTokenAdapter`.
3. **Layer Adapter In (`adapter/in/web/`)**: Menerima request dari luar. Spring Security diletakkan di sini untuk memfilter request HTTP, membaca header `Authorization`, dan mengamankan endpoint.

## Keuntungan Pola Hexagonal
- **Testability**: Sangat mudah melakukan Unit Test pada `AuthService` karena kita bisa melakukan mock pada `TokenProviderPort`.
- **Flexibility**: Jika di masa depan ingin mengganti skema JWT menjadi Paseto, OAuth2, atau implementasi pihak ketiga (misalnya Auth0 / Keycloak), kita hanya perlu membuat Adapter baru untuk `TokenProviderPort` tanpa menyentuh layer Domain atau Application.
