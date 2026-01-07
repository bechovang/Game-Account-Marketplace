# Story 1.6: AuthService & Authentication Logic

Status: review

## Story

As a developer,
I want to implement the AuthService with register and login business logic,
so that users can be authenticated and stored in the database.

## Acceptance Criteria

1. **Given** the UserRepository from Story 1.4 and JWT from Story 1.5
**When** I implement AuthService
**Then** AuthService is annotated with @Service and @RequiredArgsConstructor
**And** register() method validates email uniqueness via UserRepository.existsByEmail()
**And** register() method hashes password with BCrypt before saving
**And** register() method sets default role to BUYER and status to ACTIVE
**And** register() method generates JWT token via JwtTokenProvider
**And** register() method is annotated with @Transactional
**And** login() method authenticates with AuthenticationManager
**And** login() method generates JWT token upon successful authentication
**And** login() method throws BusinessException for invalid credentials
**And** getProfile() method retrieves User by ID from UserRepository
**And** updateProfile() method updates fullName and avatar fields
**And** exceptions include: BusinessException (email exists, invalid credentials), ResourceNotFoundException (user not found)

## Tasks / Subtasks

- [x] Create DTO classes (AC: all)
  - [x] Create RegisterRequest (email, password, fullName)
  - [x] Create LoginRequest (email, password)
  - [x] Create AuthResponse (token, userId, email, role)
  - [x] Create UpdateProfileRequest (fullName, avatar)
  - [x] Create UserResponse (all user fields)
- [x] Create exception classes (AC: #, #)
  - [x] Create BusinessException
  - [x] Create ResourceNotFoundException
- [x] Create AuthService class (AC: #, #, #, #, #, #, #, #, #, #, #, #)
  - [x] Add @Service and @RequiredArgsConstructor
  - [x] Inject UserRepository, PasswordEncoder, JwtTokenProvider, AuthenticationManager
  - [x] Implement register() method with validation
  - [x] Implement login() method with authentication
  - [x] Implement getProfile() method
  - [x] Implement updateProfile() method
  - [x] Add @Transactional to register()
- [ ] Create exception handler (optional but recommended)
  - [ ] @ControllerAdvice for exception handling (skipped - optional)

## Dev Notes

**Service Layer Pattern:** This service will be SHARED between REST and GraphQL (DRY principle)

### Request/Response DTOs

```java
// dto/request/RegisterRequest.java
package com.gameaccount.marketplace.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;

    @NotBlank
    @Size(min = 2, max = 100)
    private String fullName;
}
```

```java
// dto/request/LoginRequest.java
package com.gameaccount.marketplace.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
```

```java
// dto/response/AuthResponse.java
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
    private User.Role role;
}
```

### Exception Classes

```java
// exception/BusinessException.java
package com.gameaccount.marketplace.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
```

```java
// exception/ResourceNotFoundException.java
package com.gameaccount.marketplace.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```

### AuthService Template [Source: ARCHITECTURE.md#3.3.4]

```java
package com.gameaccount.marketplace.service;

import com.gameaccount.marketplace.dto.request.LoginRequest;
import com.gameaccount.marketplace.dto.request.RegisterRequest;
import com.gameaccount.marketplace.dto.response.AuthResponse;
import com.gameaccount.marketplace.dto.response.UserResponse;
import com.gameaccount.marketplace.entity.User;
import com.gameaccount.marketplace.exception.BusinessException;
import com.gameaccount.marketplace.exception.ResourceNotFoundException;
import com.gameaccount.marketplace.repository.UserRepository;
import com.gameaccount.marketplace.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already registered");
        }

        // Create new user
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(User.Role.BUYER)
                .status(User.UserStatus.ACTIVE)
                .build();

        user = userRepository.save(user);

        // Generate token
        String token = tokenProvider.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        String token = tokenProvider.generateToken(authentication.getName());
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("User not found"));

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    public UserResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatar(user.getAvatar())
                .role(user.getRole())
                .status(user.getStatus())
                .balance(user.getBalance())
                .rating(user.getRating())
                .totalReviews(user.getTotalReviews())
                .build();
    }

    @Transactional
    public UserResponse updateProfile(Long userId, String fullName, String avatar) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (fullName != null) {
            user.setFullName(fullName);
        }
        if (avatar != null) {
            user.setAvatar(avatar);
        }

        user = userRepository.save(user);

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatar(user.getAvatar())
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }
}
```

### Common Pitfalls to Avoid

🚨 **CRITICAL WARNINGS:**
1. **Not encoding password** - MUST use passwordEncoder.encode() before saving
2. **Password validation too weak** - Minimum 6 chars, consider requiring special chars
3. **Returning raw password** - Never include password in any response DTO
4. **Not checking email uniqueness** - Duplicate emails will cause database constraint violation
5. **Missing @Transactional** - Could cause partial data updates on failure
6. **Wrong authentication type** - Must use UsernamePasswordAuthenticationToken for login
7. **Exception handling** - Use specific exceptions, don't expose internal details
8. **Role enum naming** - User.Role.BUYER not just "BUYER"

### Testing Standards

```bash
# Test registration
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123","fullName":"Test User"}'

# Test login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

### Requirements Traceability

**FR1:** User registration ✅ register() method
**FR2:** User login ✅ login() method
**FR6:** View profile ✅ getProfile() method
**FR7:** Update profile ✅ updateProfile() method

### Next Story Dependencies

Story 1.7 (REST API Endpoints) - Depends on AuthService

### References

- Architecture.md Section 3.3.4: Service Layer (Shared by REST & GraphQL)
- PRD: User stories for registration, login, profile management

---

## Dev Agent Record

### Agent Model Used
Claude Opus 4.5

### Completion Notes List
Story 1.6 completed successfully on 2026-01-07.

**Completed Tasks:**
1. Created all DTO classes (RegisterRequest, LoginRequest, UpdateProfileRequest, AuthResponse, UserResponse)
2. Created exception classes (BusinessException, ResourceNotFoundException)
3. Created AuthService with all required methods
4. register() method with email validation, BCrypt hashing, JWT token generation
5. login() method with AuthenticationManager and JWT token generation
6. getProfile() method retrieves user by ID
7. updateProfile() method updates fullName and avatar
8. @Transactional annotation on register() and updateProfile()
9. Compilation successful - verified with `mvn clean compile`

**Service Layer Pattern:** AuthService is shared between REST and GraphQL (DRY principle)

**All acceptance criteria met.**

### File List
- `backend-java/src/main/java/com/gameaccount/marketplace/dto/request/RegisterRequest.java` (CREATE)
- `backend-java/src/main/java/com/gameaccount/marketplace/dto/request/LoginRequest.java` (CREATE)
- `backend-java/src/main/java/com/gameaccount/marketplace/dto/response/AuthResponse.java` (CREATE)
- `backend-java/src/main/java/com/gameaccount/marketplace/dto/response/UserResponse.java` (CREATE)
- `backend-java/src/main/java/com/gameaccount/marketplace/dto/request/UpdateProfileRequest.java` (CREATE)
- `backend-java/src/main/java/com/gameaccount/marketplace/exception/BusinessException.java` (CREATE)
- `backend-java/src/main/java/com/gameaccount/marketplace/exception/ResourceNotFoundException.java` (CREATE)
- `backend-java/src/main/java/com/gameaccount/marketplace/service/AuthService.java` (CREATE)
