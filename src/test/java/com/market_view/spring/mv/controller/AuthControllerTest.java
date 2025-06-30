package com.market_view.spring.mv.controller;

import com.market_view.spring.mv.dto.AuthResponse;
import com.market_view.spring.mv.dto.LoginRequest;
import com.market_view.spring.mv.dto.RegisterRequest;
import com.market_view.spring.mv.model.User;
import com.market_view.spring.mv.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password");

        User mockUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .build();

        when(authService.register(request)).thenReturn(ResponseEntity.ok(mockUser));

        ResponseEntity<User> response = authController.register(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("testuser", response.getBody().getUsername());

        verify(authService).register(request);
    }

    @Test
    void testLogin() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password");

        AuthResponse mockResponse = new AuthResponse("testuser", "test@example.com", null);
        mockResponse.setToken("fake-jwt-token");

        when(authService.login(request)).thenReturn(ResponseEntity.ok(mockResponse));

        ResponseEntity<AuthResponse> response = authController.login(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("testuser", response.getBody().getUsername());
        assertEquals("fake-jwt-token", response.getBody().getToken());

        verify(authService).login(request);
    }
}
