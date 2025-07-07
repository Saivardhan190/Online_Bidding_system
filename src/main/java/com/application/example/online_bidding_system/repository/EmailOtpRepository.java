package com.application.example.online_bidding_system.repository;

import com.application.example.online_bidding_system.entity.EmailOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {

    Optional<EmailOtp> findTopByEmailOrderByCreatedAtDesc(String email);
}
