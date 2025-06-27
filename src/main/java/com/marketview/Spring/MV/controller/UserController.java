package com.marketview.Spring.MV.controller;

import com.marketview.Spring.MV.dto.UserDTO;
import com.marketview.Spring.MV.model.User;
import com.marketview.Spring.MV.model.UserWishlist;
import com.marketview.Spring.MV.service.StockService;
import com.marketview.Spring.MV.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor // Injects final fields via constructor
public class UserController {

    private final UserService userService;
    private final StockService stockService;
    private final SimpMessagingTemplate messagingTemplate; // For WebSocket updates

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).body(null);
        }
        String username = authentication.getName(); // Get username from OAuth2/JWT
        User user = userService.findByUsername(username);
        if (user != null) {
            return ResponseEntity.ok(new UserDTO(
                    user.getUsername(),user.getEmail(),user.getFirstName(),user.getLastName()
            ));
        }
        return ResponseEntity.status(404).body(null);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public User createUser(@RequestBody UserDTO userDTO) {
        return userService.createUser(userDTO);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable String id, @RequestBody UserDTO userDTO) {
        return userService.updateUser(id, userDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
    }

    @PostMapping("/wishlist/{type}/{item}")
    public ResponseEntity<UserWishlist> addToWishlist(Authentication authentication,
                                                      @PathVariable String type,
                                                      @PathVariable String item) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        String username = authentication.getName();
        UserWishlist updatedWishlist = stockService.addToWishlist(username, item, type);
        messagingTemplate.convertAndSend("/topic/wishlist/" + username, updatedWishlist);
        return ResponseEntity.ok(updatedWishlist);
    }

    @DeleteMapping("/wishlist/{type}/{item}")
    public ResponseEntity<UserWishlist> removeFromWishlist(Authentication authentication,
                                                           @PathVariable String type,
                                                           @PathVariable String item) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        String username = authentication.getName();
        UserWishlist updatedWishlist = stockService.removeFromWishlist(username, item, type);
        messagingTemplate.convertAndSend("/topic/wishlist/" + username, updatedWishlist);
        return ResponseEntity.ok(updatedWishlist);
    }
}