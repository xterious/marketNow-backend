package com.market_view.spring.mv.dto;

/**
 * JwtResponse extends AuthResponse for backward compatibility
 * with code that uses JwtResponse instead of AuthResponse
 */
public class JwtResponse extends AuthResponse {
    public JwtResponse(String accessToken) {
        super(accessToken);
    }
}

