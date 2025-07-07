package com.application.example.online_bidding_system.repository;

import com.application.example.online_bidding_system.entity.BidderApplication;
import com.application.example.online_bidding_system.entity.Status;
import com.application.example.online_bidding_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BidderApplicationRepository extends JpaRepository<BidderApplication, Long> {
    // Find by user
    Optional<BidderApplication> findByUser(User user);

    // Find all applications with a specific status
    List<BidderApplication> findByStatus(Status status);

    // Check if user has submitted an application
    boolean existsByUser(User user);

    Optional<BidderApplication> findByUserStudentId(Long studentID); // ✅ Correct way
}
