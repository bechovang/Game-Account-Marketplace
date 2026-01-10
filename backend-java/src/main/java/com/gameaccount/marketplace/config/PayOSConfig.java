package com.gameaccount.marketplace.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.payos.PayOS;

/**
 * PayOS payment gateway configuration.
 * Loads credentials from application.yml and creates PayOS client bean.
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "payos")
public class PayOSConfig {

    private String clientId;
    private String apiKey;
    private String checksumKey;
    private String webhookUrl;
    private String returnUrl;
    private String cancelUrl;

    /**
     * Creates PayOS client bean with credentials from configuration.
     * @return PayOS client instance
     */
    @Bean
    public PayOS payOSClient() {
        return new PayOS(clientId, apiKey, checksumKey);
    }
}
