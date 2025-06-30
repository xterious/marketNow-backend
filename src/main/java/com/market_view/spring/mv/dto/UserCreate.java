package com.market_view.spring.mv.dto;


import lombok.Data;

@Data
public class UserCreate {
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
