package com.application.example.online_bidding_system.dto.response;

import com.application.example.online_bidding_system.entity.StallStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Kolkata")
    private Timestamp biddingStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Kolkata")
    private Timestamp biddingEnd;
    private StallStatus status;
    private String category;
    private String image;
    private int maxBidders;
}
