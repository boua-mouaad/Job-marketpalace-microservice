package com.mouaad.userservice.repositories;

import com.mouaad.userservice.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByRevieweeId(UUID revieweeId);

    boolean existsByJobIdAndRevieweeId(UUID jobId, UUID revieweeId);
}