package com.gameaccount.marketplace.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * DTO for PayOS webhook payload.
 * Provides type-safe handling of webhook callbacks with Jackson deserialization.
 * Ignores unknown properties to handle API changes gracefully.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayOSWebhookRequest {

    /**
     * The webhook data containing payment status and order information.
     */
    @JsonProperty("data")
    private WebhookData data;

    /**
     * Signature for verifying webhook authenticity.
     * Verification is handled by PayOS SDK.
     */
    @JsonProperty("signature")
    private String signature;

    /**
     * Inner data class containing actual payment information.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WebhookData {

        /**
         * The PayOS order code (transaction identifier).
         */
        @JsonProperty("orderCode")
        private Long orderCode;

        /**
         * Payment amount in VND.
         */
        @JsonProperty("amount")
        private Long amount;

        /**
         * Payment status: PENDING, PAID, CANCELLED, EXPIRED.
         */
        @JsonProperty("status")
        private String status;

        /**
         * PayOS response code: 00 = success (PAID), other codes = pending/failed.
         * This is the primary field PayOS sends in webhooks.
         */
        @JsonProperty("code")
        private String code;

        /**
         * PayOS result description: "Thành công" (success), "Thất bại" (failed), etc.
         */
        @JsonProperty("desc")
        private String payosResult;

        /**
         * Transaction description (e.g., "Buy game account").
         */
        @JsonProperty("description")
        private String description;

        /**
         * Account reference ID.
         */
        @JsonProperty("accountUserId")
        private Long accountUserId;

        /**
         * Transaction reference code.
         */
        @JsonProperty("referenceCode")
        private String referenceCode;

        /**
         * Transaction ID from PayOS.
         */
        @JsonProperty("transactionId")
        private Long transactionId;

        /**
         * Payment cancellation date (ISO 8601 format).
         */
        @JsonProperty("canceledAt")
        private String canceledAt;

        /**
         * Payment creation date (ISO 8601 format).
         */
        @JsonProperty("createdAt")
        private String createdAt;

        /**
         * Payment completion date (ISO 8601 format).
         */
        @JsonProperty("paidAt")
        private String paidAt;
    }

    /**
     * Gets the payment status as uppercase string.
     * PayOS sends code=00 for successful payments, not status=PAID.
     * @return Payment status (PENDING, PAID, CANCELLED, EXPIRED)
     */
    public String getPaymentStatus() {
        if (data == null) {
            return "UNKNOWN";
        }

        // PayOS webhook sends code=00 for successful payment
        if ("00".equals(data.getCode())) {
            return "PAID";
        }

        // Fall back to status field if present
        if (data.getStatus() != null) {
            return data.getStatus().toUpperCase();
        }

        return "UNKNOWN";
    }

    /**
     * Gets the order code as string.
     * @return Order code as string, or null if not present
     */
    public String getOrderCodeAsString() {
        return data != null && data.getOrderCode() != null
            ? String.valueOf(data.getOrderCode())
            : null;
    }
}
