package com.mouaad.escrowservice.clients;

import com.mouaad.escrowservice.dtos.BidDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "Bidding-service")
public interface BiddingServiceClient {

    @GetMapping("/api/bids/{id}")
    BidDto getBidById(@PathVariable("id") UUID id);
}