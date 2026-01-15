package com.gameaccount.marketplace.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

/**
 * WebSocket configuration for STOMP messaging protocol.
 * Enables real-time communication for chat and notifications.
 *
 * Features:
 * - STOMP over WebSocket with SockJS fallback
 * - JWT authentication via ChannelInterceptor
 * - Simple message broker for pub/sub messaging
 * - User-specific messaging queues
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtChannelInterceptor jwtChannelInterceptor;

    /**
     * Register STOMP endpoints for WebSocket connections.
     * Configures /ws endpoint with SockJS fallback for browser compatibility.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                // Allow frontend origins (localhost for development)
                .setAllowedOriginPatterns("http://localhost:5173", "http://localhost:3000")
                // Enable SockJS fallback for older browsers
                .withSockJS();
    }

    /**
     * Configure message broker for STOMP messaging.
     * - Simple broker: handles /topic (pub/sub) and /queue (user-specific) prefixes
     * - Application prefix: /app for messages bound for @MessageMapping methods
     * - User prefix: /user for user-specific destinations
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enable simple broker for /topic (broadcast) and /queue (user-specific)
        registry.enableSimpleBroker("/topic", "/queue");

        // Set application destination prefix for client messages
        registry.setApplicationDestinationPrefixes("/app");

        // Set user destination prefix for user-specific messages
        registry.setUserDestinationPrefix("/user");
    }

    /**
     * Configure client inbound channel with JWT authentication interceptor.
     * The interceptor validates JWT tokens on CONNECT messages.
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(jwtChannelInterceptor);
    }
}
