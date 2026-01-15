package com.gameaccount.marketplace.dto.response;

import com.gameaccount.marketplace.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Response DTO for notifications broadcast via WebSocket.
 * Sent to /topic/notifications/{userId} subscribers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private String id;
    private NotificationType type;
    private String title;
    private String message;
    private Map<String, Object> data;
    private LocalDateTime createdAt;
    private Boolean isRead;
}
