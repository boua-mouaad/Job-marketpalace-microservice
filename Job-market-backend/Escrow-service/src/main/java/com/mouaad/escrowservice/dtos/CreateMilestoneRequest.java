package com.mouaad.escrowservice.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@NoArgsConstructor
public class CreateMilestoneRequest {
    @NotBlank(message = "Milestone title cannot be empty")
    private String title;

    @NotNull(message = "Milestone amount is required")
    @DecimalMin(value = "1.00", message = "Amount must be at least $1.00")
    private BigDecimal amount;
}
