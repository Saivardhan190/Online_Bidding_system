package com.application.example.online_bidding_system.dto.request;

import com.application.example.online_bidding_system.entity.Status;
import lombok.Data;

@Data
public class BidderApplicationRequest {
    private String studentName;
    private String collageId;
    private String studentEmail;
    private Long phoneNumber;
    private String otp;
    private boolean termsAccepted;
}
