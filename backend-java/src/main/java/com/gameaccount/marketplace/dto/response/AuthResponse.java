package com.gameaccount.marketplace.dto.response;

import com.gameaccount.marketplace.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
    private Long userId;
    private String email;
    private String fullName;
    private User.Role role;
}
