package com.market_view.spring.mv.controller;

import com.market_view.spring.mv.model.LiborRate;
import com.market_view.spring.mv.repository.LiborRateRepository;
import com.market_view.spring.mv.dto.LiborRateRequest;
import lombok.Data;
import com.market_view.spring.mv.model.User;
import com.market_view.spring.mv.repository.UserRepository;
import com.market_view.spring.mv.service.CurrencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Data

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')") // Restrict to admin users
public class AdminController {

    private final UserRepository userRepository;
    private final CurrencyService currencyService;
    private final LiborRateRepository liborRateRepository;

    public AdminController(UserRepository userRepository, CurrencyService currencyService, LiborRateRepository liborRateRepository) {
        this.userRepository = userRepository;
        this.currencyService = currencyService;
        this.liborRateRepository = liborRateRepository;
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

    @PutMapping("/libor/update")
    public ResponseEntity<String> updateLibor(
            @RequestBody LiborRateRequest request) {

        LiborRate libor = liborRateRepository.findById("LIBOR")
                .orElse(new LiborRate());

        libor.setNormalRate(request.getNormalRate());
        libor.setSpecialRate(request.getSpecialRate());

        liborRateRepository.save(libor);
        return ResponseEntity.ok("LIBOR rates updated.");
    }
}