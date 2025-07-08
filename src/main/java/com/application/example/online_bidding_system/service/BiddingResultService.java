package com.application.example.online_bidding_system.service;

import com.application.example.online_bidding_system.dto.email.EmailDetails;
import com.application.example.online_bidding_system.dto.response.BiddingResultResponse;
import com.application.example.online_bidding_system.entity.Bid;
import com.application.example.online_bidding_system.entity.BiddingResult;
import com.application.example.online_bidding_system.entity.Stall;
import com.application.example.online_bidding_system.entity.StallStatus;
import com.application.example.online_bidding_system.repository.BidRepository;
import com.application.example.online_bidding_system.repository.BiddingResultRepository;
import com.application.example.online_bidding_system.repository.StallRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BiddingResultService {

    @Autowired
    private BiddingResultRepository biddingResultRepository;
    @Autowired
    private StallRepository stallRepository;
    @Autowired
    private BidRepository bidRepository;
    @Autowired
    private Emailservice emailService;

    public ResponseEntity<?> getResultByStallId(Long stallId) {
        Optional<Stall> stallOpt = stallRepository.findById(stallId);
        if (stallOpt.isEmpty()) return ResponseEntity.badRequest().body("Stall not found");

        Stall stall = stallOpt.get();
        if (stall.getBiddingEnd() != null && stall.getBiddingEnd().after(new Timestamp(System.currentTimeMillis()))) {
            return ResponseEntity.badRequest().body("Result not available until bidding ends.");
        }

        Optional<BiddingResult> resultOpt = biddingResultRepository.findByStall(stall);
        if (resultOpt.isEmpty()) return ResponseEntity.ok("No result declared for this stall yet.");

        return ResponseEntity.ok(convertToDto(resultOpt.get()));
    }

    public List<BiddingResultResponse> getAllResults() {
        return biddingResultRepository.findAll().stream()
                .filter(result -> result.getStall().getBiddingEnd() != null &&
                        result.getStall().getBiddingEnd().before(new Timestamp(System.currentTimeMillis())))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private BiddingResultResponse convertToDto(BiddingResult result) {
        BiddingResultResponse dto = new BiddingResultResponse();
        dto.setStallId(result.getStall().getStallId());
        dto.setStallName(result.getStall().getStallName());
        dto.setWinningPrice(result.getWinningPrice());
        dto.setWinnerName(result.getWinner().getStudentName());
        dto.setWinnerEmail(result.getWinner().getStudentEmail());
        dto.setResultTime(result.getResultTime());
        return dto;
    }

    // Scheduled method runs every minute
    @Scheduled(fixedRate = 60000)
    public void checkAndDeclareWinners() {
        List<Stall> closedStalls = stallRepository.findByStatus(StallStatus.CLOSED);

        for (Stall stall : closedStalls) {
            if (biddingResultRepository.findByStall(stall).isPresent()) continue;

            Optional<Bid> highestBid = bidRepository.findTopByStallOrderByBiddedPriceDesc(stall);
            highestBid.ifPresent(bid -> declareResult(bid, stall));
        }
    }

    // Declare result and send mail
    private void declareResult(Bid winningBid, Stall stall) {
        BiddingResult result = new BiddingResult();
        result.setStall(stall);
        result.setWinner(winningBid.getBidder());
        result.setWinningPrice(winningBid.getBiddedPrice());
        result.setResultTime(new Timestamp(System.currentTimeMillis()));

        biddingResultRepository.save(result);

        sendWinnerEmail(result);
    }

    private void sendWinnerEmail(BiddingResult result) {
        try {
            String to = result.getWinner().getStudentEmail();
            String subject = "🎉 Congratulations! You’ve Won the Stall Bid";
            String body = "Dear " + result.getWinner().getStudentName() + ",\n\n" +
                    "You have won the bid for the stall: " + result.getStall().getStallName() + "\n" +
                    "Winning Price: ₹" + result.getWinningPrice() + "\n" +
                    "Result Time: " + result.getResultTime() + "\n\n" +
                    "Thank you for participating!\n" +
                    "Regards,\nOnline Bidding Team";

            EmailDetails email = new EmailDetails(to, subject, body);
            emailService.sendSimpleMail(email);

        } catch (Exception e) {
            // Log error (or use logger if configured)
            System.out.println("Failed to send result email: " + e.getMessage());
        }
    }
}
