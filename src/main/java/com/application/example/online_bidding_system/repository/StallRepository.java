package com.application.example.online_bidding_system.repository;

import com.application.example.online_bidding_system.entity.Stall;
import com.application.example.online_bidding_system.entity.StallStatus;
import com.application.example.online_bidding_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StallRepository extends JpaRepository<Stall,Long> {
    Optional<Stall> findByStallNo(int stallNo);


    // Find stalls by status
    List<Stall> findByStatus(StallStatus status);


    // Find stalls by maxBidders
    List<Stall> findByMaxBiddersGreaterThan(int minBidders);

    // Find all stalls where bidding is currently active
    @Query("SELECT s FROM Stall s WHERE s.biddingStart <= CURRENT_TIMESTAMP AND s.biddingEnd >= CURRENT_TIMESTAMP")
    List<Stall> findAllActiveStalls();
}
