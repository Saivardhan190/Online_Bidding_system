package com.application.example.online_bidding_system.dto.request;

import com.application.example.online_bidding_system.entity.StallStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class CreateStallRequest {
    public StallStatus status;
    private int stallNo;
    private String stallName;
    private String location;
    private BigDecimal originalPrice;
    private String description;
    private Timestamp biddingStart;
    private Timestamp biddingEnd;
    private int maxBidders;
    private String category;
    private String image;


}
