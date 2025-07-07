package com.application.example.online_bidding_system.service;

import com.application.example.online_bidding_system.dto.request.CreateStallRequest;
import com.application.example.online_bidding_system.dto.response.StallResponse;
import com.application.example.online_bidding_system.entity.Stall;
import com.application.example.online_bidding_system.entity.StallStatus;
import com.application.example.online_bidding_system.repository.StallRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Service
public class StallService {

    private final StallRepository stallRepository;

    public StallService(StallRepository stallRepository) {
        this.stallRepository = stallRepository;
    }


    // Add stall
    public ResponseEntity<StallResponse> addStall(CreateStallRequest request) {
        Stall stall = mapRequestToEntity(request);
        stall.setStatus(StallStatus.AVAILABLE);
        stallRepository.save(stall);
        return ResponseEntity.ok(mapEntityToResponse(stall));
    }

    // Update stall
    public ResponseEntity<StallResponse> updateStall(int stallNo, CreateStallRequest request) {
        Stall stall = stallRepository.findByStallNo(stallNo)
                .orElseThrow(() -> new RuntimeException("Stall not found"));

        // Update fields
        stall.setStallName(request.getStallName());
        stall.setLocation(request.getLocation());
        stall.setOriginalPrice(request.getOriginalPrice());
        stall.setDescription(request.getDescription());
        stall.setBiddingStart(request.getBiddingStart());
        stall.setBiddingEnd(request.getBiddingEnd());
        stall.setStatus(request.getStatus());
        stall.setImage(request.getImage());
        stall.setMaxBidders(request.getMaxBidders());
        stall.setCategory(request.getCategory());

        stallRepository.save(stall);
        return ResponseEntity.ok(mapEntityToResponse(stall));
    }

    // Delete stall
    public ResponseEntity<String> deleteStall(int stallNo) {
        Stall stall = stallRepository.findByStallNo(stallNo)
                .orElseThrow(() -> new RuntimeException("Stall not found"));
        stallRepository.delete(stall);
        return ResponseEntity.ok("Stall deleted successfully.");
    }

    // Get stalls by status
    public List<StallResponse> getStallsByStatus(StallStatus status) {
        return mapEntityListToResponseList(stallRepository.findByStatus(status));
    }

    // Get active stalls
    public List<StallResponse> getActiveStalls() {
        return mapEntityListToResponseList(stallRepository.findAllActiveStalls());
    }

    //Get stalls with min bidders
    public List<StallResponse> getStallsByMinBidders(int minBidders) {
        return mapEntityListToResponseList(stallRepository.findByMaxBiddersGreaterThan(minBidders));
    }

    // Reusable mapper: Request → Entity
    private Stall mapRequestToEntity(CreateStallRequest request) {
        Stall stall = new Stall();
        stall.setStallName(request.getStallName());
        stall.setStallNo(request.getStallNo());
        stall.setLocation(request.getLocation());
        stall.setOriginalPrice(request.getOriginalPrice());
        stall.setDescription(request.getDescription());
        stall.setBiddingStart(request.getBiddingStart());
        stall.setBiddingEnd(request.getBiddingEnd());
        stall.setImage(request.getImage());
        stall.setMaxBidders(request.getMaxBidders());
        stall.setCategory(request.getCategory());
        return stall;
    }

    // Reusable mapper: Entity → Response
    private StallResponse mapEntityToResponse(Stall stall) {
        StallResponse response = new StallResponse();
        response.setStallId(stall.getStallId());
        response.setStallName(stall.getStallName());
        response.setStallNo(stall.getStallNo());
        response.setLocation(stall.getLocation());
        response.setOriginalPrice(stall.getOriginalPrice());
        response.setDescription(stall.getDescription());
        response.setBiddingStart(stall.getBiddingStart());
        response.setBiddingEnd(stall.getBiddingEnd());
        response.setStatus(stall.getStatus());
        response.setImage(stall.getImage());
        response.setMaxBidders(stall.getMaxBidders());
        response.setCategory(stall.getCategory());
        return response;
    }

    // Reusable mapper: List<Entity> → List<Response>
    private List<StallResponse> mapEntityListToResponseList(List<Stall> stallList) {
        List<StallResponse> responseList = new ArrayList<>();
        for (Stall stall : stallList) {
            responseList.add(mapEntityToResponse(stall));
        }
        return responseList;
    }

    @Scheduled(fixedRate = 60000)
    public void updateStallStatusAutomatically() {
        List<Stall> allStalls = stallRepository.findAll();
        for (Stall stall : allStalls) {
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(stall.getBiddingStart().toLocalDateTime()) && now.isBefore(stall.getBiddingEnd().toLocalDateTime())) {
                stall.setStatus(StallStatus.ACTIVE);
            } else if (now.isAfter(stall.getBiddingEnd().toLocalDateTime())) {
                stall.setStatus(StallStatus.CLOSED);
            }
            stallRepository.save(stall);
        }
    }


}
