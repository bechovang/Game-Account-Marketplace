package com.gameaccount.marketplace.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request DTO for typing indicator notifications.
 * Used by WebSocket STOMP /app/chat.typing endpoint.
 */
@Data
public class TypingIndicatorRequest {
    @NotNull(message = "Account ID is required")
    private Long accountId;

    @NotNull(message = "Receiver ID is required")
    private Long receiverId;

    @NotNull(message = "Typing status is required")
    private Boolean isTyping;
}
