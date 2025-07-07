package com.application.example.online_bidding_system.repository;

import com.application.example.online_bidding_system.entity.BiddingResult;
import com.application.example.online_bidding_system.entity.Stall;
import com.application.example.online_bidding_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BiddingResultRepository extends JpaRepository<BiddingResult,Long> {
    List<BiddingResult> findAllByWinner(User winner);
    Optional<BiddingResult> findByStall(Stall stall);
}
