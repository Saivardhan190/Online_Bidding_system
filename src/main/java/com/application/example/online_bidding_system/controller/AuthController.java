package com.application.example.online_bidding_system.controller;

import com.application.example.online_bidding_system.dto.request.LoginRequest;
import com.application.example.online_bidding_system.dto.request.SignUpRequest;
import com.application.example.online_bidding_system.dto.response.LoginResponse;
import com.application.example.online_bidding_system.dto.response.UserResponse;
import com.application.example.online_bidding_system.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/Register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        return authService.signUp(signUpRequest);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }
}
