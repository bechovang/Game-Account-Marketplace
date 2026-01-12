package com.gameaccount.marketplace.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request DTO for purchasing a game account.
 * Credentials are stored in the Account entity, not provided during purchase.
 */
@Data
public class PurchaseRequest {

    @NotNull(message = "Account ID is required")
    private Long accountId;
}
