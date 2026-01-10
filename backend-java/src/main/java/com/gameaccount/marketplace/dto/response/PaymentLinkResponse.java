package com.gameaccount.marketplace.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO containing PayOS payment link information.
 * Returned after creating payment link for a transaction.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentLinkResponse {
    private String orderCode;
    private String checkoutUrl;
    private Double amount;
}
