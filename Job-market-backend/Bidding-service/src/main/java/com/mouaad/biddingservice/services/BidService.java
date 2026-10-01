package com.mouaad.biddingservice.services;

import com.mouaad.biddingservice.clients.JobServiceClient;
import com.mouaad.biddingservice.clients.UserServiceClient;
import com.mouaad.biddingservice.dtos.*;
import com.mouaad.biddingservice.entities.Bid;
import com.mouaad.biddingservice.entities.BidStatus;
import com.mouaad.biddingservice.repositories.BidRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class BidService {
    private final BidRepository bidRepository;
    private final JobServiceClient jobServiceClient;
    private final UserServiceClient userServiceClient;


    @Transactional
    public BidResponse submitBid(SubmitBidRequest request, UUID freelancerId) {
        // 1. Prevent duplicate bids
        if (bidRepository.existsByJobIdAndFreelancerId(request.getJobId(), freelancerId)) {
            throw new IllegalStateException("You have already submitted a bid for this job.");
        }

        // 2. Fetch Job details via Feign to ensure it exists and is OPEN
        JobDto job = jobServiceClient.getJobById(request.getJobId());
        if (!"OPEN".equals(job.status)) {
            throw new IllegalStateException("Cannot bid on this job because it is no longer open.");
        }

        // 3. Fetch User details via Feign to ensure they are a FREELANCER
        UserDto user = userServiceClient.getUserById(freelancerId);
        if (!"FREELANCER".equals(user.role)) {
            throw new IllegalStateException("Only freelancers can submit bids.");
        }

        // 4. Save the Bid
        Bid bid = new Bid();
        bid.setJobId(request.getJobId());
        bid.setFreelancerId(freelancerId);
        bid.setAmount(request.getAmount());
        bid.setProposal(request.getProposal());
        bid.setDeliveryDays(request.getDeliveryDays());

        Bid savedBid = bidRepository.save(bid);
        return mapToResponse(savedBid);
    }

    @Transactional(readOnly = true)
    public List<BidResponse> getBidsForJob(UUID jobId) {
        return bidRepository.findByJobIdOrderByCreatedAtDesc(jobId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BidResponse acceptBid(UUID bidId, UUID clientId) {
        Bid acceptedBid = bidRepository.findById(bidId)
                .orElseThrow(() -> new IllegalArgumentException("Bid not found"));

        // 1. Verify the client accepting the bid actually owns the job
        JobDto job = jobServiceClient.getJobById(acceptedBid.getJobId());
        if (!job.clientId.equals(clientId)) {
            throw new IllegalStateException("You can only accept bids on your own jobs.");
        }

        // 2. Mark this bid as ACCEPTED
        acceptedBid.setStatus(BidStatus.ACCEPTED);
        bidRepository.save(acceptedBid);

        // 3. Reject all other pending bids for this job
        List<Bid> otherBids = bidRepository.findByJobIdAndIdNot(acceptedBid.getJobId(), bidId);
        for (Bid otherBid : otherBids) {
            otherBid.setStatus(BidStatus.REJECTED);
        }
        bidRepository.saveAll(otherBids);

        // 4. Tell the Job Service to change the job status to IN_PROGRESS via Feign
        jobServiceClient.updateJobStatus(
                acceptedBid.getJobId(),
                new UpdateJobStatusRequest("IN_PROGRESS"));

        return mapToResponse(acceptedBid);
    }

    private BidResponse mapToResponse(Bid bid) {
        BidResponse response = new BidResponse();
        response.setId(bid.getId());
        response.setJobId(bid.getJobId());
        response.setFreelancerId(bid.getFreelancerId());
        response.setAmount(bid.getAmount());
        response.setProposal(bid.getProposal());
        response.setDeliveryDays(bid.getDeliveryDays());
        response.setStatus(bid.getStatus());
        response.setCreatedAt(bid.getCreatedAt());
        return response;
    }
}
