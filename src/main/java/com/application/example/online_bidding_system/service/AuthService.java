package com.application.example.online_bidding_system.service;

import com.application.example.online_bidding_system.dto.request.LoginRequest;
import com.application.example.online_bidding_system.dto.request.SignUpRequest;
import com.application.example.online_bidding_system.dto.response.LoginResponse;
import com.application.example.online_bidding_system.dto.response.UserResponse;
import com.application.example.online_bidding_system.entity.Role;
import com.application.example.online_bidding_system.entity.User;
import com.application.example.online_bidding_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<UserResponse> signUp(SignUpRequest signUpRequest) {
        User user = new User();
        user.setStudentName(signUpRequest.getStudentName());
        user.setStudentEmail(signUpRequest.getStudentEmail());
        user.setStudentPassword(signUpRequest.getStudentPassword());
        user.setStudentAddress(signUpRequest.getStudentAddress());
        user.setDepartment(signUpRequest.getDepartment());
        user.setGender(signUpRequest.getGender());
        user.setYear(signUpRequest.getYear());
        user.setCollageId(signUpRequest.getCollageId());
        user.setRole(Role.USER); // default

        // Step 2: Save user in database
        userRepository.save(user);

        // Step 3: Map User entity → UserResponse DTO
        UserResponse response = new UserResponse();
        response.setStudentId(user.getStudentId());
        response.setStudentName(user.getStudentName());
        response.setStudentEmail(user.getStudentEmail());
        response.setDepartment(user.getDepartment());
        response.setCollageId(user.getCollageId());
        response.setRole(user.getRole());

        // Step 4: Return response DTO
        return ResponseEntity.ok(response);
    }


    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        User user = userRepository.findByStudentEmail(loginRequest.getStudentEmail())
                .orElseThrow(() -> new RuntimeException("User not found with this email"));

        if (!user.getStudentPassword().equals(loginRequest.getStudentPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Step 3: Map to response DTO
        UserResponse response = new UserResponse();
        response.setStudentId(user.getStudentId());
        response.setStudentName(user.getStudentName());
        response.setStudentEmail(user.getStudentEmail());
        response.setDepartment(user.getDepartment());
        response.setCollageId(user.getCollageId());
        response.setRole(user.getRole());

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken("token123");
        loginResponse.setUser(response);

        return ResponseEntity.ok(loginResponse);
    }

}
