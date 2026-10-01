package com.mouaad.escrowservice.repositories;

import com.mouaad.escrowservice.entities.Milestone;
import com.mouaad.escrowservice.entities.MilestoneStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, UUID> {
    // Fetch all milestones for a specific contract
    List<Milestone> findByEscrowId(UUID escrowId);

    // Fetch milestones based on their status (e.g., to find all "PENDING" milestones)
    List<Milestone> findByEscrowIdAndStatus(UUID escrowId, MilestoneStatus status);
}
