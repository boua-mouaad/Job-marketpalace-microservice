package com.mouaad.jobservice.services;

import com.mouaad.jobservice.dtos.UserProfileDto;
import com.mouaad.jobservice.clients.UserServiceClient;
import com.mouaad.jobservice.dtos.CreateJobRequest;
import com.mouaad.jobservice.dtos.DetailedJobResponse;
import com.mouaad.jobservice.dtos.JobResponse;
// (You might need to create this DTO if you haven't yet!)
// import com.mouaad.jobservice.dtos.JobUpdateRequest;
import com.mouaad.jobservice.entities.Job;
import com.mouaad.jobservice.entities.JobStatus;
import com.mouaad.jobservice.repositories.JobRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class JobService {
    private final JobRepository jobRepository;
    private final UserServiceClient userServiceClient;

    @Transactional
    public JobResponse createJob(@Valid CreateJobRequest request, UUID clientId) {
        // ... (Your existing code stays exactly the same)
        Job job = new Job();
        job.setClientId(clientId);
        job.setTitle(request.getTitle());
        job.setCategory(request.getCategory());
        job.setDescription(request.getDescription());
        job.setBudget(request.getBudget());
        job.setDeadline(request.getDeadline());

        Job savedJob = jobRepository.save(job);
        return mapToResponse(savedJob);
    }

    @Transactional(readOnly = true)
    public List<JobResponse> getOpenJobs(String category) {
        // ... (Your existing code stays exactly the same)
        List<Job> jobs;
        if (category != null && !category.isBlank()) {
            jobs = jobRepository.findByCategoryAndStatusOrderByCreatedAtDesc(category, JobStatus.OPEN);
        } else {
            jobs = jobRepository.findByStatusOrderByCreatedAtDesc(JobStatus.OPEN);
        }
        return jobs.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DetailedJobResponse getDetailedJobById(UUID jobId) {

        Job job = jobRepository.findById(jobId).orElseThrow(() -> new IllegalArgumentException("Job Not Found!"));
        UserProfileDto clientProfile = userServiceClient.getUserProfile(job.getClientId());

        DetailedJobResponse response = new DetailedJobResponse();
        response.setId(job.getId());
        response.setTitle(job.getTitle());
        response.setCategory(job.getCategory());
        response.setDescription(job.getDescription());
        response.setBudget(job.getBudget());
        response.setStatus(job.getStatus());
        response.setDeadline(job.getDeadline());
        response.setClientId(clientProfile.getId());
        response.setClientName(clientProfile.getFullName());
        response.setClientRating(clientProfile.getRatingAvg());

        return response;
    }

    @Transactional(readOnly = true)
    public List<JobResponse> getJobsByClient(UUID clientId) {
        // ... (Your existing code stays exactly the same)
        return jobRepository.findByClientIdOrderByCreatedAtDesc(clientId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ==========================================
    // NEW METHODS: IDOR Protected Delete & Update
    // ==========================================

    @Transactional
    public void deleteJob(UUID jobId, UUID requesterId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job Not Found!"));

        // IDOR Protection: Does the requester actually own this job?
        if (!job.getClientId().equals(requesterId)) {
            throw new IllegalStateException("Unauthorized: You can only delete your own job postings.");
        }

        jobRepository.delete(job);
    }

    /* NOTE: Uncomment this once you create the JobUpdateRequest DTO!
    @Transactional
    public JobResponse updateJob(UUID jobId, JobUpdateRequest request, UUID requesterId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job Not Found!"));

        // IDOR Protection Check
        if (!job.getClientId().equals(requesterId)) {
            throw new IllegalStateException("Unauthorized: You can only update your own job postings.");
        }

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setCategory(request.getCategory());
        job.setBudget(request.getBudget());
        job.setDeadline(request.getDeadline());

        return mapToResponse(jobRepository.save(job));
    }
    */

    // ==========================================
    // UPDATED METHOD: IDOR Protected Status Update
    // ==========================================

    public JobResponse updateJobStatus(UUID id, JobStatus status, UUID requesterId) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job Not Found!"));

        // IDOR Protection Check
        if (!job.getClientId().equals(requesterId)) {
            throw new IllegalStateException("Unauthorized: You can only update the status of your own jobs.");
        }

        job.setStatus(status);
        jobRepository.save(job);
        return mapToResponse(job);
    }

    // ==========================================

    private JobResponse mapToResponse(Job job) {
        // ... (Your existing code stays exactly the same)
        JobResponse response = new JobResponse();
        response.setId(job.getId());
        response.setClientId(job.getClientId());
        response.setTitle(job.getTitle());
        response.setDescription(job.getDescription());
        response.setCategory(job.getCategory());
        response.setBudget(job.getBudget());
        response.setStatus(job.getStatus());
        response.setDeadline(job.getDeadline());
        response.setCreatedAt(job.getCreatedAt());
        return response;
    }
}