package com.marketview.Spring.MV.dto;

public class AuthRequest {
    private String username;
    private String password;

    // Getters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // Setters (optional for deserialization)
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

