package com.marketview.Spring.MV.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;

    @Builder.Default
    private String tokenType = "Bearer";

    private String username;
    private String email;
    private Collection<? extends GrantedAuthority> roles;

    // 1-arg constructor for token only
    public AuthResponse(String accessToken) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
    }

    // 4-arg constructor: accessToken, username, email, roles (tokenType default "Bearer")
    public AuthResponse(String accessToken, String username, String email,
                        Collection<? extends GrantedAuthority> roles) {
        this.accessToken = accessToken;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.tokenType = "Bearer";
    }

    // Optional convenience method for legacy code
    public String getToken() {
        return accessToken;
    }
}
