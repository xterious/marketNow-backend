package com.marketview.Spring.MV.dto;


import lombok.Data;

@Data
public class UserCreate {
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
