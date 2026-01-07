package com.gameaccount.marketplace.controller.auth;

import com.gameaccount.marketplace.dto.request.LoginRequest;
import com.gameaccount.marketplace.dto.request.RegisterRequest;
import com.gameaccount.marketplace.dto.response.AuthResponse;
import com.gameaccount.marketplace.dto.response.UserResponse;
import com.gameaccount.marketplace.entity.User;
import com.gameaccount.marketplace.repository.UserRepository;
import com.gameaccount.marketplace.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "${frontend.url}")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        // Extract email from UserDetails and fetch user to get userId
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserResponse response = authService.getProfile(user.getId());
        return ResponseEntity.ok(response);
    }
}
