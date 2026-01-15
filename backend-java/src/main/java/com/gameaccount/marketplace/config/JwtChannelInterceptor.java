package com.gameaccount.marketplace.config;

import com.gameaccount.marketplace.security.CustomUserDetailsService;
import com.gameaccount.marketplace.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

/**
 * Channel interceptor for JWT authentication on WebSocket connections.
 * Intercepts CONNECT messages to validate JWT tokens before allowing connection.
 *
 * @see JwtTokenProvider
 * @see CustomUserDetailsService
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // Handle CONNECT messages - authenticate and set user
        if (accessor.getCommand() != null && accessor.getCommand().name().equals("CONNECT")) {
            log.debug("Intercepting WebSocket CONNECT message");

            // Extract JWT token from Authorization header
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("WebSocket connection rejected: Missing or invalid Authorization header");
                throw new RuntimeException("Missing or invalid Authorization header");
            }

            String token = authHeader.substring(7); // Strip "Bearer " prefix

            // Validate token and extract email
            if (!jwtTokenProvider.validateToken(token)) {
                log.warn("WebSocket connection rejected: Invalid JWT token");
                throw new RuntimeException("Invalid JWT token");
            }

            String email = jwtTokenProvider.extractEmail(token);
            if (email == null) {
                log.warn("WebSocket connection rejected: Could not extract email from token");
                throw new RuntimeException("Invalid JWT token");
            }

            // Load user and set authentication
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                accessor.setUser(authentication);

                // CRITICAL: Store email in session attributes (maintained for all messages in session)
                accessor.getSessionAttributes().put("userEmail", email);

                log.info("WebSocket connection authenticated for user: {}", email);
            } catch (Exception e) {
                log.warn("WebSocket connection rejected: User not found - {}", email);
                throw new RuntimeException("User not found: " + email);
            }
        }

        // Return original message - session attributes are maintained by Spring
        return message;
    }
}
