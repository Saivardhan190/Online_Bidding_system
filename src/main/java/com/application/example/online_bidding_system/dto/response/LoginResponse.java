package com.application.example.online_bidding_system.dto.response;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private UserResponse user;
}
