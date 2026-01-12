package com.gameaccount.marketplace.service;

import com.gameaccount.marketplace.dto.response.PaymentLinkResponse;
import com.gameaccount.marketplace.entity.Transaction;
import com.gameaccount.marketplace.exception.BusinessException;
import com.gameaccount.marketplace.exception.ResourceNotFoundException;
import com.gameaccount.marketplace.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLink;
import vn.payos.model.v2.paymentRequests.PaymentLinkItem;

/**
 * Service layer for PayOS payment gateway integration.
 * Handles payment link creation, status queries, and webhook confirmation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PayOSService {

    private final PayOS payOSClient;
    private final TransactionRepository transactionRepository;

    @Value("${payos.return-url}")
    private String returnUrl;

    @Value("${payos.cancel-url}")
    private String cancelUrl;

    /**
     * Creates a payment link with PayOS for the given transaction.
     * @param transactionId The transaction ID to create payment for
     * @return PaymentLinkResponse containing checkout URL and order code
     * @throws ResourceNotFoundException if transaction not found
     */
    public PaymentLinkResponse createPaymentLink(Long transactionId) {
        log.info("Creating PayOS payment link for transaction: {}", transactionId);

        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + transactionId));

        // Check if payment link already created (prevent duplicate)
        if (transaction.getOrderCode() != null) {
            log.warn("Payment link already exists for transaction: {}, orderCode={}", transactionId, transaction.getOrderCode());
            throw new BusinessException("Payment link already created for this transaction. Order code: " + transaction.getOrderCode());
        }

        // Generate unique order code (timestamp + random suffix prevents race condition)
        String orderCode = generateOrderCode();

        // Store order code in transaction BEFORE calling PayOS API
        // This ensures orderCode is always available for webhook reconciliation
        transaction.setOrderCode(orderCode);
        transactionRepository.save(transaction);

        // Create item data for payment
        PaymentLinkItem itemData = PaymentLinkItem.builder()
            .name("Game Account: " + transaction.getAccount().getTitle())
            .quantity(1)
            .price(transaction.getAmount().longValue())
            .build();

        // Create payment link request (v2 API)
        CreatePaymentLinkRequest paymentData = CreatePaymentLinkRequest.builder()
            .orderCode(Long.parseLong(orderCode))
            .amount(transaction.getAmount().longValue())
            .description("Buy game account") // Max 25 characters for PayOS
            .item(itemData)
            .cancelUrl(cancelUrl)
            .returnUrl(returnUrl)
            .build();

        try {
            // Create payment link via PayOS v2 API
            CreatePaymentLinkResponse response = payOSClient.paymentRequests().create(paymentData);

            log.info("Payment link created: orderCode={}, checkoutUrl={}",
                orderCode, response.getCheckoutUrl());

            return PaymentLinkResponse.builder()
                .orderCode(orderCode)
                .checkoutUrl(response.getCheckoutUrl())
                .amount(transaction.getAmount())
                .build();

        } catch (Exception e) {
            log.error("Failed to create PayOS payment link", e);
            throw new RuntimeException("Failed to create payment link: " + e.getMessage(), e);
        }
    }

    /**
     * Gets payment status from PayOS by order code.
     * @param orderCode The PayOS order code as string
     * @return PaymentLink containing payment status
     */
    public PaymentLink getPaymentStatus(String orderCode) {
        log.debug("Querying PayOS payment status for orderCode: {}", orderCode);
        try {
            long orderCodeLong = Long.parseLong(orderCode);
            return payOSClient.paymentRequests().get(orderCodeLong);
        } catch (Exception e) {
            log.error("Failed to get payment status for orderCode: {}", orderCode, e);
            throw new RuntimeException("Failed to get payment status", e);
        }
    }

    /**
     * Confirms webhook URL with PayOS (must be called before first webhook).
     * @param webhookUrl The webhook URL to register
     */
    public void confirmWebhook(String webhookUrl) {
        log.info("Confirming webhook URL with PayOS: {}", webhookUrl);
        try {
            payOSClient.webhooks().confirm(webhookUrl);
            log.info("Webhook URL confirmed successfully");
        } catch (Exception e) {
            log.error("Failed to confirm webhook URL", e);
            throw new RuntimeException("Failed to confirm webhook URL", e);
        }
    }

    /**
     * Gets the PayOS client for webhook verification.
     * @return PayOS client instance
     */
    public PayOS getPayOSClient() {
        return payOSClient;
    }

    /**
     * Generates unique order code for PayOS transaction.
     * Format: timestamp in seconds + 5-digit random suffix
     * This prevents race conditions from concurrent requests while staying
     * within JavaScript's MAX_SAFE_INTEGER (9007199254740991).
     */
    private String generateOrderCode() {
        long timestampSeconds = System.currentTimeMillis() / 1000; // Convert to seconds (10 digits)
        int random = (int) (Math.random() * 90000) + 10000; // 5-digit suffix (10000-99999)
        long orderCode = timestampSeconds * 100000L + random; // Combine: timestamp + random suffix
        return String.valueOf(orderCode);
    }
}
