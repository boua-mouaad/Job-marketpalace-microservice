package com.mouaad.escrowservice.dtos;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class BidDto {
    private UUID id;
    private UUID jobId;
    private UUID freelancerId;
    private BigDecimal amount;
    private String status; // We will check if this is "ACCEPTED"
}
