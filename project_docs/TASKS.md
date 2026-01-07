# Implementation Plan
## Game Account Marketplace

---

## Document Control

| Field | Value |
|-------|-------|
| **Version** | 1.0 |
| **Author** | Tech Lead / Project Manager |
| **Date** | 2026-01-06 |
| **Status** | Active |
| **Total Duration** | 5 Phases (estimated 8-10 weeks) |

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Phase 1: Project Skeleton](#phase-1-project-skeleton)
3. [Phase 2: Database & Core Entities](#phase-2-database--core-entities)
4. [Phase 3: Authentication & User System](#phase-3-authentication--user-system)
5. [Phase 4: Marketplace Features](#phase-4-marketplace-features)
6. [Phase 5: Real-time Chat](#phase-5-real-time-chat)
7. [Task Tracking Template](#task-tracking-template)
8. [Definition of Done](#definition-of-done)

---

## 1. Project Overview

### 1.1 Implementation Strategy

**Philosophy:** Atomic tasks that can be completed by an AI Developer in a single turn.

**Key Principles:**
- Each task is self-contained and independently verifiable
- Tasks follow dependency order (foundations first)
- Each task includes specific file paths and acceptance criteria
- Tasks are designed for AI execution with clear instructions

### 1.2 Task Format

Each task includes:
- **Task ID:** Unique identifier (e.g., `1.1`, `2.3`)
- **Task Name:** Clear, actionable title
- **Description:** What needs to be done
- **Files to Create/Modify:** Specific file paths
- **Acceptance Criteria:** How to verify completion
- **Dependencies:** Prerequisite tasks
- **Estimated Time:** For human reference

### 1.3 Technology Stack Reference

| Component | Technology | Version |
|-----------|-----------|---------|
| Backend | Java Spring Boot | 3.2.x |
| Language | Java | 21 |
| Database | MySQL | 8.0+ |
| Cache | Redis | 7.0+ |
| Frontend | React + Vite | 18.x |
| Language | TypeScript | 5.x |
| Styling | Tailwind CSS | 3.x |
| GraphQL | Apollo | 3.x |
| WebSocket | STOMP/SockJS | - |

---

## Phase 1: Project Skeleton

**Goal:** Set up the foundational project structure with all configuration files.

**Duration:** 1-2 days

---

### Task 1.1: Initialize Project Structure

**Description:** Create the root monorepo structure and initialize Git repository.

**Files to Create:**
```
GameAccount-Marketplace/
├── .git/
├── .gitignore
├── README.md
├── docker-compose.yml
├── backend-java/
└── frontend-react/
```

**Acceptance Criteria:**
- [ ] Root directory `GameAccount-Marketplace/` exists
- [ ] Git repository initialized with `git init`
- [ ] `.gitignore` includes: `node_modules/`, `target/`, `.idea/`, `.DS_Store`, `*.log`, `.env`
- [ ] Subdirectories `backend-java/` and `frontend-react/` created
- [ ] README.md contains project title and description

**Dependencies:** None

**Instructions:**
1. Create root directory structure
2. Initialize Git repository
3. Create comprehensive .gitignore for Java + Node.js projects
4. Add basic README with project overview

---

### Task 1.2: Backend Setup (Spring Boot)

**Description:** Initialize Spring Boot project with Maven, configure pom.xml and application.yml.

**Files to Create:**
```
backend-java/
├── pom.xml
└── src/
    └── main/
        ├── java/com/gameaccount/marketplace/
        │   └── MarketplaceApplication.java
        └── resources/
            ├── application.yml
            └── application-dev.yml
```

**Files to Modify:**
- Create `backend-java/pom.xml` with all dependencies
- Create `backend-java/src/main/java/com/gameaccount/marketplace/MarketplaceApplication.java`
- Create `backend-java/src/main/resources/application.yml`

**Acceptance Criteria:**
- [ ] `pom.xml` includes: Spring Boot 3.2.x, Spring Data JPA, Spring Security, GraphQL, WebSocket, Redis, MySQL, Lombok, MapStruct, jjwt
- [ ] `MarketplaceApplication.java` has `@SpringBootApplication` annotation
- [ ] `application.yml` configured with datasource, JPA (ddl-auto: update), Redis, server port 8080
- [ ] `application-dev.yml` with dev-specific settings
- [ ] Project builds successfully with `mvn clean install`

**Dependencies:** Task 1.1

**Key Dependencies for pom.xml:**
```xml
<properties>
    <java.version>21</java.version>
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
        <artifactId>spring-boot-starter-validation</artifactId>
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
        <version>0.12.3</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.12.3</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.12.3</version>
        <scope>runtime</scope>
    </dependency>

    <!-- Developer Tools -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>1.5.5.Final</version>
    </dependency>
</dependencies>
```

**application.yml Structure:**
```yaml
spring:
  application:
    name: gameaccount-marketplace
  profiles:
    active: dev
  datasource:
    url: jdbc:mysql://localhost:3306/gameaccount_marketplace?createDatabaseIfNotExist=true
    username: root
    password:
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
  data:
    redis:
      host: localhost
      port: 6379

server:
  port: 8080

jwt:
  secret: MyVerySecretKeyForJWTTokenGeneration
  expiration: 86400000

frontend:
  url: http://localhost:3000
```

---

### Task 1.3: Docker Environment Setup

**Description:** Create docker-compose.yml for MySQL and Redis services.

**Files to Create:**
- `docker-compose.yml` (in root directory)

**Acceptance Criteria:**
- [ ] MySQL 8.0 service configured with environment variables
- [ ] Redis 7-alpine service configured
- [ ] Volume persistence configured for both services
- [ ] Ports exposed: MySQL (3306), Redis (6379)
- [ ] Services start successfully with `docker-compose up -d`

**Dependencies:** Task 1.1

**docker-compose.yml Template:**
```yaml
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
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7-alpine
    container_name: gameaccount-redis
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

volumes:
  mysql-data:
  redis-data:
```

---

### Task 1.4: Frontend Setup (Vite + React + TypeScript)

**Description:** Initialize React frontend with Vite, TypeScript, and Tailwind CSS.

**Files to Create:**
```
frontend-react/
├── package.json
├── vite.config.ts
├── tsconfig.json
├── tailwind.config.js
├── postcss.config.js
├── index.html
└── src/
    ├── main.tsx
    ├── App.tsx
    ├── vite-env.d.ts
    └── styles/
        └── index.css
```

**Acceptance Criteria:**
- [ ] Project initialized with Vite + React + TypeScript template
- [ ] `package.json` includes: react, react-router-dom, @apollo/client, graphql, axios, sockjs-client, @stomp/stompjs, react-hook-form, zustand
- [ ] Tailwind CSS configured and working
- [ ] `vite.config.ts` has proxy setup for backend (port 8080)
- [ ] Project runs successfully with `npm run dev` on port 3000
- [ ] TypeScript compilation works

**Dependencies:** Task 1.1

**package.json Key Dependencies:**
```json
{
  "name": "gameaccount-marketplace-frontend",
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "tsc && vite build",
    "preview": "vite preview"
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
    "react-hook-form": "^7.49.2",
    "zustand": "^4.4.7",
    "react-hot-toast": "^2.4.1"
  },
  "devDependencies": {
    "@types/react": "^18.2.47",
    "@types/react-dom": "^18.2.18",
    "@vitejs/plugin-react": "^4.2.1",
    "autoprefixer": "^10.4.16",
    "postcss": "^8.4.33",
    "tailwindcss": "^3.4.0",
    "typescript": "^5.3.3",
    "vite": "^5.0.11"
  }
}
```

**vite.config.ts Template:**
```typescript
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/graphql': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/ws': {
        target: 'ws://localhost:8080',
        ws: true,
      },
    },
  },
})
```

---

## Phase 2: Database & Core Entities

**Goal:** Define and implement all JPA entities with relationships.

**Duration:** 2-3 days

**Prerequisites:** Phase 1 complete, Docker services running

---

### Task 2.1: Define User Entity & Enums

**Description:** Create User entity with JPA annotations and Role/UserStatus enums.

**Files to Create:**
```
backend-java/src/main/java/com/gameaccount/marketplace/
├── entity/
│   ├── User.java
│   └── constant/
│       ├── Role.java (enum)
│       └── UserStatus.java (enum)
```

**Acceptance Criteria:**
- [ ] User entity has fields: id, email, password, fullName, avatar, role, status, balance, rating, totalReviews, createdAt, updatedAt
- [ ] Email has @Column(unique = true, nullable = false)
- [ ] Role enum values: BUYER, SELLER, ADMIN
- [ ] UserStatus enum values: ACTIVE, BANNED, SUSPENDED
- [ ] Lombok annotations: @Entity, @Getter, @Setter, @Builder, @NoArgsConstructor, @AllArgsConstructor
- [ ] JPA annotations: @Id, @GeneratedValue, @Table, @Enumerated
- [ ] Spring Data JPA auditing: @CreatedDate, @LastModifiedDate
- [ ] @OneToMany relationships to Account and Transaction initialized

**Entity Template:**
```java
@Entity
@Table(name = "users")
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

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Account> accounts = new ArrayList<>();

    public enum Role { BUYER, SELLER, ADMIN }
    public enum UserStatus { ACTIVE, BANNED, SUSPENDED }
}
```

**Dependencies:** Task 1.2

---

### Task 2.2: Define Account & Game Entities

**Description:** Create Account and Game entities with relationships.

**Files to Create:**
```
backend-java/src/main/java/com/gameaccount/marketplace/entity/
├── Game.java
├── Account.java
└── constant/
    └── AccountStatus.java (enum)
```

**Acceptance Criteria:**
- [ ] Game entity: id, name, slug, description, iconUrl, accountCount, createdAt
- [ ] Account entity: id, seller(@ManyToOne), game(@ManyToOne), title, description, level, rank, price, status, viewsCount, isFeatured, createdAt, updatedAt
- [ ] AccountStatus enum: PENDING, APPROVED, REJECTED, SOLD
- [ ] Game has @OneToMany to Account
- [ ] Account has @ElementCollection for images list
- [ ] All required JPA and Lombok annotations
- [ ] Slug field unique for Game

**Dependencies:** Task 2.1

**Account Entity Template:**
```java
@Entity
@Table(name = "accounts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    private String title;
    private String description;

    private Integer level;
    private String rank;

    @Column(precision = 10, scale = 2)
    private Double price;

    @Enumerated(EnumType.STRING)
    private AccountStatus status = AccountStatus.PENDING;

    private Integer viewsCount = 0;
    private Boolean isFeatured = false;

    @ElementCollection
    @CollectionTable(name = "account_images", joinColumns = @JoinColumn(name = "account_id"))
    @Column(name = "image_url")
    @Builder.Default
    private List<String> images = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum AccountStatus { PENDING, APPROVED, REJECTED, SOLD }
}
```

---

### Task 2.3: Define Transaction & Message Entities

**Description:** Create Transaction and Message entities for marketplace operations.

**Files to Create:**
```
backend-java/src/main/java/com/gameaccount/marketplace/entity/
├── Transaction.java
├── Message.java
└── constant/
    └── TransactionStatus.java (enum)
```

**Acceptance Criteria:**
- [ ] Transaction entity: id, account(@ManyToOne), buyer(@ManyToOne), seller(@ManyToOne), amount, status, encryptedCredentials(@Lob), createdAt, completedAt
- [ ] TransactionStatus enum: PENDING, COMPLETED, CANCELLED
- [ ] Message entity: id, account(@ManyToOne), sender(@ManyToOne), receiver(@ManyToOne), content(@Column columnDefinition="TEXT"), isRead, createdAt
- [ ] All relationships properly configured with @ManyToOne
- [ ] All required JPA and Lombok annotations
- [ ] createdAt timestamps configured

**Dependencies:** Task 2.2

---

### Task 2.4: Setup Repository Layer

**Description:** Create Spring Data JPA repository interfaces for all entities.

**Files to Create:**
```
backend-java/src/main/java/com/gameaccount/marketplace/repository/
├── UserRepository.java
├── GameRepository.java
├── AccountRepository.java
├── TransactionRepository.java
└── MessageRepository.java
```

**Acceptance Criteria:**
- [ ] All repositories extend JpaRepository<Entity, Long>
- [ ] UserRepository has: findByEmail(), existsByEmail(), findByRole()
- [ ] AccountRepository has: findBySellerId(), findByGameId(), findByStatus()
- [ ] TransactionRepository has: findByBuyerId(), findBySellerId()
- [ ] MessageRepository has: findByAccountIdAndSenderIdOrReceiverId()
- [ ] All interfaces annotated with @Repository
- [ ] Custom queries using @Query where needed

**UserRepository Template:**
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRole(User.Role role);
    Page<User> findByRole(User.Role role, Pageable pageable);
}
```

**AccountRepository Template:**
```java
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findBySellerId(Long sellerId);
    List<Account> findByGameId(Long gameId);
    List<Account> findByStatus(Account.AccountStatus status);
    Page<Account> findByStatus(Account.AccountStatus status, Pageable pageable);

    @Query("SELECT a FROM Account a WHERE a.game.id = :gameId AND a.status = 'APPROVED'")
    List<Account> findApprovedAccountsByGame(@Param("gameId") Long gameId);
}
```

**Dependencies:** Task 2.3

---

### Task 2.5: Verify Database Schema

**Description:** Start the application and verify Hibernate creates all tables correctly.

**Acceptance Criteria:**
- [ ] Docker MySQL and Redis services are running
- [ ] Application starts without errors
- [ ] MySQL database `gameaccount_marketplace` created
- [ ] All tables created: users, games, accounts, transactions, messages, account_images
- [ ] Foreign key constraints properly set up
- [ ] Verify in MySQL: `SHOW TABLES;` and `DESCRIBE users;`

**Dependencies:** Task 2.4

**Verification SQL Commands:**
```sql
-- Check all tables exist
SHOW TABLES;

-- Verify users table structure
DESCRIBE users;

-- Verify foreign keys
SELECT CONSTRAINT_NAME, TABLE_NAME, COLUMN_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'gameaccount_marketplace';
```

---

## Phase 3: Authentication & User System (REST)

**Goal:** Implement JWT-based authentication and user profile management.

**Duration:** 3-4 days

**Prerequisites:** Phase 2 complete

---

### Task 3.1: Implement JWT Security Configuration

**Description:** Create SecurityConfig, JWT utility classes, and password encoder bean.

**Files to Create:**
```
backend-java/src/main/java/com/gameaccount/marketplace/
├── config/
│   └── SecurityConfig.java
├── security/
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   └── UserDetailsServiceImpl.java
```

**Files to Modify:**
- Update `application.yml` with JWT secret and expiration

**Acceptance Criteria:**
- [ ] SecurityConfig disables CSRF and enables CORS
- [ ] Stateful session management disabled (SessionCreationPolicy.STATELESS)
- [ ] Public endpoints: `/api/auth/**`, `/graphql`, `/graphiql/**`, `/ws/**`
- [ ] Admin endpoints protected: `/api/admin/**` requires ROLE_ADMIN
- [ ] JwtTokenProvider generates and validates JWT tokens
- [ ] JwtAuthenticationFilter extracts and validates token from Authorization header
- [ ] BCrypt password encoder bean configured
- [ ] CORS configured for frontend URL

**SecurityConfig Template:**
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/graphql", "/graphiql/**", "/ws/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
```

**Dependencies:** Task 2.5

---

### Task 3.2: Create DTOs and Mappers

**Description:** Create request/response DTOs and MapStruct mappers for user operations.

**Files to Create:**
```
backend-java/src/main/java/com/gameaccount/marketplace/dto/
├── request/
│   ├── LoginRequest.java
│   ├── RegisterRequest.java
│   └── UpdateProfileRequest.java
├── response/
│   ├── AuthResponse.java
│   └── UserResponse.java
└── mapper/
    └── UserMapper.java (MapStruct interface)
```

**Acceptance Criteria:**
- [ ] LoginRequest: email, password fields with @Valid validation
- [ ] RegisterRequest: email, password, fullName with @Valid and @Email validation
- [ ] AuthResponse: token, userId, email, role, fullName
- [ ] UserResponse: id, email, fullName, avatar, role, balance, rating, createdAt
- [ ] UserMapper MapStruct interface with toEntity() and toResponse() methods
- [ ] All DTOs use Lombok @Data, @Builder, @NoArgsConstructor, @AllArgsConstructor
- [ ] Jakarta validation annotations on request DTOs

**LoginRequest Template:**
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
```

**UserMapper Template:**
```java
@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(RegisterRequest request);
    @Mapping("password", ignore = true)
    UserResponse toResponse(User user);
}
```

**Dependencies:** Task 3.1

---

### Task 3.3: Implement AuthService

**Description:** Create AuthService with login, register, and profile management logic.

**Files to Create:**
```
backend-java/src/main/java/com/gameaccount/marketplace/service/
└── AuthService.java
```

**Acceptance Criteria:**
- [ ] AuthService annotated with @Service and @RequiredArgsConstructor
- [ ] register() method validates email uniqueness, hashes password, saves user, generates JWT
- [ ] login() method authenticates with AuthenticationManager, generates JWT
- [ ] getProfile() method retrieves user by ID from SecurityContext
- [ ] updateProfile() method updates fullName and avatar
- [ ] All methods use UserRepository
- [ ] Passwords hashed with BCrypt before saving
- [ ] @Transactional used for state-changing operations

**AuthService Template:**
```java
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already registered");
        }

        User user = User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .fullName(request.getFullName())
            .role(User.Role.BUYER)
            .status(User.UserStatus.ACTIVE)
            .build();

        user = userRepository.save(user);

        String token = tokenProvider.generateToken(user.getEmail());

        return AuthResponse.builder()
            .token(token)
            .userId(user.getId())
            .email(user.getEmail())
            .role(user.getRole().name())
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
            .role(user.getRole().name())
            .build();
    }

    public UserResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserMapper.INSTANCE.toResponse(user);
    }
}
```

**Dependencies:** Task 3.2

---

### Task 3.4: Implement AuthController

**Description:** Create REST controller for authentication endpoints.

**Files to Create:**
```
backend-java/src/main/java/com/gameaccount/marketplace/controller/
└── auth/
    └── AuthController.java
```

**Acceptance Criteria:**
- [ ] @RestController with @RequestMapping("/api/auth")
- [ ] @CrossOrigin annotation for frontend URL
- [ ] POST /api/auth/register endpoint
- [ ] POST /api/auth/login endpoint
- [ ] GET /api/auth/me endpoint (requires authentication)
- [ ] All endpoints use @Valid for request validation
- [ ] Returns ResponseEntity with appropriate HTTP status codes
- [ ] Global exception handling for BusinessException

**AuthController Template:**
```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "${frontend.url}")
public class AuthController {

    private final AuthService authService;

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
    public ResponseEntity<UserResponse> getProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        UserResponse response = authService.getProfile(user.getId());
        return ResponseEntity.ok(response);
    }
}
```

**Dependencies:** Task 3.3

---

### Task 3.5: Create GlobalExceptionHandler

**Description:** Implement centralized exception handling with proper HTTP response codes.

**Files to Create:**
```
backend-java/src/main/java/com/gameaccount/marketplace/exception/
├── BusinessException.java
├── ResourceNotFoundException.java
└── GlobalExceptionHandler.java
```

**Acceptance Criteria:**
- [ ] BusinessException extends RuntimeException
- [ ] ResourceNotFoundException extends RuntimeException
- [ ] GlobalExceptionHandler annotated with @RestControllerAdvice
- [ ] Handles BusinessException (returns 400 Bad Request)
- [ ] Handles ResourceNotFoundException (returns 404 Not Found)
- [ ] Handles MethodArgumentNotValidException (returns 400 with field errors)
- [ ] Handles AuthenticationException (returns 401 Unauthorized)
- [ ] Returns consistent error response format: { timestamp, status, error, message, path }

**GlobalExceptionHandler Template:**
```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, WebRequest request) {
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Business Error")
            .message(ex.getMessage())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Not Found")
            .message(ex.getMessage())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}

@Data
@Builder
class ErrorResponse {
    private Instant timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
}
```

**Dependencies:** Task 3.4

---

### Task 3.6: Frontend Auth Setup (Apollo Client)

**Description:** Set up Apollo Client with JWT authentication and create AuthContext.

**Files to Create:**
```
frontend-react/src/
├── services/
│   └── graphql/
│       └── client.ts
├── contexts/
│   └── AuthContext.tsx
├── hooks/
│   └── useAuth.ts
└── types/
    └── user.ts
```

**Acceptance Criteria:**
- [ ] Apollo Client configured with HTTP link to GraphQL endpoint
- [ ] AuthLink adds JWT token from localStorage to each request
- [ ] ErrorLink handles 401 by clearing token and redirecting to login
- [ ] AuthContext provides: user, token, login, logout, isAuthenticated
- [ ] useAuth hook for consuming AuthContext
- [ ] TypeScript types defined for User and AuthResponse
- [ ] Login function stores token in localStorage

**client.ts Template:**
```typescript
import { ApolloClient, InMemoryCache, createHttpLink, from } from '@apollo/client';
import { setContext } from '@apollo/client/link/context';
import { onError } from '@apollo/client/link/error';

const httpLink = createHttpLink({
  uri: 'http://localhost:8080/graphql',
});

const authLink = setContext((_, { headers }) => {
  const token = localStorage.getItem('access_token');
  return {
    headers: {
      ...headers,
      authorization: token ? `Bearer ${token}` : '',
    },
  };
});

const errorLink = onError(({ graphQLErrors, networkError }) => {
  if (networkError && 'statusCode' in networkError && networkError.statusCode === 401) {
    localStorage.removeItem('access_token');
    window.location.href = '/login';
  }
});

export const apolloClient = new ApolloClient({
  link: from([authLink, errorLink, httpLink]),
  cache: new InMemoryCache(),
});
```

**Dependencies:** Task 1.4, Task 3.4

---

### Task 3.7: Frontend Auth Pages (Login/Register)

**Description:** Create login and register pages with forms and validation.

**Files to Create:**
```
frontend-react/src/
├── pages/
│   ├── LoginPage.tsx
│   └── RegisterPage.tsx
├── components/
│   └── features/
│       └── auth/
│           ├── LoginForm.tsx
│           └── RegisterForm.tsx
└── services/
    └── rest/
        └── authApi.ts
```

**Acceptance Criteria:**
- [ ] LoginForm with email and password fields
- [ ] RegisterForm with email, password, fullName fields
- [ ] Form validation with react-hook-form
- [ ] Error messages displayed on failure
- [ ] Successful login stores token and redirects to home
- [ ] Tailwind CSS styling
- [ ] TypeScript types for all props
- [ ] Loading state during API call

**LoginForm Template:**
```typescript
interface LoginFormData {
  email: string;
  password: string;
}

export function LoginForm() {
  const { login } = useAuth();
  const { register, handleSubmit, formState: { errors } } = useForm<LoginFormData>();

  const onSubmit = async (data: LoginFormData) => {
    try {
      await login(data.email, data.password);
      window.location.href = '/';
    } catch (error) {
      console.error('Login failed:', error);
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <div>
        <label>Email</label>
        <input type="email" {...register('email', { required: true })} />
        {errors.email && <span>Email is required</span>}
      </div>
      <div>
        <label>Password</label>
        <input type="password" {...register('password', { required: true, minLength: 6 })} />
        {errors.password && <span>Password is required</span>}
      </div>
      <button type="submit">Login</button>
    </form>
  );
}
```

**Dependencies:** Task 3.6

---

### Task 3.8: Frontend Router & Protected Routes

**Description:** Set up React Router with protected routes and navigation.

**Files to Create:**
```
frontend-react/src/
├── App.tsx (update)
├── main.tsx (wrap with ApolloProvider + AuthProvider)
└── components/
    └── layout/
        └── ProtectedRoute.tsx
```

**Acceptance Criteria:**
- [ ] BrowserRouter configured in App.tsx
- [ ] Routes: /login, /register, / (home - protected)
- [ ] ProtectedRoute component checks authentication
- [ ] Redirects to /login if not authenticated
- [ ] ApolloProvider wraps the app
- [ ] AuthProvider wraps the app
- [ ] Navigation/Header with logout button

**ProtectedRoute Template:**
```typescript
export function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
}
```

**Dependencies:** Task 3.7

---

## Phase 4: Marketplace Features (GraphQL)

**Goal:** Implement GraphQL API for browsing and searching game accounts.

**Duration:** 3-4 days

**Prerequisites:** Phase 3 complete

---

### Task 4.1: Implement AccountService

**Description:** Create service layer for account CRUD operations and search logic.

**Files to Create:**
```
backend-java/src/main/java/com/gameaccount/marketplace/service/
├── AccountService.java
└── GameService.java
```

**Acceptance Criteria:**
- [ ] AccountService annotated with @Service
- [ ] createAccount() method validates seller, creates account with PENDING status
- [ ] searchAccounts() method with filters: gameId, minPrice, maxPrice, status, isFeatured
- [ ] getAccountById() method with view count increment
- [ ] approveAccount() method (admin only) changes status to APPROVED
- [ ] rejectAccount() method (admin only) changes status to REJECTED
- [ ] GameService: getAllGames(), getGameById(), getGameBySlug()
- [ ] @Cacheable on frequently accessed methods
- [ ] @Transactional on state-changing methods

**AccountService Template:**
```java
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    @Transactional
    public Account createAccount(Long sellerId, CreateAccountRequest request) {
        User seller = userRepository.findById(sellerId)
            .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        Game game = gameRepository.findById(request.getGameId())
            .orElseThrow(() -> new ResourceNotFoundException("Game not found"));

        Account account = Account.builder()
            .seller(seller)
            .game(game)
            .title(request.getTitle())
            .description(request.getDescription())
            .level(request.getLevel())
            .rank(request.getRank())
            .price(request.getPrice())
            .images(request.getImages())
            .status(Account.AccountStatus.PENDING)
            .build();

        return accountRepository.save(account);
    }

    @Cacheable(value = "accounts", key = "#gameId + '_' + #minPrice + '_' + #maxPrice")
    public List<Account> searchAccounts(Long gameId, Double minPrice, Double maxPrice, Account.AccountStatus status) {
        // Implementation with dynamic query building
        return accountRepository.findAll(); // Add filtering logic
    }

    @Transactional
    public Account approveAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        account.setStatus(Account.AccountStatus.APPROVED);
        return accountRepository.save(account);
    }
}
```

**Dependencies:** Task 3.5

---

### Task 4.2: Setup GraphQL Schema

**Description:** Define GraphQL schema with types, queries, and mutations.

**Files to Create:**
```
backend-java/src/main/resources/graphql/
└── schema.graphqls
```

**Acceptance Criteria:**
- [ ] Type definitions: User, Account, Game, Transaction
- [ ] Enums: Role, AccountStatus, TransactionStatus
- [ ] Input types: CreateAccountInput, UpdateAccountInput
- [ ] Queries: accounts(filter), account(id), games, game(id), me
- [ ] Mutations: createAccount, updateAccount, deleteAccount, approveAccount, rejectAccount
- [ ] Proper relationships defined between types
- [ ] Pagination support with AccountConnection, PageInfo

**schema.graphqls Template:**
```graphql
type User {
  id: ID!
  email: String!
  fullName: String
  role: Role!
  avatar: String
  balance: Float!
  rating: Float!
  createdAt: String!
}

type Account {
  id: ID!
  seller: User!
  game: Game!
  title: String!
  description: String
  level: Int
  rank: String
  price: Float!
  images: [String!]!
  status: AccountStatus!
  viewsCount: Int!
  isFeatured: Boolean!
  createdAt: String!
  updatedAt: String!
}

type Game {
  id: ID!
  name: String!
  slug: String!
  description: String
  iconUrl: String
  accountCount: Int!
}

enum Role {
  BUYER
  SELLER
  ADMIN
}

enum AccountStatus {
  PENDING
  APPROVED
  REJECTED
  SOLD
}

type Query {
  accounts(
    gameId: ID
    minPrice: Float
    maxPrice: Float
    status: AccountStatus
    isFeatured: Boolean
    page: Int
    limit: Int
  ): [Account!]!

  account(id: ID!): Account!

  games: [Game!]!

  game(id: ID!): Game!

  me: User
}

type Mutation {
  createAccount(input: CreateAccountInput!): Account!

  updateAccount(id: ID!, input: UpdateAccountInput!): Account!

  deleteAccount(id: ID!): Boolean!

  approveAccount(id: ID!): Account!

  rejectAccount(id: ID!): Account!
}

input CreateAccountInput {
  gameId: ID!
  title: String!
  description: String
  level: Int
  rank: String
  price: Float!
  images: [String!]!
}

input UpdateAccountInput {
  title: String
  description: String
  level: Int
  rank: String
  price: Float
  images: [String!]
}
```

**Dependencies:** Task 4.1

---

### Task 4.3: Implement GraphQL Resolvers

**Description:** Create Query and Mutation resolvers for GraphQL.

**Files to Create:**
```
backend-java/src/main/java/com/gameaccount/marketplace/graphql/
├── query/
│   ├── AccountQuery.java
│   ├── GameQuery.java
│   └── UserQuery.java
└── mutation/
    ├── AccountMutation.java
    └── AuthMutation.java
```

**Acceptance Criteria:**
- [ ] All query classes implement GraphQLQueryResolver
- [ ] All mutation classes implement GraphQLMutationResolver
- [ ] AccountQuery: accounts(), account() methods
- [ ] GameQuery: games(), game() methods
- [ ] UserQuery: me() method
- [ ] AccountMutation: createAccount(), updateAccount(), deleteAccount(), approveAccount(), rejectAccount()
- [ ] All resolvers delegate to Service layer (not Repository directly)
- [ ] Proper error handling with custom exceptions
- [ ] Security checks (e.g., only admin can approve)

**AccountQuery Template:**
```java
@Component
@RequiredArgsConstructor
public class AccountQuery implements GraphQLQueryResolver {

    private final AccountService accountService;

    public List<Account> accounts(Long gameId, Double minPrice, Double maxPrice,
                                   Account.AccountStatus status, Boolean isFeatured) {
        return accountService.searchAccounts(gameId, minPrice, maxPrice, status);
    }

    public Account account(Long id) {
        return accountService.getAccountById(id);
    }
}
```

**AccountMutation Template:**
```java
@Component
@RequiredArgsConstructor
public class AccountMutation implements GraphQLMutationResolver {

    private final AccountService accountService;

    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public Account createAccount(CreateAccountInput input) {
        // Get seller ID from SecurityContext
        return accountService.createAccount(getCurrentUserId(), input);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Account approveAccount(Long id) {
        return accountService.approveAccount(id);
    }
}
```

**Dependencies:** Task 4.2

---

### Task 4.4: Frontend GraphQL Setup

**Description:** Create GraphQL queries and mutations in frontend.

**Files to Create:**
```
frontend-react/src/services/graphql/
├── queries.ts
└── mutations.ts
```

**Acceptance Criteria:**
- [ ] GET_ACCOUNTS query with filter variables
- [ ] GET_ACCOUNT query by ID
- [ ] GET_GAMES query
- [ ] CREATE_ACCOUNT mutation
- [ ] UPDATE_ACCOUNT mutation
- [ ] All queries properly typed with TypeScript
- [ ] useQuery hooks for data fetching
- [ ] useMutation hooks for data modification

**queries.ts Template:**
```typescript
import { gql } from '@apollo/client';

export const GET_ACCOUNTS = gql`
  query GetAccounts(
    $gameId: ID
    $minPrice: Float
    $maxPrice: Float
    $status: AccountStatus
    $isFeatured: Boolean
  ) {
    accounts(
      gameId: $gameId
      minPrice: $minPrice
      maxPrice: $maxPrice
      status: $status
      isFeatured: $isFeatured
    ) {
      id
      title
      price
      level
      rank
      images
      status
      seller {
        id
        fullName
        rating
      }
      game {
        id
        name
        slug
      }
    }
  }
`;

export const GET_GAMES = gql`
  query GetGames {
    games {
      id
      name
      slug
      iconUrl
      accountCount
    }
  }
`;
```

**mutations.ts Template:**
```typescript
import { gql } from '@apollo/client';

export const CREATE_ACCOUNT = gql`
  mutation CreateAccount($input: CreateAccountInput!) {
    createAccount(input: $input) {
      id
      title
      price
      status
    }
  }
`;
```

**Dependencies:** Task 4.3

---

### Task 4.5: Frontend Marketplace UI

**Description:** Create account listing page and account detail page.

**Files to Create:**
```
frontend-react/src/
├── pages/
│   ├── HomePage.tsx
│   └── AccountDetailPage.tsx
└── components/
    └── features/
        └── account/
            ├── AccountCard.tsx
            ├── AccountList.tsx
            ├── AccountFilters.tsx
            └── AccountDetail.tsx
```

**Acceptance Criteria:**
- [ ] AccountList component displays accounts in grid layout
- [ ] AccountCard shows: image, title, price, game, seller rating
- [ ] AccountFilters component: game dropdown, price range slider
- [ ] useQuery hook to fetch accounts with filters
- [ ] AccountDetailPage shows full account details
- [ ] Tailwind CSS responsive design
- [ ] Loading skeleton while fetching
- [ ] Error handling

**AccountList Template:**
```typescript
export function AccountList() {
  const [filters, setFilters] = useState({});
  const { data, loading, error } = useQuery(GET_ACCOUNTS, {
    variables: filters,
  });

  if (loading) return <AccountListSkeleton />;
  if (error) return <div>Error loading accounts</div>;

  return (
    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
      {data.accounts.map(account => (
        <AccountCard key={account.id} account={account} />
      ))}
    </div>
  );
}
```

**Dependencies:** Task 4.4

---

## Phase 5: Real-time Chat (WebSocket)

**Goal:** Implement WebSocket-based chat system between buyers and sellers.

**Duration:** 2-3 days

**Prerequisites:** Phase 4 complete

---

### Task 5.1: WebSocket Configuration

**Description:** Configure Spring WebSocket with STOMP protocol.

**Files to Create:**
```
backend-java/src/main/java/com/gameaccount/marketplace/config/
└── WebSocketConfig.java
```

**Acceptance Criteria:**
- [ ] @EnableWebSocketMessageBroker annotation
- [ ] Register STOMP endpoint at `/ws` with SockJS support
- [ ] Enable simple broker for `/topic` and `/queue` prefixes
- [ ] Set application destination prefix to `/app`
- [ ] CORS allowed for frontend URL
- [ ] Connected users tracked via ChannelInterceptor

**WebSocketConfig Template:**
```java
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
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }
}
```

**Dependencies:** Task 4.5

---

### Task 5.2: ChatService & Message Handling

**Description:** Create service for chat operations and WebSocket message handlers.

**Files to Create:**
```
backend-java/src/main/java/com/gameaccount/marketplace/
├── service/
│   └── ChatService.java
└── websocket/
    └── ChatController.java
```

**Acceptance Criteria:**
- [ ] ChatService annotated with @Service
- [ ] sendMessage() method saves message to database and broadcasts to topic
- [ ] getConversation() method retrieves chat history for an account
- [ ] ChatController handles WebSocket messages with @MessageMapping
- [ ] @SendTo annotation broadcasts to `/topic/chat/{accountId}`
- [ ] Typing indicators handled separately
- [ ] @Transactional for database operations

**ChatController Template:**
```java
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    @SendTo("/topic/chat/{accountId}")
    public Message sendMessage(@Payload ChatMessageRequest request, Principal principal) {
        Message message = chatService.sendMessage(
            request.getAccountId(),
            principal.getName(),
            request.getReceiverId(),
            request.getContent()
        );
        return message;
    }

    @MessageMapping("/chat.typing")
    public void handleTyping(@Payload TypingIndicatorRequest request, Principal principal) {
        messagingTemplate.convertAndSendToUser(
            request.getReceiverId().toString(),
            "/queue/typing/" + request.getAccountId(),
            new TypingIndicator(principal.getName(), true)
        );
    }
}
```

**Dependencies:** Task 5.1

---

### Task 5.3: Frontend WebSocket Setup

**Description:** Create WebSocket client service in frontend.

**Files to Create:**
```
frontend-react/src/
├── services/
│   └── websocket/
│       ├── socketClient.ts
│       └── types.ts
└── hooks/
    └── useChat.ts
```

**Acceptance Criteria:**
- [ ] SockJS and STOMP client initialized
- [ ] Connection with JWT token in headers
- [ ] subscribeToChat() method for message subscription
- [ ] sendMessage() method for sending messages
- [ ] sendTypingIndicator() method
- [ ] Auto-reconnect on disconnect
- [ ] Cleanup on unmount
- [ ] TypeScript types for all WebSocket messages

**socketClient.ts Template:**
```typescript
import { Client, StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

class WebSocketService {
  private client: Client | null = null;

  connect(token: string) {
    this.client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      reconnectDelay: 5000,
      onConnect: () => console.log('WebSocket connected'),
      onDisconnect: () => console.log('WebSocket disconnected'),
    });
    this.client.activate();
  }

  subscribeToChat(accountId: number, callback: (message: any) => void) {
    return this.client?.subscribe(`/topic/chat/${accountId}`, (message) => {
      callback(JSON.parse(message.body));
    });
  }

  sendMessage(accountId: number, receiverId: number, content: string) {
    this.client?.publish({
      destination: '/app/chat.send',
      body: JSON.stringify({ accountId, receiverId, content }),
    });
  }
}

export const websocketService = new WebSocketService();
```

**Dependencies:** Task 5.2

---

### Task 5.4: Frontend Chat UI

**Description:** Create chat interface components.

**Files to Create:**
```
frontend-react/src/components/features/chat/
├── ChatBox.tsx
├── ConversationList.tsx
└── MessageBubble.tsx
```

**Acceptance Criteria:**
- [ ] ChatBox displays message history
- [ ] Message input with send button
- [ ] Auto-scroll to latest message
- [ ] Typing indicator display
- [ ] MessageBubble styled for sent/received messages
- [ ] Timestamp display
- [ ] Tailwind CSS styling
- [ ] useChat hook for state management

**ChatBox Template:**
```typescript
export function ChatBox({ accountId, receiverId }: ChatBoxProps) {
  const [messages, setMessages] = useState<Message[]>([]);
  const { sendMessage, subscribeToChat } = useChat();

  useEffect(() => {
    const subscription = subscribeToChat(accountId, (message) => {
      setMessages(prev => [...prev, message]);
    });
    return () => subscription.unsubscribe();
  }, [accountId]);

  const handleSend = (content: string) => {
    sendMessage(accountId, receiverId, content);
  };

  return (
    <div className="flex flex-col h-96">
      <MessageList messages={messages} />
      <MessageInput onSend={handleSend} />
    </div>
  );
}
```

**Dependencies:** Task 5.3

---

## Task Tracking Template

### Phase Status Tracker

| Phase | Status | Start Date | End Date | Notes |
|-------|--------|------------|----------|-------|
| Phase 1: Project Skeleton | ⬜ Not Started | | | |
| Phase 2: Database & Entities | ⬜ Not Started | | | |
| Phase 3: Authentication | ⬜ Not Started | | | |
| Phase 4: Marketplace (GraphQL) | ⬜ Not Started | | | |
| Phase 5: Real-time Chat | ⬜ Not Started | | | |

### Task Checklist

Copy this template for tracking individual tasks:

```
Task X.X: [Task Name]
├── Status: ⬜ Not Started | 🟡 In Progress | ✅ Complete | ❌ Blocked
├── Assigned To: [Name/AI Agent]
├── Started: [Date]
├── Completed: [Date]
├── Time Spent: [Hours]
└── Notes: [Any blockers, deviations, or additional work]
```

---

## Definition of Done

A task is considered **COMPLETE** when:

- [ ] All acceptance criteria are met
- [ ] Code follows the project's style guidelines
- [ ] No compilation or runtime errors
- [ ] Manual testing completed successfully
- [ ] Git commit created with descriptive message
- [ ] Code reviewed (if peer review required)

### Phase Completion Criteria

A **PHASE** is complete when:

- [ ] All tasks in the phase are complete
- [ ] Integration testing across phase components done
- [ ] Documentation updated (if applicable)
- [ ] Demo/verification walkthrough completed
- [ ] No critical bugs remaining

---

## Risk Mitigation

| Risk | Probability | Impact | Mitigation |
|------|-------------|--------|------------|
| Database schema changes after Phase 2 | Medium | High | Use Flyway migrations for production |
| JWT token expiration handling | Low | Medium | Implement refresh token mechanism |
| GraphQL N+1 queries | High | Medium | Use DataLoader patterns |
| WebSocket connection drops | Medium | Medium | Implement auto-reconnect with exponential backoff |
| CORS issues in development | Low | Low | Configure proxy in vite.config.ts |

---

## Next Steps After Phase 5

1. **Testing & QA**
   - Unit tests for services
   - Integration tests for controllers
   - E2E tests with Cypress/Playwright

2. **Performance Optimization**
   - Add database indexes
   - Implement Redis caching strategy
   - Optimize GraphQL queries

3. **Security Hardening**
   - Rate limiting on public endpoints
   - Input validation review
   - Security audit

4. **Deployment**
   - Dockerize backend and frontend
   - Set up CI/CD pipeline
   - Deploy to staging environment

---

**Document Version:** 1.0
**Last Updated:** 2026-01-06
**Author:** Tech Lead / Project Manager
**Status:** Active

**Questions?** Refer to `PRD.md` for requirements and `ARCHITECTURE.md` for technical design.
