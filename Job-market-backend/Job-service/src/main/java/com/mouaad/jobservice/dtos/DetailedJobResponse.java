package com.mouaad.jobservice.dtos;

import com.mouaad.jobservice.entities.JobStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class DetailedJobResponse {
    private UUID id;
    private String title;
    private String description;
    private String category;
    public BigDecimal budget;
    private JobStatus status;
    private LocalDateTime deadline;

    //Client information stitched in from user service
    private UUID clientId;
    private String clientName;
    private Double clientRating;
}
