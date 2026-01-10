package com.gameaccount.marketplace.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO containing transaction details for API responses.
 * Includes transaction status, account info, and participant details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {

    private Long id;
    private Double amount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private String orderCode;

    // Account details
    private Long accountId;
    private String accountTitle;
    private Double accountPrice;

    // User details
    private Long buyerId;
    private String buyerName;
    private Long sellerId;
    private String sellerName;
}
