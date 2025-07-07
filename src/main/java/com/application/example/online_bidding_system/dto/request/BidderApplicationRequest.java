package com.application.example.online_bidding_system.dto.request;

import com.application.example.online_bidding_system.entity.Status;
import lombok.Data;

@Data
public class BidderApplicationRequest {
    private String studentName;        // autofilled
    private String studentEmail;              // autofilled
    private Long phoneNumber;          // user input
    private String otp;                // user enters received OTP
    private boolean termsAccepted;
}
