package com.market_view.spring.mv.service;

import com.market_view.spring.mv.dto.AuthResponse;
import com.market_view.spring.mv.dto.LoginRequest;
import com.market_view.spring.mv.dto.RegisterRequest;
import com.market_view.spring.mv.model.Role;
import com.market_view.spring.mv.model.User;
import com.market_view.spring.mv.repository.RoleRepository;
import com.market_view.spring.mv.repository.UserRepository;
import com.market_view.spring.mv.security.JwtUtil;
import com.market_view.spring.mv.security.UserPrincipal;
import com.market_view.spring.mv.util.CustomException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
@AllArgsConstructor
@Service
public class AuthService {


    private final AuthenticationManager authenticationManager;


    private final UserRepository userRepository;


    private final RoleRepository roleRepository;


    private final PasswordEncoder passwordEncoder;


    private final JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public ResponseEntity<User> register(RegisterRequest registerRequest) {
        // Validate request

        if (registerRequest.getUsername() == null || registerRequest.getUsername().trim().isEmpty()) {
            throw new CustomException("Username cannot be empty", HttpStatus.BAD_REQUEST);
        }
        if (registerRequest.getEmail() == null || registerRequest.getEmail().trim().isEmpty()) {
            throw new CustomException("Email cannot be empty", HttpStatus.BAD_REQUEST);
        }
        if (registerRequest.getPassword() == null || registerRequest.getPassword().trim().isEmpty()) {
            throw new CustomException("Password cannot be empty", HttpStatus.BAD_REQUEST);
        }
        // Check if the username is already taken
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new CustomException("Username is already taken", HttpStatus.CONFLICT);
        }

        // Check if email is already in use
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new CustomException("Email is already in use", HttpStatus.CONFLICT);
        }

        // Create a new user
        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .provider("local")
                .build();

        // Set default role ROLE_USER
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(Role.ROLE_USER)
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .name(Role.ROLE_USER)
                                .build()));
        roles.add(userRole);
        user.setRoles(roles);

        try {
            userRepository.save(user);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            throw new CustomException("Failed to register user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<AuthResponse> login(LoginRequest request) {
        // Validate request
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new CustomException("Username cannot be empty", HttpStatus.BAD_REQUEST);
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new CustomException("Password cannot be empty", HttpStatus.BAD_REQUEST);
        }

        try {
            logger.info("Attempting to authenticate user: {}", request.getUsername());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

            logger.info("Authentication successful for user: {}", userPrincipal.getUsername());

            // Generate JWT token
            String token = jwtUtil.generateToken(userPrincipal);

            AuthResponse authResponse = new AuthResponse(
                userPrincipal.getUsername(),
                userPrincipal.getEmail(),
                userPrincipal.getAuthorities()
            );

            // Add token to the response
            authResponse.setToken(token);

            return ResponseEntity.ok(authResponse);

        } catch (BadCredentialsException e) {
            logger.warn("Invalid credentials for user: {}", request.getUsername());
            throw new CustomException("Invalid username or password", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            throw new CustomException("Login failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}