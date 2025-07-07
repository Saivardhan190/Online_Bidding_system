package com.application.example.online_bidding_system.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String studentEmail;
    private String studentPassword;
}
