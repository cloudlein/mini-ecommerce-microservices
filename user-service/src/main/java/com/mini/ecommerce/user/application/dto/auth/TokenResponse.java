package com.mini.ecommerce.user.application.dto.auth;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenResponse {

    private String token;
    private String tokenType = "Bearer";
    private Long expiresIn;

}
