package com.mouaad.escrowservice.dtos;

import com.mouaad.escrowservice.entities.EscrowStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class EscrowResponse {
    private UUID id;
    private UUID bidId;
    private UUID jobId;
    private BigDecimal amountHeld;
    private EscrowStatus status;
    private String clientSecret;
    private List<MilestoneResponse> milestones;
    private LocalDateTime createdAt;
}
