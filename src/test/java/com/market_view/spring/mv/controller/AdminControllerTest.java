package com.market_view.spring.mv.controller;

import com.market_view.spring.mv.model.User;
import com.market_view.spring.mv.repository.UserRepository;
import com.market_view.spring.mv.service.CurrencyService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CurrencyService currencyService;

    @InjectMocks
    private AdminController adminController;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId("123");
        user.setUsername("testuser");
        user.setEmail("test@example.com");
    }

    // --- User CRUD Tests ---

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        ResponseEntity<List<User>> response = adminController.getAllUsers();

        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().isEmpty());
        assertEquals("testuser", response.getBody().get(0).getUsername());
    }

    @Test
    void testGetUserById_Found() {
        when(userRepository.findById("123")).thenReturn(Optional.of(user));

        ResponseEntity<User> response = adminController.getUserById("123");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("testuser", response.getBody().getUsername());
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById("999")).thenReturn(Optional.empty());

        ResponseEntity<User> response = adminController.getUserById("999");

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void testCreateUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        ResponseEntity<User> response = adminController.createUser(user);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("testuser", response.getBody().getUsername());
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateUser_Found() {
        when(userRepository.existsById("123")).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(user);

        user.setUsername("updatedUser");

        ResponseEntity<User> response = adminController.updateUser("123", user);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("updatedUser", response.getBody().getUsername());
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateUser_NotFound() {
        when(userRepository.existsById("999")).thenReturn(false);

        ResponseEntity<User> response = adminController.updateUser("999", user);

        assertEquals(404, response.getStatusCodeValue());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testDeleteUser_Found() {
        when(userRepository.existsById("123")).thenReturn(true);
        doNothing().when(userRepository).deleteById("123");

        ResponseEntity<Void> response = adminController.deleteUser("123");

        assertEquals(204, response.getStatusCodeValue());
        verify(userRepository).deleteById("123");
    }

    @Test
    void testDeleteUser_NotFound() {
        when(userRepository.existsById("999")).thenReturn(false);

        ResponseEntity<Void> response = adminController.deleteUser("999");

        assertEquals(404, response.getStatusCodeValue());
        verify(userRepository, never()).deleteById(anyString());
    }

    // --- LIBOR Rate Management Tests ---

    @Test
    void testGetLiborSpreadNormal() {
        BigDecimal spread = new BigDecimal("0.005");
        when(currencyService.getLiborSpreadNormal()).thenReturn(spread);

        ResponseEntity<BigDecimal> response = adminController.getLiborSpreadNormal();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(spread, response.getBody());
    }

    @Test
    void testGetLiborSpreadSpecial() {
        BigDecimal spread = new BigDecimal("0.002");
        when(currencyService.getLiborSpreadSpecial()).thenReturn(spread);

        ResponseEntity<BigDecimal> response = adminController.getLiborSpreadSpecial();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(spread, response.getBody());
    }

    @Test
    void testSetLiborSpreadNormal() {
        BigDecimal newSpread = new BigDecimal("0.006");

        doNothing().when(currencyService).setLiborSpreadNormal(newSpread);

        ResponseEntity<Void> response = adminController.setLiborSpreadNormal(newSpread);

        assertEquals(200, response.getStatusCodeValue());
        verify(currencyService).setLiborSpreadNormal(newSpread);
    }

    @Test
    void testSetLiborSpreadSpecial() {
        BigDecimal newSpread = new BigDecimal("0.003");

        doNothing().when(currencyService).setLiborSpreadSpecial(newSpread);

        ResponseEntity<Void> response = adminController.setLiborSpreadSpecial(newSpread);

        assertEquals(200, response.getStatusCodeValue());
        verify(currencyService).setLiborSpreadSpecial(newSpread);
    }
}
