package com.mouaad.biddingservice.dtos;

import com.mouaad.biddingservice.entities.BidStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class BidResponse {
    private UUID id;
    private UUID jobId;
    private UUID freelancerId;
    private BigDecimal amount;
    private String proposal;
    private Integer deliveryDays;
    private BidStatus status;
    private LocalDateTime createdAt;
}
