package com.application.example.online_bidding_system.service;

import com.application.example.online_bidding_system.dto.request.BidderApplicationRequest;
import com.application.example.online_bidding_system.dto.response.BidderApplicationResponse;
import com.application.example.online_bidding_system.entity.*;
import com.application.example.online_bidding_system.repository.BidderApplicationRepository;
import com.application.example.online_bidding_system.repository.EmailOtpRepository;
import com.application.example.online_bidding_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BidderApplicationServiceImpl implements BidderApplicationService {

    @Autowired
    private BidderApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private EmailOtpRepository  emailOtpRepository;

    // Student applies with userId
    @Override
    public ResponseEntity<BidderApplicationResponse> applyAsBidder(Long userId, BidderApplicationRequest request) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        // Validate name
        if (request.getStudentName() == null || request.getStudentName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null); // Or return a message
        }

        // Validate email format
        if (request.getStudentEmail() == null || !request.getStudentEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            return ResponseEntity.badRequest().body(null);
        }

        // Validate Indian phone number (starts with +91 or 10 digits starting with 6-9)
        String phone = String.valueOf(request.getPhoneNumber());
        if (!phone.matches("^[6-9]\\d{9}$")) {
            return ResponseEntity.badRequest().body(null);
        }

        // Check OTP is not null
        if (request.getOtp() == null || request.getOtp().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        // Verify OTP from your EmailOtpRepository
        Optional<EmailOtp> otpOptional = emailOtpRepository.findTopByEmailOrderByCreatedAtDesc(request.getStudentEmail());
        if (otpOptional.isEmpty() || !otpOptional.get().isVerified()) {
            return ResponseEntity.badRequest().body(null); // "Email not verified"
        }

        // Check terms and conditions
        if (!request.isTermsAccepted()) {
            return ResponseEntity.badRequest().body(null); // "Terms not accepted"
        }

        // Check if already applied
        Optional<BidderApplication> existing = applicationRepository.findByUserStudentId(userId);
        if (existing.isPresent()) {
            return ResponseEntity.badRequest().body(null); // "Already applied"
        }

        // Create and save application
        BidderApplication application = new BidderApplication();
        application.setUser(userOpt.get());
        application.setPhoneNumber(request.getPhoneNumber());
        application.setStatus(Status.PENDING);
        applicationRepository.save(application);

        BidderApplicationResponse response = new BidderApplicationResponse();
        response.setApplicationId(application.getApplicationId());
        response.setPhoneNumber(application.getPhoneNumber());
        response.setStatus(application.getStatus());
        response.setStudentEmail(request.getStudentEmail());
        response.setStudentName(request.getStudentName());

        return ResponseEntity.ok(response);
    }


    // Student applies using Principal
    @Override
    public ResponseEntity<String> applyForBidding(BidderApplicationRequest request, Principal principal) {
        String email = principal.getName(); // get current user's email
        Optional<User> userOpt = userRepository.findByStudentEmail(request.getStudentEmail());
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body("User not found");

        Long userId = userOpt.get().getStudentId();

        // Check if already applied
        if (applicationRepository.findByUserStudentId(userId).isPresent()) {
            return ResponseEntity.badRequest().body("You have already applied.");
        }

        // Check OTP verified
        Optional<EmailOtp> otpOpt = emailOtpRepository.findTopByEmailOrderByCreatedAtDesc(email);
        if (otpOpt.isEmpty() || !otpOpt.get().isVerified()) {
            return ResponseEntity.badRequest().body("Please verify your email with OTP before applying.");
        }

        // Save application
        BidderApplication application = new BidderApplication();
        application.setUser(userOpt.get());
        application.setPhoneNumber(request.getPhoneNumber());
        application.setStatus(Status.PENDING);
        applicationRepository.save(application);

        return ResponseEntity.ok("Application submitted successfully");
    }


    // Admin approves
    @Override
    public ResponseEntity<String> approveApplication(Long applicationId) {
        return updateApplicationStatus(applicationId, Status.APPROVED);
    }

    // Admin rejects
    @Override
    public ResponseEntity<String> rejectApplication(Long applicationId) {
        return updateApplicationStatus(applicationId, Status.REJECTED);
    }

    // Update application status
    @Override
    public ResponseEntity<String> updateApplicationStatus(Long applicationId, Status newStatus) {
        Optional<BidderApplication> appOpt = applicationRepository.findById(applicationId);
        if (appOpt.isEmpty()) return ResponseEntity.badRequest().body("Application not found");

        BidderApplication app = appOpt.get();
        app.setStatus(newStatus);
        applicationRepository.save(app);
        return ResponseEntity.ok("Status updated to " + newStatus);
    }

    // Filter applications by status
    @Override
    public List<BidderApplicationResponse> getApplicationsByStatus(Status status) {
        return applicationRepository.findByStatus(status).stream().map(app -> {
            BidderApplicationResponse res = new BidderApplicationResponse();
            res.setApplicationId(app.getApplicationId());
            res.setPhoneNumber(app.getPhoneNumber());
            res.setStatus(app.getStatus());
            return res;
        }).collect(Collectors.toList());
    }

    // Check if user already applied using ID
    @Override
    public boolean hasUserApplied(Long userId) {
        return applicationRepository.findByUserStudentId(userId).isPresent();
    }

    // Check if user already applied using Principal
    @Override
    public Boolean hasUserAlreadyApplied(Principal principal) {
        if (principal == null) return false;
        String email = principal.getName();
        Optional<User> userOpt = userRepository.findByStudentEmail(email);
        return userOpt.map(user -> applicationRepository.findByUser(user).isPresent()).orElse(false);
    }



}
