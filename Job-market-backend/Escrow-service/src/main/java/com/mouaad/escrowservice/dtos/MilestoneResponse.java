package com.mouaad.escrowservice.dtos;

import com.mouaad.escrowservice.entities.MilestoneStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class MilestoneResponse {
    private UUID id;
    private String title;
    private BigDecimal amount;
    private MilestoneStatus status;
}
