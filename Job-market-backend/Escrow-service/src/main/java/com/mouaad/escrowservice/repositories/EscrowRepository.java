package com.mouaad.escrowservice.repositories;

import com.mouaad.escrowservice.entities.Escrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EscrowRepository extends JpaRepository<Escrow, UUID> {
    Optional<Escrow> findByBidId(UUID BidId);
    Optional<Escrow> findByJobId(UUID jobId);
}
