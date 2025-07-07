package com.application.example.online_bidding_system.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BidRequest {
    private Long stallId;
    private Long bidderId;
    private BigDecimal biddedPrice;

}
