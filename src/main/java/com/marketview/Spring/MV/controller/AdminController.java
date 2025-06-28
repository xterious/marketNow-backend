package com.marketview.Spring.MV.controller;

import com.marketview.Spring.MV.model.User;
import com.marketview.Spring.MV.repository.UserRepository;
import com.marketview.Spring.MV.service.CurrencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')") // Restrict to admin users
public class AdminController {

    private final UserRepository userRepository;
    private final CurrencyService currencyService;

    public AdminController(UserRepository userRepository, CurrencyService currencyService) {
        this.userRepository = userRepository;
        this.currencyService = currencyService;
    }

    // CRUD Operations for Users

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User user) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        user.setId(id);
        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // LIBOR Rate Management

    @GetMapping("/libor/normal")
    public ResponseEntity<BigDecimal> getLiborSpreadNormal() {
        return ResponseEntity.ok(currencyService.getLiborSpreadNormal());
    }

    @GetMapping("/libor/special")
    public ResponseEntity<BigDecimal> getLiborSpreadSpecial() {
        return ResponseEntity.ok(currencyService.getLiborSpreadSpecial());
    }

    @PutMapping("/libor/normal")
    public ResponseEntity<Void> setLiborSpreadNormal(@RequestBody BigDecimal newSpread) {
        currencyService.setLiborSpreadNormal(newSpread);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/libor/special")
    public ResponseEntity<Void> setLiborSpreadSpecial(@RequestBody BigDecimal newSpread) {
        currencyService.setLiborSpreadSpecial(newSpread);
        return ResponseEntity.ok().build();
    }
}