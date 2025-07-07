package com.application.example.online_bidding_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;


@Entity
@Getter
@Setter
public class Stall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stallId;

    private int stallNo;
    private String stallName;
    private String location;
    private BigDecimal originalPrice;
    private String description;
    private Timestamp biddingStart;
    private Timestamp biddingEnd;
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StallStatus status;
    private String image;
    private int maxBidders;
    private String category;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToMany(mappedBy = "stall", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Bid> bids;

    @OneToOne(mappedBy = "stall", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private BiddingResult result;
}

