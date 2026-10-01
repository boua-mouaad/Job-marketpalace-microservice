package com.mouaad.jobservice.dtos;


import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
public class CreateJobRequest {
    @NotBlank(message = "Job title cannot be empty")
    private String Title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Budget is required")
    @Min(value = 5, message = "Budget must be at least $5.00")
    private BigDecimal budget;

    @NotBlank(message = "Deadline is required")
    @Future(message = "Deadline must be a future date")
    private LocalDateTime deadline;

}
