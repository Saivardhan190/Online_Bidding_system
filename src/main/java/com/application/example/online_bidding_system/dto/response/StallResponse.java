package com.application.example.online_bidding_system.dto.response;

import com.application.example.online_bidding_system.entity.StallStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class StallResponse {
    private Long stallId;
    private int stallNo;
    private String description;
    private String stallName;
    private String location;
    private BigDecimal originalPrice;
    private Timestamp biddingStart;
    private Timestamp biddingEnd;
    private StallStatus status;
    private String category;
    private String image;
    private int maxBidders;
}
