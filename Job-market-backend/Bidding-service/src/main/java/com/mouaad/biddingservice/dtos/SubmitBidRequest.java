package com.mouaad.biddingservice.dtos;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class SubmitBidRequest {
    @NotNull(message = "Job ID is required")
    private UUID jobId;

    @NotNull(message = "Bid amount is required")
    @DecimalMin(value = "1.00", message = "Bid amount must be at least $1.00")
    private BigDecimal amount;

    @NotBlank(message = "Proposal description cannot be blank")
    @Size(min = 20, message = "Proposal must be at least 20 characters long")
    private String proposal;

    @NotNull(message = "Delivery days is required")
    @Min(value = 1, message = "Delivery time must be at least 1 day")
    private Integer deliveryDays;
}
