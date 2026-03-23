package com.mini.ecommerce.user.application.port.out;

import com.mini.ecommerce.user.domain.model.User;

public interface TokenProviderPort {

    String generateToken(User user);
    boolean validateToken(String token);
    String getUsernameFromToken(String token);
}
