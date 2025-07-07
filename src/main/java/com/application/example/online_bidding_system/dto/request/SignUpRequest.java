package com.application.example.online_bidding_system.dto.request;

import jakarta.validation.constraints.*;

import lombok.Data;


@Data
public class SignUpRequest {
    private String studentId;

    @NotBlank(message = "College ID is required")
    private String collegeId;

    @NotBlank(message = "Student name is required")
    @Size(min = 2, max = 50, message = "Student name must be between 2 and 50 characters")
    private String studentName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String studentEmail;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    private String studentPassword;


    @NotBlank(message = "Address is required")
    @Size(max = 100, message = "Address must not exceed 100 characters")
    private String studentAddress;

    @NotBlank(message = "Department is required")
    private String department;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "Male|Female|Other", message = "Gender must be Male, Female, or Other")
    private String gender;

    @Min(value = 1, message = "Year must be at least 1")
    @Max(value = 5, message = "Year must not exceed 5")
    private int year;
}
