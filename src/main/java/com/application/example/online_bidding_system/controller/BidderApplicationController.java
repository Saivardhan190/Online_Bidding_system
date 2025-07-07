package com.application.example.online_bidding_system.controller;

import com.application.example.online_bidding_system.dto.request.BidderApplicationRequest;
import com.application.example.online_bidding_system.dto.response.BidderApplicationResponse;
import com.application.example.online_bidding_system.entity.Status;
import com.application.example.online_bidding_system.service.BidderApplicationService;
import com.application.example.online_bidding_system.service.Emailservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/bidder-applications")
public class BidderApplicationController {

    @Autowired
    private BidderApplicationService applicationService;

    // ✅ Student applies
    @PostMapping("/apply")
    public ResponseEntity<String> apply(@RequestBody BidderApplicationRequest request, Principal principal) {
        if (principal == null) {
            return ResponseEntity.badRequest().body("User not authenticated. Please login first.");
        }
        return applicationService.applyForBidding(request, principal);
    }

    //  Admin approves
    @PutMapping("/{applicationId}/approve")
    public ResponseEntity<String> approve(@PathVariable Long applicationId) {
        return applicationService.approveApplication(applicationId);
    }

    // Admin rejects
    @PutMapping("/{applicationId}/reject")
    public ResponseEntity<String> reject(@PathVariable Long applicationId) {
        return applicationService.rejectApplication(applicationId);
    }

    // Admin views all by status
    @GetMapping("/status/{status}")
    public List<BidderApplicationResponse> getByStatus(@PathVariable Status status) {
        return applicationService.getApplicationsByStatus(status);
    }

    // Student checks if already applied
    @GetMapping("/has-applied")
    public ResponseEntity<Boolean> hasAlreadyApplied(Principal principal) {
        return ResponseEntity.ok(applicationService.hasUserAlreadyApplied(principal));
    }



}
