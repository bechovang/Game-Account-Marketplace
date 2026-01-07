# Product Requirements Document (PRD)
## Game Account Marketplace

---

## 1. Overview

### 1.1 Project Description

**Project Name:** Game Account Marketplace

**Product Vision:** A secure, real-time marketplace platform for buying and selling game accounts, featuring hybrid API architecture for optimal performance and user experience.

### 1.2 Technology Stack

| Layer | Technology | Purpose |
|-------|-----------|---------|
| **Frontend** | React.js + TypeScript + Tailwind CSS | SPA with responsive UI |
| **Backend** | Java 17/21, Spring Boot 3.x | Enterprise-grade REST & GraphQL APIs |
| **Security** | Spring Security + JWT | Authentication & Authorization |
| **ORM** | Hibernate / Spring Data JPA | Database abstraction |
| **Build Tool** | Maven | Dependency management |
| **Database** | MySQL 8.0+ | Primary data storage |
| **Cache** | Redis | Caching & session management |
| **API Architecture** | REST + GraphQL + WebSocket | Hybrid approach for optimal performance |

### 1.3 Hybrid API Architecture Strategy

| API Type | Use Case | Features |
|----------|----------|----------|
| **REST** | Admin operations, Authentication, File Upload | Login/Register, Admin Dashboard, CRUD operations, Payment callbacks |
| **GraphQL** | Marketplace browsing, Search, Details | Flexible querying, Filtering, Nested data fetching |
| **WebSocket** | Real-time communication | Chat system, Live notifications, Status updates |

### 1.4 Project Timeline

**Total Duration:** 8-10 weeks

---

## 2. User Stories

### 2.1 Buyer Stories

| ID | Story | Priority |
|----|-------|----------|
| US-BUY-001 | As a buyer, I want to browse game accounts by game type, price range, and rank so that I can find accounts that match my preferences | High |
| US-BUY-002 | As a buyer, I want to view detailed account information including screenshots, seller rating, and transaction history | High |
| US-BUY-003 | As a buyer, I want to chat with sellers in real-time to ask questions before purchasing | High |
| US-BUY-004 | As a buyer, I want to purchase accounts securely using payment integration | High |
| US-BUY-005 | As a buyer, I want to save favorite accounts to my wishlist | Medium |
| US-BUY-006 | As a buyer, I want to leave reviews and ratings for sellers after purchase | Medium |
| US-BUY-007 | As a buyer, I want to receive real-time notifications for transaction updates | High |

### 2.2 Seller Stories

| ID | Story | Priority |
|----|-------|----------|
| US-SEL-001 | As a seller, I want to register and create a seller profile | High |
| US-SEL-002 | As a seller, I want to list game accounts with images, descriptions, and pricing | High |
| US-SEL-003 | As a seller, I want to edit or delete my listed accounts | High |
| US-SEL-004 | As a seller, I want to receive approval notifications for my listings | High |
| US-SEL-005 | As a seller, I want to chat with potential buyers in real-time | High |
| US-SEL-006 | As a seller, I want to view my sales history and earnings | Medium |
| US-SEL-007 | As a seller, I want to maintain a rating based on buyer reviews | Medium |

### 2.3 Admin Stories

| ID | Story | Priority |
|----|-------|----------|
| US-ADM-001 | As an admin, I want to approve or reject account listings before they go live | High |
| US-ADM-002 | As an admin, I want to manage users (ban/unban, change roles) | High |
| US-ADM-003 | As an admin, I want to view platform statistics (revenue, users, transactions) | High |
| US-ADM-004 | As an admin, I want to manage game categories | Medium |
| US-ADM-005 | As an admin, I want to feature/promote selected accounts | Medium |
| US-ADM-006 | As an admin, I want to moderate reviews and disputes | Medium |

---

## 3. Functional Requirements

### 3.1 REST API Endpoints (Admin & Auth)

#### 3.1.1 Authentication (REST)

```
POST   /api/auth/register
POST   /api/auth/login
POST   /api/auth/logout
POST   /api/auth/refresh-token
POST   /api/auth/forgot-password
POST   /api/auth/reset-password
```

**Requirements:**
- JWT-based authentication with access and refresh tokens
- Password hashing using BCrypt
- Role-based access control (BUYER, SELLER, ADMIN)
- Rate limiting on login/register endpoints

#### 3.1.2 User Management (REST)

```
GET    /api/users/profile
PUT    /api/users/profile
PUT    /api/users/password
GET    /api/users/:id
POST   /api/users/avatar          # Multipart file upload
```

#### 3.1.3 Admin Operations (REST)

```
GET    /api/admin/accounts/pending
PUT    /api/admin/accounts/:id/approve
PUT    /api/admin/accounts/:id/reject
PUT    /api/admin/accounts/:id/featured
GET    /api/admin/statistics/dashboard
GET    /api/admin/statistics/revenue
PUT    /api/admin/users/:id/ban
PUT    /api/admin/users/:id/unban
```

#### 3.1.4 Account Listing Management (REST - Seller)

```
POST   /api/accounts              # Create listing (multipart for images)
PUT    /api/accounts/:id          # Update listing
DELETE /api/accounts/:id          # Delete listing
POST   /api/accounts/:id/images   # Upload additional images
```

#### 3.1.5 Transaction & Payment (REST)

```
POST   /api/transactions/purchase
GET    /api/transactions
GET    /api/transactions/:id
PUT    /api/transactions/:id/complete
PUT    /api/transactions/:id/cancel
POST   /api/payment/vnpay-callback # Webhook
POST   /api/payment/momo-callback  # Webhook
```

#### 3.1.6 Reviews (REST)

```
POST   /api/reviews
GET    /api/reviews/user/:userId
PUT    /api/reviews/:id
DELETE /api/reviews/:id
```

#### 3.1.7 Favorites (REST)

```
GET    /api/favorites
POST   /api/favorites
DELETE /api/favorites/:accountId
```

### 3.2 GraphQL API (Marketplace)

#### 3.2.1 GraphQL Schema

```graphql
type User {
  id: ID!
  email: String!
  fullName: String!
  role: Role!
  avatar: String
  balance: Float!
  accounts: [Account!]!
  reviews: [Review!]!
  rating: Float
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

type Review {
  id: ID!
  reviewer: User!
  rating: Int!
  comment: String!
  createdAt: String!
}

type Transaction {
  id: ID!
  account: Account!
  buyer: User!
  seller: User!
  amount: Float!
  status: TransactionStatus!
  createdAt: String!
  completedAt: String
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

enum TransactionStatus {
  PENDING
  COMPLETED
  CANCELLED
}

type AccountConnection {
  edges: [AccountEdge!]!
  pageInfo: PageInfo!
  totalCount: Int!
}

type AccountEdge {
  node: Account!
  cursor: String!
}

type PageInfo {
  hasNextPage: Boolean!
  hasPreviousPage: Boolean!
  startCursor: String
  endCursor: String
}

type AuthPayload {
  token: String!
  user: User!
}
```

#### 3.2.2 Queries

```graphql
type Query {
  # User queries
  me: User
  user(id: ID!): User

  # Account queries (Marketplace)
  accounts(
    gameId: ID
    minPrice: Float
    maxPrice: Float
    minLevel: Int
    maxLevel: Int
    rank: String
    status: AccountStatus
    isFeatured: Boolean
    search: String
    sortBy: String
    page: Int
    limit: Int
  ): AccountConnection!

  account(id: ID!): Account

  # Game queries
  games: [Game!]!
  game(id: ID!): Game
  gameBySlug(slug: String!): Game

  # Transaction queries
  myTransactions: [Transaction!]!
  transaction(id: ID!): Transaction

  # Review queries
  reviews(userId: ID!): [Review!]!
}
```

#### 3.2.3 Mutations

```graphql
type Mutation {
  # Auth mutations
  register(email: String!, password: String!, fullName: String!): AuthPayload!
  login(email: String!, password: String!): AuthPayload!

  # Account mutations (Seller)
  createAccount(input: CreateAccountInput!): Account!
  updateAccount(id: ID!, input: UpdateAccountInput!): Account!
  deleteAccount(id: ID!): Boolean!

  # Transaction mutations
  purchaseAccount(accountId: ID!): Transaction!
  completeTransaction(id: ID!): Transaction!
  cancelTransaction(id: ID!): Transaction!

  # Admin mutations
  approveAccount(id: ID!): Account!
  rejectAccount(id: ID!, reason: String): Account!
  featureAccount(id: ID!): Account!
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

#### 3.2.4 Subscriptions

```graphql
type Subscription {
  accountStatusChanged(accountId: ID!): Account!
  newAccountPosted(gameId: ID): Account!
  transactionUpdated(userId: ID!): Transaction!
}
```

### 3.3 WebSocket Events (Real-time)

#### 3.3.1 Connection

```
Endpoint: ws://localhost:8080/ws
Authentication: JWT token in handshake query
```

#### 3.3.2 Chat System

```javascript
// Client → Server
send_message      { accountId, receiverId, content }
typing            { accountId, receiverId }
stop_typing       { accountId, receiverId }
mark_read         { accountId, senderId }

// Server → Client
new_message       { id, sender, content, timestamp }
user_typing       { userId, accountId }
user_stop_typing  { userId, accountId }
messages_read     { userId, count }
```

#### 3.3.3 Notifications

```javascript
// Server → Client
notification              { id, type, title, message }
account_approved          { accountId }
account_rejected          { accountId, reason }
account_sold              { accountId, price }
new_transaction           { transactionId }
payment_received          { amount }
```

#### 3.3.4 Real-time Updates

```javascript
// Server → Client (Broadcast)
new_account_posted         { account }
account_status_changed     { accountId, status }
account_price_changed      { accountId, oldPrice, newPrice }
user_online_status         { userId, isOnline }
```

### 3.4 Data Models

#### 3.4.1 User Entity

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String fullName;
    private String avatar;
    private Role role;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    private Double balance = 0.0;
    private Double rating = 0.0;
    private Integer totalReviews = 0;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "seller")
    private List<Account> accounts;

    @OneToMany(mappedBy = "buyer")
    private List<Transaction> purchases;

    // Enum definitions
    public enum Role { BUYER, SELLER, ADMIN }
    public enum UserStatus { ACTIVE, BANNED, SUSPENDED }
}
```

#### 3.4.2 Account Entity

```java
@Entity
@Table(name = "accounts")
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
    private Double price;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    private Integer viewsCount = 0;
    private Boolean isFeatured = false;

    @ElementCollection
    @CollectionTable(name = "account_images", joinColumns = @JoinColumn(name = "account_id"))
    @Column(name = "image_url")
    private List<String> images = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum AccountStatus { PENDING, APPROVED, REJECTED, SOLD }
}
```

#### 3.4.3 Transaction Entity

```java
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    // Encrypted credentials
    @Lob
    private String encryptedCredentials;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    public enum TransactionStatus { PENDING, COMPLETED, CANCELLED }
}
```

#### 3.4.4 Message Entity

```java
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private Boolean isRead = false;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
```

---

## 4. Non-Functional Requirements

### 4.1 Performance

| Requirement | Target | Measurement |
|-------------|--------|-------------|
| API Response Time | < 200ms (p95) | REST endpoints |
| GraphQL Query Time | < 300ms (p95) | Complex nested queries |
| WebSocket Latency | < 100ms | Chat message delivery |
| Page Load Time | < 2s | Initial page load |
| Concurrent Users | 10,000+ | Active sessions |
| Requests/sec | 1000+ | Peak load |

**Optimization Strategies:**
- Redis caching for hot data (game lists, featured accounts)
- Database indexing on frequently queried fields
- DataLoader for GraphQL N+1 query prevention
- Pagination for all list endpoints
- Image optimization and lazy loading
- Code splitting (React.lazy)

### 4.2 Security

| Requirement | Implementation |
|-------------|----------------|
| Authentication | JWT with Spring Security |
| Password Storage | BCrypt hashing |
| Authorization | Role-based access control (RBAC) |
| API Protection | Rate limiting, CORS headers |
| Data Encryption | AES-256 for account credentials |
| SQL Injection | Prepared statements (JPA/Hibernate) |
| XSS Protection | Input validation, sanitization |
| CSRF Protection | Tokens for state-changing operations |
| WebSocket Security | WSS, JWT handshake auth |
| File Upload | Type validation, size limits, virus scanning |

### 4.3 Scalability

| Component | Strategy |
|-----------|----------|
| Application Server | Horizontal scaling with load balancer |
| Database | Master-slave replication, connection pooling |
| Cache | Redis cluster for distributed caching |
| WebSocket | Redis pub/sub for multi-server support |
| Static Assets | CDN delivery |
| Session | Stateless JWT design |

### 4.4 Availability

| Requirement | Target |
|-------------|--------|
| Uptime | 99.5% (monthly) |
| Response Time | < 1s (p99) during normal load |
| Failover | < 5 min recovery time |

### 4.5 Maintainability

| Requirement | Implementation |
|-------------|----------------|
| Code Structure | N-Layer architecture (Controller → Service → Repository) |
| Documentation | Swagger/OpenAPI for REST, GraphQL docs |
| Logging | Structured logging (SLF4J + Logback) |
| Error Handling | Global exception handler |
| Monitoring | Actuator metrics, custom dashboards |
| Testing | Unit tests (80%+ coverage), integration tests |

### 4.6 Usability

| Requirement | Target |
|-------------|--------|
| Mobile Support | Responsive design (mobile-first) |
| Browser Support | Chrome, Firefox, Safari, Edge (latest 2 versions) |
| Accessibility | WCAG 2.1 AA compliance |
| Language | Vietnamese (primary), English (secondary) |

### 4.7 Compatibility

| Requirement | Specification |
|-------------|---------------|
| Java Version | Java 17 or 21 (LTS) |
| Spring Boot | 3.x |
| Database | MySQL 8.0+ |
| Browser | Modern browsers with ES6+ support |
| Mobile Devices | iOS 13+, Android 10+ |

### 4.8 Reliability

| Requirement | Target |
|-------------|--------|
| Data Consistency | ACID transactions |
| Backup | Daily automated backups |
| Error Recovery | Graceful degradation |
| Message Delivery | At-least-once guarantee for WebSocket |

---

## 5. System Architecture

### 5.1 N-Layer Architecture (Backend)

```
┌─────────────────────────────────────────────────────┐
│                   Presentation Layer                 │
│  ┌──────────────┐  ┌──────────────┐  ┌────────────┐ │
│  │ REST Controller│  │ GraphQL    │  │ WebSocket │ │
│  │              │  │ Resolvers   │  │ Handler    │ │
│  └──────────────┘  └──────────────┘  └────────────┘ │
└─────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────┐
│                   Business Logic Layer               │
│  ┌──────────────────────────────────────────────┐  │
│  │              Service Layer                    │  │
│  │  AuthService │ AccountService │ ChatService  │  │
│  └──────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────┐
│                   Data Access Layer                  │
│  ┌──────────────────────────────────────────────┐  │
│  │          Repository Layer (JPA)              │  │
│  │  UserRepository │ AccountRepository │ etc.   │  │
│  └──────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────┐
│                   Database Layer                     │
│       MySQL + Redis (Cache/Session)                  │
└─────────────────────────────────────────────────────┘
```

### 5.2 Client-Server Architecture

```
┌──────────────────┐         ┌──────────────────┐
│   Frontend       │         │   Backend        │
│   (React)        │◄────────┤   (Spring Boot)  │
│   Port: 3000     │  HTTP   │   Port: 8080     │
└──────────────────┘         └──────────────────┘
                                      │
                    ┌─────────────────┼─────────────────┐
                    ↓                 ↓                 ↓
            ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
            │   MySQL      │  │   Redis      │  │  External    │
            │   Port: 3306 │  │   Port: 6379 │  │  APIs        │
            └──────────────┘  └──────────────┘  └──────────────┘
```

---

## 6. Acceptance Criteria

### 6.1 User Acceptance Criteria

| Feature | Criteria |
|---------|----------|
| Registration | User can register with email/password and receive confirmation |
| Login | User can login and receive valid JWT token |
| Browse Accounts | User can filter accounts by game, price, rank with < 300ms response |
| View Details | User can see account details with seller info in single GraphQL query |
| Purchase | User can complete purchase with secure transaction |
| Chat | Messages deliver in < 100ms with typing indicators |
| Notifications | User receives real-time notifications for relevant events |

### 6.2 Admin Acceptance Criteria

| Feature | Criteria |
|---------|----------|
| Dashboard | Admin can view statistics with charts and metrics |
| Account Approval | Admin can approve/reject accounts with one click |
| User Management | Admin can ban/unban users with immediate effect |
| Reports | Admin can export reports in CSV/PDF format |

---

## 7. Constraints & Assumptions

### 7.1 Constraints

- **Timeline:** 8-10 weeks total development time
- **Team:** 1 lead developer + 4 junior developers
- **Budget:** Cloud hosting costs within $50/month initially
- **Payment Gateway:** VNPay and Momo integration required

### 7.2 Assumptions

- Users have modern browsers with JavaScript enabled
- Payment gateway APIs are stable and documented
- Network latency is acceptable (< 100ms within region)
- Legal compliance for digital goods trading is permissible

---

## 8. Risks & Mitigation

| Risk | Impact | Probability | Mitigation |
|------|--------|-------------|------------|
| GraphQL N+1 queries | High | Medium | Use DataLoader, query complexity analysis |
| WebSocket scaling | High | Medium | Redis adapter for multi-server support |
| Database performance | High | Low | Proper indexing, Redis caching |
| Team coordination | Medium | High | Clear documentation, code reviews |
| Payment gateway issues | High | Low | Fallback options, error handling |
| Security breaches | Critical | Low | Regular security audits, penetration testing |

---

## 9. Success Metrics

| Metric | Target | Timeline |
|--------|--------|----------|
| Registered Users | 1,000+ | 3 months post-launch |
| Active Listings | 500+ | 3 months post-launch |
| Successful Transactions | 200+ | 3 months post-launch |
| Platform Uptime | 99.5% | Monthly |
| Average Response Time | < 300ms | Monthly |
| User Satisfaction | 4.0+ / 5.0 | Quarterly survey |

---

## 10. Appendix

### 10.1 Maven Dependencies (Key)

```xml
<properties>
    <java.version>17</java.version>
    <spring-boot.version>3.2.0</spring-boot.version>
</properties>

<dependencies>
    <!-- Spring Boot -->
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
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>

    <!-- GraphQL -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-graphql</artifactId>
    </dependency>

    <!-- Database -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
    </dependency>

    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.3</version>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

### 10.2 Frontend Dependencies (Key)

```json
{
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-router-dom": "^6.15.0",
    "@apollo/client": "^3.8.3",
    "graphql": "^16.8.0",
    "socket.io-client": "^4.7.2",
    "axios": "^1.5.0",
    "tailwindcss": "^3.3.3",
    "formik": "^2.4.3",
    "yup": "^1.2.0",
    "recharts": "^2.8.0"
  }
}
```

---

**Document Version:** 1.0
**Last Updated:** 2026-01-06
**Author:** Admin
**Status:** Draft
