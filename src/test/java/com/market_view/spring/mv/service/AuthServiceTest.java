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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("password")
                .build();

        loginRequest = LoginRequest.builder()
                .username("testuser")
                .password("password")
                .build();
    }

    @Test
    void testRegisterSuccess() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("hashed");
        when(roleRepository.findByName(Role.ROLE_USER)).thenReturn(Optional.empty());
        when(roleRepository.save(any())).thenReturn(Role.builder().name(Role.ROLE_USER).build());
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<User> response = authService.register(registerRequest);
        assertEquals("testuser", response.getBody().getUsername());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testRegisterFailsWhenUsernameTaken() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        CustomException ex = assertThrows(CustomException.class,
                () -> authService.register(registerRequest));

        assertEquals("Username is already taken", ex.getMessage());
    }

    @Test
    void testLoginSuccess() {
        Authentication authMock = mock(Authentication.class);
        UserPrincipal userPrincipal = UserPrincipal.create(User.builder()
                .username("testuser")
                .email("test@example.com")
                .roles(Set.of(Role.builder().name(Role.ROLE_USER).build()))
                .build());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authMock);
        when(authMock.getPrincipal()).thenReturn(userPrincipal);
        when(jwtUtil.generateToken(userPrincipal)).thenReturn("mock-jwt");

        ResponseEntity<AuthResponse> response = authService.login(loginRequest);

        assertEquals("testuser", response.getBody().getUsername());
        assertEquals("mock-jwt", response.getBody().getToken());
    }

    @Test
    void testLoginFailsWhenMissingUsername() {
        LoginRequest badRequest = LoginRequest.builder().username("").password("123").build();

        CustomException ex = assertThrows(CustomException.class,
                () -> authService.login(badRequest));

        assertEquals("Username cannot be empty", ex.getMessage());
    }
}
