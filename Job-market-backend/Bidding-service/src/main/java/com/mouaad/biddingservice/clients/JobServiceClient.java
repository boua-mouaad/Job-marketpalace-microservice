package com.mouaad.biddingservice.clients;

import com.mouaad.biddingservice.dtos.JobDto;
import com.mouaad.biddingservice.dtos.UpdateJobStatusRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "job-service")
public interface JobServiceClient {
    @GetMapping("/api/jobs/{id}")
    JobDto getJobById(@PathVariable("id") UUID id);
    @PatchMapping("/api/jobs/{id}/status")
    void updateJobStatus(@PathVariable("id") UUID id, @RequestBody UpdateJobStatusRequest request);
}
