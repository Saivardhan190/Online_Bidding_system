package com.application.example.online_bidding_system.controller;

import com.application.example.online_bidding_system.dto.request.CreateStallRequest;
import com.application.example.online_bidding_system.dto.response.StallResponse;
import com.application.example.online_bidding_system.entity.StallStatus;
import com.application.example.online_bidding_system.service.StallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/stall")
public class StallController {
    @Autowired
    private StallService stallService;

    @PostMapping("/addstall")
    public ResponseEntity<StallResponse> addStall(@RequestBody CreateStallRequest createStallRequest) {
        return stallService.addStall(createStallRequest);
    }
    @GetMapping("/status/{status}")
    public List<StallResponse> getStallsByStatus(@PathVariable String status) {
        return stallService.getStallsByStatus(StallStatus.valueOf(status.toUpperCase()));
    }

    @GetMapping("/active")
    public List<StallResponse> getActiveStalls() {
        return stallService.getActiveStalls();
    }

    @GetMapping("/min-bidders/{count}")
    public List<StallResponse> getByMinBidders(@PathVariable int count) {
        return stallService.getStallsByMinBidders(count);
    }

    @PutMapping("/updatestall/{stallNo}")
    public ResponseEntity<StallResponse> updateStall(@PathVariable int stallNo, @RequestBody CreateStallRequest request) {
        return stallService.updateStall(stallNo, request);
    }

    @DeleteMapping("/deletestall/{stallNo}")
    public ResponseEntity<String> deleteStall(@PathVariable int stallNo) {
        return stallService.deleteStall(stallNo);
    }


}
