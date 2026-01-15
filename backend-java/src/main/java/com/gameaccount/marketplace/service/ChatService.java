package com.gameaccount.marketplace.service;

import com.gameaccount.marketplace.dto.response.ConversationResponse;
import com.gameaccount.marketplace.dto.response.ConversationResponse.LastMessageDto;
import com.gameaccount.marketplace.dto.response.ConversationResponse.OtherUserDto;
import com.gameaccount.marketplace.entity.Account;
import com.gameaccount.marketplace.entity.Message;
import com.gameaccount.marketplace.entity.User;
import com.gameaccount.marketplace.exception.ResourceNotFoundException;
import com.gameaccount.marketplace.repository.AccountRepository;
import com.gameaccount.marketplace.repository.MessageRepository;
import com.gameaccount.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service layer for chat functionality.
 * Provides message operations for real-time communication between users.
 *
 * @see Message
 * @see MessageRepository
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final MessageRepository messageRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    /**
     * Send a message from one user to another about a specific account.
     *
     * @param accountId ID of the account being discussed
     * @param senderId ID of the user sending the message
     * @param receiverId ID of the user receiving the message
     * @param content Message content
     * @return Saved message entity
     * @throws ResourceNotFoundException if account, sender, or receiver not found
     */
    @Transactional
    public Message sendMessage(Long accountId, Long senderId, Long receiverId, String content) {
        log.info("Sending message: accountId={}, senderId={}, receiverId={}", accountId, senderId, receiverId);

        // Validate entities exist
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountId));
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found: " + senderId));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found: " + receiverId));

        // Build and save message
        Message message = Message.builder()
                .account(account)
                .sender(sender)
                .receiver(receiver)
                .content(content)
                .isRead(false)
                .build();

        return messageRepository.save(message);
    }

    /**
     * Get conversation between two users for a specific account.
     *
     * @param accountId ID of the account
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @return List of messages in chronological order
     */
    public List<Message> getConversation(Long accountId, Long userId1, Long userId2) {
        log.debug("Fetching conversation: accountId={}, userId1={}, userId2={}", accountId, userId1, userId2);
        return messageRepository.findConversation(accountId, userId1, userId2);
    }

    /**
     * Mark messages as read for a user.
     *
     * @param accountId ID of the account
     * @param fromUserId ID of the user whose messages to mark as read
     * @param toUserId ID of the user marking messages as read
     */
    @Transactional
    public void markAsRead(Long accountId, Long fromUserId, Long toUserId) {
        log.debug("Marking messages as read: accountId={}, fromUserId={}, toUserId={}", accountId, fromUserId, toUserId);
        List<Message> unreadMessages = messageRepository.findConversation(accountId, fromUserId, toUserId)
                .stream()
                .filter(m -> !m.getIsRead() && m.getReceiver().getId().equals(toUserId))
                .toList();

        unreadMessages.forEach(m -> m.setIsRead(true));
        messageRepository.saveAll(unreadMessages);
    }

    /**
     * Get count of unread messages for a user.
     *
     * @param userId ID of the user
     * @return Count of unread messages
     */
    public Long getUnreadCount(Long userId) {
        return messageRepository.findUnreadCount(userId);
    }

    /**
     * Get all conversations for a user.
     * A conversation is defined as a unique (account, otherUser) pair where the user has exchanged messages.
     *
     * @param userId ID of the user
     * @return List of conversations with last message and unread count
     */
    @Transactional(readOnly = true)
    public List<ConversationResponse> getConversations(Long userId) {

        log.debug("Fetching conversations for userId={}", userId);
        log.debug("Fetching conversations for userId={}", userId);

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        // Get all unique account IDs where user has participated
        List<Long> accountIds = messageRepository.findUserConversationAccountIds(userId);
        log.debug("Found {} unique account conversations for user={}", accountIds.size(), userId);

        // For each account, find the other user and build conversation response
        List<ConversationResponse> conversations = new ArrayList<>();

        for (Long accountId : accountIds) {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountId));

            // Find the other user in this conversation
            User otherUser = findOtherUserInConversation(accountId, userId);

            if (otherUser == null) {
                log.warn("No other user found for accountId={}, userId={}", accountId, userId);
                continue;
            }

            // Get last message
            List<Message> latestMessages = messageRepository.findLatestMessageForAccountAndUser(accountId, userId);
            LastMessageDto lastMessage = null;
            if (!latestMessages.isEmpty()) {
                Message msg = latestMessages.get(0);
                lastMessage = LastMessageDto.builder()
                        .content(msg.getContent())
                        .createdAt(msg.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant())
                        .build();
            }

            // Count unread messages for this conversation
            Long unreadCount = messageRepository.findConversation(accountId, userId, otherUser.getId())
                    .stream()
                    .filter(m -> !m.getIsRead() && m.getReceiver().getId().equals(userId))
                    .count();

            // Build conversation response
            ConversationResponse conversation = ConversationResponse.builder()
                    .accountId(account.getId())
                    .accountTitle(account.getTitle())
                    .otherUser(OtherUserDto.builder()
                            .id(otherUser.getId())
                            .email(otherUser.getEmail())
                            .avatar(otherUser.getAvatar())
                            .build())
                    .lastMessage(lastMessage)
                    .unreadCount(unreadCount.intValue())
                    .build();

            conversations.add(conversation);
        }

        // Sort by last message time (most recent first), conversations without messages last
        conversations.sort((a, b) -> {
            if (a.getLastMessage() == null && b.getLastMessage() == null) return 0;
            if (a.getLastMessage() == null) return 1;
            if (b.getLastMessage() == null) return -1;
            return b.getLastMessage().getCreatedAt().compareTo(a.getLastMessage().getCreatedAt());
        });

        return conversations;
    }

    /**
     * Find the other user in a conversation for a specific account.
     * The conversation is between the given user and another user.
     *
     * @param accountId ID of the account
     * @param currentUserId ID of the current user
     * @return The other user in the conversation, or null if not found
     */
    private User findOtherUserInConversation(Long accountId, Long currentUserId) {
        // Get all messages for this account involving the current user
        List<Message> messages = messageRepository.findLatestMessageForAccountAndUser(accountId, currentUserId);

        if (messages.isEmpty()) {
            return null;
        }

        // Find the other user (either sender or receiver who is not the current user)
        for (Message message : messages) {
            if (!message.getSender().getId().equals(currentUserId)) {
                return message.getSender();
            }
            if (!message.getReceiver().getId().equals(currentUserId)) {
                return message.getReceiver();
            }
        }

        return null;
    }

    /**
     * Get all messages for an account involving a specific user.
     * Returns messages where the user is either sender or receiver.
     *
     * @param accountId ID of the account
     * @param userId ID of the user
     * @return List of messages in chronological order
     */
    @Transactional(readOnly = true)
    public List<Message> getMessagesForAccountAndUser(Long accountId, Long userId) {
        log.debug("Fetching messages for accountId={}, userId={}", accountId, userId);
        return messageRepository.findByAccountIdAndSenderIdOrReceiverId(accountId, userId);
    }

    /**
     * Get account by ID.
     *
     * @param accountId ID of the account
     * @return Account entity
     * @throws ResourceNotFoundException if account not found
     */
    @Transactional(readOnly = true)
    public Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountId));
    }
}
