package com.application.example.online_bidding_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long studentId;
    private String collageId;
    private String studentName;
    @Column(unique = true)
    private String studentEmail;
    private String studentPassword;
    private String studentAddress;
    private String department;
    private String gender;
    private int year;
    @Enumerated(EnumType.STRING)
    private Role role;

    // 🔗 One-to-One with BidderApplication
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private BidderApplication bidderApplication;

    @OneToMany(mappedBy = "bidder")
    private List<Bid> bids;

    @OneToMany(mappedBy = "createdBy")
    private List<Stall> createdStalls;

    @OneToMany(mappedBy = "winner")
    private List<BiddingResult> resultsWon;


}
