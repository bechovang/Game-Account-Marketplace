package com.gameaccount.marketplace.service;

import com.gameaccount.marketplace.dto.response.NotificationResponse;
import com.gameaccount.marketplace.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Service layer for real-time notifications and broadcasts.
 * Provides async notification methods for account and transaction events.
 *
 * @see NotificationResponse
 * @see NotificationType
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Send account approval notification to seller.
     *
     * @param sellerId ID of the seller receiving notification
     * @param accountId ID of the approved account
     */
    @Async
    public void sendAccountApprovedNotification(Long sellerId, Long accountId) {
        validateNotificationParams(sellerId, accountId, "sendAccountApprovedNotification");
        log.info("Sending account approved notification: sellerId={}, accountId={}", sellerId, accountId);

        NotificationResponse notification = NotificationResponse.builder()
                .id(UUID.randomUUID().toString())
                .type(NotificationType.ACCOUNT_APPROVED)
                .title("Account Approved!")
                .message("Your game account has been approved and is now visible to buyers.")
                .data(Map.of("accountId", accountId))
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();

        messagingTemplate.convertAndSend("/topic/notifications/" + sellerId, notification);
    }

    /**
     * Send account rejection notification to seller.
     *
     * @param sellerId ID of the seller receiving notification
     * @param accountId ID of the rejected account
     * @param reason Reason for rejection
     */
    @Async
    public void sendAccountRejectedNotification(Long sellerId, Long accountId, String reason) {
        validateNotificationParams(sellerId, accountId, "sendAccountRejectedNotification");
        log.info("Sending account rejected notification: sellerId={}, accountId={}", sellerId, accountId);

        NotificationResponse notification = NotificationResponse.builder()
                .id(UUID.randomUUID().toString())
                .type(NotificationType.ACCOUNT_REJECTED)
                .title("Account Rejected")
                .message("Your game account was rejected. Reason: " + reason)
                .data(Map.of("accountId", accountId, "reason", reason))
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();

        messagingTemplate.convertAndSend("/topic/notifications/" + sellerId, notification);
    }

    /**
     * Send account sold notification to seller.
     *
     * @param sellerId ID of the seller receiving notification
     * @param accountId ID of the sold account
     * @param buyerId ID of the buyer
     */
    @Async
    public void sendAccountSoldNotification(Long sellerId, Long accountId, Long buyerId) {
        validateNotificationParams(sellerId, accountId, "sendAccountSoldNotification");
        if (buyerId == null) {
            throw new IllegalArgumentException("buyerId cannot be null");
        }
        log.info("Sending account sold notification: sellerId={}, accountId={}", sellerId, accountId);

        NotificationResponse notification = NotificationResponse.builder()
                .id(UUID.randomUUID().toString())
                .type(NotificationType.ACCOUNT_SOLD)
                .title("Account Sold!")
                .message("Congratulations! Your game account has been sold.")
                .data(Map.of("accountId", accountId, "buyerId", buyerId))
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();

        messagingTemplate.convertAndSend("/topic/notifications/" + sellerId, notification);
    }

    /**
     * Send new transaction notification to seller.
     *
     * @param sellerId ID of the seller receiving notification
     * @param transactionId ID of the new transaction
     */
    @Async
    public void sendNewTransactionNotification(Long sellerId, Long transactionId) {
        if (sellerId == null || transactionId == null) {
            throw new IllegalArgumentException("sellerId and transactionId cannot be null");
        }
        log.info("Sending new transaction notification: sellerId={}, transactionId={}", sellerId, transactionId);

        NotificationResponse notification = NotificationResponse.builder()
                .id(UUID.randomUUID().toString())
                .type(NotificationType.NEW_TRANSACTION)
                .title("New Transaction")
                .message("You have a new purchase transaction for your account.")
                .data(Map.of("transactionId", transactionId))
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();

        messagingTemplate.convertAndSend("/topic/notifications/" + sellerId, notification);
    }

    /**
     * Send payment received notification to seller.
     *
     * @param sellerId ID of the seller receiving notification
     * @param transactionId ID of the transaction
     */
    @Async
    public void sendPaymentReceivedNotification(Long sellerId, Long transactionId) {
        if (sellerId == null || transactionId == null) {
            throw new IllegalArgumentException("sellerId and transactionId cannot be null");
        }
        log.info("Sending payment received notification: sellerId={}, transactionId={}", sellerId, transactionId);

        NotificationResponse notification = NotificationResponse.builder()
                .id(UUID.randomUUID().toString())
                .type(NotificationType.PAYMENT_RECEIVED)
                .title("Payment Received")
                .message("Payment has been received for your transaction.")
                .data(Map.of("transactionId", transactionId))
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();

        messagingTemplate.convertAndSend("/topic/notifications/" + sellerId, notification);
    }

    /**
     * Broadcast new account to all subscribers.
     *
     * @param accountData Map containing account data for broadcasting
     */
    @Async
    public void broadcastNewAccount(Map<String, Object> accountData) {
        if (accountData == null || !accountData.containsKey("id")) {
            throw new IllegalArgumentException("accountData cannot be null and must contain 'id'");
        }
        log.info("Broadcasting new account: accountId={}", accountData.get("id"));

        Map<String, Object> broadcast = Map.of(
                "eventType", "new_account_posted",
                "account", accountData
        );

        messagingTemplate.convertAndSend("/topic/accounts", broadcast);
    }

    /**
     * Broadcast account status change to all subscribers.
     *
     * @param accountId ID of the account
     * @param status New status
     * @param previousStatus Previous status
     */
    @Async
    public void broadcastAccountStatusChanged(Long accountId, String status, String previousStatus) {
        if (accountId == null || status == null || previousStatus == null) {
            throw new IllegalArgumentException("accountId, status, and previousStatus cannot be null");
        }
        log.info("Broadcasting account status change: accountId={}, status={}", accountId, status);

        Map<String, Object> broadcast = Map.of(
                "eventType", "account_status_changed",
                "accountId", accountId,
                "status", status,
                "previousStatus", previousStatus
        );

        messagingTemplate.convertAndSend("/topic/accounts", broadcast);
    }

    /**
     * Validate common notification parameters.
     *
     * @param sellerId Seller ID to validate
     * @param accountId Account ID to validate
     * @param methodName Method name for error message
     * @throws IllegalArgumentException if parameters are invalid
     */
    private void validateNotificationParams(Long sellerId, Long accountId, String methodName) {
        if (sellerId == null || accountId == null) {
            throw new IllegalArgumentException(
                    String.format("%s: sellerId and accountId cannot be null", methodName)
            );
        }
    }
}
