package com.mini.ecommerce.user.application.dto.user;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private UUID id;
    private String email;
    private String fullName;
}
