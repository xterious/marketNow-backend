package com.market_view.spring.mv.model;

import java.util.HashSet;
import java.util.Set;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    private String password;

    @Indexed(unique = true)
    private String email;

    private String firstName;
    private String lastName;


    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    private String provider; // "local" or "google"
    private String providerId; // for OAuth2 user ID

    // Helper method to add a role
    public void addRole(Role role) {
        this.roles.add(role);
    }

    // Check if a user has a specific role
    public boolean hasRole(String roleName) {
        return this.roles.stream()
                .anyMatch(role -> role.getName().equals(roleName));
    }

}