package com.mouaad.escrowservice.controllers;

import com.mouaad.escrowservice.dtos.CreateMilestoneRequest;
import com.mouaad.escrowservice.dtos.EscrowResponse;
import com.mouaad.escrowservice.dtos.FundEscrowRequest;
import com.mouaad.escrowservice.dtos.MilestoneResponse;
import com.mouaad.escrowservice.services.EscrowService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/escrow")
@AllArgsConstructor
public class EscrowController {
    private final EscrowService escrowService;

    @PostMapping("/fund")
    public ResponseEntity<EscrowResponse> fundContract(@Valid @RequestBody FundEscrowRequest request) {
        EscrowResponse response = escrowService.fundEscrow(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PostMapping("/{escrowId}/milestones")
    public ResponseEntity<MilestoneResponse> createMilestone(
            @PathVariable UUID escrowId,
            @Valid @RequestBody CreateMilestoneRequest request) {

        MilestoneResponse response = escrowService.createMilestone(escrowId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PatchMapping("/milestones/{milestoneId}/submit")
    public ResponseEntity<MilestoneResponse> submitMilestone(@PathVariable UUID milestoneId) {
        MilestoneResponse response = escrowService.submitMilestone(milestoneId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/milestones/{milestoneId}/approve")
    public ResponseEntity<MilestoneResponse> approveMilestone(
            @PathVariable UUID milestoneId,
            @RequestHeader("X-User-Id") UUID clientId) {

        MilestoneResponse response = escrowService.approveMilestone(milestoneId, clientId);
        return ResponseEntity.ok(response);
    }
}
