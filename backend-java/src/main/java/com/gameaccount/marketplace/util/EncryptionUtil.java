package com.gameaccount.marketplace.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for AES-256-GCM encryption/decryption of sensitive data.
 * Used for encrypting account credentials before storage.
 *
 * @see <a href="https://docs.oracle.com/en/java/javase/17/security/java-cryptography-architecture-jca-reference-guide.html">Java Cryptography Architecture (JCA)</a>
 */
@Component
@Slf4j
public class EncryptionUtil {

    @Value("${encryption.secret-key}")
    private String secretKey;

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int KEY_SIZE = 256;
    private static final int GCM_TAG_LENGTH = 128;
    private static final int IV_LENGTH = 12; // GCM standard IV length
    private static final int EXPECTED_KEY_LENGTH = 64; // 32 bytes = 64 hex chars for AES-256

    /**
     * Validates encryption configuration on startup.
     * Ensures the secret key is properly configured.
     */
    @PostConstruct
    public void validateConfiguration() {
        if (secretKey == null || secretKey.trim().isEmpty()) {
            throw new IllegalStateException("Encryption secret key is not configured. " +
                "Please set 'encryption.secret-key' in application.yml");
        }

        if (secretKey.length() != EXPECTED_KEY_LENGTH) {
            throw new IllegalStateException("Encryption secret key must be " + EXPECTED_KEY_LENGTH +
                " hexadecimal characters (32 bytes) for AES-256. Current length: " + secretKey.length());
        }

        if (!secretKey.matches("[0-9a-fA-F]+")) {
            throw new IllegalStateException("Encryption secret key must contain only hexadecimal characters");
        }

        log.info("Encryption configuration validated successfully");
    }

    /**
     * Encrypts plaintext using AES-256-GCM.
     * Each encryption uses a random IV for security, which is prepended to the ciphertext.
     *
     * @param plaintext The plaintext to encrypt
     * @return Base64-encoded ciphertext (IV + ciphertext)
     * @throws RuntimeException if encryption fails
     */
    public String encrypt(String plaintext) {
        try {
            // Generate random IV
            byte[] iv = new byte[IV_LENGTH];
            SecureRandom.getInstanceStrong().nextBytes(iv);

            // Create secret key from hex string
            byte[] keyBytes = hexStringToByteArray(secretKey);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

            // Initialize cipher
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, parameterSpec);

            // Encrypt
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            // Combine IV and ciphertext
            byte[] combined = ByteBuffer.allocate(iv.length + ciphertext.length)
                .put(iv)
                .put(ciphertext)
                .array();

            // Return Base64 encoded
            String result = Base64.getEncoder().encodeToString(combined);
            log.debug("Data encrypted successfully");
            return result;
        } catch (Exception e) {
            log.error("Encryption failed", e);
            throw new RuntimeException("Failed to encrypt data", e);
        }
    }

    /**
     * Decrypts Base64-encoded ciphertext using AES-256-GCM.
     * Expects the IV to be prepended to the ciphertext.
     *
     * @param ciphertext Base64-encoded ciphertext (IV + ciphertext)
     * @return Decrypted plaintext
     * @throws RuntimeException if decryption fails
     */
    public String decrypt(String ciphertext) {
        try {
            // Decode Base64
            byte[] combined = Base64.getDecoder().decode(ciphertext);

            // Extract IV and ciphertext
            ByteBuffer buffer = ByteBuffer.wrap(combined);
            byte[] iv = new byte[IV_LENGTH];
            buffer.get(iv);
            byte[] encrypted = new byte[buffer.remaining()];
            buffer.get(encrypted);

            // Create secret key
            byte[] keyBytes = hexStringToByteArray(secretKey);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

            // Initialize cipher
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, parameterSpec);

            // Decrypt
            byte[] plaintext = cipher.doFinal(encrypted);
            log.debug("Data decrypted successfully");
            return new String(plaintext, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Decryption failed", e);
            throw new RuntimeException("Failed to decrypt data", e);
        }
    }

    /**
     * Converts a hexadecimal string to a byte array.
     *
     * @param hex Hexadecimal string (must be even length)
     * @return Byte array representation
     * @throws IllegalArgumentException if hex string is invalid
     */
    private byte[] hexStringToByteArray(String hex) {
        if (hex == null || hex.isEmpty()) {
            throw new IllegalArgumentException("Hex string cannot be null or empty");
        }

        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException("Hex string must have even length. Current length: " + hex.length());
        }

        if (!hex.matches("[0-9a-fA-F]+")) {
            throw new IllegalArgumentException("Hex string contains invalid hexadecimal characters");
        }

        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}
