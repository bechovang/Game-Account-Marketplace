package com.gameaccount.marketplace.dto.response;

import com.gameaccount.marketplace.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String fullName;
    private String avatar;
    private User.Role role;
    private User.UserStatus status;
    private Double balance;
    private Double rating;
    private Integer totalReviews;
}
