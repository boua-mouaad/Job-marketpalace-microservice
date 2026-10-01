package com.mouaad.escrowservice.services;

import com.mouaad.escrowservice.clients.BiddingServiceClient;
import com.mouaad.escrowservice.clients.JobServiceClient;
import com.mouaad.escrowservice.clients.UserServiceClient;
import com.mouaad.escrowservice.dtos.*;
import com.mouaad.escrowservice.entities.Escrow;
import com.mouaad.escrowservice.entities.Milestone;
import com.mouaad.escrowservice.entities.MilestoneStatus;
import com.mouaad.escrowservice.repositories.EscrowRepository;
import com.mouaad.escrowservice.repositories.MilestoneRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static com.stripe.Stripe.clientId;

@Service
@AllArgsConstructor
public class EscrowService {
    private final EscrowRepository escrowRepository;
    private final BiddingServiceClient biddingServiceClient;
    private final StripeService stripeService;
    private final MilestoneRepository milestoneRepository;
    private final UserServiceClient userServiceClient;
    private final JobServiceClient jobServiceClient;

    @Transactional
    public EscrowResponse fundEscrow(@Valid FundEscrowRequest request) {
        //1- verify the bid exists and was actually ACCEPTED
        BidDto bid = biddingServiceClient.getBidById(request.getBidId());
        if(!"ACCEPTED".equals(bid.getStatus())){
            throw new IllegalArgumentException("Only accepted bids can be funded .");
        }
        //2 - ensure an escrow doesn't already exist for this bid
        if (escrowRepository.findByBidId(bid.getId()).isPresent()){
            throw new IllegalArgumentException("This Contract has already been funded.");
        }
        try {
            // 3. Ask Stripe to create a PaymentIntent for the bid amount
            PaymentIntent paymentIntent = stripeService.createPaymentIntent(bid.getAmount());
            // 4. Save the Escrow record to PostgreSQL
            Escrow escrow = new Escrow();
            escrow.setBidId(bid.getId());
            escrow.setJobId(bid.getJobId());
            escrow.setAmountHeld(bid.getAmount());
            escrow.setStripePaymentIntentId(paymentIntent.getId());

            Escrow savedEscrow = escrowRepository.save(escrow);
            // 5. Build the response (including the Stripe Client Secret for React)
            return mapToResponse(savedEscrow, paymentIntent.getClientSecret());
        }catch (StripeException e){
            throw new RuntimeException("Failed to process payment with Stripe: " + e.getMessage());
        }
    }
    @Transactional
    public MilestoneResponse createMilestone(UUID escrowId, CreateMilestoneRequest request) {
        Escrow escrow = escrowRepository.findById(escrowId)
                .orElseThrow(() -> new IllegalArgumentException("Escrow contract not found"));

        // Check if the total milestones exceed the total amount held in escrow
        BigDecimal currentMilestonesTotal = escrow.getMilestones().stream()
                .map(Milestone::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal newTotal = currentMilestonesTotal.add(request.getAmount());
        if (newTotal.compareTo(escrow.getAmountHeld()) > 0) {
            throw new IllegalStateException("Milestone amounts cannot exceed the total escrow balance.");
        }

        Milestone milestone = new Milestone();
        milestone.setTitle(request.getTitle());
        milestone.setAmount(request.getAmount());

        // This helper method we wrote earlier automatically links the milestone to the escrow
        escrow.addMilestone(milestone);

        // Saving the escrow automatically saves the milestone because of CascadeType.ALL
        escrowRepository.save(escrow);

        return mapToMilestoneResponse(milestone);
    }
    @Transactional
    public MilestoneResponse submitMilestone(UUID milestoneId) {
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new IllegalArgumentException("Milestone not found"));

        if (milestone.getStatus() != MilestoneStatus.PENDING) {
            throw new IllegalStateException("Only PENDING milestones can be submitted.");
        }

        milestone.setStatus(MilestoneStatus.SUBMITTED);
        Milestone saved = milestoneRepository.save(milestone);
        return mapToMilestoneResponse(saved);
    }

    @Transactional
    public MilestoneResponse approveMilestone(UUID milestoneId, UUID clientId) {
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new IllegalArgumentException("Milestone not found"));

        // Enforce state machine rules
        if (milestone.getStatus() != MilestoneStatus.SUBMITTED) {
            throw new IllegalStateException("Milestone must be SUBMITTED before it can be approved.");
        }

        Escrow escrow = milestone.getEscrow();

        // 1. Get the original Bid to find the Job ID and Freelancer ID
        BidDto bid = biddingServiceClient.getBidById(escrow.getBidId());

        // 2. Security Check: Fetch the Job to ensure this client actually owns it
        JobDto job = jobServiceClient.getJobById(bid.getJobId());
        if (!job.getClientId().equals(clientId)) {
            throw new IllegalStateException("Security Error: Only the client who created the job can approve its milestones.");
        }

        // 3. Get Freelancer's Stripe ID
        UserDto freelancer = userServiceClient.getUserById(bid.getFreelancerId());
        if (freelancer.getStripeAccountId() == null || freelancer.getStripeAccountId().isBlank()) {
            throw new IllegalStateException("Freelancer has not connected a Stripe account.");
        }

        try {
            // 4. Execute idempotent Stripe transfer
            String idempotencyKey = "transfer_milestone_" + milestone.getId();
            stripeService.transferToFreelancer(milestone.getAmount(), freelancer.getStripeAccountId(), idempotencyKey);

            // 5. Update database state
            milestone.setStatus(MilestoneStatus.PAID);
            Milestone saved = milestoneRepository.save(milestone);

            return mapToMilestoneResponse(saved);

        } catch (com.stripe.exception.CardException e) {
            throw new RuntimeException("Payment declined by the processor: " + e.getMessage());
        } catch (com.stripe.exception.InvalidRequestException e) {
            throw new RuntimeException("Freelancer payout account is restricted or invalid.");
        } catch (StripeException e) {
            throw new RuntimeException("A critical payment gateway error occurred. Funds were not moved.");
        }
    }

    private MilestoneResponse mapToMilestoneResponse(Milestone milestone) {
        MilestoneResponse response = new MilestoneResponse();
        response.setId(milestone.getId());
        response.setTitle(milestone.getTitle());
        response.setAmount(milestone.getAmount());
        response.setStatus(milestone.getStatus());
        return response;
    }

    private EscrowResponse mapToResponse(Escrow escrow, String clientSecret) {
        EscrowResponse response = new EscrowResponse();
        response.setId(escrow.getId());
        response.setBidId(escrow.getBidId());
        response.setJobId(escrow.getJobId());
        response.setAmountHeld(escrow.getAmountHeld());
        response.setStatus(escrow.getStatus());
        response.setCreatedAt(escrow.getCreatedAt());
        response.setClientSecret(clientSecret); // React needs this!
        response.setMilestones(Collections.emptyList());
        return response;
    }
}
