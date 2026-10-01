package com.mouaad.biddingservice.repositories;

import com.mouaad.biddingservice.entities.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BidRepository extends JpaRepository<Bid, UUID> {
    // For a Client looking at their posted job
    List<Bid> findByJobIdOrderByCreatedAtDesc(UUID jobId);

    // For a Freelancer looking at their dashboard
    List<Bid> findByFreelancerIdOrderByCreatedAtDesc(UUID freelancerId);

    // Check if a freelancer already bid on this job (to prevent duplicate bids)
    boolean existsByJobIdAndFreelancerId(UUID jobId, UUID freelancerId);

    // Helper to reject all other bids when one is accepted
    List<Bid> findByJobIdAndIdNot(UUID jobId, UUID acceptedBidId);

}
