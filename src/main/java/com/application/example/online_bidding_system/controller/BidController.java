package com.application.example.online_bidding_system.controller;

import com.application.example.online_bidding_system.dto.request.BidRequest;
import com.application.example.online_bidding_system.dto.response.BidResponse;
import com.application.example.online_bidding_system.service.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bid")
public class BidController {

    @Autowired
    private BidService bidService;

    //  Place bid
    @PostMapping("/place")
    public ResponseEntity<BidResponse> placeBid(@RequestBody BidRequest bidRequest) {
        return bidService.placeBid(bidRequest);
    }


    // All bids of stall
    @GetMapping("/all/{stallId}")
    public List<BidResponse> getAllBids(@PathVariable Long stallId) {
        return bidService.getAllBids(stallId);
    }

    // Highest bid of stall
    @GetMapping("/highest/{stallId}")
    public BidResponse getHighestBid(@PathVariable Long stallId) {
        return bidService.getHighestBid(stallId);
    }

    // Declare winner
    @GetMapping("/declare-winner/{stallId}")
    public ResponseEntity<BidResponse> declareWinner(@PathVariable Long stallId) {
        return bidService.declareWinner(stallId);
    }
}
