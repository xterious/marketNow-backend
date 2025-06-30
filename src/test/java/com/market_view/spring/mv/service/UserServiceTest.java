package com.market_view.spring.mv.service;

import com.market_view.spring.mv.dto.UserDTO;
import com.market_view.spring.mv.model.User;
import com.market_view.spring.mv.repository.UserRepository;
import com.market_view.spring.mv.util.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user1, user2;

    @BeforeEach
    void setup() {
        user1 = new User();
        user1.setId("1");
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");

        user2 = new User();
        user2.setId("2");
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetAllUsernames() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<String> usernames = userService.getAllUsernames();

        assertEquals(List.of("user1", "user2"), usernames);
    }

    @Test
    void testGetUserById_Found() {
        when(userRepository.findById("1")).thenReturn(Optional.of(user1));

        User found = userService.getUserById("1");

        assertEquals("user1", found.getUsername());
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById("3")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserById("3"));
    }

    @Test
    void testCreateUser() {
        UserDTO dto = new UserDTO();
        dto.setUsername("newuser");
        dto.setEmail("newuser@example.com");
        dto.setFirstName("New");
        dto.setLastName("User");

        User newUser = new User();
        newUser.setUsername(dto.getUsername());
        newUser.setEmail(dto.getEmail());
        newUser.setFirstName(dto.getFirstName());
        newUser.setLastName(dto.getLastName());

        when(userRepository.save(any(User.class))).thenReturn(newUser);

        User created = userService.createUser(dto);

        assertEquals("newuser", created.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUser_Found() {
        UserDTO dto = new UserDTO();
        dto.setUsername("updatedUser");
        dto.setEmail("updated@example.com");

        when(userRepository.findById("1")).thenReturn(Optional.of(user1));
        when(userRepository.save(any(User.class))).thenReturn(user1);

        User updated = userService.updateUser("1", dto);

        assertEquals("updatedUser", updated.getUsername());
        assertEquals("updated@example.com", updated.getEmail());
    }

    @Test
    void testUpdateUser_NotFound() {
        UserDTO dto = new UserDTO();
        when(userRepository.findById("99")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.updateUser("99", dto));
    }

    @Test
    void testDeleteUser_Found() {
        when(userRepository.findById("1")).thenReturn(Optional.of(user1));
        doNothing().when(userRepository).delete(user1);

        userService.deleteUser("1");

        verify(userRepository, times(1)).delete(user1);
    }

    @Test
    void testDeleteUser_NotFound() {
        when(userRepository.findById("99")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.deleteUser("99"));
    }

    @Test
    void testFindByUsername_Found() {
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user1));

        User found = userService.findByUsername("user1");

        assertEquals("user1", found.getUsername());
    }

    @Test
    void testFindByUsername_NotFound() {
        when(userRepository.findByUsername("noUser")).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class, () -> userService.findByUsername("noUser"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertTrue(ex.getMessage().contains("User not found with username"));
    }
}
