package com.gameaccount.marketplace.controller.user;

import com.gameaccount.marketplace.dto.request.UpdateProfileRequest;
import com.gameaccount.marketplace.dto.response.UserResponse;
import com.gameaccount.marketplace.entity.User;
import com.gameaccount.marketplace.repository.UserRepository;
import com.gameaccount.marketplace.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "${frontend.url}")
public class UserController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Extract userId from authenticated user
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserResponse response = authService.updateProfile(
                user.getId(),
                request.getFullName(),
                request.getAvatar()
        );
        return ResponseEntity.ok(response);
    }

    // Password change endpoint - TODO: Implement in future story
    // @PutMapping("/password")
    // public ResponseEntity<Void> changePassword(...)
}
