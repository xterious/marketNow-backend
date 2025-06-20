package com.marketview.Spring.MV.controller;

import com.marketview.Spring.MV.dto.LoginRequest;
import com.marketview.Spring.MV.dto.JwtResponse;
import com.marketview.Spring.MV.model.User;
import com.marketview.Spring.MV.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        return authService.register(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        System.out.println("Login request received from: " + request.getRemoteAddr() + 
                           " for user: " + loginRequest.getUsername());
        return authService.login(loginRequest);
    }
}

