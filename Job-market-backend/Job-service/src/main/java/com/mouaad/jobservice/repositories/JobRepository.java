package com.mouaad.jobservice.repositories;

import com.mouaad.jobservice.entities.Job;
import com.mouaad.jobservice.entities.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {
    //For freelancers to browser available gigs
    List<Job> findByStatusOrderByCreatedAtDesc(JobStatus status);

    //For filtering by category
    List<Job> findByCategoryAndStatusOrderByCreatedAtDesc(String category, JobStatus status);

    //For a client viewing thein own posts jobs via the dashboard
    List<Job> findByClientIdOrderByCreatedAtDesc(UUID clientId);
}
