package com.gameaccount.marketplace.controller;

import com.gameaccount.marketplace.dto.response.ChatMessageResponse;
import com.gameaccount.marketplace.dto.response.ConversationResponse;
import com.gameaccount.marketplace.entity.Message;
import com.gameaccount.marketplace.entity.User;
import com.gameaccount.marketplace.repository.UserRepository;
import com.gameaccount.marketplace.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for chat functionality.
 * Provides REST API endpoints for chat operations complementing the WebSocket ChatController.
 *
 * @see com.gameaccount.marketplace.controller.ChatController
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${frontend.url}")
public class ChatRestController {

    private final ChatService chatService;
    private final UserRepository userRepository;

    /**
     * Get all conversations for the authenticated user.
     * Returns a list of conversations with last message preview and unread count.
     *
     * GET /api/chat/conversations
     *
     * @param userDetails Authenticated user details
     * @return List of conversations
     */
    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationResponse>> getConversations(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails
    ) {
        String email = userDetails.getUsername();
        log.info("Fetching conversations for user={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

        List<ConversationResponse> conversations = chatService.getConversations(user.getId());

        log.info("Found {} conversations for user={}", conversations.size(), email);
        return ResponseEntity.ok(conversations);
    }

    /**
     * Get message history for a specific account.
     * Returns all messages for this account involving the current user.
     *
     * GET /api/chat/messages?accountId={accountId}
     *
     * @param accountId ID of the account
     * @param userDetails Authenticated user details
     * @return List of messages
     */
    @GetMapping("/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @RequestParam Long accountId,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails
    ) {
        String email = userDetails.getUsername();
        log.info("Fetching messages for accountId={}, user={}", accountId, email);

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

        // Get all messages for this account where current user is either sender or receiver
        List<Message> messages = chatService.getMessagesForAccountAndUser(accountId, currentUser.getId());

        // Convert to response DTOs
        List<ChatMessageResponse> response = messages.stream()
                .map(msg -> ChatMessageResponse.builder()
                        .id(msg.getId())
                        .accountId(msg.getAccount().getId())
                        .senderId(msg.getSender().getId())
                        .senderEmail(msg.getSender().getEmail())
                        .receiverId(msg.getReceiver().getId())
                        .content(msg.getContent())
                        .isRead(msg.getIsRead())
                        .createdAt(msg.getCreatedAt())
                        .build())
                .toList();

        log.info("Found {} messages for accountId={}, user={}", response.size(), accountId, email);
        return ResponseEntity.ok(response);
    }
}
