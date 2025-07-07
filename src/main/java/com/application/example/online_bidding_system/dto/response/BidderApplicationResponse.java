package com.application.example.online_bidding_system.dto.response;

import com.application.example.online_bidding_system.entity.Status;
import lombok.Data;

@Data
public class BidderApplicationResponse {
    private Long applicationId;
    private String studentName;
    private String studentEmail;
    private Long phoneNumber;
    private Status status;
}
