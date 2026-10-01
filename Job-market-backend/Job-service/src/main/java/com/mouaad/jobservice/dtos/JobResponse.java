package com.mouaad.jobservice.dtos;

import com.mouaad.jobservice.entities.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobResponse {
    private UUID id;
    private UUID clientId;
    private String title;
    private String description;
    private String category;
    private BigDecimal budget;
    private JobStatus status;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
}
