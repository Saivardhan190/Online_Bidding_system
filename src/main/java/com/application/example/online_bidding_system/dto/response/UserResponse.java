package com.application.example.online_bidding_system.dto.response;

import com.application.example.online_bidding_system.entity.Role;
import lombok.Data;

@Data
public class UserResponse {
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private String department;
    private Role role;
    private String collegeId;
}
