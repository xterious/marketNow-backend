package com.marketview.Spring.MV.service;

import com.marketview.Spring.MV.dto.AuthResponse;
import com.marketview.Spring.MV.dto.LoginRequest;
import com.marketview.Spring.MV.dto.RegisterRequest;
import com.marketview.Spring.MV.model.Role;
import com.marketview.Spring.MV.model.User;
import com.marketview.Spring.MV.repository.RoleRepository;
import com.marketview.Spring.MV.repository.UserRepository;
import com.marketview.Spring.MV.security.CustomUserDetailsService;
import com.marketview.Spring.MV.security.JwtUtil;
import com.marketview.Spring.MV.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Optional;
import java.util.Set;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public ResponseEntity<?> register(RegisterRequest registerRequest) {
        // Check if username is already taken
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Username is already taken!");
        }

        // Check if email is already in use
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Email is already in use!");
        }

        // Create new user
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
                                .description("Default role for regular users")
                                .build()));
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    public ResponseEntity<?> login(LoginRequest request) {
        try {
            System.out.println("Attempting to authenticate user: " + request.getUsername());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userPrincipal);

            System.out.println("Authentication successful for user: " + userPrincipal.getUsername());

            AuthResponse authResponse = new AuthResponse(token, userPrincipal.getUsername(), userPrincipal.getEmail(), userPrincipal.getAuthorities());

            return ResponseEntity.ok(authResponse);

        } catch (BadCredentialsException e) {
            System.out.println("Invalid credentials for user: " + request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
        } catch (Exception e) {
            System.out.println("Login exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed: " + e.getMessage());
        }
    }
}
