package com.gameaccount.marketplace.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * Request DTO for purchasing a game account.
 * Contains account ID and encrypted credentials.
 */
@Data
public class PurchaseRequest {

    @NotNull(message = "Account ID is required")
    private Long accountId;

    @NotNull(message = "Credentials are required")
    private Map<String, String> credentials;
}
