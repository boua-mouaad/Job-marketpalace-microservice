package com.mouaad.escrowservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "escrows")
public class Escrow {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "bid_id", nullable = false, unique = true)
    private UUID bidId;

    @Column(name = "job_id", nullable = false)
    private UUID jobId;

    @Column(name = "amount_held", nullable = false)
    private BigDecimal amountHeld;

    @Column(name = "stripe_payment_intent_id")
    private String stripePaymentIntentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EscrowStatus status;

    // Internal relationship managed by JPA
    @OneToMany(mappedBy = "escrow", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Milestone> milestones = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = EscrowStatus.HELD;
        }
    }
    public void addMilestone(Milestone milestone) {
        milestones.add(milestone);
        milestone.setEscrow(this);
    }
}
