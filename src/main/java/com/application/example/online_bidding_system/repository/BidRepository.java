package com.application.example.online_bidding_system.repository;

import com.application.example.online_bidding_system.entity.Bid;
import com.application.example.online_bidding_system.entity.Stall;
import com.application.example.online_bidding_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid,Long> {

    // ✅ All bids for a specific stall
    List<Bid> findByStall(Stall stall);

    // ✅ All bids placed by a specific bidder
    List<Bid> findByBidder(User bidder);

    // ✅ Highest bid for a stall
    Optional<Bid> findTopByStallOrderByBiddedPriceDesc(Stall stall);

    // ✅ Latest bid (by time) for a stall
    Optional<Bid> findTopByStallOrderByBidTimeDesc(Stall stall);

    // ✅ Count bids on a stall
    long countByStall(Stall stall);


}
