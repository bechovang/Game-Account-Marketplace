package com.gameaccount.marketplace.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request DTO for sending a chat message.
 * Used by WebSocket STOMP /app/chat.send endpoint.
 */
@Data
public class SendMessageRequest {
    @NotNull(message = "Account ID is required")
    private Long accountId;

    @NotNull(message = "Receiver ID is required")
    private Long receiverId;

    @NotBlank(message = "Content is required")
    private String content;
}
