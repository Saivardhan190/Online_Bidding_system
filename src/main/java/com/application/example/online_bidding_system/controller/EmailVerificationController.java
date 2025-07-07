package com.application.example.online_bidding_system.controller;

import com.application.example.online_bidding_system.entity.EmailOtp;
import com.application.example.online_bidding_system.repository.EmailOtpRepository;
import com.application.example.online_bidding_system.service.Emailservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.application.example.online_bidding_system.dto.request.OtpVerifyRequest;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.Random;
@RestController
@RequestMapping("/api/otp/")
public class EmailVerificationController {
    @Autowired
    private EmailOtpRepository emailOtpRepository;

    private final Emailservice emailservice;

    public EmailVerificationController(Emailservice emailservice) {
        this.emailservice = emailservice;
    }

    //  Send OTP
    @PostMapping("/send")
    public ResponseEntity<String> sendOtp(@RequestParam String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));

        EmailOtp otpEntity = new EmailOtp();
        otpEntity.setEmail(email);
        otpEntity.setOtp(otp);
        otpEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        otpEntity.setVerified(false);
        emailOtpRepository.save(otpEntity);

        emailservice.sendOtpEmail(email, otp);

        return ResponseEntity.ok("OTP sent to " + email);
    }

    //  Verify OTP
    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpVerifyRequest request) {
        String studentEmail = request.getStudentEmail();
        String otp = request.getOtp();

        Optional<EmailOtp> otpOptional = emailOtpRepository.findTopByEmailOrderByCreatedAtDesc(studentEmail);
        if (otpOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("No OTP found for this email.");
        }

        EmailOtp savedOtp = otpOptional.get();

        long timeDiff = System.currentTimeMillis() - savedOtp.getCreatedAt().getTime();
        if (timeDiff > 5 * 60 * 1000) {
            return ResponseEntity.badRequest().body("OTP expired.");
        }

        if (!savedOtp.getOtp().equals(otp)) {
            return ResponseEntity.badRequest().body("Invalid OTP.");
        }

        savedOtp.setVerified(true);
        emailOtpRepository.save(savedOtp);

        return ResponseEntity.ok("OTP verified successfully.");
    }
}
