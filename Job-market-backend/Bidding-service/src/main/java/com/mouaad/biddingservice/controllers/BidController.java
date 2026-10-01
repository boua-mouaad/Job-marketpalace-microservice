package com.mouaad.biddingservice.controllers;

import com.mouaad.biddingservice.dtos.BidResponse;
import com.mouaad.biddingservice.dtos.SubmitBidRequest;
import com.mouaad.biddingservice.services.BidService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/bids")
public class BidController {
    private final BidService bidService;

    //Freelancer submits a bid
    @PostMapping("/submit")
    public ResponseEntity<BidResponse> submitBid(@Valid @RequestBody SubmitBidRequest request,
                                                 @RequestHeader("X-User-Id") UUID freelancerId) {
        BidResponse response = bidService.submitBid(request, freelancerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Client views all bids for their job
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<BidResponse>> getBidsForJob(@PathVariable UUID jobId) {
        List<BidResponse> responses = bidService.getBidsForJob(jobId);
        return ResponseEntity.ok(responses);
    }

    // Client accepts a specific bid
    @PatchMapping("/{id}/accept")
    public ResponseEntity<BidResponse> acceptBid(
            @PathVariable("id") UUID bidId,
            @RequestHeader("X-User-Id") UUID clientId) {

        BidResponse response = bidService.acceptBid(bidId, clientId);
        return ResponseEntity.ok(response);
    }
}
