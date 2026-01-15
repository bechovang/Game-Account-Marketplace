package com.gameaccount.marketplace.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for chat messages broadcast via WebSocket.
 * Sent to /topic/chat/{accountId} subscribers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    private Long id;
    private Long accountId;
    private Long senderId;
    private String senderEmail;
    private Long receiverId;
    private String content;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
