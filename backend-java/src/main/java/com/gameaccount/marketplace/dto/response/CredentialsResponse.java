package com.gameaccount.marketplace.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO containing decrypted account credentials.
 * Only returned to buyer after successful payment completion.
 *
 * WARNING: This data should only be transmitted over HTTPS and never logged.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CredentialsResponse {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;
}
