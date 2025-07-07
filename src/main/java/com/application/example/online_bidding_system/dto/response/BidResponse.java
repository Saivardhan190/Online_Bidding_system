package com.application.example.online_bidding_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BidResponse {
    private String studentName; // or message
    private BigDecimal biddedPrice;
    private Timestamp bidTime;
    private Long stallId;
    private Long bidId;

    // For quick response messages like "Stall not found"
    public BidResponse(String studentName, BigDecimal biddedPrice) {
        this.studentName = studentName;
        this.biddedPrice = biddedPrice;
    }
}
