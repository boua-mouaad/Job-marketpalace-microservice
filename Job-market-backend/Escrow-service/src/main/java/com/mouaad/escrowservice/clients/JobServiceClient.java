package com.mouaad.escrowservice.clients;

import com.mouaad.escrowservice.dtos.JobDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;


    @FeignClient(name = "Job-service")
    public interface JobServiceClient {

        @GetMapping("/api/jobs/{id}")
        JobDto getJobById(@PathVariable("id") UUID id);
    }

