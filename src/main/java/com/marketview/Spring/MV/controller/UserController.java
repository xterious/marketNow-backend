package com.marketview.Spring.MV.controller;

import com.marketview.Spring.MV.dto.UserDTO;
import com.marketview.Spring.MV.model.User;
import com.marketview.Spring.MV.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor // Injects final fields via constructor
@Tag(name = "Users", description = "User management APIs")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Retrieves the profile of the currently authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User profile retrieved successfully",
                content = @Content(schema = @Schema(implementation = UserDTO.class))),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).body(null);
        }
        String username = authentication.getName(); // Get username from OAuth2/JWT
        User user = userService.findByUsername(username);
        if (user != null) {
            return ResponseEntity.ok(new UserDTO(
                    user.getUsername(), user.getPassword(), user.getEmail(),user.getFirstName(),user.getLastName()
            ));
        }
        return ResponseEntity.status(404).body(null);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Updates an existing user's information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully",
                content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public User updateUser(
            @Parameter(description = "User ID", example = "507f1f77bcf86cd799439011")
            @PathVariable String id, 
            @RequestBody UserDTO userDTO) {
        return userService.updateUser(id, userDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Deletes a user from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public void deleteUser(
            @Parameter(description = "User ID", example = "507f1f77bcf86cd799439011")
            @PathVariable String id) {
        userService.deleteUser(id);
    }
}