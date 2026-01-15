package com.gameaccount.marketplace.enums;

/**
 * Enumeration of notification types for real-time notifications.
 * Used by NotificationService to categorize different event types.
 */
public enum NotificationType {
    /**
     * Account has been approved by admin
     */
    ACCOUNT_APPROVED,

    /**
     * Account has been rejected by admin
     */
    ACCOUNT_REJECTED,

    /**
     * Account has been sold to a buyer
     */
    ACCOUNT_SOLD,

    /**
     * New transaction has been created
     */
    NEW_TRANSACTION,

    /**
     * Payment has been received for transaction
     */
    PAYMENT_RECEIVED
}
