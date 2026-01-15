package com.gameaccount.marketplace.controller;

import com.gameaccount.marketplace.dto.request.SendMessageRequest;
import com.gameaccount.marketplace.dto.request.TypingIndicatorRequest;
import com.gameaccount.marketplace.dto.response.ChatMessageResponse;
import com.gameaccount.marketplace.entity.Message;
import com.gameaccount.marketplace.entity.User;
import com.gameaccount.marketplace.exception.ResourceNotFoundException;
import com.gameaccount.marketplace.repository.UserRepository;
import com.gameaccount.marketplace.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

/**
 * WebSocket controller for real-time chat functionality.
 * Handles STOMP message mappings for sending messages, marking read, and typing indicators.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    /**
     * Handle sending a chat message.
     * Client sends to: /app/chat.send
     * Server broadcasts to: /topic/chat/{accountId}
     *
     * @param request Message send request
     * @param userEmail User email from native header set by JWT interceptor
     */
    @MessageMapping("/chat.send")
    public void sendMessage(
            @Payload @Valid SendMessageRequest request,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        String email = (String) headerAccessor.getSessionAttributes().get("userEmail");
        if (email == null) {
            log.error("No userEmail found in session attributes");
            throw new RuntimeException("User must be authenticated to send messages");
        }
        log.info("Received message from {}: accountId={}, receiverId={}", email, request.getAccountId(), request.getReceiverId());

        // Resolve sender ID from authenticated user
        User sender = getUserByEmail(email);
        Long senderId = sender.getId();

        // Send message via service
        Message message = chatService.sendMessage(
                request.getAccountId(),
                senderId,
                request.getReceiverId(),
                request.getContent()
        );

        // Convert to response DTO and broadcast to topic
        ChatMessageResponse response = ChatMessageResponse.builder()
                .id(message.getId())
                .accountId(message.getAccount().getId())
                .senderId(message.getSender().getId())
                .senderEmail(message.getSender().getEmail())
                .receiverId(message.getReceiver().getId())
                .content(message.getContent())
                .isRead(message.getIsRead())
                .createdAt(message.getCreatedAt())
                .build();

        // Broadcast to all subscribers of this account's chat topic
        messagingTemplate.convertAndSend("/topic/chat/" + request.getAccountId(), response);
    }

    /**
     * Handle marking messages as read.
     * Client sends to: /app/chat.read
     *
     * @param request Mark as read request with accountId and fromUserId
     * @param userEmail User email from native header set by JWT interceptor
     */
    @MessageMapping("/chat.read")
    public void markRead(
            @Payload SendMessageRequest request,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        String email = (String) headerAccessor.getSessionAttributes().get("userEmail");
        if (email == null) {
            log.error("No userEmail found in session attributes for mark read");
            throw new RuntimeException("User must be authenticated");
        }
        User currentUser = getUserByEmail(email);
        chatService.markAsRead(request.getAccountId(), request.getReceiverId(), currentUser.getId());
        log.debug("Messages marked as read: accountId={}, fromUserId={}, byUserId={}",
                request.getAccountId(), request.getReceiverId(), currentUser.getId());
    }

    /**
     * Handle typing indicator.
     * Client sends to: /app/chat.typing
     * Server sends to: /queue/typing/{receiverId}
     *
     * @param request Typing indicator request
     * @param userEmail User email from native header set by JWT interceptor
     */
    @MessageMapping("/chat.typing")
    public void handleTyping(
            @Payload @Valid TypingIndicatorRequest request,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        String email = (String) headerAccessor.getSessionAttributes().get("userEmail");
        if (email == null) {
            log.error("No userEmail found in session attributes for typing indicator");
            throw new RuntimeException("User must be authenticated");
        }
        User sender = getUserByEmail(email);
        log.debug("Typing indicator: from={}, to={}, accountId={}", sender.getId(), request.getReceiverId(), request.getAccountId());

        // Send typing indicator to receiver's personal queue
        messagingTemplate.convertAndSend(
                "/queue/typing/" + request.getReceiverId(),
                Map.of(
                        "accountId", request.getAccountId(),
                        "senderId", sender.getId(),
                        "senderEmail", sender.getEmail(),
                        "isTyping", request.getIsTyping()
                )
        );
    }

    /**
     * Resolve user from email address.
     *
     * @param email User's email address
     * @return User entity
     * @throws ResourceNotFoundException if user not found
     */
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}
