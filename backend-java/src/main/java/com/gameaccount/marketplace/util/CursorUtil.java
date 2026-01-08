package com.gameaccount.marketplace.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Utility class for cursor-based pagination.
 * Encodes/decodes cursors following Relay specification.
 */
@Slf4j
@Component
public class CursorUtil {

    /**
     * Encode cursor from account ID and timestamp.
     * Format: base64(accountId:timestamp)
     */
    public String encodeCursor(Long accountId, Long timestamp) {
        if (accountId == null || timestamp == null) {
            throw new IllegalArgumentException("Account ID and timestamp cannot be null");
        }

        String cursorData = accountId + ":" + timestamp;
        return Base64.getUrlEncoder().encodeToString(
            cursorData.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Decode cursor to extract account ID and timestamp.
     */
    public CursorData decodeCursor(String cursor) {
        if (cursor == null || cursor.trim().isEmpty()) {
            throw new IllegalArgumentException("Cursor cannot be null or empty");
        }

        try {
            String decoded = new String(
                Base64.getUrlDecoder().decode(cursor),
                StandardCharsets.UTF_8
            );

            String[] parts = decoded.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid cursor format: expected 'id:timestamp'");
            }

            Long accountId = Long.parseLong(parts[0]);
            Long timestamp = Long.parseLong(parts[1]);

            return new CursorData(accountId, timestamp);

        } catch (Exception e) {
            log.error("Failed to decode cursor: {}", cursor, e);
            throw new IllegalArgumentException("Invalid cursor format: " + cursor, e);
        }
    }

    /**
     * Validate cursor format without decoding.
     */
    public boolean isValidCursor(String cursor) {
        if (cursor == null || cursor.trim().isEmpty()) {
            return false;
        }

        try {
            Base64.getUrlDecoder().decode(cursor);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Data class for cursor information.
     */
    public static class CursorData {
        public final Long accountId;
        public final Long timestamp;

        public CursorData(Long accountId, Long timestamp) {
            this.accountId = accountId;
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return "CursorData{accountId=" + accountId + ", timestamp=" + timestamp + "}";
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            CursorData that = (CursorData) obj;
            return accountId.equals(that.accountId) && timestamp.equals(that.timestamp);
        }

        @Override
        public int hashCode() {
            return accountId.hashCode() * 31 + timestamp.hashCode();
        }
    }
}
