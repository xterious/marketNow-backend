package com.marketview.Spring.MV.controller;

import com.marketview.Spring.MV.dto.AuthResponse;
import com.marketview.Spring.MV.dto.LoginRequest;
import com.marketview.Spring.MV.dto.RegisterRequest;
import com.marketview.Spring.MV.model.User;
import com.marketview.Spring.MV.service.AuthService;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Data
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }
}
