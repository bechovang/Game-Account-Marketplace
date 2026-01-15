package com.gameaccount.marketplace.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Message entity for storing chat messages between users about specific accounts.
 * Messages support real-time communication features in Epic 5.
 *
 * @see User
 * @see Account
 */
@Entity
@Table(name = "messages",
    indexes = {
        @Index(name = "idx_message_account", columnList = "account_id"),
        @Index(name = "idx_message_sender", columnList = "sender_id"),
        @Index(name = "idx_message_receiver", columnList = "receiver_id"),
        @Index(name = "idx_message_read", columnList = "is_read")
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The account being discussed in this message.
     * All messages are associated with a specific account listing.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    /**
     * The user who sent this message.
     * LAZY fetch to avoid loading user data until needed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    /**
     * The user who receives this message.
     * LAZY fetch to avoid loading user data until needed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    /**
     * The actual message content.
     * TEXT type allows for long messages without VARCHAR length limits.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * Read status flag.
     * False by default - messages start as unread.
     * Uses Boolean wrapper class with @Builder.Default to allow null checks.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    /**
     * Timestamp when the message was created.
     * Automatically set by JPA auditing.
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
