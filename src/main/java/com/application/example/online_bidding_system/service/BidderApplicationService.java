package com.application.example.online_bidding_system.service;

import com.application.example.online_bidding_system.dto.request.BidderApplicationRequest;
import com.application.example.online_bidding_system.dto.response.BidderApplicationResponse;
import com.application.example.online_bidding_system.entity.Status;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

public interface BidderApplicationService {
    ResponseEntity<BidderApplicationResponse> applyAsBidder(Long userId, BidderApplicationRequest request);
    ResponseEntity<String> approveApplication(Long applicationId);
    ResponseEntity<String> rejectApplication(Long applicationId);

    // Update application status
    ResponseEntity<String> updateApplicationStatus(Long applicationId, Status newStatus);

    List<BidderApplicationResponse> getApplicationsByStatus(Status status);
    boolean hasUserApplied(Long userId);
    Boolean hasUserAlreadyApplied(Principal principal);
    ResponseEntity<String> applyForBidding(BidderApplicationRequest request, Principal principal);
}
