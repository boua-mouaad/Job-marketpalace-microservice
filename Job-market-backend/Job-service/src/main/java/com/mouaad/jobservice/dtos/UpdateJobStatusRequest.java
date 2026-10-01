package com.mouaad.jobservice.dtos;


import com.mouaad.jobservice.entities.JobStatus;
import jakarta.validation.constraints.NotBlank;

public class UpdateJobStatusRequest {

    @NotBlank(message = "Status cannot be null")
    private JobStatus status;

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }
}
