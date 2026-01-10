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
         * Transaction description.
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
     * @return Payment status (PENDING, PAID, CANCELLED, EXPIRED)
     */
    public String getPaymentStatus() {
        return data != null && data.getStatus() != null
            ? data.getStatus().toUpperCase()
            : "UNKNOWN";
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
