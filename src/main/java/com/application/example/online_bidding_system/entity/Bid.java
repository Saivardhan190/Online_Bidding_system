package com.application.example.online_bidding_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bidId;

    @ManyToOne
    @JoinColumn(name = "stall_id")
    private Stall stall;

    @ManyToOne
    @JoinColumn(name = "bidder_id")
    private User bidder;

    private BigDecimal biddedPrice;
    private Timestamp bidTime;
}
