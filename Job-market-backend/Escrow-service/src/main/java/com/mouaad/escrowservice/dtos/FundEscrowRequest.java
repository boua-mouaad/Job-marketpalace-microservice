package com.mouaad.escrowservice.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class FundEscrowRequest {
    @NotNull(message = "Bid ID is required to fund an esrow")
    private UUID bidId;
}
