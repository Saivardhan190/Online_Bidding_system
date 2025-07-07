package com.application.example.online_bidding_system.service;

import com.application.example.online_bidding_system.dto.email.EmailDetails;
import com.application.example.online_bidding_system.dto.response.BiddingResultResponse;
import com.application.example.online_bidding_system.entity.BiddingResult;
import com.application.example.online_bidding_system.entity.Stall;
import com.application.example.online_bidding_system.repository.BiddingResultRepository;
import com.application.example.online_bidding_system.repository.StallRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    // Get result by stallId, only after biddingEnd
    public ResponseEntity<?> getResultByStallId(Long stallId) {
        Optional<Stall> stallOpt = stallRepository.findById(stallId);
        if (stallOpt.isEmpty()) return ResponseEntity.badRequest().body("Stall not found");

        Stall stall = stallOpt.get();

        // Check if bidding has ended
        if (stall.getBiddingEnd() != null && stall.getBiddingEnd().after(new Timestamp(System.currentTimeMillis()))) {
            return ResponseEntity.badRequest().body("Result not available until bidding ends.");
        }

        Optional<BiddingResult> resultOpt = biddingResultRepository.findByStall(stall);
        if (resultOpt.isEmpty()) return ResponseEntity.ok("No result declared for this stall yet.");

        BiddingResultResponse dto = convertToDto(resultOpt.get());
        return ResponseEntity.ok(dto);
    }

    // Get all results (winners), only show if bidding ended
    public List<BiddingResultResponse> getAllResults() {
        List<BiddingResult> all = biddingResultRepository.findAll();

        return all.stream()
                .filter(result -> result.getStall().getBiddingEnd() != null &&
                        result.getStall().getBiddingEnd().before(new Timestamp(System.currentTimeMillis())))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Helper to convert Entity → DTO
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

    @Autowired
    private Emailservice emailService;

    public void declareResult(BiddingResult result) {
        biddingResultRepository.save(result);

        // After saving result, send email to winner
        String to = result.getWinner().getStudentEmail();
        String subject = "Congratulations! You've won the Stall Bid";
        String body = "Dear " + result.getWinner().getStudentName() + ",\n\n" +
                "You have won the bid for stall: " + result.getStall().getStallName() + "\n" +
                "Winning Price: ₹" + result.getWinningPrice() + "\n" +
                "Result Time: " + result.getResultTime() + "\n\n" +
                "Regards,\nOnline Bidding Team";

        EmailDetails email = new EmailDetails(to, subject, body);
        emailService.sendSimpleMail(email);
    }
}
