package com.gameaccount.marketplace.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO containing purchase initiation response.
 * Returns transaction ID and payment checkout URL.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseResponse {

    private Long transactionId;
    private String checkoutUrl;
    private Double amount;
    private String orderCode;
}
