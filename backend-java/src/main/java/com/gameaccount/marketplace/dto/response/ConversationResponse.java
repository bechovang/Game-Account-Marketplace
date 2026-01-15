package com.gameaccount.marketplace.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response DTO for a conversation in the user's chat list.
 * Represents a chat thread about a specific account with another user.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponse {

    /**
     * The account ID this conversation is about.
     */
    private Long accountId;

    /**
     * The account title.
     */
    private String accountTitle;

    /**
     * The other user in this conversation (not the current user).
     */
    private OtherUserDto otherUser;

    /**
     * The last message in this conversation, if any.
     */
    private LastMessageDto lastMessage;

    /**
     * Count of unread messages in this conversation for the current user.
     */
    private Integer unreadCount;

    /**
     * Nested DTO for the other user in the conversation.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OtherUserDto {
        private Long id;
        private String email;
        private String avatar;
    }

    /**
     * Nested DTO for the last message in the conversation.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LastMessageDto {
        private String content;
        private Instant createdAt;
    }
}
