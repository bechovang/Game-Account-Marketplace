package com.gameaccount.marketplace.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for adding account to favorites.
 * Used in REST API POST /api/favorites endpoint.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddFavoriteRequest {

    @NotNull(message = "Account ID is required")
    private Long accountId;
}
