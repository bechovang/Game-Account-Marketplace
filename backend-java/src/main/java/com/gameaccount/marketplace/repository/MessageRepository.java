package com.gameaccount.marketplace.repository;

import com.gameaccount.marketplace.entity.Message;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Message entity operations.
 * Provides query methods for chat functionality including:
 * - Finding messages by account and user participation
 * - Counting unread messages
 * - Finding conversation history between two users
 *
 * @see Message
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Find all messages for an account where the specified user is either sender or receiver.
     * Ordered chronologically (oldest first) for proper chat display.
     * Query hint enables caching for improved performance on repeated queries.
     *
     * @param accountId the account ID
     * @param userId the user ID (either sender or receiver)
     * @return list of messages in chronological order
     */
    @Query("SELECT m FROM Message m WHERE m.account.id = :accountId " +
           "AND (m.sender.id = :userId OR m.receiver.id = :userId) " +
           "ORDER BY m.createdAt ASC")
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    List<Message> findByAccountIdAndSenderIdOrReceiverId(
        @Param("accountId") Long accountId,
        @Param("userId") Long userId
    );

    /**
     * Find unread message count for a specific receiver.
     * Used for displaying unread message badges/notifications.
     *
     * @param receiverId the receiver user ID
     * @return count of unread messages
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver.id = :receiverId AND m.isRead = false")
    Long findUnreadCount(@Param("receiverId") Long receiverId);

    /**
     * Find conversation between two specific users for a specific account.
     * Returns messages in both directions (user1 to user2, and user2 to user1).
     * Ordered chronologically for proper chat history display.
     *
     * @param accountId the account ID
     * @param userId1 first user ID
     * @param userId2 second user ID
     * @return list of messages between the two users
     */
    @Query("SELECT m FROM Message m WHERE m.account.id = :accountId " +
           "AND ((m.sender.id = :userId1 AND m.receiver.id = :userId2) " +
           "OR (m.sender.id = :userId2 AND m.receiver.id = :userId1)) " +
           "ORDER BY m.createdAt ASC")
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    List<Message> findConversation(
        @Param("accountId") Long accountId,
        @Param("userId1") Long userId1,
        @Param("userId2") Long userId2
    );

    /**
     * Find paginated messages for an account where user is either sender or receiver.
     * Ordered by creation time descending (newest first) for pagination.
     * Useful for loading conversation history in chunks.
     *
     * @param accountId the account ID
     * @param userId the user ID (either sender or receiver)
     * @param pageable pagination parameters (page, size, sort)
     * @return page of messages
     */
    @Query("SELECT m FROM Message m WHERE m.account.id = :accountId " +
           "AND (m.sender.id = :userId OR m.receiver.id = :userId) " +
           "ORDER BY m.createdAt DESC")
    Page<Message> findMessagesPaginated(
        @Param("accountId") Long accountId,
        @Param("userId") Long userId,
        Pageable pageable
    );

    /**
     * Find all unique account IDs where the user has participated in conversations.
     * Returns accounts where the user has either sent or received messages.
     *
     * @param userId the user ID
     * @return list of unique account IDs
     */
    @Query("SELECT DISTINCT m.account.id FROM Message m " +
           "WHERE m.sender.id = :userId OR m.receiver.id = :userId")
    List<Long> findUserConversationAccountIds(@Param("userId") Long userId);

    /**
     * Find the most recent message for a specific account and user.
     * Returns the last message regardless of direction (sent or received).
     *
     * @param accountId the account ID
     * @param userId the user ID
     * @return the most recent message, or null if no messages exist
     */
    @Query("SELECT m FROM Message m WHERE m.account.id = :accountId " +
           "AND (m.sender.id = :userId OR m.receiver.id = :userId) " +
           "ORDER BY m.createdAt DESC")
    List<Message> findLatestMessageForAccountAndUser(
        @Param("accountId") Long accountId,
        @Param("userId") Long userId
    );
}
