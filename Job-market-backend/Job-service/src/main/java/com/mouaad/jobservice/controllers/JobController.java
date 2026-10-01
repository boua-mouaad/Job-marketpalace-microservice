package com.mouaad.jobservice.controllers;

import com.mouaad.jobservice.dtos.CreateJobRequest;
import com.mouaad.jobservice.dtos.DetailedJobResponse;
import com.mouaad.jobservice.dtos.JobResponse;
import com.mouaad.jobservice.dtos.UpdateJobStatusRequest;
import com.mouaad.jobservice.services.JobService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    //post a new job
    @PostMapping("/create")
    public ResponseEntity<JobResponse> createJob
            (@Valid @RequestBody CreateJobRequest request,
             @RequestHeader("X-User-Id") UUID clientId) {

    JobResponse response = jobService.createJob(request, clientId);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //public feed for freelancers to browse OPEN jobs
    @GetMapping("/search")
    public ResponseEntity<List<JobResponse>> getOpenJobs(@RequestParam(required = false)String category) {
        List<JobResponse> jobs = jobService.getOpenJobs(category);
        return ResponseEntity.status(HttpStatus.OK).body(jobs);
    }
    //Get specified job details
    @GetMapping("/{id}/detailed")
    public ResponseEntity<DetailedJobResponse>  getDetailedJobById(@PathVariable UUID id) {
        DetailedJobResponse response = jobService.getDetailedJobById(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //client dashboard : get all the jobs posted by the authenticated client
    @GetMapping("/my-jobs")
    public ResponseEntity<List<JobResponse>> getMyJobs(@RequestHeader("X-User-Id") UUID clientId){
        List<JobResponse> jobs = jobService.getJobsByClient(clientId);
        return ResponseEntity.ok(jobs);
    }
    //update jobs status
    @PatchMapping("/{id}/status")
    public ResponseEntity<JobResponse> updateJobStatus(@PathVariable UUID id, @Valid @RequestBody UpdateJobStatusRequest request, UUID requesterId) {
        JobResponse response = jobService.updateJobStatus(id, request.getStatus(),  requesterId);
        return ResponseEntity.ok(response);
    }

}
