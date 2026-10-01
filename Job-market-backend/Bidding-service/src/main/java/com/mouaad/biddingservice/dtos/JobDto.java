package com.mouaad.biddingservice.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class JobDto {
    public UUID id;
    public UUID clientId;
    public String status;
    public BigDecimal budget;
}
