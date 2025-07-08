package com.application.example.online_bidding_system.service;

import com.application.example.online_bidding_system.dto.request.BidRequest;
import com.application.example.online_bidding_system.dto.response.BidResponse;
import com.application.example.online_bidding_system.entity.*;
import com.application.example.online_bidding_system.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class BidService {

    @Autowired private BidRepository bidRepository;
    @Autowired private StallRepository stallRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private BiddingResultRepository biddingResultRepository;

    private final Map<Long, LocalDateTime> lastBidTimeMap = new HashMap<>();

    // Place a bid
    public ResponseEntity<BidResponse> placeBid(BidRequest request) {
        if (request.getStallId() == null || request.getBidderId() == null || request.getBiddedPrice() == null) {
            return ResponseEntity.badRequest().body(new BidResponse("Missing required fields", BigDecimal.ZERO));
        }

        Optional<Stall> stallOpt = stallRepository.findById(request.getStallId());
        Optional<User> bidderOpt = userRepository.findById(request.getBidderId());

        if (stallOpt.isEmpty()) return ResponseEntity.badRequest().body(new BidResponse("Stall not found", BigDecimal.ZERO));
        if (bidderOpt.isEmpty()) return ResponseEntity.badRequest().body(new BidResponse("Bidder not found", BigDecimal.ZERO));

        Stall stall = stallOpt.get();
        User bidder = bidderOpt.get();

        if (stall.getStatus() == StallStatus.CLOSED) {
            return ResponseEntity.badRequest().body(new BidResponse("Bidding is closed for this stall", BigDecimal.ZERO));
        }

        if (stall.getStatus() != StallStatus.ACTIVE) {
            return ResponseEntity.badRequest().body(new BidResponse("Stall is not active", BigDecimal.ZERO));
        }

        BigDecimal newBid = request.getBiddedPrice();
        BigDecimal currentHighest = bidRepository.findTopByStallOrderByBiddedPriceDesc(stall)
                .map(Bid::getBiddedPrice)
                .orElse(BigDecimal.ZERO);

        if (newBid.compareTo(currentHighest) <= 0) {
            return ResponseEntity.badRequest().body(new BidResponse("Bid must be higher than current", BigDecimal.ZERO));
        }

        Bid bid = new Bid();
        bid.setStall(stall);
        bid.setBidder(bidder);
        bid.setBiddedPrice(newBid);
        bid.setBidTime(Timestamp.valueOf(LocalDateTime.now()));
        bidRepository.save(bid);

        lastBidTimeMap.put(stall.getStallId(), LocalDateTime.now());

        if (newBid.compareTo(stall.getOriginalPrice()) >= 0) {
            stall.setStatus(StallStatus.CLOSED);

            if (stall.getResult() == null && biddingResultRepository.findByStall(stall).isEmpty()) {
                BiddingResult result = new BiddingResult();
                result.setStall(stall);
                result.setWinner(bidder);
                result.setWinningPrice(newBid);
                result.setResultTime(bid.getBidTime());
                biddingResultRepository.save(result);

                stall.setResult(result);
            }

            stallRepository.save(stall);
        }

        // ✅ Finally, return the response
        BidResponse response = new BidResponse();
        response.setBidId(bid.getBidId());
        response.setBiddedPrice(bid.getBiddedPrice());
        response.setBidTime(bid.getBidTime());
        response.setStallId(stall.getStallId());
        response.setStudentName(bidder.getStudentName());

        return ResponseEntity.ok(response);
    }



    // Get all bids for a stall
    public List<BidResponse> getAllBids(Long stallId) {
        Optional<Stall> stallOpt = stallRepository.findById(stallId);
        if (stallOpt.isEmpty()) return Collections.emptyList();

        Stall stall = stallOpt.get();
        List<Bid> bids = bidRepository.findByStall(stall);
        List<BidResponse> responseList = new ArrayList<>();

        for (Bid bid : bids) {
            BidResponse response = new BidResponse();
            response.setStudentName(bid.getBidder().getStudentName());
            response.setBiddedPrice(bid.getBiddedPrice());
            response.setBidTime(bid.getBidTime());
            response.setStallId(stall.getStallId());
            response.setBidId(bid.getBidId());
            responseList.add(response);
        }

        return responseList;
    }

    // Declare winner manually
    public ResponseEntity<BidResponse> declareWinner(Long stallId) {
        Optional<Stall> stallOpt = stallRepository.findById(stallId);
        if (stallOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new BidResponse("Stall not found", BigDecimal.ZERO));
        }

        Stall stall = stallOpt.get();

        // Ensure stall is CLOSED
        if (stall.getStatus() != StallStatus.CLOSED) {
            return ResponseEntity.badRequest().body(new BidResponse("Bidding not closed yet", BigDecimal.ZERO));
        }

        // Get top bid
        Optional<Bid> topBidOpt = bidRepository.findTopByStallOrderByBiddedPriceDesc(stall);
        if (topBidOpt.isEmpty()) {
            return ResponseEntity.ok(new BidResponse("No bids placed", BigDecimal.ZERO));
        }

        Bid topBid = topBidOpt.get();

        // Now check if bid meets or exceeds original price
        if (topBid.getBiddedPrice().compareTo(stall.getOriginalPrice()) < 0) {
            return ResponseEntity.ok(new BidResponse("No valid winner: highest bid below original price", topBid.getBiddedPrice()));
        }

        // Return winner
        return ResponseEntity.ok(new BidResponse("Winner: " + topBid.getBidder().getStudentName(), topBid.getBiddedPrice()));
    }


    // Scheduler: handle bidding times & auto-winner
    @Scheduled(fixedRate = 30000) // every 30 seconds
    public void updateStallsAndDeclareWinners() {
        List<Stall> allStalls = stallRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Stall stall : allStalls) {
            LocalDateTime start = stall.getBiddingStart().toLocalDateTime();
            LocalDateTime end = stall.getBiddingEnd().toLocalDateTime();

            if (now.isBefore(start)) {
                stall.setStatus(StallStatus.AVAILABLE);
            } else if (!now.isBefore(start) && now.isBefore(end)) {
                stall.setStatus(StallStatus.ACTIVE);
            } else if (now.isAfter(end)) {
                stall.setStatus(StallStatus.CLOSED);

                // Declare winner if not already declared
                if (stall.getResult() == null) {
                    Optional<Bid> topBid = bidRepository.findTopByStallOrderByBiddedPriceDesc(stall);
                    if (topBid.isPresent()) {
                        Bid bid = topBid.get();

                        BiddingResult result = new BiddingResult();
                        result.setStall(stall);
                        result.setWinner(bid.getBidder());
                        result.setWinningPrice(bid.getBiddedPrice());
                        result.setResultTime(bid.getBidTime());
                        biddingResultRepository.save(result);

                        stall.setResult(result);
                    }
                }
            }
            stallRepository.save(stall);
        }
    }

    public BidResponse getHighestBid(Long stallId) {
        Optional<Stall> stallOpt = stallRepository.findById(stallId);
        if (stallOpt.isEmpty()) {
            return new BidResponse("Stall not found", BigDecimal.ZERO);
        }

        Stall stall = stallOpt.get();

        return bidRepository.findTopByStallOrderByBiddedPriceDesc(stall)
                .map(bid -> {
                    BidResponse response = new BidResponse();
                    response.setStudentName(bid.getBidder().getStudentName());
                    response.setBiddedPrice(bid.getBiddedPrice());
                    response.setBidTime(bid.getBidTime());
                    response.setStallId(stall.getStallId());
                    response.setBidId(bid.getBidId());
                    return response;
                })
                .orElse(new BidResponse("No bids yet", BigDecimal.ZERO));
    }

}
