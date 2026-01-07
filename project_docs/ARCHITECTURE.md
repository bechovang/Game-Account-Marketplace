# Technical Architecture Document
## Game Account Marketplace

---

## Document Control

| Field | Value |
|-------|-------|
| **Version** | 1.0 |
| **Author** | Winston (Architect) |
| **Date** | 2026-01-06 |
| **Status** | Draft |
| **Review Date** | 2026-01-13 |

---

## Table of Contents

1. [Architecture Overview](#1-architecture-overview)
2. [Project Structure](#2-project-structure)
3. [Backend Architecture](#3-backend-architecture)
4. [Frontend Architecture](#4-frontend-architecture)
5. [Database Design](#5-database-design)
6. [Security Architecture](#6-security-architecture)
7. [API Architecture](#7-api-architecture)
8. [Real-time Communication](#8-real-time-communication)
9. [Caching Strategy](#9-caching-strategy)
10. [Deployment Architecture](#10-deployment-architecture)
11. [Development Guidelines](#11-development-guidelines)

---

## 1. Architecture Overview

### 1.1 Architectural Vision

**Philosophy:** Simple, boring technology that scales. Enterprise-grade patterns without over-engineering.

**Core Principles:**
- Separation of concerns via N-Layer architecture
- Shared Service Layer for REST and GraphQL (DRY principle)
- Statelessness for horizontal scalability
- Security by default (defense in depth)
- Developer productivity through convention over configuration

### 1.2 High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         CLIENT LAYER                            │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │         React SPA (TypeScript + Tailwind)                │  │
│  │  Port: 3000 | Apollo Client | Axios | Socket.io Client  │  │
│  └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ HTTPS/WSS
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                    API GATEWAY / CDN (Optional)                │
│                    Nginx / CloudFlare / AWS ALB                │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                    BACKEND LAYER (Spring Boot 3)               │
│                        Port: 8080                              │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              PRESENTATION LAYER                          │  │
│  │  ┌─────────────┐  ┌──────────────┐  ┌────────────────┐  │  │
│  │  │ REST        │  │ GraphQL      │  │ WebSocket      │  │  │
│  │  │ Controller  │  │ Resolver     │  │ Handler        │  │  │
│  │  └─────────────┘  └──────────────┘  └────────────────┘  │  │
│  └──────────────────────────────────────────────────────────┘  │
│                              ↓                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              BUSINESS LOGIC LAYER                        │  │
│  │         (SHARED by REST & GraphQL)                       │  │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │  │
│  │  │ AuthService  │  │ Account      │  │ ChatService  │  │  │
│  │  │              │  │ Service      │  │              │  │  │
│  │  └──────────────┘  └──────────────┘  └──────────────┘  │  │
│  └──────────────────────────────────────────────────────────┘  │
│                              ↓                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              DATA ACCESS LAYER                           │  │
│  │  ┌──────────────────────────────────────────────────┐   │  │
│  │  │     Spring Data JPA (Hibernate)                  │   │  │
│  │  │  UserRepository │ AccountRepository │ etc.       │   │  │
│  │  └──────────────────────────────────────────────────┘   │  │
│  └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              │
                ┌─────────────┼─────────────┐
                ↓             ↓             ↓
        ┌───────────┐  ┌───────────┐  ┌───────────┐
        │  MySQL    │  │  Redis    │  │ External  │
        │  Primary  │  │  Cache    │  │ APIs      │
        │  Port:3306│  │  Port:6379│  │ VNPay/    │
        └───────────┘  └───────────┘  │ Momo      │
                                       └───────────┘
```

### 1.3 Technology Stack Summary

| Component | Technology | Version |
|-----------|-----------|---------|
| **Backend Language** | Java | 17 (LTS) |
| **Backend Framework** | Spring Boot | 3.2.x |
| **Build Tool** | Maven | 3.9+ |
| **ORM** | Hibernate (via Spring Data JPA) | - |
| **Security** | Spring Security + jjwt | - |
| **Database** | MySQL | 8.0+ |
| **Cache** | Redis | 7.0+ |
| **Frontend Framework** | React | 18.x |
| **Frontend Language** | TypeScript | 5.x |
| **Build Tool** | Vite | 5.x |
| **Styling** | Tailwind CSS | 3.x |
| **State Management** | React Context + Apollo Cache | - |
| **GraphQL Client** | Apollo Client | 3.x |
| **HTTP Client** | Axios | 1.x |
| **WebSocket Client** | SockJS + STOMP | - |

---

## 2. Project Structure

### 2.1 Monorepo Structure

```
GameAccount-Marketplace/                          # Root directory
│
├── .git/                                         # Git repository
├── .gitignore
├── docker-compose.yml                            # Local development orchestration
├── README.md                                     # Project overview
├── LICENSE
│
├── backend-java/                                 # Spring Boot Backend
│   ├── pom.xml                                   # Maven dependencies
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/gameaccount/marketplace/
│   │   │   │   ├── MarketplaceApplication.java  # Main entry point
│   │   │   │   ├── config/                       # Configuration
│   │   │   │   │   ├── SecurityConfig.java
│   │   │   │   │   ├── WebSocketConfig.java
│   │   │   │   │   ├── GraphQLConfig.java
│   │   │   │   │   ├── RedisConfig.java
│   │   │   │   │   └── DatabaseConfig.java
│   │   │   │   ├── controller/                   # REST Controllers
│   │   │   │   │   ├── auth/
│   │   │   │   │   │   └── AuthController.java
│   │   │   │   │   ├── user/
│   │   │   │   │   │   └── UserController.java
│   │   │   │   │   ├── account/
│   │   │   │   │   │   └── AccountController.java
│   │   │   │   │   ├── admin/
│   │   │   │   │   │   └── AdminController.java
│   │   │   │   │   └── transaction/
│   │   │   │   │       └── TransactionController.java
│   │   │   │   ├── graphql/                      # GraphQL Resolvers
│   │   │   │   │   ├── query/
│   │   │   │   │   │   ├── AccountQuery.java
│   │   │   │   │   │   ├── UserQuery.java
│   │   │   │   │   │   └── GameQuery.java
│   │   │   │   │   ├── mutation/
│   │   │   │   │   │   ├── AuthMutation.java
│   │   │   │   │   │   └── AccountMutation.java
│   │   │   │   │   └── schema/
│   │   │   │   │       └── schema.graphqls
│   │   │   │   ├── websocket/                    # WebSocket Handlers
│   │   │   │   │   ├── ChatWebSocketHandler.java
│   │   │   │   │   └── NotificationWebSocketHandler.java
│   │   │   │   ├── service/                      # Business Logic (SHARED)
│   │   │   │   │   ├── AuthService.java
│   │   │   │   │   ├── AccountService.java
│   │   │   │   │   ├── UserService.java
│   │   │   │   │   ├── TransactionService.java
│   │   │   │   │   ├── ChatService.java
│   │   │   │   │   └── NotificationService.java
│   │   │   │   ├── repository/                   # Data Access Layer
│   │   │   │   │   ├── UserRepository.java
│   │   │   │   │   ├── AccountRepository.java
│   │   │   │   │   ├── GameRepository.java
│   │   │   │   │   ├── TransactionRepository.java
│   │   │   │   │   └── MessageRepository.java
│   │   │   │   ├── entity/                       # JPA Entities
│   │   │   │   │   ├── User.java
│   │   │   │   │   ├── Account.java
│   │   │   │   │   ├── Game.java
│   │   │   │   │   ├── Transaction.java
│   │   │   │   │   ├── Message.java
│   │   │   │   │   └── Review.java
│   │   │   │   ├── dto/                          # Data Transfer Objects
│   │   │   │   │   ├── request/
│   │   │   │   │   │   ├── LoginRequest.java
│   │   │   │   │   │   ├── RegisterRequest.java
│   │   │   │   │   │   └── CreateAccountRequest.java
│   │   │   │   │   ├── response/
│   │   │   │   │   │   ├── AuthResponse.java
│   │   │   │   │   │   └── AccountResponse.java
│   │   │   │   │   └── mapper/                   # MapStruct mappers
│   │   │   │   │       └── AccountMapper.java
│   │   │   │   ├── security/                     # Security
│   │   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   │   └── UserDetailsServiceImpl.java
│   │   │   │   ├── exception/                    # Exception Handling
│   │   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   │   └── BusinessException.java
│   │   │   │   ├── util/                         # Utilities
│   │   │   │   │   ├── EncryptionUtil.java
│   │   │   │   │   └── DateUtil.java
│   │   │   │   └── constant/                     # Constants
│   │   │   │       ├── Role.java
│   │   │   │       └── AccountStatus.java
│   │   │   └── resources/
│   │   │       ├── application.yml               # Main configuration
│   │   │       ├── application-dev.yml           # Dev profile
│   │   │       ├── application-prod.yml          # Prod profile
│   │   │       └── db/migration/                 # Flyway migrations (optional)
│   │   └── test/
│   │       └── java/.../                         # Unit & Integration tests
│   └── Dockerfile
│
├── frontend-react/                               # React Frontend
│   ├── package.json                              # NPM dependencies
│   ├── vite.config.ts                            # Vite configuration
│   ├── tsconfig.json                             # TypeScript config
│   ├── tailwind.config.js                        # Tailwind config
│   ├── index.html
│   ├── public/
│   │   ├── favicon.ico
│   │   └── assets/
│   └── src/
│       ├── main.tsx                              # Entry point
│       ├── App.tsx                               # Root component
│       ├── vite-env.d.ts
│       ├── assets/                               # Static assets
│       ├── components/                           # Reusable components
│       │   ├── common/
│       │   │   ├── Button.tsx
│       │   │   ├── Input.tsx
│       │   │   ├── Modal.tsx
│       │   │   └── LoadingSpinner.tsx
│       │   ├── layout/
│       │   │   ├── Header.tsx
│       │   │   ├── Sidebar.tsx
│       │   │   └── Footer.tsx
│       │   └── features/
│       │       ├── auth/
│       │       │   ├── LoginForm.tsx
│       │       │   └── RegisterForm.tsx
│       │       ├── account/
│       │       │   ├── AccountCard.tsx
│       │       │   ├── AccountList.tsx
│       │       │   └── AccountDetail.tsx
│       │       ├── chat/
│       │       │   ├── ChatBox.tsx
│       │       │   └── ConversationList.tsx
│       │       └── admin/
│       │           └── Dashboard.tsx
│       ├── pages/                                # Page components
│       │   ├── HomePage.tsx
│       │   ├── LoginPage.tsx
│       │   ├── AccountDetailPage.tsx
│       │   └── AdminDashboardPage.tsx
│       ├── services/                             # API Clients
│       │   ├── graphql/
│       │   │   ├── client.ts                    # Apollo Client setup
│       │   │   ├── queries.ts                   # GraphQL queries
│       │   │   └── mutations.ts                 # GraphQL mutations
│       │   ├── rest/
│       │   │   ├── axiosInstance.ts             # Axios setup
│       │   │   └── api.ts                       # REST API calls
│       │   └── websocket/
│       │       ├── socketClient.ts              # WebSocket setup
│       │       └── handlers.ts                  # Event handlers
│       ├── contexts/                             # React Context
│       │   ├── AuthContext.tsx
│       │   └── NotificationContext.tsx
│       ├── hooks/                                # Custom Hooks
│       │   ├── useAuth.ts
│       │   ├── useAccounts.ts
│       │   └── useChat.ts
│       ├── types/                                # TypeScript types
│       │   ├── user.ts
│       │   ├── account.ts
│       │   └── index.ts
│       ├── utils/                                # Utilities
│       │   ├── validators.ts
│       │   └── formatters.ts
│       └── styles/                               # Global styles
│           └── index.css
│
├── project_docs/                                 # Documentation
│   ├── PRD.md                                    # Product Requirements
│   ├── ARCHITECTURE.md                           # This document
│   └── api/                                      # API documentation
│       ├── rest-api.yaml                         # OpenAPI spec
│       └── graphql-schema.graphql                # GraphQL schema
│
└── .github/                                      # GitHub workflows
    └── workflows/
        ├── ci-backend.yml
        └── ci-frontend.yml
```

### 2.2 Structure Rationale

| Decision | Rationale |
|----------|-----------|
| **Monorepo** | Simplifies coordination, shared tooling, atomic commits across frontend/backend |
| **Separate backend/frontend dirs** | Clear separation of concerns, independent deployment, different tech stacks |
| **Service layer sharing** | DRY principle - both REST and GraphQL use same business logic |
| **DTOs with MapStruct** | Decouple entities from API contracts, enable versioning |
| **Feature-based frontend folders** | Scalable organization, co-located related code |

---

## 3. Backend Architecture

### 3.1 N-Layer Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                   PRESENTATION LAYER                        │
│                                                              │
│  ┌─────────────────┐  ┌─────────────────┐  ┌────────────┐ │
│  │  REST           │  │  GraphQL        │  │ WebSocket  │ │
│  │  @RestController│  │  @SchemaMapping │  │  @MessageM │ │
│  │                 │  │                 │  │            │ │
│  │  - Validates    │  │  - Validates    │  │ - Validates│ │
│  │  - Routes       │  │  - Resolves     │  │ - Routes   │ │
│  │  - Returns DTO  │  │  - Returns DTO  │  │ - Returns  │ │
│  └────────┬────────┘  └────────┬────────┘  └─────┬──────┘ │
└───────────┼────────────────────┼──────────────────┼────────┘
            │                    │                  │
            └────────────────────┼──────────────────┘
                                 ↓
┌─────────────────────────────────────────────────────────────┐
│                   BUSINESS LOGIC LAYER                      │
│                     (SHARED)                                │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │              SERVICE LAYER                            │  │
│  │                                                       │  │
│  │  @Service                                             │  │
│  │  - Business Rules                                     │  │
│  │  - Transaction Management (@Transactional)            │  │
│  │  - External API Calls                                 │  │
│  │  - Caching Logic (@Cacheable)                         │  │
│  │                                                       │  │
│  │  ┌─────────────────┐  ┌──────────────────────────┐   │  │
│  │  │ AuthService     │  │ AccountService           │   │  │
│  │  │ - login()       │  │ - createAccount()        │   │  │
│  │  │ - register()    │  │ - approveAccount()       │   │  │
│  │  │ - validateJwt() │  │ - searchAccounts()       │   │  │
│  │  └─────────────────┘  └──────────────────────────┘   │  │
│  └──────────────────────────────────────────────────────┘  │
└────────────────────────────────────────┬────────────────────┘
                                          ↓
┌─────────────────────────────────────────────────────────────┐
│                   DATA ACCESS LAYER                         │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │           REPOSITORY LAYER (Spring Data JPA)         │  │
│  │                                                       │  │
│  │  @Repository (Interface extends JpaRepository)       │  │
│  │  - CRUD Operations                                   │  │
│  │  - Custom Queries (@Query)                           │  │
│  │  - Pagination (Pageable)                             │  │
│  │  - Query Derivation                                  │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                                          ↓
                              ┌───────────────────────┐
                              │   DATABASE (MySQL)     │
                              │   + REDIS (Cache)      │
                              └───────────────────────┘
```

### 3.2 Key Design Patterns

| Pattern | Implementation | Purpose |
|---------|---------------|---------|
| **Dependency Injection** | Spring IoC Container | Loose coupling, testability |
| **Service Layer Pattern** | @Service classes | Business logic encapsulation |
| **Repository Pattern** | Spring Data JPA interfaces | Data access abstraction |
| **DTO Pattern** | Request/Response DTOs + MapStruct | API contract stability |
| **Specification Pattern** | Custom specifications in queries | Dynamic query building |
| **Strategy Pattern** | Payment gateway implementations | Pluggable algorithms |
| **Observer Pattern** | WebSocket subscriptions | Event-driven updates |

### 3.3 Core Backend Components

#### 3.3.1 Main Application Entry

```java
// src/main/java/com/gameaccount/marketplace/MarketplaceApplication.java
package com.gameaccount.marketplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MarketplaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketplaceApplication.class, args);
    }
}
```

#### 3.3.2 Entity Example (JPA)

```java
// src/main/java/com/gameaccount/marketplace/entity/User.java
package com.gameaccount.marketplace.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 100)
    private String fullName;

    @Column(length = 255)
    private String avatar;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.BUYER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(precision = 15, scale = 2)
    private Double balance = 0.0;

    @Column(precision = 3, scale = 2)
    private Double rating = 0.0;

    private Integer totalReviews = 0;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Account> accounts = new ArrayList<>();

    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Transaction> purchases = new ArrayList<>();

    public enum Role {
        BUYER, SELLER, ADMIN
    }

    public enum UserStatus {
        ACTIVE, BANNED, SUSPENDED
    }
}
```

#### 3.3.3 Repository Interface

```java
// src/main/java/com/gameaccount/marketplace/repository/UserRepository.java
package com.gameaccount.marketplace.repository;

import com.gameaccount.marketplace.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.status = 'ACTIVE'")
    Page<User> findByRole(@Param("role") User.Role role, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.fullName LIKE %:keyword% OR u.email LIKE %:keyword%")
    Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);
}
```

#### 3.3.4 Service Layer (Shared by REST & GraphQL)

```java
// src/main/java/com/gameaccount/marketplace/service/AuthService.java
package com.gameaccount.marketplace.service;

import com.gameaccount.marketplace.dto.request.LoginRequest;
import com.gameaccount.marketplace.dto.request.RegisterRequest;
import com.gameaccount.marketplace.dto.response.AuthResponse;
import com.gameaccount.marketplace.entity.User;
import com.gameaccount.marketplace.exception.BusinessException;
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
}
```

#### 3.3.5 REST Controller

```java
// src/main/java/com/gameaccount/marketplace/controller/auth/AuthController.java
package com.gameaccount.marketplace.controller.auth;

import com.gameaccount.marketplace.dto.request.LoginRequest;
import com.gameaccount.marketplace.dto.request.RegisterRequest;
import com.gameaccount.marketplace.dto.response.AuthResponse;
import com.gameaccount.marketplace.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "${frontend.url}")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
```

#### 3.3.6 GraphQL Resolver (Uses Same Service)

```java
// src/main/java/com/gameaccount/marketplace/graphql/query/AccountQuery.java
package com.gameaccount.marketplace.graphql.query;

import com.gameaccount.marketplace.dto.response.AccountResponse;
import com.gameaccount.marketplace.entity.Account;
import com.gameaccount.marketplace.service.AccountService;
import com.gameaccount.marketplace.dto.mapper.AccountMapper;
import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AccountQuery implements GraphQLQueryResolver {

    private final AccountService accountService;
    private final AccountMapper accountMapper;

    public List<AccountResponse> accounts(Long gameId, Double minPrice, Double maxPrice) {
        // Reuses the SAME service as REST controller
        List<Account> accounts = accountService.searchAccounts(gameId, minPrice, maxPrice);
        return accounts.stream()
                .map(accountMapper::toResponse)
                .collect(Collectors.toList());
    }

    public AccountResponse account(Long id) {
        Account account = accountService.getAccountById(id);
        return accountMapper.toResponse(account);
    }
}
```

### 3.4 Configuration Files

#### 3.4.1 pom.xml (Backend Maven Configuration)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.1</version>
        <relativePath/>
    </parent>

    <groupId>com.gameaccount</groupId>
    <artifactId>marketplace-backend</artifactId>
    <version>1.0.0</version>
    <name>GameAccount Marketplace Backend</name>
    <description>Game Account Marketplace with Spring Boot 3</description>

    <properties>
        <java.version>21</java.version>
        <lombok.version>1.18.30</lombok.version>
        <mapstruct.version>1.5.5.Final</mapstruct.version>
        <jjwt.version>0.12.3</jjwt.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-websocket</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-graphql</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-cache</artifactId>
        </dependency>

        <!-- Database -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>${jjwt.version}</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>

        <!-- Developer Tools -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
            <optional>true</optional>
        </dependency>

        <dependency>
            <groupId>org.mapstruct</groupId>
            <artifactId>mapstruct</artifactId>
            <version>${mapstruct.version}</version>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>

        <!-- Documentation -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>2.3.0</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>21</source>
                    <target>21</target>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                            <version>${lombok.version}</version>
                        </path>
                        <path>
                            <groupId>org.mapstruct</groupId>
                            <artifactId>mapstruct-processor</artifactId>
                            <version>${mapstruct.version}</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

#### 3.4.2 application.yml

```yaml
# src/main/resources/application.yml
spring:
  application:
    name: gameaccount-marketplace

  profiles:
    active: ${SPRING_PROFILE:dev}

  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:gameaccount_marketplace}?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:}
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
        format_sql: true
        use_sql_comments: true

  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}
      timeout: 60000ms
      lettuce:
        pool:
          max-active: 20
          max-idle: 10
          min-idle: 5

  cache:
    type: redis
    redis:
      time-to-live: 600000

  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

server:
  port: ${SERVER_PORT:8080}
  servlet:
    context-path: /
  compression:
    enabled: true

# JWT Configuration
jwt:
  secret: ${JWT_SECRET:MyVerySecretKeyForJWTTokenGenerationPleaseChangeThisInProduction}
  expiration: ${JWT_EXPIRATION:86400000}  # 24 hours in milliseconds

# Frontend URL for CORS
frontend:
  url: ${FRONTEND_URL:http://localhost:3000}

# File Upload
file:
  upload-dir: ${FILE_UPLOAD_DIR:./uploads}

# Logging
logging:
  level:
    com.gameaccount.marketplace: DEBUG
    org.springframework.security: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE

# GraphQL
graphql:
  graphiql:
    enabled: true
    path: /graphiql

# Management endpoints
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
```

---

## 4. Frontend Architecture

### 4.1 Frontend Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                      REACT SPA (Vite)                       │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │                  PAGES / ROUTES                       │  │
│  │  HomePage | LoginPage | AccountPage | AdminPage      │  │
│  └──────────────────────────────────────────────────────┘  │
│                              ↓                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │                 FEATURE COMPONENTS                    │  │
│  │  AccountCard | ChatBox | LoginForm | Dashboard       │  │
│  └──────────────────────────────────────────────────────┘  │
│                              ↓                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │                  SHARED COMPONENTS                    │  │
│  │  Button | Input | Modal | Layout Components          │  │
│  └──────────────────────────────────────────────────────┘  │
│                              ↓                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │                  CUSTOM HOOKS                         │  │
│  │  useAuth | useAccounts | useChat | useQuery          │  │
│  └──────────────────────────────────────────────────────┘  │
│                              ↓                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │                 REACT CONTEXT                         │  │
│  │  AuthContext | NotificationContext | ThemeContext    │  │
│  └──────────────────────────────────────────────────────┘  │
│                              ↓                              │
│  ┌────────────┐  ┌──────────────┐  ┌────────────────────┐ │
│  │  Apollo    │  │    Axios     │  │  WebSocket Client  │ │
│  │  Client    │  │  Instance    │  │  (SockJS + STOMP)  │ │
│  │  (GraphQL) │  │   (REST)     │  │                    │ │
│  └─────┬──────┘  └──────┬───────┘  └──────────┬─────────┘ │
└────────┼──────────────────┼────────────────────┼───────────┘
         │                  │                    │
         └──────────────────┼────────────────────┘
                            ↓
              ┌─────────────────────────────────┐
              │      BACKEND API (PORT 8080)    │
              │  REST | GraphQL | WebSocket     │
              └─────────────────────────────────┘
```

### 4.2 Key Frontend Files

#### 4.2.1 package.json

```json
{
  "name": "gameaccount-marketplace-frontend",
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "tsc && vite build",
    "preview": "vite preview",
    "lint": "eslint . --ext ts,tsx --report-unused-disable-directives --max-warnings 0",
    "type-check": "tsc --noEmit"
  },
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-router-dom": "^6.21.1",
    "@apollo/client": "^3.8.10",
    "graphql": "^16.8.1",
    "axios": "^1.6.5",
    "sockjs-client": "^1.6.1",
    "@stomp/stompjs": "^7.0.0",
    "tailwindcss": "^3.4.0",
    "react-hook-form": "^7.49.2",
    "yup": "@hookform/resolvers": "^3.3.4",
    "react-hot-toast": "^2.4.1",
    "recharts": "^2.10.3",
    "zustand": "^4.4.7",
    "date-fns": "^3.0.6"
  },
  "devDependencies": {
    "@types/react": "^18.2.47",
    "@types/react-dom": "^18.2.18",
    "@types/sockjs-client": "^1.5.4",
    "@typescript-eslint/eslint-plugin": "^6.18.1",
    "@typescript-eslint/parser": "^6.18.1",
    "@vitejs/plugin-react": "^4.2.1",
    "autoprefixer": "^10.4.16",
    "eslint": "^8.56.0",
    "eslint-plugin-react-hooks": "^4.6.0",
    "eslint-plugin-react-refresh": "^0.4.5",
    "postcss": "^8.4.33",
    "typescript": "^5.3.3",
    "vite": "^5.0.11"
  }
}
```

#### 4.2.2 Apollo Client Setup (GraphQL)

```typescript
// src/services/graphql/client.ts
import { ApolloClient, InMemoryCache, HttpLink, from } from '@apollo/client';
import { setContext } from '@apollo/client/link/context';
import { onError } from '@apollo/client/link/error';

// HTTP connection to the API
const httpLink = new HttpLink({
  uri: import.meta.env.VITE_GRAPHQL_URL || 'http://localhost:8080/graphql',
});

// Auth link - adds JWT token to each request
const authLink = setContext((_, { headers }) => {
  const token = localStorage.getItem('access_token');

  return {
    headers: {
      ...headers,
      authorization: token ? `Bearer ${token}` : '',
    },
  };
});

// Error handling link
const errorLink = onError(({ graphQLErrors, networkError }) => {
  if (graphQLErrors) {
    graphQLErrors.forEach(({ message, locations, path }) => {
      console.error(`[GraphQL error]: Message: ${message}, Location: ${locations}, Path: ${path}`);
    });
  }

  if (networkError) {
    console.error(`[Network error]: ${networkError}`);
    // Handle 401 - redirect to login
    if ('statusCode' in networkError && networkError.statusCode === 401) {
      localStorage.removeItem('access_token');
      window.location.href = '/login';
    }
  }
});

// Apollo Client instance
export const apolloClient = new ApolloClient({
  link: from([authLink, errorLink, httpLink]),
  cache: new InMemoryCache({
    typePolicies: {
      Query: {
        fields: {
          accounts: {
            keyArgs: ['gameId', 'minPrice', 'maxPrice', 'status'],
            merge(existing = [], incoming) {
              return incoming;
            },
          },
        },
      },
    },
  }),
  defaultOptions: {
    watchQuery: {
      fetchPolicy: 'cache-and-network',
      errorPolicy: 'all',
    },
    query: {
      fetchPolicy: 'network-only',
      errorPolicy: 'all',
    },
    mutate: {
      errorPolicy: 'all',
    },
  },
});
```

#### 4.2.3 Axios Instance (REST API)

```typescript
// src/services/rest/axiosInstance.ts
import axios, { AxiosError, InternalAxiosRequestConfig, AxiosResponse } from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

// Create axios instance
const axiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor - adds JWT token
axiosInstance.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('access_token');

    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  }
);

// Response interceptor - handles errors globally
axiosInstance.interceptors.response.use(
  (response: AxiosResponse) => {
    return response;
  },
  (error: AxiosError) => {
    if (error.response) {
      const status = error.response.status;

      switch (status) {
        case 401:
          // Unauthorized - clear token and redirect to login
          localStorage.removeItem('access_token');
          window.location.href = '/login';
          break;
        case 403:
          console.error('Forbidden: You do not have permission');
          break;
        case 404:
          console.error('Not Found: Resource does not exist');
          break;
        case 500:
          console.error('Server Error: Please try again later');
          break;
        default:
          console.error(`Request failed with status ${status}`);
      }
    } else if (error.request) {
      console.error('No response received from server');
    } else {
      console.error('Request setup error:', error.message);
    }

    return Promise.reject(error);
  }
);

export default axiosInstance;

// Typed API client
export const apiClient = {
  get: <T>(url: string, params?: object) =>
    axiosInstance.get<T>(url, { params }).then((res) => res.data),

  post: <T>(url: string, data: object) =>
    axiosInstance.post<T>(url, data).then((res) => res.data),

  put: <T>(url: string, data: object) =>
    axiosInstance.put<T>(url, data).then((res) => res.data),

  delete: <T>(url: string) =>
    axiosInstance.delete<T>(url).then((res) => res.data),

  // For file uploads
  upload: <T>(url: string, file: File, onProgress?: (progress: number) => void) => {
    const formData = new FormData();
    formData.append('file', file);

    return axiosInstance.post<T>(url, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
      onUploadProgress: (progressEvent) => {
        if (onProgress && progressEvent.total) {
          const progress = Math.round((progressEvent.loaded * 100) / progressEvent.total);
          onProgress(progress);
        }
      },
    }).then((res) => res.data);
  },
};
```

#### 4.2.4 WebSocket Client Setup

```typescript
// src/services/websocket/socketClient.ts
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const WEBSOCKET_URL = import.meta.env.VITE_WEBSOCKET_URL || 'ws://localhost:8080/ws';

class WebSocketService {
  private client: Client | null = null;
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;

  connect(token: string, onConnected: () => void, onDisconnected: () => void) {
    if (this.client && this.client.connected) {
      console.log('WebSocket already connected');
      return;
    }

    this.client = new Client({
      webSocketFactory: () => new SockJS(WEBSOCKET_URL),
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      debug: (str) => {
        console.log('WebSocket:', str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,

      onConnect: () => {
        console.log('WebSocket connected');
        this.reconnectAttempts = 0;
        onConnected();
      },

      onDisconnect: () => {
        console.log('WebSocket disconnected');
        onDisconnected();
      },

      onStompError: (frame) => {
        console.error('WebSocket error:', frame);
        this.reconnect();
      },
    });

    this.client.activate();
  }

  disconnect() {
    if (this.client) {
      this.client.deactivate();
      this.client = null;
    }
  }

  private reconnect() {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;
      console.log(`Reconnecting... Attempt ${this.reconnectAttempts}`);
      // Client will auto-reconnect based on reconnectDelay
    } else {
      console.error('Max reconnection attempts reached');
    }
  }

  // Subscribe to chat messages
  subscribeToChat(
    accountId: number,
    onMessage: (message: any) => void
  ) {
    if (!this.client || !this.client.connected) {
      console.error('WebSocket not connected');
      return;
    }

    return this.client.subscribe(`/topic/chat/${accountId}`, (message: IMessage) => {
      onMessage(JSON.parse(message.body));
    });
  }

  // Subscribe to notifications
  subscribeToNotifications(
    userId: number,
    onNotification: (notification: any) => void
  ) {
    if (!this.client || !this.client.connected) {
      console.error('WebSocket not connected');
      return;
    }

    return this.client.subscribe(`/topic/notifications/${userId}`, (message: IMessage) => {
      onNotification(JSON.parse(message.body));
    });
  }

  // Subscribe to account updates
  subscribeToAccountUpdates(
    onAccountUpdate: (update: any) => void
  ) {
    if (!this.client || !this.client.connected) {
      console.error('WebSocket not connected');
      return;
    }

    return this.client.subscribe('/topic/accounts', (message: IMessage) => {
      onAccountUpdate(JSON.parse(message.body));
    });
  }

  // Send chat message
  sendChatMessage(accountId: number, receiverId: number, content: string) {
    if (!this.client || !this.client.connected) {
      console.error('WebSocket not connected');
      return;
    }

    this.client.publish({
      destination: '/app/chat.send',
      body: JSON.stringify({
        accountId,
        receiverId,
        content,
      }),
    });
  }

  // Send typing indicator
  sendTypingIndicator(accountId: number, receiverId: number) {
    if (!this.client || !this.client.connected) return;

    this.client.publish({
      destination: '/app/chat.typing',
      body: JSON.stringify({ accountId, receiverId }),
    });
  }
}

export const websocketService = new WebSocketService();
```

#### 4.2.5 React Context (Auth)

```typescript
// src/contexts/AuthContext.tsx
import React, { createContext, useContext, useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

interface User {
  id: number;
  email: string;
  fullName: string;
  role: string;
  avatar?: string;
}

interface AuthContextType {
  user: User | null;
  token: string | null;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
  isLoading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(localStorage.getItem('access_token'));
  const [isLoading, setIsLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    // Validate token on mount
    const validateToken = async () => {
      const storedToken = localStorage.getItem('access_token');

      if (storedToken) {
        try {
          // Verify token with backend
          const response = await fetch('http://localhost:8080/api/auth/validate', {
            headers: {
              Authorization: `Bearer ${storedToken}`,
            },
          });

          if (response.ok) {
            const userData = await response.json();
            setUser(userData);
            setToken(storedToken);
          } else {
            localStorage.removeItem('access_token');
            setToken(null);
            setUser(null);
          }
        } catch (error) {
          console.error('Token validation failed:', error);
          localStorage.removeItem('access_token');
          setToken(null);
          setUser(null);
        }
      }

      setIsLoading(false);
    };

    validateToken();
  }, []);

  const login = async (email: string, password: string) => {
    try {
      const response = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, password }),
      });

      if (!response.ok) {
        throw new Error('Login failed');
      }

      const data = await response.json();

      localStorage.setItem('access_token', data.token);
      setToken(data.token);
      setUser({
        id: data.userId,
        email: data.email,
        role: data.role,
      });

      navigate('/');
    } catch (error) {
      console.error('Login error:', error);
      throw error;
    }
  };

  const logout = () => {
    localStorage.removeItem('access_token');
    setToken(null);
    setUser(null);
    navigate('/login');
  };

  const value: AuthContextType = {
    user,
    token,
    login,
    logout,
    isAuthenticated: !!token,
    isLoading,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);

  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }

  return context;
};
```

---

## 5. Database Design

### 5.1 Database Strategy

**Approach:** Code-first with Hibernate `ddl-auto: update`

**Rationale:**
- JPA entities define the schema
- Hibernate automatically creates/updates tables
- No manual SQL scripts needed (initially)
- For production, consider Flyway/Liquibase for versioned migrations

### 5.2 Entity Relationship Diagram (ERD)

```
┌──────────────────┐
│      users       │
├──────────────────┤
│ id (PK)          │───┐
│ email (UNIQUE)   │   │
│ password         │   │ 1:N
│ full_name        │   │
│ avatar           │   │
│ role             │   │
│ status           │   │
│ balance          │   │
│ rating           │   │
│ total_reviews    │   │
│ created_at       │   │
│ updated_at       │   │
└──────────────────┘   │
                        │
                        ▼
                ┌──────────────────┐
                │    accounts      │
                ├──────────────────┤
                │ id (PK)          │───┐
                │ seller_id (FK)   │   │
                │ game_id (FK)     │   │ 1:N
                │ title            │   │
                │ description      │   │
                │ level            │   │
                │ rank             │   │
                │ price            │   │
                │ status           │   │
                │ views_count      │   │
                │ is_featured      │   │
                │ created_at       │   │
                │ updated_at       │   │
                └──────────────────┘   │
                                        │
                        ┌───────────────┼───────────────┐
                        │               │               │
                        ▼               ▼               ▼
                ┌──────────────┐ ┌──────────────┐ ┌──────────────────┐
                │    games     │ │  messages    │ │  transactions    │
                ├──────────────┤ ├──────────────┤ ├──────────────────┤
                │ id (PK)      │ │ id (PK)      │ │ id (PK)          │
                │ name         │ │ account_id   │ │ account_id (FK)  │
                │ slug         │ │ sender_id    │ │ buyer_id (FK)    │
                │ description  │ │ receiver_id  │ │ seller_id (FK)   │
                │ icon_url     │ │ content      │ │ amount           │
                │ created_at   │ │ is_read      │ │ status           │
                └──────────────┘ │ created_at   │ │ encrypted_cred   │
                                 └──────────────┘ │ created_at       │
                                                  │ completed_at     │
                                                  └──────────────────┘
```

### 5.3 Indexes

```sql
-- Performance indexes
CREATE INDEX idx_accounts_seller ON accounts(seller_id);
CREATE INDEX idx_accounts_game ON accounts(game_id);
CREATE INDEX idx_accounts_status ON accounts(status);
CREATE INDEX idx_accounts_price ON accounts(price);
CREATE INDEX idx_accounts_created ON accounts(created_at);

CREATE INDEX idx_transactions_buyer ON transactions(buyer_id);
CREATE INDEX idx_transactions_seller ON transactions(seller_id);
CREATE INDEX idx_transactions_status ON transactions(status);

CREATE INDEX idx_messages_account ON messages(account_id);
CREATE INDEX idx_messages_sender ON messages(sender_id);
CREATE INDEX idx_messages_receiver ON messages(receiver_id);
```

---

## 6. Security Architecture

### 6.1 Security Layer Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    SECURITY LAYERS                          │
│                                                              │
│  1. Network Security                                        │
│     - HTTPS (TLS 1.3)                                       │
│     - WSS (Secure WebSocket)                                │
│     - CORS Configuration                                    │
│                                                              │
│  2. Authentication & Authorization                          │
│     - JWT (Access + Refresh Token)                          │
│     - Spring Security Filter Chain                          │
│     - Role-Based Access Control (RBAC)                      │
│                                                              │
│  3. API Security                                            │
│     - Rate Limiting                                         │
│     - Input Validation                                      │
│     - SQL Injection Prevention (JPA)                        │
│     - XSS Protection                                        │
│                                                              │
│  4. Data Security                                           │
│     - BCrypt Password Hashing                               │
│     - AES-256 Credential Encryption                         │
│     - Sensitive Data Redaction in Logs                      │
└─────────────────────────────────────────────────────────────┘
```

### 6.2 Spring Security Configuration

```java
// src/main/java/com/gameaccount/marketplace/config/SecurityConfig.java
package com.gameaccount.marketplace.config;

import com.gameaccount.marketplace.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (stateless API)
                .csrf(AbstractHttpConfigurer::disable)

                // CORS configuration
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Session management - stateless
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers(
                                "/api/auth/**",
                                "/graphql",
                                "/graphiql/**",
                                "/ws/**",
                                "/actuator/health",
                                "/v3/api-docs/**",
                                "/swagger-ui/**"
                        ).permitAll()

                        // Admin only
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Seller endpoints
                        .requestMatchers("/api/seller/**").hasAnyRole("SELLER", "ADMIN")

                        // All other requests need authentication
                        .anyRequest().authenticated()
                )

                // Add JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // Frontend URL
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
```

### 6.3 JWT Authentication Filter

```java
// src/main/java/com/gameaccount/marketplace/security/JwtAuthenticationFilter.java
package com.gameaccount.marketplace.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String email = tokenProvider.getEmailFromToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
```

---

## 7. API Architecture

### 7.1 Hybrid API Strategy

```
┌─────────────────────────────────────────────────────────────────┐
│                     API TYPE SELECTION                          │
│                                                                  │
│  ┌─────────────────┐  ┌─────────────────┐  ┌────────────────┐  │
│  │     REST        │  │    GraphQL      │  │   WebSocket    │  │
│  │                 │  │                 │  │                │  │
│  │  ✓ CRUD Ops     │  │  ✓ Complex      │  │  ✓ Real-time   │  │
│  │  ✓ File Upload  │  │    Queries      │  │    Chat        │  │
│  │  ✓ Admin APIs   │  │  ✓ Nested Data  │  │  ✓ Live Updates│  │
│  │  ✓ Webhooks     │  │  ✓ Filtering    │  │  ✓ Typing      │  │
│  │  ✓ Simple CRUD  │  │  ✓ Marketplace  │  │                │  │
│  └─────────────────┘  └─────────────────┘  └────────────────┘  │
│                                                                  │
│                      ┌─────────────────┐                        │
│                      │  SHARED SERVICE │                        │
│                      │     LAYER       │                        │
│                      └─────────────────┘                        │
└─────────────────────────────────────────────────────────────────┘
```

### 7.2 API Endpoint Summary

| Category | Type | Endpoint | Purpose |
|----------|------|----------|---------|
| **Auth** | REST | `/api/auth/login` | Login |
| **Auth** | REST | `/api/auth/register` | Register |
| **Users** | REST | `/api/users/profile` | Get profile |
| **Users** | REST | `/api/users/avatar` | Upload avatar |
| **Accounts** | GraphQL | `accounts(...)` | Browse accounts |
| **Accounts** | REST | `/api/accounts` | Create account (seller) |
| **Admin** | REST | `/api/admin/accounts/pending` | Get pending accounts |
| **Admin** | REST | `/api/admin/accounts/:id/approve` | Approve account |
| **Chat** | WebSocket | `/topic/chat/{id}` | Real-time messages |
| **Notifications** | WebSocket | `/topic/notifications/{userId}` | Real-time notifications |

---

## 8. Real-time Communication

### 8.1 WebSocket Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                      WEBSOCKET FLOW                             │
│                                                                  │
│  Client                Spring Boot               Redis           │
│    │                        │                      │             │
│    │  1. Connect (JWT)      │                      │             │
│    ├───────────────────────>│                      │             │
│    │                        │                      │             │
│    │  2. Subscribe          │                      │             │
│    ├───────────────────────>│                      │             │
│    │  /topic/chat/{id}      │                      │             │
│    │                        │                      │             │
│    │  3. Send Message       │                      │             │
│    ├───────────────────────>│                      │             │
│    │  /app/chat.send        │                      │             │
│    │                        │                      │             │
│    │                        │  4. Save to DB       │             │
│    │                        ├─────────────────────>│             │
│    │                        │                      │             │
│    │                        │  5. Publish to Topic │             │
│    │                        │<─────────────────────┤             │
│    │                        │                      │             │
│    │  6. Broadcast Message  │                      │             │
│    │<───────────────────────┤                      │             │
│    │  (All Subscribers)     │                      │             │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 8.2 WebSocket Configuration

```java
// src/main/java/com/gameaccount/marketplace/config/WebSocketConfig.java
package com.gameaccount.marketplace.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enable a simple memory-based message broker to send messages to clients
        // on destinations prefixed with "/topic"
        registry.enableSimpleBroker("/topic", /queue");

        // Designate the "/app" prefix for messages bound for @MessageMapping methods
        registry.setApplicationDestinationPrefixes("/app");
    }
}
```

---

## 9. Caching Strategy

### 9.1 Redis Caching Layers

```
┌─────────────────────────────────────────────────────────────────┐
│                      CACHING STRATEGY                           │
│                                                                  │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │                 L1: Application Cache                       │ │
│  │  (Spring Cache Abstraction - @Cacheable)                   │ │
│  │  - Game lists (TTL: 1 hour)                                │ │
│  │  - Featured accounts (TTL: 5 minutes)                      │ │
│  └────────────────────────────────────────────────────────────┘ │
│                              ↓                                  │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │                 L2: Redis Cache                            │ │
│  │  - User sessions (TTL: 24 hours)                           │ │
│  │  - Rate limiting counters (TTL: 1 minute)                  │ │
│  │  - Hot account data (TTL: 10 minutes)                      │ │
│  │  - WebSocket connection tracking                           │ │
│  └────────────────────────────────────────────────────────────┘ │
│                              ↓                                  │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │                 L3: Database (MySQL)                       │ │
│  │  - Source of truth                                         │ │
│  └────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

### 9.2 Cache Configuration

```java
// src/main/java/com/gameaccount/marketplace/config/RedisConfig.java
package com.gameaccount.marketplace.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.cache.RedisCacheManager;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager() {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new GenericJackson2JsonRedisSerializer())
                );

        return RedisCacheManager.builder(redisConnectionFactory())
                .cacheDefaults(config)
                .withCacheConfiguration("games",
                        config.entryTtl(Duration.ofHours(1)))
                .withCacheConfiguration("featuredAccounts",
                        config.entryTtl(Duration.ofMinutes(5)))
                .build();
    }
}
```

---

## 10. Deployment Architecture

### 10.1 Development Environment

```yaml
# docker-compose.yml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: gameaccount-mysql
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: gameaccount_marketplace
      MYSQL_USER: appuser
      MYSQL_PASSWORD: apppassword
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql

  redis:
    image: redis:7-alpine
    container_name: gameaccount-redis
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data

  backend:
    build:
      context: ./backend-java
      dockerfile: Dockerfile
    container_name: gameaccount-backend
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: dev
      DB_HOST: mysql
      DB_PORT: 3306
      DB_NAME: gameaccount_marketplace
      DB_USERNAME: appuser
      DB_PASSWORD: apppassword
      REDIS_HOST: redis
      REDIS_PORT: 6379
    depends_on:
      - mysql
      - redis

  frontend:
    build:
      context: ./frontend-react
      dockerfile: Dockerfile
    container_name: gameaccount-frontend
    ports:
      - "3000:3000"
    depends_on:
      - backend

volumes:
  mysql-data:
  redis-data:
```

### 10.2 Production Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                      PRODUCTION DEPLOYMENT                      │
│                                                                  │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │                      DNS / CDN                              │ │
│  │              (CloudFlare, Route53)                          │ │
│  └────────────────────────────────────────────────────────────┘ │
│                              ↓                                  │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │                  LOAD BALANCER                              │ │
│  │                  (Nginx / AWS ALB)                          │ │
│  │              SSL Termination (HTTPS)                        │ │
│  └────────────────────────────────────────────────────────────┘ │
│                              ↓                                  │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │              APPLICATION SERVERS (Auto-scaling)             │ │
│  │  ┌────────────────┐  ┌────────────────┐  ┌─────────────┐  │ │
│  │  │   Backend 1    │  │   Backend 2    │  │  Backend N  │  │ │
│  │  │   (Spring)     │  │   (Spring)     │  │  (Spring)   │  │ │
│  │  │   Port: 8080   │  │   Port: 8080   │  │  Port: 8080 │  │ │
│  │  └────────────────┘  └────────────────┘  └─────────────┘  │ │
│  └────────────────────────────────────────────────────────────┘ │
│                              ↓                                  │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │                    STATIC ASSETS                            │ │
│  │                 (S3 / CloudFront CDN)                       │ │
│  └────────────────────────────────────────────────────────────┘ │
│                              ↓                                  │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │                    DATA LAYER                               │ │
│  │  ┌─────────────────┐  ┌─────────────────┐                  │ │
│  │  │  MySQL Primary  │  │  MySQL Replica  │                  │ │
│  │  │  (RDS / Aurora) │  │  (Read Replicas)│                  │ │
│  │  └─────────────────┘  └─────────────────┘                  │ │
│  │                                                              │ │
│  │  ┌─────────────────┐  ┌─────────────────┐                  │ │
│  │  │  Redis Cluster  │  │  Redis Cluster  │                  │ │
│  │  │  (ElastiCache)  │  │  (Replication)  │                  │ │
│  │  └─────────────────┘  └─────────────────┘                  │ │
│  └────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

---

## 11. Development Guidelines

### 11.1 Backend Development Guidelines

| Rule | Description |
|------|-------------|
| **Service First** | Write business logic in Service layer. Controllers/Resolvers just delegate. |
| **DTOs Required** | Never expose entities directly. Use DTOs for all API contracts. |
| **Validation** | Use `@Valid` with Jakarta Validation annotations. |
| **Transactions** | Annotate Service methods with `@Transactional` for state changes. |
| **Exceptions** | Use `GlobalExceptionHandler` with custom exception classes. |
| **Null Safety** | Use `Optional<T>` for repository return types. |
| **Logging** | Use SLF4J with proper log levels (ERROR, WARN, INFO, DEBUG). |
| **Testing** | Write unit tests for services, integration tests for controllers. |

### 11.2 Frontend Development Guidelines

| Rule | Description |
|------|-------------|
| **Type Safety** | All components and utilities must have TypeScript types. |
| **Component Structure** | Functional components with hooks. No class components. |
| **State Management** | Use React Context for global state, Zustand for complex state. |
| **API Calls** | Use custom hooks (`useQuery`, `useMutation`) for data fetching. |
| **Error Handling** | Global error boundary + toast notifications. |
| **Styling** | Tailwind CSS utility classes. No inline styles. |
| **Performance** | Code splitting with `React.lazy()`, memo with `useMemo()` |
| **Testing** | React Testing Library for component tests. |

### 11.3 Git Workflow

```
main (production)
  ↑
  └── develop (integration)
        ↑
        ├── feature/auth
        ├── feature/accounts
        ├── feature/chat
        └── feature/admin
```

**Branch Naming:**
- `feature/feature-name` - New features
- `bugfix/bug-name` - Bug fixes
- `hotfix/critical-fix` - Production hotfixes
- `refactor/component-name` - Code refactoring

**Commit Message Format:**
```
type(scope): subject

body

footer
```

Types: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

---

## 12. Architecture Decision Records (ADR)

### ADR-001: Hybrid API Architecture

**Status:** Accepted

**Context:**
- Need to support both admin operations and public marketplace
- GraphQL provides flexibility for complex queries
- REST is better for file uploads and webhooks
- WebSocket required for real-time features

**Decision:**
- Use REST for Admin, Auth, File uploads, Payment callbacks
- Use GraphQL for Marketplace browsing, searching, details
- Use WebSocket for Chat, Notifications, Live updates
- Share Service layer between REST and GraphQL (DRY)

**Consequences:**
- + Optimal technology for each use case
- + Shared business logic reduces duplication
- - Increased complexity in API design
- - Need to document API boundaries clearly

### ADR-002: Code-First Database Schema

**Status:** Accepted

**Context:**
- Team is more comfortable with Java than SQL
- Need to iterate quickly during development
- Hibernate provides automatic schema generation

**Decision:**
- Use JPA entities as source of truth
- Set `ddl-auto: update` for development
- Consider Flyway for production migrations

**Consequences:**
- + Faster development cycle
- + Type-safe entity definitions
- - Less control over SQL optimization
* - Need to manage schema changes carefully in production

### ADR-003: Monorepo Structure

**Status:** Accepted

**Context:**
- Small team (1 lead + 4 juniors)
- Need coordination between frontend and backend
- Atomic commits across full stack valuable

**Decision:**
- Single repository with separate backend/frontend directories
- Shared documentation in root
- Docker Compose for local development

**Consequences:**
- + Simplified coordination
- + Shared tooling and CI/CD
- - Larger repository size
- - Potential for independent deployment complexity

---

## Appendix A: Quick Start Guide

### A.1 Backend Setup

```bash
# Navigate to backend
cd backend-java

# Build project
mvn clean install

# Run application
mvn spring-boot:run

# Or run JAR directly
java -jar target/marketplace-backend-1.0.0.jar
```

### A.2 Frontend Setup

```bash
# Navigate to frontend
cd frontend-react

# Install dependencies
npm install

# Run dev server
npm run dev

# Build for production
npm run build
```

### A.3 Full Stack with Docker

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

---

**Document Version:** 1.0
**Last Updated:** 2026-01-06
**Author:** Winston (Architect)
**Status:** Draft
**Next Review:** After implementation sprint 1
