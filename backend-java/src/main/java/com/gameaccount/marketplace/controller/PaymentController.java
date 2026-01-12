package com.gameaccount.marketplace.controller;

import com.gameaccount.marketplace.dto.request.PayOSWebhookRequest;
import com.gameaccount.marketplace.service.PayOSService;
import com.gameaccount.marketplace.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.PaymentLink;

import java.util.Map;

/**
 * REST controller for PayOS payment gateway integration.
 * Handles webhook callbacks and payment status queries.
 */
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${frontend.url}")
public class PaymentController {

    private final PayOSService payOSService;
    private final TransactionService transactionService;

    @Value("${payos.webhook-url}")
    private String webhookUrl;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    /**
     * Webhook endpoint to receive payment callbacks from PayOS.
     * Validates signature and processes payment status updates.
     *
     * For development: Signature verification is logged but doesn't block processing
     * For production: Signature must be valid to process webhook
     *
     * @param webhookBody The webhook data from PayOS (as raw Map for SDK verification)
     * @return HTTP 200 to acknowledge receipt
     */
    @PostMapping("/payos-webhook")
    public ResponseEntity<Map<String, Object>> handlePayOSWebhook(@RequestBody Map<String, Object> webhookBody) {
        try {
            log.info("=== PayOS Webhook Received ===");
            log.info("Webhook body: {}", webhookBody);

            // Verify webhook signature using PayOS SDK v2 API
            boolean signatureValid = false;
            try {
                payOSService.getPayOSClient().webhooks().verify(webhookBody);
                signatureValid = true;
                log.info("Webhook signature verified successfully");
            } catch (Exception sigError) {
                log.warn("Webhook signature verification failed: {}", sigError.getMessage());
                // In development, allow processing without valid signature
                // In production, you may want to reject invalid signatures
                if (!"dev".equals(activeProfile) && !"default".equals(activeProfile)) {
                    log.error("Production mode: Rejecting webhook with invalid signature");
                    return ResponseEntity.ok(Map.of("success", false, "error", "Invalid signature"));
                }
                log.info("Development mode: Processing webhook despite signature failure");
            }

            // Parse webhook body using DTO for type-safe access
            PayOSWebhookRequest webhookRequest = parseWebhookRequest(webhookBody);
            String orderCode = webhookRequest.getOrderCodeAsString();
            String paymentStatus = webhookRequest.getPaymentStatus();

            log.info("Processing webhook: orderCode={}, status={}, signatureValid={}",
                orderCode, paymentStatus, signatureValid);

            // Process payment status with idempotency handling
            switch (paymentStatus) {
                case "PAID":
                    // Complete the transaction and release credentials to buyer
                    try {
                        transactionService.completeTransactionByOrderCode(orderCode);
                        log.info("Transaction completed for orderCode: {}", orderCode);
                    } catch (Exception e) {
                        log.warn("Transaction may already be completed for orderCode: {}", orderCode);
                        // Continue to return 200 - we don't want PayOS to retry
                    }
                    break;

                case "CANCELLED":
                case "EXPIRED":
                    // Cancel the transaction
                    try {
                        transactionService.cancelTransactionByOrderCode(orderCode);
                        log.info("Transaction cancelled for orderCode: {}", orderCode);
                    } catch (Exception e) {
                        log.warn("Transaction may already be cancelled for orderCode: {}", orderCode);
                        // Continue to return 200 - we don't want PayOS to retry
                    }
                    break;

                case "PENDING":
                    // Payment still pending, no action needed
                    log.info("Payment pending for orderCode: {}", orderCode);
                    break;

                default:
                    log.warn("Unknown payment status: {} for orderCode: {}", paymentStatus, orderCode);
            }

            // Return HTTP 200 to acknowledge webhook receipt
            return ResponseEntity.ok(Map.of("success", true));

        } catch (Exception e) {
            log.error("Error processing PayOS webhook", e);
            // Still return 200 to prevent PayOS from retrying
            return ResponseEntity.ok(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * Parses raw webhook Map to typed DTO.
     * Uses ObjectMapper for safe deserialization.
     *
     * @param webhookBody Raw webhook body from PayOS
     * @return Parsed PayOSWebhookRequest
     */
    private PayOSWebhookRequest parseWebhookRequest(Map<String, Object> webhookBody) {
        try {
            // Use Jackson to convert Map to POJO
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            PayOSWebhookRequest request = mapper.convertValue(webhookBody, PayOSWebhookRequest.class);
            log.info("Parsed webhook: data={}, orderCode={}, status={}",
                request.getData(), request.getOrderCodeAsString(), request.getPaymentStatus());
            return request;
        } catch (Exception e) {
            log.error("Failed to parse webhook request", e);
            throw new RuntimeException("Invalid webhook payload", e);
        }
    }

    /**
     * Gets payment status by order code.
     * @param orderCode The PayOS order code
     * @return PaymentLink containing payment status
     */
    @GetMapping("/status/{orderCode}")
    public ResponseEntity<PaymentLink> getPaymentStatus(@PathVariable String orderCode) {
        PaymentLink paymentData = payOSService.getPaymentStatus(orderCode);
        return ResponseEntity.ok(paymentData);
    }

    /**
     * Confirms webhook URL with PayOS.
     * This endpoint should be called once to register the webhook URL with PayOS.
     *
     * @return Success message
     */
    @PostMapping("/confirm-webhook")
    public ResponseEntity<Map<String, String>> confirmWebhook() {
        payOSService.confirmWebhook(webhookUrl);
        return ResponseEntity.ok(Map.of("message", "Webhook URL confirmed successfully"));
    }

    /**
     * Mock webhook endpoint for testing payment completion.
     * Simulates PayOS sending a PAID webhook for a transaction.
     * This is for development/testing only - in production, PayOS sends real webhooks.
     *
     * @param orderCode The PayOS order code to complete payment for
     * @return Success message
     */
    @PostMapping("/mock-complete-payment/{orderCode}")
    public ResponseEntity<Map<String, Object>> mockCompletePayment(@PathVariable String orderCode) {
        log.info("Mock webhook: Completing payment for orderCode: {}", orderCode);

        try {
            // Simulate what PayOS webhook does
            transactionService.completeTransactionByOrderCode(orderCode);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Payment completed successfully for orderCode: " + orderCode
            ));
        } catch (Exception e) {
            log.error("Mock webhook failed", e);
            return ResponseEntity.ok(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
}
