package com.mouaad.escrowservice.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Transfer;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.TransferCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StripeService {
    @Value("${stripe.api.key}")
    private String stripeApiKey;

    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    public PaymentIntent createPaymentIntent(BigDecimal amount) throws StripeException {
        // Stripe processes all fiat currencies in their smallest unit (cents for USD)
        // Example: $50.00 becomes 5000 cents
        long amountInCents = amount.multiply(new BigDecimal("100")).longValue();

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency("usd")
                // AUTOMATIC means the funds are charged and held in your Stripe account immediately
                .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.AUTOMATIC)
                .build();

        return PaymentIntent.create(params);
    }
    public Transfer transferToFreelancer(BigDecimal amount, String destinationAccountId, String idempotencyKey) throws StripeException {
        long amountInCents = amount.multiply(new BigDecimal("100")).longValue();

        TransferCreateParams params = TransferCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency("usd")
                .setDestination(destinationAccountId)
                .build();

        // The key ensures this specific transfer only happens once
        RequestOptions options = RequestOptions.builder()
                .setIdempotencyKey(idempotencyKey)
                .build();

        return Transfer.create(params, options);
    }

}
