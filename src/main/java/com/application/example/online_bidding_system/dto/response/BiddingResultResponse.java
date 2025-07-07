package com.application.example.online_bidding_system.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class BiddingResultResponse {
    private Long stallId;
    private String stallName;
    private BigDecimal winningPrice;
    private String winnerName;
    private String winnerEmail;
    private Timestamp resultTime;
}
