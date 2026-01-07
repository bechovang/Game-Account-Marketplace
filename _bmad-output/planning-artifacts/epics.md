---
stepsCompleted: ["step-01-validate-prerequisites", "step-02-design-epics", "step-03-create-stories", "step-04-final-validation"]
inputDocuments: ["project_docs/PRD.md", "project_docs/ARCHITECTURE.md"]
validationPassed: true
---

# Game Account Marketplace - Epic Breakdown

## Overview

This document provides the complete epic and story breakdown for Game Account Marketplace, decomposing the requirements from the PRD, UX Design if it exists, and Architecture requirements into implementable stories.

## Requirements Inventory

### Functional Requirements

**FR1:** Users must be able to register with email, password, and full name
**FR2:** Users must be able to login with email/password and receive JWT access tokens
**FR3:** Users must be able to logout and invalidate JWT tokens
**FR4:** Users must be able to refresh JWT tokens without re-authentication
**FR5:** Users must be able to reset password via forgot password flow
**FR6:** Users must be able to view their profile information
**FR7:** Users must be able to update their profile (full name, avatar)
**FR8:** Users must be able to change their password
**FR9:** Users must be able to upload avatar images via multipart file upload
**FR10:** Sellers must be able to create game account listings with title, description, level, rank, price, and images
**FR11:** Sellers must be able to edit their account listings
**FR12:** Sellers must be able to delete their account listings
**FR13:** Sellers must be able to upload additional images to existing listings
**FR14:** Buyers must be able to browse game accounts with filters (game type, price range, rank)
**FR15:** Buyers must be able to view detailed account information including screenshots, seller rating, and transaction history
**FR16:** Buyers must be able to search accounts by text search
**FR17:** Buyers must be able to sort accounts by price, level, date posted
**FR18:** Buyers must be able to add accounts to favorites/wishlist
**FR19:** Buyers must be able to remove accounts from favorites
**FR20:** Buyers must be able to view their favorites list
**FR21:** Buyers must be able to purchase accounts securely
**FR22:** Buyers must be able to view their transaction history
**FR23:** Buyers must be able to complete a transaction
**FR24:** Buyers must be able to cancel a pending transaction
**FR25:** Users must be able to send real-time chat messages to other users
**FR26:** Users must be able to receive real-time chat messages
**FR27:** Users must be able to see typing indicators in chat
**FR28:** Users must be able to mark messages as read
**FR29:** Users must be able to receive real-time notifications (account approved, rejected, sold, new transaction)
**FR30:** Users must be able to receive real-time broadcast updates (new account posted, status changed)
**FR31:** Users must be able to leave reviews and ratings for sellers after purchase
**FR32:** Users must be able to update their reviews
**FR33:** Users must be able to delete their reviews
**FR34:** Users must be able to view reviews for a specific user
**FR35:** Admins must be able to view pending account listings
**FR36:** Admins must be able to approve account listings
**FR37:** Admins must be able to reject account listings with reason
**FR38:** Admins must be able to feature/promote selected accounts
**FR39:** Admins must be able to ban users
**FR40:** Admins must be able to unban users
**FR41:** Admins must be able to view platform statistics (revenue, users, transactions)
**FR42:** Admins must be able to view revenue reports
**FR43:** Admins must be able to export reports in CSV/PDF format
**FR44:** Payment webhooks (VNPay, Momo) must be able to receive callbacks
**FR45:** Payment callbacks must update transaction status
**FR46:** GraphQL API must support flexible account querying with nested data
**FR47:** GraphQL API must support cursor-based pagination
**FR48:** WebSocket connection must authenticate via JWT token
**FR49:** WebSocket must maintain persistent connections for real-time updates

### NonFunctional Requirements

**NFR1:** API Response Time must be < 200ms (p95) for REST endpoints
**NFR2:** GraphQL Query Time must be < 300ms (p95) for complex nested queries
**NFR3:** WebSocket Latency must be < 100ms for chat message delivery
**NFR4:** Page Load Time must be < 2s for initial page load
**NFR5:** System must support 10,000+ concurrent users
**NFR6:** System must handle 1000+ requests per second at peak load
**NFR7:** Authentication must use JWT with Spring Security
**NFR8:** Passwords must be hashed using BCrypt
**NFR9:** Authorization must use Role-based access control (BUYER, SELLER, ADMIN)
**NFR10:** API must have rate limiting on login/register endpoints
**NFR11:** API must have CORS headers configured for frontend
**NFR12:** Account credentials must be encrypted using AES-256
**NFR13:** Database must use prepared statements (JPA/Hibernate) to prevent SQL injection
**NFR14:** Application must have input validation and sanitization to prevent XSS
**NFR15:** State-changing operations must use CSRF tokens
**NFR16:** WebSocket must use WSS with JWT handshake authentication
**NFR17:** File uploads must validate file types and enforce size limits
**NFR18:** Application must support horizontal scaling with load balancer
**NFR19:** Database must support master-slave replication
**NFR20:** Cache must use Redis for distributed caching
**NFR21:** WebSocket must use Redis pub/sub for multi-server support
**NFR22:** Static assets must use CDN delivery
**NFR23:** Session must be stateless using JWT design
**NFR24:** Uptime must be 99.5% monthly
**NFR25:** Response time must be < 1s (p99) during normal load
**NFR26:** Failover time must be < 5 minutes
**NFR27:** Code must follow N-Layer architecture (Controller → Service → Repository)
**NFR28:** API must have Swagger/OpenAPI documentation for REST
**NFR29:** Application must have structured logging (SLF4J + Logback)
**NFR30:** Application must have global exception handler
**NFR31:** Application must have Actuator metrics and custom dashboards
**NFR32:** Unit tests must achieve 80%+ coverage
**NFR33:** Frontend must be responsive with mobile-first design
**NFR34:** Application must support Chrome, Firefox, Safari, Edge (latest 2 versions)
**NFR35:** Application must meet WCAG 2.1 AA accessibility compliance
**NFR36:** Primary language must be Vietnamese, secondary English
**NFR37:** Backend must use Java 17 or 21 (LTS)
**NFR38:** Backend must use Spring Boot 3.x
**NFR39:** Database must be MySQL 8.0+
**NFR40:** Redis must be version 7.0+
**NFR41:** Data consistency must use ACID transactions
**NFR42:** System must have daily automated backups
**NFR43:** System must support graceful degradation on errors
**NFR44:** WebSocket message delivery must have at-least-once guarantee
**NFR45:** Hot data (game lists, featured accounts) must be cached in Redis
**NFR46:** Frequently queried database fields must be indexed
**NFR47:** GraphQL must use DataLoader to prevent N+1 queries
**NFR48:** All list endpoints must use pagination
**NFR49:** Images must be optimized and lazy loaded
**NFR50:** Frontend must use code splitting (React.lazy)

### Additional Requirements

**Technical Requirements from Architecture:**
- Project must use monorepo structure with backend-java/ and frontend-react/ directories
- Backend must use Maven 3.9+ as build tool
- Frontend must use Vite 5.x as build tool
- Frontend must use TypeScript 5.x
- Frontend must use Tailwind CSS 3.x for styling
- Frontend must use Apollo Client 3.x for GraphQL
- Frontend must use Axios 1.x for REST API calls
- Frontend must use SockJS + STOMP for WebSocket
- Backend must use Lombok for reducing boilerplate code
- Backend must use MapStruct 1.5.5.Final for DTO mapping
- Backend must use jjwt 0.12.3 for JWT token generation/validation
- Backend must use Spring Data JPA for database operations
- Backend must use Hibernate ddl-auto: update for schema generation
- Backend must implement N-Layer architecture (Controller → Service → Repository)
- Service layer must be shared between REST and GraphQL (DRY principle)
- All entities must use JPA annotations with proper relationships
- All repositories must extend JpaRepository<Entity, Long>
- All controllers must use @RestController or @SchemaMapping annotations
- All services must use @Service annotation
- Security must use Spring Security filter chain
- JWT authentication filter must extract token from Authorization header
- GraphQL must use schema.graphqls file for type definitions
- WebSocket must use STOMP protocol with /app and /topic prefixes
- Docker Compose must include MySQL 8.0 and Redis 7.0 services
- Application must run on port 8080 (backend) and 3000 (frontend)
- CORS must be configured for http://localhost:3000

**Infrastructure Requirements:**
- Development environment must use Docker Compose for MySQL and Redis
- MySQL service must expose port 3306
- Redis service must expose port 6379
- MySQL must have volume persistence for data
- Redis must have volume persistence for cache data
- Health checks must be configured for both services

**Integration Requirements:**
- Payment gateway integration with VNPay
- Payment gateway integration with Momo
- Webhook endpoints must handle payment callbacks
- Webhook handlers must validate payment signatures

**Data Requirements:**
- Users table with fields: id, email, password, full_name, avatar, role, status, balance, rating, total_reviews, created_at, updated_at
- Games table with fields: id, name, slug, description, icon_url, account_count
- Accounts table with fields: id, seller_id, game_id, title, description, level, rank, price, status, views_count, is_featured, created_at, updated_at
- Account images must be stored in separate account_images table
- Transactions table with fields: id, account_id, buyer_id, seller_id, amount, status, encrypted_credentials, created_at, completed_at
- Messages table with fields: id, account_id, sender_id, receiver_id, content, is_read, created_at
- Reviews table with fields: id, reviewer_id, target_user_id, rating, comment, created_at

**Monitoring Requirements:**
- Application must expose Spring Boot Actuator metrics
- Custom dashboards must be configured for monitoring
- Structured logging must be implemented

**No UX Design document found** - UI/UX requirements will be derived from PRD user stories and common marketplace patterns.

### FR Coverage Map

```
FR1: Epic 1 - User registration
FR2: Epic 1 - User login with JWT
FR3: Epic 1 - User logout
FR4: Epic 1 - Token refresh
FR5: Epic 1 - Password reset
FR6: Epic 1 - View profile
FR7: Epic 1 - Update profile
FR8: Epic 1 - Change password
FR9: Epic 1 - Upload avatar
FR10: Epic 2 - Create account listing
FR11: Epic 2 - Edit account listing
FR12: Epic 2 - Delete account listing
FR13: Epic 2 - Upload additional images
FR14: Epic 3 - Browse accounts with filters
FR15: Epic 3 - View account details
FR16: Epic 3 - Search accounts
FR17: Epic 3 - Sort accounts
FR18: Epic 3 - Add to favorites
FR19: Epic 3 - Remove from favorites
FR20: Epic 3 - View favorites
FR21: Epic 4 - Purchase accounts
FR22: Epic 4 - View transaction history
FR23: Epic 4 - Complete transaction
FR24: Epic 4 - Cancel transaction
FR25: Epic 5 - Send chat messages
FR26: Epic 5 - Receive chat messages
FR27: Epic 5 - Typing indicators
FR28: Epic 5 - Mark messages as read
FR29: Epic 5 - Real-time notifications
FR30: Epic 5 - Broadcast updates
FR31: Epic 6 - Leave reviews
FR32: Epic 6 - Update reviews
FR33: Epic 6 - Delete reviews
FR34: Epic 6 - View reviews
FR35: Epic 7 - View pending listings
FR36: Epic 7 - Approve listings
FR37: Epic 7 - Reject listings
FR38: Epic 7 - Feature accounts
FR39: Epic 7 - Ban users
FR40: Epic 7 - Unban users
FR41: Epic 7 - View platform statistics
FR42: Epic 7 - View revenue reports
FR43: Epic 7 - Export reports
FR44: Epic 4 - Payment webhooks
FR45: Epic 4 - Payment callbacks
FR46: Epic 3 - GraphQL flexible querying
FR47: Epic 3 - Cursor-based pagination
FR48: Epic 5 - WebSocket JWT authentication
FR49: Epic 5 - Persistent connections
```

## Epic List

### Epic 1: User Authentication & Identity

**Goal:** Users can register, login, and manage their profiles on the platform

**FRs covered:** FR1-FR9

**User Value:** Users need to establish their identity before they can participate in any marketplace activities. This epic provides the foundational trust layer - registration, authentication, and profile management.

**Standalone:** Complete authentication system - users can register, login, logout, manage profiles independently.

---

### Epic 2: Account Listing Management

**Goal:** Sellers can create, edit, and manage their game account listings

**FRs covered:** FR10-FR13

**User Value:** Sellers need to showcase their game accounts to potential buyers. This epic enables the core seller workflow - creating inventory with images, descriptions, and pricing.

**Dependencies:** Uses Epic 1 (sellers must be authenticated)
**Standalone:** Complete CRUD operations for account listings - independent marketplace presence.

---

### Epic 3: Marketplace Discovery

**Goal:** Buyers can browse, search, filter, and view account listings

**FRs covered:** FR14-FR20, FR46-FR47

**User Value:** Buyers need to find accounts that match their criteria. This epic enables the core discovery workflow - searching, filtering, sorting, and viewing details with GraphQL.

**Dependencies:** Uses Epic 1 (buyers must be authenticated), Epic 2 (needs listings to discover)
**Standalone:** Complete browsing experience - users can explore the marketplace independently.

---

### Epic 4: Secure Transactions

**Goal:** Users can buy accounts, manage transactions, and integrate payments

**FRs covered:** FR21-FR24, FR44-FR45

**User Value:** Buyers and sellers need to exchange value securely. This epic enables the core transaction workflow - purchasing, payment callbacks, and transaction lifecycle management.

**Dependencies:** Uses Epic 1 (users authenticated), Epic 2 (accounts to buy), Epic 3 (discovery leads to purchase)
**Standalone:** Complete transaction system - end-to-end buying flow with payment integration.

---

### Epic 5: Real-time Communication

**Goal:** Users can chat and receive live notifications

**FRs covered:** FR25-FR30, FR48-FR49

**User Value:** Users need to communicate in real-time before and after transactions. This epic enables live chat, typing indicators, notifications, and broadcast updates.

**Dependencies:** Uses Epic 1 (authenticated users)
**Standalone:** Complete real-time messaging system - independent communication channel with WebSocket.

---

### Epic 6: Reviews & Reputation

**Goal:** Users can rate and review sellers after transactions

**FRs covered:** FR31-FR34

**User Value:** Users need to establish trust in the marketplace through reputation. This epic enables the review workflow - rating, commenting, and viewing seller history.

**Dependencies:** Uses Epic 1 (authenticated users), Epic 4 (requires completed transactions)
**Standalone:** Complete review system - users can rate and view reviews independently.

---

### Epic 7: Platform Administration

**Goal:** Admins can manage users, listings, and platform statistics

**FRs covered:** FR35-FR43

**User Value:** Platform operators need to maintain marketplace integrity. This epic enables administrative workflows - moderation, approvals, analytics, and reporting.

**Dependencies:** Uses Epic 1 (admin authentication), Epic 2 (listings to moderate)
**Standalone:** Complete admin dashboard - full platform management capabilities.

---

## Epic Summary

| Epic | Stories (est.) | User Value | Dependencies |
|------|----------------|------------|--------------|
| **E1: Authentication** | ~8 | Identity & access | None |
| **E2: Listing Management** | ~6 | Seller inventory | E1 |
| **E3: Discovery** | ~10 | Browse & search | E1, E2 |
| **E4: Transactions** | ~8 | Buy & sell | E1, E2, E3 |
| **E5: Real-time** | ~8 | Chat & notifications | E1 |
| **E6: Reviews** | ~6 | Reputation | E1, E4 |
| **E7: Administration** | ~10 | Platform management | E1, E2 |

**Total: ~56 user stories across 7 epics**

---

## Epic 1: User Authentication & Identity

**Goal:** Users can register, login, and manage their profiles on the platform

**FRs covered:** FR1-FR9

**NFRs covered:** NFR7-NFR11, NFR27, NFR37-NFR40

**User Value:** Users need to establish their identity before they can participate in any marketplace activities. This epic provides the foundational trust layer - registration, authentication, and profile management.

**Standalone:** Complete authentication system - users can register, login, logout, manage profiles independently.

---

### Story 1.1: Project Structure & Environment Setup

As a developer,
I want to initialize the project structure with monorepo layout and Docker environment,
So that we have a solid foundation for both backend and frontend development.

**Acceptance Criteria:**

**Given** a new project directory
**When** I initialize the Git repository and create the monorepo structure
**Then** the root directory contains `backend-java/` and `frontend-react/` subdirectories
**And** `.gitignore` includes `node_modules/`, `target/`, `.idea/`, `*.log`, `.env`
**And** `docker-compose.yml` includes MySQL 8.0 and Redis 7.0 services
**And** MySQL service exposes port 3306 with volume persistence
**And** Redis service exposes port 6379 with volume persistence
**And** health checks are configured for both services

**Technical Notes:**
- Refer to Architecture document section 2.1 for monorepo structure
- Docker Compose must match infrastructure requirements from additional requirements
- No authentication needed yet - this is purely infrastructure setup

**Requirements:** NFR39, NFR40 (MySQL, Redis versions)

---

### Story 1.2: Backend Spring Boot Skeleton

As a developer,
I want to create a Spring Boot 3.x project with Maven and all required dependencies,
So that the backend has the proper foundation for N-Layer architecture.

**Acceptance Criteria:**

**Given** the project structure from Story 1.1
**When** I initialize the Spring Boot project with Maven
**Then** `pom.xml` includes Spring Boot 3.2.x parent
**And** dependencies include: spring-boot-starter-web, spring-boot-starter-data-jpa, spring-boot-starter-security, spring-boot-starter-websocket, spring-boot-starter-graphql, spring-boot-starter-validation, spring-boot-starter-data-redis
**And** dependencies include: mysql-connector-j, jjwt-api/impl/jackson 0.12.3
**And** dependencies include: Lombok, MapStruct 1.5.5.Final
**And** `application.yml` is configured with datasource (MySQL), JPA (ddl-auto: update), Redis, server port 8080
**And** `MarketplaceApplication.java` main class is created with `@SpringBootApplication`
**And** project builds successfully with `mvn clean install`

**Technical Notes:**
- Java version 21 (LTS)
- Follow Architecture document section 3.2 for pom.xml template
- Create folder structure: config/, controller/, service/, repository/, entity/, dto/
- No authentication logic yet - just skeleton configuration

**Requirements:** NFR37 (Java 21), NFR38 (Spring Boot 3.x), NFR27 (N-Layer)

---

### Story 1.3: Frontend Vite + React + TypeScript Setup

As a developer,
I want to initialize a React frontend with Vite, TypeScript, and Tailwind CSS,
So that we have a modern, type-safe frontend foundation.

**Acceptance Criteria:**

**Given** the monorepo structure from Story 1.1
**When** I initialize the Vite + React + TypeScript project
**Then** `package.json` includes React 18.x, React Router DOM 6.x, TypeScript 5.x
**And** dependencies include: @apollo/client 3.x, graphql 16.x, axios 1.x, sockjs-client, @stomp/stompjs
**And** dev dependencies include: Vite 5.x, Tailwind CSS 3.x, autoprefixer, postcss
**And** `vite.config.ts` has proxy setup for backend (port 8080)
**And** `tsconfig.json` is configured for strict mode
**And** `tailwind.config.js` is configured with content paths
**And** project runs successfully with `npm run dev` on port 3000

**Technical Notes:**
- Follow Architecture document section 4.2.1 for package.json template
- Create folder structure: src/components/, src/pages/, src/services/, src/hooks/, src/contexts/, src/types/
- No UI or API integration yet - just build setup

**Requirements:** NFR33 (Responsive), NFR50 (Code splitting)

---

### Story 1.4: User Entity & Repository

As a developer,
I want to create the User JPA entity with UserRepository,
So that user data can be persisted and retrieved from the database.

**Acceptance Criteria:**

**Given** the Spring Boot project from Story 1.2
**When** I create the User entity and UserRepository
**Then** User entity has fields: id (Long, PK, auto-increment), email (unique, not null), password (not null), fullName, avatar, role (enum: BUYER, SELLER, ADMIN), status (enum: ACTIVE, BANNED, SUSPENDED), balance, rating, totalReviews, createdAt, updatedAt
**And** User entity has JPA annotations: @Entity, @Table, @Id, @GeneratedValue, @Column, @Enumerated, @OneToMany for accounts and purchases
**And** User entity uses Lombok: @Getter, @Setter, @Builder, @NoArgsConstructor, @AllArgsConstructor
**And** UserRepository extends JpaRepository<User, Long>
**And** UserRepository has methods: findByEmail(), existsByEmail(), findByRole(), findByRole(Pageable)
**And** application starts successfully and MySQL `users` table is created via Hibernate ddl-auto

**Technical Notes:**
- Follow PRD section 3.4.1 for User entity structure
- Password field length must support BCrypt hashes (60+ chars)
- Role and Status enums at entity level
- createdAt uses @CreatedDate, updatedAt uses @LastModifiedDate
- No authentication logic yet - just data model

**Requirements:** FR1 (registration data), FR6 (view profile), NFR41 (ACID)

---

### Story 1.5: Security Configuration & JWT Implementation

As a developer,
I want to configure Spring Security and implement JWT token generation/validation,
So that the API can authenticate and authorize users securely.

**Acceptance Criteria:**

**Given** the User entity from Story 1.4
**When** I implement security configuration and JWT
**Then** SecurityConfig disables CSRF and enables CORS for frontend URL
**And** SecurityConfig sets session management to stateless (SessionCreationPolicy.STATELESS)
**And** SecurityConfig permits public access to `/api/auth/**`, `/graphql`, `/ws/**`
**And** SecurityConfig requires authentication for `/api/admin/**` (ROLE_ADMIN)
**And** JwtTokenProvider generates JWT tokens with HS256 algorithm
**And** JwtTokenProvider validates tokens and extracts email from claims
**And** JwtTokenProvider has configurable secret and expiration time
**And** JwtAuthenticationFilter extends OncePerRequestFilter
**And** JwtAuthenticationFilter extracts token from `Authorization: Bearer {token}` header
**And** JwtAuthenticationFilter validates token and sets SecurityContext authentication
**And** Password encoder bean is configured with BCrypt
**And** UserDetailsService loads User entity from UserRepository

**Technical Notes:**
- JWT secret from application.yml property
- Token expiration: 24 hours (86400000 ms)
- Follow Architecture document section 6.2 for SecurityConfig template
- No controller/service yet - just security infrastructure

**Requirements:** FR2 (login with JWT), FR3 (logout), FR4 (refresh token), NFR7 (JWT), NFR8 (BCrypt), NFR9 (RBAC)

---

### Story 1.6: AuthService & Authentication Logic

As a developer,
I want to implement the AuthService with register and login business logic,
So that users can be authenticated and stored in the database.

**Acceptance Criteria:**

**Given** the UserRepository from Story 1.4 and JWT from Story 1.5
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

**Technical Notes:**
- Follow Architecture document section 3.4 for Service template
- Service layer is shared between REST and GraphQL
- No controller yet - just business logic

**Requirements:** FR1 (register), FR2 (login), FR6 (view profile), FR7 (update profile)

---

### Story 1.7: Authentication REST API Endpoints

As a developer,
I want to create REST API endpoints for authentication (register, login, profile),
So that frontend can authenticate users via HTTP requests.

**Acceptance Criteria:**

**Given** the AuthService from Story 1.6
**When** I create AuthController
**Then** AuthController is annotated with @RestController and @RequestMapping("/api/auth")
**And** AuthController has @CrossOrigin annotation for frontend URL
**And** POST /api/auth/register accepts RegisterRequest (email, password, fullName)
**And** POST /api/auth/register returns AuthResponse (token, userId, email, role) with HTTP 201
**And** POST /api/auth/register validates input with @Valid and returns 400 for validation errors
**And** POST /api/auth/login accepts LoginRequest (email, password)
**And** POST /api/auth/login returns AuthResponse with HTTP 200 on success
**And** POST /api/auth/login returns 401 for invalid credentials
**And** GET /api/auth/me returns UserResponse (authenticated user) with HTTP 200
**And** GET /api/auth/me returns 401 if no valid JWT token provided
**And** PUT /api/users/profile accepts UpdateProfileRequest and updates user
**And** PUT /api/users/password accepts ChangePasswordRequest and updates password with BCrypt

**Technical Notes:**
- Request/Response DTOs in dto/request/ and dto/response/ packages
- Use MapStruct for DTO mapping if needed
- Follow Architecture document section 3.4 for controller template
- No frontend integration yet - just REST endpoints

**Requirements:** FR1 (register endpoint), FR2 (login endpoint), FR6 (view profile), FR7 (update profile), FR8 (change password), NFR1 (< 200ms response)

---

### Story 1.8: Frontend Authentication Pages & Context

As a developer,
I want to create login/register pages with AuthContext and protected routes,
So that users can authenticate and access protected pages.

**Acceptance Criteria:**

**Given** the REST endpoints from Story 1.7
**When** I implement frontend authentication
**Then** AuthContext provides user, token, login, logout, isAuthenticated, isLoading state
**Then** AuthContext stores JWT token in localStorage under 'access_token' key
**And** login() function calls POST /api/auth/login with email/password
**And** login() function stores token and user data on success
**And** login() function redirects to home page on success
**And** login() function displays error message on failure
**And** logout() function removes token and redirects to login page
**And** useAuth hook provides access to AuthContext
**And** LoginPage component has email and password input fields
**And** LoginPage validates email format and password length (min 6 chars)
**And** LoginPage displays loading state during API call
**And** RegisterPage component has email, password, fullName fields
**And** RegisterPage validates all inputs and shows validation errors
**And** ProtectedRoute component checks isAuthenticated before rendering children
**And** ProtectedRoute redirects to /login if not authenticated
**And** React Router is configured with routes: /login, /register, / (protected)
**And** App wraps children with ApolloProvider and AuthProvider
**And** Axios interceptor attaches JWT token to all requests

**Technical Notes:**
- Use react-hook-form for form validation
- Use Axios for REST API calls
- Follow Architecture document section 3.6-3.8 for frontend templates
- Tailwind CSS for styling

**Requirements:** FR1 (register UI), FR2 (login UI), FR3 (logout), NFR4 (< 2s page load), NFR33 (responsive)

---

## Epic 2: Account Listing Management

**Goal:** Sellers can create, edit, and manage their game account listings

**FRs covered:** FR10-FR13

**NFRs covered:** NFR1, NFR45 (Redis caching), NFR46 (indexing), NFR48 (pagination)

**User Value:** Sellers need to showcase their game accounts to potential buyers. This epic enables the core seller workflow - creating inventory with images, descriptions, and pricing.

**Dependencies:** Uses Epic 1 (sellers must be authenticated)
**Standalone:** Complete CRUD operations for account listings - independent marketplace presence.

---

### Story 2.1: Game & Account Entities with Repositories

As a developer,
I want to create Game and Account JPA entities with repositories,
So that account listings can be stored and associated with games and sellers.

**Acceptance Criteria:**

**Given** the User entity from Epic 1
**When** I create Game and Account entities
**Then** Game entity has fields: id (Long, PK), name, slug (unique), description, iconUrl, accountCount, createdAt
**And** Game entity has @OneToMany relationship to Account
**And** Game entity uses Lombok annotations and JPA annotations
**And** GameRepository extends JpaRepository<Game, Long>
**And** GameRepository has methods: findByName(), findBySlug(), findAll()
**And** Account entity has fields: id (Long, PK), seller (ManyToOne User, lazy), game (ManyToOne Game, eager), title, description, level, rank, price, status (enum: PENDING, APPROVED, REJECTED, SOLD), viewsCount, isFeatured, createdAt, updatedAt
**And** Account entity has @ElementCollection for images list (stored in account_images table)
**And** Account entity uses Lombok and JPA annotations
**And** AccountRepository extends JpaRepository<Account, Long>
**And** AccountRepository has methods: findBySellerId(), findByGameId(), findByStatus(), findPendingAccounts()
**And** application starts successfully and MySQL creates games, accounts, account_images tables
**And** foreign key constraints are properly set up (seller_id, game_id)

**Technical Notes:**
- Follow PRD section 3.4.2 for Account entity structure
- Use @ElementCollection for images to support multiple images per account
- No business logic yet - just data model
- Slug field should be unique for SEO-friendly URLs

**Requirements:** FR10 (listing data), NFR41 (ACID), NFR46 (indexing on game_id, seller_id, status)

---

### Story 2.2: AccountService Business Logic

As a developer,
I want to implement AccountService with CRUD and approval logic,
So that sellers can manage their listings and admins can approve them.

**Acceptance Criteria:**

**Given** the repositories from Story 2.1
**When** I implement AccountService
**Then** AccountService is annotated with @Service and @RequiredArgsConstructor
**And** createAccount() method validates seller exists via UserRepository
**And** createAccount() method validates game exists via GameRepository
**And** createAccount() method sets status to PENDING
**And** createAccount() method sets seller and game from repositories
**And** createAccount() method is annotated with @Transactional
**And** updateAccount() method verifies account belongs to authenticated seller
**And** updateAccount() method updates allowed fields only
**And** deleteAccount() method verifies ownership or admin role
**And** deleteAccount() method is annotated with @Transactional
**And** approveAccount() method changes status to APPROVED
**And** approveAccount() method is restricted to ADMIN role via @PreAuthorize
**And** rejectAccount() method changes status to REJECTED with reason
**And** searchAccounts() method supports filtering by gameId, minPrice, maxPrice, status
**And** searchAccounts() method is annotated with @Cacheable for Redis caching
**And** getAccountById() method increments viewsCount
**And** exceptions include: ResourceNotFoundException, BusinessException (not owner, invalid status transition)

**Technical Notes:**
- Follow Architecture document section 4.1 for Service template
- Shared between REST and GraphQL (DRY principle)
- Use @Cacheable with TTL of 10 minutes for hot data
- No controller yet - just business logic

**Requirements:** FR10 (create), FR11 (edit), FR12 (delete), FR35-FR37 (approve/reject), NFR45 (Redis cache)

---

### Story 2.3: GraphQL Schema & Resolvers for Accounts

As a developer,
I want to create GraphQL schema and resolvers for account queries and mutations,
So that frontend can perform flexible account operations via GraphQL.

**Acceptance Criteria:**

**Given** the AccountService from Story 2.2
**When** I create GraphQL schema and resolvers
**Then** schema.graphqls defines types: Account, Game, User (as output types)
**And** schema.graphqls defines enums: AccountStatus, Role
**And** schema.graphqls defines inputs: CreateAccountInput, UpdateAccountInput
**And** schema.graphqls defines Query: accounts(gameId, minPrice, maxPrice, status, page, limit), account(id), games
**And** schema.graphqls defines Mutation: createAccount(input), updateAccount(id, input), deleteAccount(id), approveAccount(id), rejectAccount(id, reason)
**And** AccountQuery implements GraphQLQueryResolver
**And** AccountQuery.accounts() delegates to AccountService.searchAccounts()
**And** AccountQuery.account() delegates to AccountService.getAccountById()
**And** AccountMutation implements GraphQLMutationResolver
**And** AccountMutation.createAccount() delegates to AccountService.createAccount()
**And** AccountMutation.updateAccount() delegates to AccountService.updateAccount()
**And** AccountMutation.deleteAccount() delegates to AccountService.deleteAccount()
**And** AccountMutation approves require @PreAuthorize("hasRole('ADMIN')")
**And** GraphQL endpoint is accessible at /graphql
**And** DataLoader is configured to prevent N+1 queries on Account.seller and Account.game
**And** GraphQL playground is available at /graphiql for testing

**Technical Notes:**
- Follow PRD section 3.2 for GraphQL schema structure
- Use DataLoader pattern for resolving User and Game relationships
- Follow Architecture document section 4.3 for resolver templates
- Share AccountService with REST controllers (DRY)

**Requirements:** FR46 (flexible querying), FR47 (pagination), NFR2 (< 300ms), NFR47 (N+1 prevention)

---

### Story 2.4: REST Controllers for Seller Operations

As a developer,
I want to create REST API endpoints for seller account management,
So that sellers can perform CRUD operations via HTTP requests.

**Acceptance Criteria:**

**Given** the AccountService from Story 2.2
**When** I create AccountController for seller operations
**Then** AccountController is annotated with @RestController and @RequestMapping("/api/accounts")
**And** POST /api/accounts accepts multipart/form-data for account creation with images
**And** POST /api/accounts requires authentication with role SELLER or ADMIN
**And** POST /api/accounts accepts CreateAccountRequest with gameId, title, description, level, rank, price, images
**And** POST /api/uploads/avatar endpoint handles file upload
**And** POST /api/accounts returns AccountResponse with HTTP 201 on success
**And** POST /api/accounts returns 400 for validation errors
**And** PUT /api/accounts/{id} accepts UpdateAccountRequest
**And** PUT /api/accounts/{id} verifies ownership (only seller can update their own listings)
**And** PUT /api/accounts/{id} returns AccountResponse with HTTP 200 on success
**And** PUT /api/accounts/{id} returns 403 if not owner
**And** DELETE /api/accounts/{id} verifies ownership or admin role
**And** DELETE /api/accounts/{id} returns HTTP 204 on success
**And** GET /api/seller/my-accounts returns seller's listings with pagination

**Technical Notes:**
- Use MultipartFile for image uploads
- Validate file types (jpg, png) and size limits (max 10MB)
- Use @PreAuthorize for role checks
- Store image URLs as strings in account_images table
- Follow Architecture document for controller template

**Requirements:** FR10 (create endpoint), FR11 (edit endpoint), FR12 (delete endpoint), FR13 (upload images), NFR1 (< 200ms), NFR17 (file validation)

---

### Story 2.5: Frontend GraphQL Queries & Mutations

As a developer,
I want to create GraphQL queries and mutations for account operations,
So that frontend can interact with the GraphQL API.

**Acceptance Criteria:**

**Given** the GraphQL schema from Story 2.3
**When** I create frontend GraphQL operations
**Then** GET_ACCOUNTS query accepts variables: gameId, minPrice, maxPrice, status, page, limit
**And** GET_ACCOUNTS query returns accounts with seller, game, images, status
**And** GET_ACCOUNT query fetches single account by id with full details
**And** GET_GAMES query fetches all available games
**And** CREATE_ACCOUNT mutation accepts CreateAccountInput
**And** UPDATE_ACCOUNT mutation accepts accountId and UpdateAccountInput
**And** DELETE_ACCOUNT mutation accepts accountId
**And** all queries use useQuery hook from Apollo Client
**And** all mutations use useMutation hook from Apollo Client
**And** Apollo Client is configured with HTTP link to /graphql
**And** Apollo Client has authLink to attach JWT token to each request
**And** Apollo Client has errorLink to handle 401 errors
**And** useQuery hook provides loading, error, and data states
**And** useMutation hook provides onCompleted and onError callbacks

**Technical Notes:**
- Create services/graphql/queries.ts and services/graphql/mutations.ts
- Follow Architecture document section 4.4 for query templates
- TypeScript types for all GraphQL responses
- No UI yet - just API layer

**Requirements:** FR46 (flexible queries), NFR2 (< 300ms)

---

### Story 2.6: Seller Account Listing Pages

As a developer,
I want to create the seller UI for creating and editing account listings,
So that sellers can manage their inventory.

**Acceptance Criteria:**

**Given** the GraphQL operations from Story 2.5
**When** I create seller account management pages
**Then** CreateListingPage has form fields: game (dropdown), title, description, level, rank, price
**And** CreateListingPage has image upload component with drag-and-drop
**And** CreateListingPage validates all required fields
**And** CreateListingPage calls CREATE_ACCOUNT mutation on submit
**And** CreateListingPage shows success message and redirects to My Listings on success
**And** CreateListingPage shows error message on failure
**And** EditListingPage pre-fills form with existing account data
**And** EditListingPage calls UPDATE_ACCOUNT mutation on submit
**And** MyListingsPage displays seller's accounts in grid layout
**And** MyListingsPage uses GET_ACCOUNTS query filtered by seller
**And** MyListingsPage shows account status badge (PENDING, APPROVED, REJECTED, SOLD)
**And** MyListingsPage has edit and delete buttons for each account
**And** MyListingsPage has loading skeleton during query
**And** DeleteAccountModal confirms before deleting
**And** all pages use Tailwind CSS for responsive design
**And** all pages are protected routes requiring authentication

**Technical Notes:**
- Use react-hook-form for form validation
- Use react-dropzone for file uploads
- Use mutation with optimistic UI updates
- Follow Architecture document section 4.5 for UI templates
- Protected routes require SELLER or ADMIN role

**Requirements:** FR10 (create UI), FR11 (edit UI), FR12 (delete UI), FR13 (upload UI), NFR4 (< 2s load), NFR33 (responsive)

---

## Epic 3: Marketplace Discovery

**Goal:** Buyers can browse, search, filter, and view account listings

**FRs covered:** FR14-FR20, FR46-FR47

**NFRs covered:** NFR2, NFR45, NFR47, NFR49, NFR50

**User Value:** Buyers need to find accounts that match their criteria. This epic enables the core discovery workflow - searching, filtering, sorting, and viewing details with GraphQL.

**Dependencies:** Uses Epic 1 (buyers must be authenticated), Epic 2 (needs listings to discover)
**Standalone:** Complete browsing experience - users can explore the marketplace independently.

---

### Story 3.1: Advanced Filtering & Search Implementation

As a developer,
I want to implement advanced filtering and search in AccountService,
So that buyers can find accounts matching their criteria.

**Acceptance Criteria:**

**Given** the AccountService from Epic 2
**When** I enhance searchAccounts() method
**Then** searchAccounts() supports filtering by gameId, minPrice, maxPrice, minLevel, maxLevel, rank, status, isFeatured
**Then** searchAccounts() supports full-text search on title and description fields
**Then** searchAccounts() supports sorting by price (ASC/DESC), level (DESC), createdAt (DESC)
**Then** searchAccounts() supports pagination with page and limit parameters
**Then** searchAccounts() returns Page<Account> with total count and page info
**Then** searchAccounts() uses @Cacheable with key including all filter parameters
**Then** searchAccounts() uses JPA Specification or Criteria API for dynamic queries
**Then** searchAccounts() only returns APPROVED accounts to public buyers
**Then** searchAccounts() returns PENDING accounts only to sellers (own listings) and admins
**Then** database indexes exist on: game_id, seller_id, status, price, level, created_at
**Then** query performance is < 300ms for p95 with 1000+ accounts

**Technical Notes:**
- Use @Query annotation with dynamic criteria or JPA Specifications
- Implement proper indexing strategy in Account entity
- Cache results in Redis with TTL based on filter parameters
- Use @QueryHints for performance optimization
- Follow PRD section 3.2.2 for Query parameters

**Requirements:** FR14 (browse with filters), FR16 (search), FR17 (sort), FR46 (flexible querying), FR47 (pagination), NFR2 (< 300ms), NFR45 (Redis), NFR46 (indexing)

---

### Story 3.2: Favorites/ Wishlist Feature

As a developer,
I want to implement favorites functionality so buyers can save accounts,
So that buyers can track accounts they're interested in.

**Acceptance Criteria:**

**Given** the User and Account entities from previous epics
**When** I create Favorite entity and repository
**Then** Favorite entity has fields: id (Long, PK), user (ManyToOne User), account (ManyToOne Account), createdAt
**Then** Favorite entity has @Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "account_id"}))
**Then** FavoriteRepository extends JpaRepository<Favorite, Long>
**Then** FavoriteRepository has methods: findByUserId(), existsByUserIdAndAccountId(), deleteByUserIdAndAccountId()
**Then** FavoriteService is annotated with @Service and @RequiredArgsConstructor
**Then** FavoriteService.addToFavorites() checks if already favorited and throws BusinessException if duplicate
**Then** FavoriteService.removeFromFavorites() deletes favorite or throws ResourceNotFoundException
**Then** FavoriteService.getUserFavorites() returns list of Account objects
**Then** FavoriteService is annotated with @Transactional
**Then** application starts successfully and MySQL creates favorites table

**Technical Notes:**
- Many-to-many relationship between User and Account with Favorite as join table
- Use unique constraint to prevent duplicate favorites
- No frontend yet - just backend logic

**Requirements:** FR18 (add to favorites), FR19 (remove from favorites), FR20 (view favorites), NFR41 (ACID)

---

### Story 3.3: Favorites REST API & GraphQL Integration

As a developer,
I want to create REST and GraphQL endpoints for favorites,
So that frontend can manage user favorites.

**Acceptance Criteria:**

**Given** the FavoriteService from Story 3.2
**When** I create FavoriteController and GraphQL resolvers
**Then** POST /api/favorites accepts {accountId} in request body
**Then** POST /api/favorites requires authentication
**Then** POST /api/favorites returns HTTP 201 on success, 409 if already favorited
**Then** GET /api/favorites returns user's favorite accounts with pagination
**Then** DELETE /api/favorites/{accountId} removes from favorites
**Then** DELETE /api/favorites/{accountId} returns HTTP 204 on success
**Then** GraphQL Query: favorites() returns user's favorite accounts
**Then** GraphQL Query: favorites() uses @PreAuthorize("isAuthenticated()")
**Then** GraphQL Mutation: addToFavorites(accountId) adds to favorites
**Then** GraphQL Mutation: removeFromFavorites(accountId) removes from favorites
**Then** Account type in GraphQL schema has isFavorited field (computed)
**Then** DataLoader optimizes favorite queries to prevent N+1

**Technical Notes:**
- Share FavoriteService between REST and GraphQL
- Use @NamedEntityGraph for optimizing favorite queries
- Cache favorite lists in Redis with user-specific key

**Requirements:** FR18-FR20, NFR1 (< 200ms REST)

---

### Story 3.4: Account Detail Page with Related Data

As a developer,
I want to create an account detail page showing complete information,
So that buyers can make informed purchase decisions.

**Acceptance Criteria:**

**Given** the GraphQL API from Story 2.3
**When** I create the account detail page
**Then** AccountDetailPage uses GET_ACCOUNT query with accountId
**Then** GET_ACCOUNT query fetches: account details, seller info (fullName, rating, totalReviews), game info, all images
**Then** AccountDetailPage displays title, price, level, rank in header
**Then** AccountDetailPage displays image gallery with thumbnail navigation
**Then** AccountDetailPage displays description field with markdown support
**Then** AccountDetailPage displays seller card with avatar, name, rating, total reviews
**Then** AccountDetailPage displays "Chat with Seller" button
**Then** AccountDetailPage displays "Add to Favorites" button (toggle)
**Then** AccountDetailPage displays "Buy Now" button (if APPROVED status)
**Then** AccountDetailPage shows "PENDING" badge if not yet approved
**Then** AccountDetailPage shows loading skeleton while fetching
**Then** AccountDetailPage shows error message if account not found
**Then** AccountDetailPage increments view count when loaded (via separate API call)
**Then** page is responsive on mobile and desktop
**Then** page uses Tailwind CSS for styling

**Technical Notes:**
- Use Apollo Client's useQuery with fetchPolicy 'cache-and-network'
- Implement optimistic UI for favorites toggle
- Use React Router's useParams to get accountId
- Follow PRD section 3.2.2 for Account query structure

**Requirements:** FR15 (view details), FR18 (add to favorites), NFR2 (< 300ms), NFR4 (< 2s load)

---

### Story 3.5: Marketplace Homepage with Featured Listings

As a developer,
I want to create the marketplace homepage showing featured and new listings,
So that buyers can discover popular and new accounts.

**Acceptance Criteria:**

**Given** the GraphQL API from Story 2.3
**When** I create the HomePage component
**Then** HomePage queries games via GET_GAMES
**Then** HomePage displays game categories in horizontal scroll section
**Then** HomePage queries featured accounts via GET_ACCOUNTS with isFeatured: true
**Then** HomePage displays featured accounts in hero section (carousel or grid)
**Then** HomePage queries new accounts via GET_ACCOUNTS with sortBy: createdAt
**Then** HomePage displays new accounts in grid layout below hero
**Then** each account card shows: image, title, price, game icon, seller rating
**Then** account card links to AccountDetailPage on click
**Then** HomePage has search bar in header
**Then** HomePage displays loading skeleton while fetching
**Then** HomePage uses Tailwind CSS for responsive design
**Then** HomePage implements infinite scroll or pagination for accounts
**Then** HomePage caches games list in React state (infrequently changes)

**Technical Notes:**
- Use Apollo Client's useInfiniteQuery for pagination if implementing infinite scroll
- Cache game list to avoid refetching
- Use React IntersectionObserver for lazy loading
- Use React.lazy for code splitting

**Requirements:** FR14 (browse), FR46 (flexible queries), NFR50 (code splitting), NFR49 (lazy load images)

---

### Story 3.6: Advanced Search & Filter UI

As a developer,
I want to create advanced search and filter components,
So that buyers can find accounts matching specific criteria.

**Acceptance Criteria:**

**Given** the filtering implementation from Story 3.1
**When** I create the SearchPage and filter components
**Then** SearchPage has search input field in header
**Then** SearchPage debounces search input (300ms delay)
**Then** SearchPage displays filter sidebar with: Game dropdown, Price range slider, Level inputs, Rank dropdown, Status toggle
**Then** SearchPage displays results in grid layout
**Then** FilterSidebar component persists filter state in URL query params
**Then** FilterSidebar has "Clear Filters" button
**Then** AccountSearchBar has autocomplete suggestions for game names
**Then** GET_ACCOUNTS query accepts filter variables from URL params
**Then** GET_ACCOUNTS query refetches when filters change
**Then** search results show "No accounts found" message when empty
**Then** search results show result count
**Then** filters are collapsible on mobile
**Then** SortDropdown allows sorting by: Price (Low/High), Price (High/Low), Level, Newest
**Then** active filters are displayed as chips with remove button

**Technical Notes:**
- Use react-router-dom's useSearchParams for URL state
- Use lodash.debounce for search input
- Use custom hook useFilters for filter state management
- Implement filter validation (minPrice < maxPrice)
- Use React.memo for performance optimization

**Requirements:** FR14 (browse with filters), FR16 (search), FR17 (sort), NFR2 (< 300ms), NFR33 (responsive)

---

### Story 3.7: Favorites Management Page

As a developer,
I want to create the favorites/wishlist page,
So that buyers can view and manage their saved accounts.

**Acceptance Criteria:**

**Given** the favorites API from Story 3.3
**When** I create the FavoritesPage component
**Then** FavoritesPage queries user favorites via GET_FAVORITES query
**Then** FavoritesPage displays favorites in grid layout
**Then** each favorite card shows: account image, title, price, game, remove button
**Then** FavoritesPage has "Remove" button on each card (trash icon)
**Then** FavoritesPage shows empty state when no favorites (icon + message)
**Then** FavoritesPage shows loading skeleton while fetching
**Then** FavoritesPage uses optimistic UI for removing favorites (updates immediately)
**Then** FavoritesPage displays error message if removal fails
**Then** FavoritesPage sorts by createdAt (newest first)
**Then** FavoritesPage implements pagination (20 per page)
**Then** remove action requires confirmation modal
**Then** FavoritesPage is protected route
**Then** FavoritesPage uses Tailwind CSS for styling

**Technical Notes:**
- Use useMutation with optimistic response for instant UI updates
- Rollback optimistic update on error
- Use Apollo Client's cache.evict to update isFavorited on other pages
- Implement pull-to-refresh for mobile

**Requirements:** FR20 (view favorites), FR19 (remove from favorites), NFR4 (< 2s load)

---

### Story 3.8: Redis Caching Strategy Implementation

As a developer,
I want to implement Redis caching for frequently accessed data,
So that the marketplace can handle high traffic with low latency.

**Acceptance Criteria:**

**Given** the Redis infrastructure from Epic 1
**When** I implement caching strategy
**Then** @Cacheable is configured on AccountService.searchAccounts() with TTL 10 minutes
**Then** @Cacheable is configured on GameService.getAllGames() with TTL 1 hour
**Then** @Cacheable is configured on AccountService.getFeaturedAccounts() with TTL 5 minutes
**Then** @CacheEvict is configured on create/update/delete account methods
**Then** @CacheEvict is configured on favorite add/remove methods
**Then** cache keys include relevant filter parameters (e.g., "accounts:gameId:123:minPrice:100")
**Then** RedisConfig is annotated with @EnableCaching
**Then** RedisConfig uses GenericJackson2JsonRedisSerializer for JSON serialization
**Then** cache entries have configurable TTL per use case
**Then** @CachePut is used for updating cache without eviction
**Then** cache statistics are logged (hits, misses)

**Technical Notes:**
- Use Spring Cache abstraction (RedisCacheManager)
- Follow Architecture document section 9 for cache configuration
- Use caffeine as local cache fallback if Redis unavailable
- Implement cache warming on application startup

**Requirements:** FR46 (flexible queries), NFR2 (< 300ms), NFR45 (hot data caching)

---

### Story 3.9: DataLoader for N+1 Query Prevention

As a developer,
I want to implement DataLoader to optimize GraphQL queries,
So that nested data fetching doesn't cause performance issues.

**Acceptance Criteria:**

**Given** the GraphQL schema from Story 2.3
**When** I implement DataLoader for account queries
**Then** DataLoader is configured for User batch loading (account.seller)
**Then** DataLoader is configured for Game batch loading (account.game)
**Then** DataLoader is configured in GraphQL context
**Then** DataLoader uses batching to load all unique sellers in one query
**Then** DataLoader uses caching to avoid reloading same entity in single request
**Then** AccountQuery resolvers use DataLoader for seller and game relationships
**Then** GraphQL query complexity analyzer is configured
**Then** max query complexity is set to 1000
**Then** query depth is limited to 10 levels
**Then** DataLoader reduces database queries from N+1 to batch queries
**Then** query performance is < 300ms for accounts with nested seller/game data

**Technical Notes:**
- Use graphql-java-data-loader library
- Configure DataLoaderRegistry in GraphQLConfig
- Use @Dataloader annotation on resolver methods
- Follow PRD section 4.1 optimization strategies
- Test with accounts query returning 50+ accounts

**Requirements:** FR46 (nested data), NFR2 (< 300ms), NFR47 (N+1 prevention)

---

### Story 3.10: Pagination & Infinite Scroll

As a developer,
I want to implement cursor-based pagination for account lists,
So that users can browse large numbers of accounts efficiently.

**Acceptance Criteria:**

**Given** the filtering from Story 3.1
**When** I implement cursor-based pagination
**Then** AccountConnection type is defined in GraphQL schema with edges, pageInfo, totalCount
**Then** AccountEdge type is defined with node (Account) and cursor
**Then** PageInfo type is defined with hasNextPage, hasPreviousPage, startCursor, endCursor
**Then** GET_ACCOUNTS query accepts after and before cursor parameters
**Then** GET_ACCOUNTS query accepts first and limit parameters (max 50)
**Then** accounts query returns AccountConnection with all fields
**Then** cursors are base64 encoded strings of account ID + timestamp
**Then** hasNextPage is true when more results exist
**Then** hasPreviousPage is true when paginating backwards
**Then** frontend Apollo Client implements useInfiniteQuery hook
**Then** frontend has "Load More" button or infinite scroll trigger
**Then** frontend caches fetched results in Apollo Client cache
**Then** pagination maintains filter state from URL params
**Then** pagination state is preserved when navigating back to page

**Technical Notes:**
- Use Relay-style cursor pagination specification
- Follow PRD section 3.2.4 for AccountConnection structure
- Use React IntersectionObserver for infinite scroll trigger
- Implement reverse pagination (before cursor) if needed

**Requirements:** FR47 (cursor-based pagination), NFR48 (pagination), NFR2 (< 300ms)

---

## Epic 4: Secure Transactions

**Goal:** Users can buy accounts, manage transactions, and integrate payments

**FRs covered:** FR21-FR24, FR44-FR45

**NFRs covered:** NFR1, NFR12 (AES-256), NFR41 (ACID)

**User Value:** Buyers and sellers need to exchange value securely. This epic enables the core transaction workflow - purchasing, payment callbacks, and transaction lifecycle management.

**Dependencies:** Uses Epic 1 (users authenticated), Epic 2 (accounts to buy), Epic 3 (discovery leads to purchase)
**Standalone:** Complete transaction system - end-to-end buying flow with payment integration.

---

### Story 4.1: Transaction & Review Entities

As a developer,
I want to create Transaction and Review JPA entities,
So that purchases and reviews can be tracked.

**Acceptance Criteria:**

**Given** the entities from previous epics
**When** I create Transaction and Review entities
**Then** Transaction entity has fields: id (Long, PK), account (ManyToOne Account, lazy), buyer (ManyToOne User, lazy), seller (ManyToOne User, lazy), amount, status (enum: PENDING, COMPLETED, CANCELLED), encryptedCredentials (@Lob String), createdAt, completedAt
**Then** Transaction entity uses Lombok and JPA annotations
**Then** Transaction entity has indexes on buyer_id, seller_id, account_id, status
**Then** TransactionRepository extends JpaRepository<Transaction, Long>
**Then** TransactionRepository has methods: findByBuyerId(), findBySellerId(), findByAccount(), findByStatus()
**Then** Review entity has fields: id (Long, PK), reviewer (ManyToOne User), targetUser (ManyToOne User), rating, comment, createdAt
**Then** Review entity has @Table(uniqueConstraints = @UniqueConstraint(columnNames = {"reviewer_id", "transaction_id"}))
**Then** ReviewRepository extends JpaRepository<Review, Long>
**Then** ReviewRepository has methods: findByTargetUserId(), findByReviewerId(), existsByReviewerAndTransaction()
**Then** application starts successfully and MySQL creates transactions and reviews tables

**Technical Notes:**
- Follow PRD section 3.4.3 for Transaction entity structure
- Use AES-256 encryption for account credentials (passwords, username)
- No business logic yet - just data model
- Review linked to Transaction (one review per transaction)

**Requirements:** FR21 (purchase data), FR22 (history), FR31 (review data), NFR12 (AES-256), NFR41 (ACID)

---

### Story 4.2: TransactionService & Encryption

As a developer,
I want to implement TransactionService with secure credential encryption,
So that buyers receive account credentials after payment.

**Acceptance Criteria:**

**Given** the repositories from Story 4.1
**When** I implement TransactionService
**Then** TransactionService is annotated with @Service and @RequiredArgsConstructor
**Then** TransactionService has dependency on EncryptionUtil for AES-256 encryption
**Then** purchaseAccount() method validates account exists and is APPROVED status
**Then** purchaseAccount() method validates buyer is not the seller
**Then** purchaseAccount() method creates Transaction with PENDING status
**Then** purchaseAccount() method encrypts account credentials (username, password) before storing
**Then** purchaseAccount() method is annotated with @Transactional
**Then** completeTransaction() method changes status to COMPLETED and sets completedAt timestamp
**Then** completeTransaction() method decrypts and returns credentials to buyer
**Then** completeTransaction() method verifies transaction is PENDING
**Then** cancelTransaction() method changes status to CANCELLED
**Then** cancelTransaction() method is only available to buyer or admin
**Then** getMyTransactions() returns buyer's transaction history
**Then** getSellerTransactions() returns seller's transaction history
**Then** exceptions include: BusinessException (account not available, already sold), ResourceNotFoundException

**Technical Notes:**
- Use Java Cryptography Architecture (JCA) for AES encryption
- Store encryption key in application.yml (environment variable in production)
- Never log or return decrypted credentials in error messages
- Follow NFR12 for encryption requirements

**Requirements:** FR21 (purchase), FR22 (view history), FR23 (complete), FR24 (cancel), NFR12 (encryption)

---

### Story 4.3: Payment Gateway Integration (VNPay)

As a developer,
I want to integrate VNPay payment gateway for processing payments,
So that buyers can pay for accounts securely.

**Acceptance Criteria:**

**Given** the TransactionService from Story 4.2
**When** I implement VNPay integration
**Then** VNPayService creates payment URL with amount, orderInfo, return URL
**Then** VNPayService generates secure hash with shared secret key
**Then** VNPayService redirects user to VNPay payment page
**Then** POST /api/payment/vnpay-callback endpoint receives payment callbacks
**Then** callback endpoint validates VNPay signature (vnp_SecureHash)
**Then** callback endpoint verifies transaction amount matches
**Then** callback endpoint calls TransactionService.completeTransaction() on success
**Then** callback endpoint calls TransactionService.cancelTransaction() on failure
**Then** callback endpoint returns HTTP 200 to VNPay
**Then** VNPay configuration has vnp_TmnCode, vnp_HashSecret, vnp_PayUrl, vnp_ReturnUrl from application.yml
**Then** payment flow works: create transaction → redirect to VNPay → payment → callback → complete

**Technical Notes:**
- Follow VNPay integration documentation
- Use Spring's @RestController for callback endpoint
- Store transaction ID in orderInfo for reconciliation
- Implement idempotency to prevent duplicate callback processing
- No frontend yet - just backend integration

**Requirements:** FR44 (VNPay webhook), FR45 (payment callback), FR23 (complete transaction)

---

### Story 4.4: Payment Gateway Integration (Momo)

As a developer,
I want to integrate Momo payment gateway as alternative payment method,
So that buyers have multiple payment options.

**Acceptance Criteria:**

**Given** the payment infrastructure from Story 4.3
**When** I implement Momo integration
**Then** MomoService creates payment request with partnerCode, accessKey, requestId, amount, orderInfo
**Then** MomoService generates signature using HMAC SHA256
**Then** MomoService submits payment request to Momo API
**Then** MomoService receives payment URL and redirects user
**Then** POST /api/payment/momo-callback endpoint receives Momo callbacks
**Then** callback endpoint validates Momo signature
**Then** callback endpoint verifies transaction status (0 = success)
**Then** callback endpoint calls TransactionService.completeTransaction() on success
**Then** callback endpoint returns HTTP 200 to Momo
**Then** Momo configuration has partnerCode, accessKey, secretKey from application.yml
**Then** payment flow works: create transaction → redirect to Momo → payment → callback → complete

**Technical Notes:**
- Follow Momo API documentation for partner integration
- Implement signature validation carefully
- Use requestId for idempotency
- Handle both sandbox and production environments

**Requirements:** FR44 (Momo webhook), FR45 (payment callback), FR23 (complete transaction)

---

### Story 4.5: Transaction REST API

As a developer,
I want to create REST endpoints for transaction management,
So that frontend can handle purchase flow via HTTP requests.

**Acceptance Criteria:**

**Given** the TransactionService and payment services from Stories 4.2-4.4
**When** I create TransactionController
**Then** POST /api/transactions/purchase accepts {accountId} in request body
**Then** POST /api/transactions/purchase requires authentication
**Then** POST /api/transactions/purchase validates account is APPROVED
**Then** POST /api/transactions/purchase creates Transaction with PENDING status
**Then** POST /api/transactions/purchase returns {transactionId, paymentUrl} with HTTP 201
**Then** POST /api/transactions/purchase returns 400 if account not available
**Then** GET /api/transactions requires authentication
**Then** GET /api/transactions returns user's transactions (buyer or seller view)
**Then** GET /api/transactions/{id} requires authentication and ownership check
**Then** GET /api/transactions/{id} returns transaction details with status
**Then** PUT /api/transactions/{id}/complete calls TransactionService.completeTransaction()
**Then** PUT /api/transactions/{id}/complete is restricted to admin or seller
**Then** PUT /api/transactions/{id}/cancel calls TransactionService.cancelTransaction()
**Then** PUT /api/transactions/{id}/cancel is restricted to buyer or admin
**Then** all endpoints return proper HTTP status codes (200, 201, 400, 403, 404)

**Technical Notes:**
- Use @PreAuthorize for role checks
- Implement proper error handling with GlobalExceptionHandler
- Return DTOs (TransactionResponse) not entities directly

**Requirements:** FR21 (purchase endpoint), FR22 (history), FR23 (complete), FR24 (cancel), NFR1 (< 200ms)

---

### Story 4.6: Purchase Flow & Payment UI

As a developer,
I want to create the purchase flow UI with payment options,
So that buyers can complete transactions.

**Acceptance Criteria:**

**Given** the Transaction API from Story 4.5
**When** I create the purchase flow components
**Then** AccountDetailPage has "Buy Now" button (opens PurchaseModal)
**Then** PurchaseModal shows account details: title, price, seller info
**Then** PurchaseModal shows payment method selection: VNPay, Momo
**Then** PurchaseModal has "Confirm Purchase" button
**Then** PurchaseModal shows loading state during transaction creation
**Then** PurchaseModal redirects to payment URL on success
**Then** PurchaseModal shows error message on failure
**Then** PurchasePage shows after payment callback
**Then** PurchasePage shows success message with transaction details
**Then** PurchasePage shows "View Credentials" button (reveals account credentials via API call)
**Then** PurchasePage has "Back to Home" button
**Then** TransactionHistoryPage lists user's transactions
**Then** TransactionHistoryPage shows transaction status badges: PENDING, COMPLETED, CANCELLED
**Then** TransactionHistoryPage has filters: status, date range
**Then** TransactionHistoryPage uses pagination
**Then** all components use Tailwind CSS for styling
**Then** all pages are protected routes

**Technical Notes:**
- Use Axios for REST API calls
- Use React state for payment method selection
- Store transaction ID in sessionStorage during payment flow
- Implement callback handling (payment gateway return URL)
- Use crypto-js for client-side operations if needed

**Requirements:** FR21 (purchase UI), FR22 (history UI), FR23 (complete UI), NFR4 (< 2s load)

---

### Story 4.7: Review System Backend

As a developer,
I want to implement ReviewService and REST endpoints for ratings,
So that users can rate and review sellers after transactions.

**Acceptance Criteria:**

**Given** the Review entity from Story 4.1
**When** I implement ReviewService and ReviewController
**Then** ReviewService is annotated with @Service and @RequiredArgsConstructor
**Then** ReviewService.createReview() validates transaction is COMPLETED
**Then** ReviewService.createReview() validates user hasn't already reviewed this transaction
**Then** ReviewService.createReview() updates seller's rating and totalReviews
**Then** ReviewService.createReview() is annotated with @Transactional
**Then** ReviewService.updateReview() validates ownership
**Then** ReviewService.updateReview() recalculates seller rating
**Then** ReviewService.deleteReview() removes review and recalculates rating
**Then** ReviewService.getUserReviews() returns user's given reviews
**Then** ReviewService.getSellerReviews() returns reviews received by seller
**Then** ReviewService calculates average rating: sum(ratings) / totalReviews
**Then** POST /api/reviews accepts {transactionId, rating, comment}
**Then** POST /api/reviews requires authentication and valid transaction
**Then** GET /api/reviews/user/{userId} returns user's reviews
**Then** PUT /api/reviews/{id} accepts updated rating and comment
**Then** DELETE /api/reviews/{id} requires ownership
**Then** all endpoints return appropriate HTTP status codes

**Technical Notes:**
- Update seller's User.rating and User.totalReviews after each review change
- Use formula: newRating = ((oldRating * oldTotal) + newRating) / (oldTotal + 1)
- Ensure transactional consistency for rating updates
- No frontend yet - just backend

**Requirements:** FR31 (create review), FR32 (update), FR33 (delete), FR34 (view reviews), NFR41 (ACID)

---

### Story 4.8: Review UI Components

As a developer,
I want to create the review system UI,
So that buyers can rate sellers after successful transactions.

**Acceptance Criteria:**

**Given** the Review API from Story 4.7
**When** I create review components
**Then** TransactionDetailPage has "Leave Review" button if transaction is COMPLETED and not yet reviewed
**Then** ReviewModal displays star rating component (1-5 stars)
**Then** ReviewModal has comment text area
**Then** ReviewModal has "Submit Review" button
**Then** ReviewModal validates rating is selected (required)
**Then** ReviewModal shows loading state during submission
**Then** ReviewModal shows success message and closes on submit
**Then** ReviewModal shows error message on failure
**Then** StarRating component displays interactive stars (hover state)
**Then** SellerProfilePage displays seller's average rating with stars
**Then** SellerProfilePage displays review count
**Then** SellerProfilePage lists recent reviews with: reviewer name, rating (stars), comment, date
**Then** SellerProfilePage has "View All Reviews" link
**Then** ReviewsPage displays paginated list of seller reviews
**Then** ReviewsPage has "Write Review" button (if valid transaction exists)
**Then** ReviewsPage shows empty state if no reviews
**Then** all components use Tailwind CSS for styling

**Technical Notes:**
- Use react-rating library or custom star component
- Use react-hook-form for validation
- Implement optimistic UI updates
- Cache seller rating in Apollo Client cache

**Requirements:** FR31 (create review UI), FR32 (update UI), FR33 (delete UI), FR34 (view reviews UI)

---

## Epic 5: Real-time Communication

**Goal:** Users can chat and receive live notifications

**FRs covered:** FR25-FR30, FR48-FR49

**NFRs covered:** NFR3 (WebSocket latency), NFR18 (scaling), NFR21 (WebSocket pub/sub), NFR44 (message delivery)

**User Value:** Users need to communicate in real-time before and after transactions. This epic enables live chat, typing indicators, notifications, and broadcast updates.

**Dependencies:** Uses Epic 1 (authenticated users)
**Standalone:** Complete real-time messaging system - independent communication channel with WebSocket.

---

### Story 5.1: Message Entity & Repository

As a developer,
I want to create Message entity for storing chat messages,
So that chat history can be persisted and retrieved.

**Acceptance Criteria:**

**Given** the entities from previous epics
**When** I create Message entity
**Then** Message entity has fields: id (Long, PK), account (ManyToOne Account), sender (ManyToOne User), receiver (ManyToOne User), content (TEXT, not null), isRead (Boolean, default false), createdAt
**Then** Message entity uses Lombok and JPA annotations
**Then** Message entity has indexes on: account_id, sender_id, receiver_id
**Then** MessageRepository extends JpaRepository<Message, Long>
**Then** MessageRepository has methods: findByAccountIdAndSenderIdOrReceiverId(), findUnreadCount(), findConversation(accountId, userId1, userId2)
**Then** MessageRepository has @Query for finding messages between two users for specific account
**Then** application starts successfully and MySQL creates messages table
**Then** messages table has foreign key constraints to accounts and users

**Technical Notes:**
- Use TEXT type for content to support long messages
- No WebSocket logic yet - just data model
- Follow PRD section 3.4.4 for Message entity structure

**Requirements:** FR25 (send message), FR26 (receive), FR28 (mark read), NFR41 (ACID)

---

### Story 5.2: WebSocket Configuration (STOMP)

As a developer,
I want to configure Spring WebSocket with STOMP protocol,
So that real-time communication is possible.

**Acceptance Criteria:**

**Given** the Spring Boot project from Epic 1
**When** I configure WebSocket
**Then** WebSocketConfig class is annotated with @EnableWebSocketMessageBroker
**Then** WebSocketConfig implements WebSocketMessageBrokerConfigurer
**Then** registerStompEndpoints() registers /ws endpoint with SockJS support
**Then** registerStompEndpoints() sets allowed origins to frontend URL
**Then** registerStompEndpoints() enables SockJS fallback
**Then** configureMessageBroker() enables simple broker for /topic and /queue prefixes
**Then** configureMessageBroker() sets application destination prefix to /app
**Then** configureMessageBroker() sets user destination prefix to /user
**Then** WebSocket connection requires JWT token in handshake query
**Then** JWT validation is performed during WebSocket connection
**Then** connected users are tracked via ChannelInterceptor
**Then** WebSocket is accessible at ws://localhost:8080/ws (or wss:// in production)

**Technical Notes:**
- Follow Architecture document section 8 for WebSocket configuration
- Use STOMP over WebSocket messaging protocol
- Implement JwtChannelInterceptor for authentication
- No message handlers yet - just configuration

**Requirements:** FR48 (JWT auth), FR49 (persistent connections), NFR16 (WSS, JWT handshake)

---

### Story 5.3: ChatService & Message Handlers

As a developer,
I want to implement ChatService and WebSocket message handlers,
So that users can send and receive chat messages.

**Acceptance Criteria:**

**Given** the MessageRepository from Story 5.1 and WebSocket config from Story 5.2
**When** I implement ChatService and ChatController
**Then** ChatService is annotated with @Service and @RequiredArgsConstructor
**Then** ChatService.sendMessage() saves Message entity via MessageRepository
**Then** ChatService.sendMessage() is annotated with @Transactional
**Then** ChatService.getConversation() returns messages between two users for an account
**Then** ChatService.markAsRead() updates isRead field for messages
**Then** ChatService.getUnreadCount() returns count of unread messages for user
**Then** ChatController is annotated with @Controller
**Then** ChatController has @MessageMapping("/app/chat.send")
**Then** ChatController.sendMessage() accepts {accountId, receiverId, content}
**Then** ChatController.sendMessage() extracts sender from Principal
**Then** ChatController.sendMessage() calls ChatService.sendMessage()
**Then** ChatController.sendMessage() broadcasts to /topic/chat/{accountId} via @SendTo
**Then** ChatController has @MessageMapping("/app/chat.read")
**Then** ChatController.markRead() marks messages as read
**Then** ChatController has @MessageMapping("/app/chat.typing")
**Then** ChatController.handleTyping() broadcasts typing indicator to /queue/typing/{receiverId}
**Then** messages are delivered to all subscribers of the account's chat topic

**Technical Notes:**
- Follow Architecture document section 8.2 for ChatController template
- Use SimpMessagingTemplate for broadcasting
- Extract authenticated user from SecurityContextHolder
- No frontend yet - just backend WebSocket handlers

**Requirements:** FR25 (send), FR26 (receive), FR27 (typing), FR28 (mark read), NFR3 (< 100ms latency)

---

### Story 5.4: NotificationService & Broadcasts

As a developer,
I want to implement NotificationService for real-time notifications and broadcasts,
So that users receive updates about account status, transactions, and new listings.

**Acceptance Criteria:**

**Given** the WebSocket infrastructure from Story 5.2
**When** I implement NotificationService
**Then** NotificationService is annotated with @Service and @RequiredArgsConstructor
**Then** NotificationService has dependency on SimpMessagingTemplate
**Then** NotificationService.sendAccountApprovedNotification() sends to /topic/notifications/{sellerId}
**Then** NotificationService.sendAccountRejectedNotification() sends to /topic/notifications/{sellerId}
**Then** NotificationService.sendAccountSoldNotification() sends to /topic/notifications/{sellerId}
**Then** NotificationService.sendNewTransactionNotification() sends to /topic/notifications/{sellerId}
**Then** NotificationService.sendPaymentReceivedNotification() sends to /topic/notifications/{sellerId}
**Then** NotificationService.broadcastNewAccount() sends to /topic/accounts with account data
**Then** NotificationService.broadcastAccountStatusChanged() sends to /topic/accounts with status update
**Then** NotificationService broadcasts to /topic/accounts when account is created, approved, rejected, sold
**Then** notification payloads include: {id, type, title, message, data}
**Then** notifications are sent asynchronously
**Then** WebSocket connection retries automatically on disconnect

**Technical Notes:**
- Use @Async for async notification sending
- Implement retry logic with exponential backoff
- Create notification DTOs for different event types
- Integrate with AccountService, TransactionService

**Requirements:** FR29 (notifications), FR30 (broadcasts), NFR3 (< 100ms), NFR18 (horizontal scaling)

---

### Story 5.5: Frontend WebSocket Client Setup

As a developer,
I want to create the SockJS/STOMP WebSocket client,
So that frontend can connect to real-time features.

**Acceptance Criteria:**

**Given** the WebSocket endpoint from Story 5.2
**When** I create the WebSocket client service
**Then** websocketService.ts exports WebSocketService singleton class
**Then** WebSocketService.connect() accepts JWT token parameter
**Then** WebSocketService.connect() creates SockJS connection to ws://localhost:8080/ws
**Then** WebSocketService.connect() passes JWT token in connectHeaders.Authorization
**Then** WebSocketService.connect() enables debug logging
**Then** WebSocketService.connect() sets reconnectDelay to 5000ms
**Then** WebSocketService.connect() sets heartbeatIncoming to 4000ms
**Then** WebSocketService.connect() sets heartbeatOutgoing to 4000ms
**Then** WebSocketService.onConnect callback logs "WebSocket connected"
**Then** WebSocketService.onDisconnect callback logs "WebSocket disconnected"
**Then** WebSocketService.subscribeToChat(accountId, callback) subscribes to /topic/chat/{accountId}
**Then** WebSocketService.subscribeToNotifications(userId, callback) subscribes to /topic/notifications/{userId}
**Then** WebSocketService.subscribeToAccountUpdates(callback) subscribes to /topic/accounts
**Then** WebSocketService.sendMessage(accountId, receiverId, content) publishes to /app/chat.send
**Then** WebSocketService.sendTypingIndicator(accountId, receiverId) publishes to /app/chat.typing
**Then** WebSocketService.disconnect() calls client.deactivate()
**Then** WebSocket connection is established when user logs in
**Then** WebSocket connection is closed when user logs out

**Technical Notes:**
- Use @stomp/stompjs and sockjs-client libraries
- Follow Architecture document section 4.2.4 for client template
- Store WebSocketService instance in module scope
- Implement auto-reconnect logic

**Requirements:** FR48 (JWT handshake), FR49 (persistent), NFR16 (WSS)

---

### Story 5.6: Chat UI Components

As a developer,
I want to create the chat interface components,
So that users can communicate in real-time.

**Acceptance Criteria:**

**Given** the WebSocket client from Story 5.5
**When** I create chat UI components
**Then** ChatBox component displays message history as scrollable list
**Then** ChatBox shows messages grouped by sender (left for received, right for sent)
**Then** ChatBox displays message sender name and timestamp
**Then** ChatBox has message input field at bottom
**Then** ChatBox has "Send" button
**Then** ChatBox auto-scrolls to bottom when new message arrives
**Then** ChatBox shows "X is typing..." indicator
**Then** ChatBox handles Enter key to send message
**Then** MessageBubble component styles sent messages differently from received messages
**Then** MessageBubble displays sender avatar for received messages
**Then** ConversationList component displays list of conversations (grouped by account)
**Then** ConversationList shows last message preview and timestamp
**Then** ConversationList shows unread count badge
**Then** ConversationList is clickable to switch between conversations
**Then** ChatBox uses useChat custom hook for WebSocket integration
**Then** useChat hook manages connection state, messages, and typing indicators
**Then** all components use Tailwind CSS for styling

**Technical Notes:**
- Use useChat hook with useState for messages array
- Use useEffect for WebSocket subscription and cleanup
- Use useRef for auto-scrolling
- Implement message grouping by date if needed
- Use formatDistanceToNow for timestamps

**Requirements:** FR25 (send UI), FR26 (receive UI), FR27 (typing UI), NFR3 (< 100ms)

---

### Story 5.7: Notification Components

As a developer,
I want to create the notification system UI,
So that users receive real-time updates about marketplace events.

**Acceptance Criteria:**

**Given** the WebSocket client from Story 5.5
**When** I create notification components
**Then** NotificationBell component in header shows unread count badge
**Then** NotificationBell displays bell icon that opens dropdown on click
**Then** NotificationDropdown lists recent notifications
**Then** NotificationDropdown shows notification icon based on type: approved (check), rejected (x), sold (money), new transaction (receipt)
**Then** NotificationDropdown marks notification as read when clicked
**Then** NotificationDropdown has "Mark all as read" button
**Then** NotificationDropdown shows "No notifications" empty state
**Then** NotificationDropdown is position absolute with proper z-index
**Then** NotificationDropdown closes when clicking outside
**Then** NotificationToast displays toast popup for new notifications
**Then** NotificationToast auto-hides after 5 seconds
**Then** NotificationToast displays different colors based on type: success (green), error (red), info (blue)
**Then** NotificationContext provides notifications array, addNotification(), markAsRead(), clearAll()
**Then** useNotification hook provides access to NotificationContext
**Then** useNotification hook subscribes to /topic/notifications/{userId} on mount
**Then** notifications persist in localStorage for cross-session access

**Technical Notes:**
- Use react-hot-toast for toast notifications
- Implement notification types: ACCOUNT_APPROVED, ACCOUNT_REJECTED, ACCOUNT_SOLD, NEW_TRANSACTION, PAYMENT_RECEIVED
- Use React Context for global notification state
- Use localStorage for persistence

**Requirements:** FR29 (notifications UI), NFR3 (< 100ms delivery)

---

### Story 5.8: Real-time Account Updates

As a developer,
I want to implement real-time account updates in the marketplace,
So that users see live changes without refreshing.

**Acceptance Criteria:**

**Given** the WebSocket infrastructure from Story 5.2
**When** I implement real-time updates
**Then** frontend subscribes to /topic/accounts via websocketService.subscribeToAccountUpdates()
**Then** frontend handles 'new_account_posted' message
**Then** AccountList component adds new account to list when received
**Then** AccountList component shows toast notification "New account posted!"
**Then** frontend handles 'account_status_changed' message
**Then** AccountCard updates status badge when status changes
**Then** AccountCard removes itself if status is SOLD
**Then** AccountDetailPage updates status if viewing account
**Then** Apollo Client cache is updated via cache.writeQuery or client.evict()
**Then** updates are optimistic for immediate UI feedback
**Then** useAccountUpdates hook provides subscriptions
**Then** useAccountUpdates hook handles subscription in useEffect with cleanup

**Technical Notes:**
- Use Apollo Client's cache.evict({ fieldName: 'accounts') })
- Use client.refetchQueries() for hard refresh if needed
- Implement reconnection logic with exponential backoff
- Use React Context for global account state if needed

**Requirements:** FR30 (broadcast updates), NFR3 (< 100ms), NFR21 (Redis pub/sub)

---

## Epic 6: Reviews & Reputation

**Goal:** Users can rate and review sellers after transactions

**FRs covered:** FR31-FR34

**NFRs covered:** NFR41 (ACID)

**User Value:** Users need to establish trust in the marketplace through reputation. This epic enables the review workflow - rating, commenting, and viewing seller history.

**Dependencies:** Uses Epic 1 (authenticated users), Epic 4 (requires completed transactions)
**Standalone:** Complete review system - users can rate and view reviews independently.

---

### Story 6.1: ReviewSystem Backend Enhancement

As a developer,
I want to enhance the ReviewService with comprehensive review management,
So that the review system is complete and robust.

**Acceptance Criteria:**

**Given** the Review entity from Epic 4
**When** I enhance ReviewService
**Then** ReviewService.createReview() validates transaction exists and is COMPLETED
**Then** ReviewService.createReview() validates user hasn't reviewed this seller before (unique on transaction)
**Then** ReviewService.createReview() validates rating is 1-5
**Then** ReviewService.createReview() calculates new seller rating: ((oldRating × totalReviews) + newRating) / (totalReviews + 1)
**Then** ReviewService.createReview() updates User.rating and User.totalReviews atomically
**Then** ReviewService.updateReview() validates review ownership
**Then** ReviewService.updateReview() recalculates seller rating with new values
**Then** ReviewService.deleteReview() recalculates seller rating excluding deleted review
**Then** ReviewService.getSellerReviews() returns paginated reviews with sorting
**Then** ReviewService.getSellerAverageRating() returns average rating and count
**Then** ReviewService.getTransactionReview() returns review for specific transaction
**Then** all state-changing methods are annotated with @Transactional
**Then** exceptions include: BusinessException (duplicate review, invalid rating), ResourceNotFoundException

**Technical Notes:**
- Use floating-point arithmetic for rating calculations
- Ensure atomic updates to prevent race conditions
- Use @Version on User entity for optimistic locking
- Cache seller ratings in Redis for performance

**Requirements:** FR31 (create), FR32 (update), FR33 (delete), FR34 (view), NFR41 (ACID)

---

### Story 6.2: Review GraphQL API

As a developer,
I want to create GraphQL queries and mutations for reviews,
So that frontend can perform review operations via GraphQL.

**Acceptance Criteria:**

**Given** the ReviewService from Story 6.1
**When** I create GraphQL review resolvers
**Then** schema.graphqls defines Review type with id, reviewer, rating, comment, createdAt
**Then** schema.graphqls defines ReviewInput with rating, comment
**Then** Query: reviews(transactionId: ID!) returns reviews for transaction
**Then** Query: userReviews(userId: ID!) returns user's given reviews
**Then** Query: sellerReviews(sellerId: ID!) returns seller's received reviews with pagination
**Then** Query: sellerRating(sellerId: ID!) returns average rating and total count
**Then** Mutation: createReview(input) creates review and returns success
**Then** Mutation: createReview validates transaction is COMPLETED via custom data fetcher
**Then** Mutation: updateReview(id, input) updates existing review
**Then** Mutation: deleteReview(id) deletes review
**Then** ReviewQuery implements GraphQLQueryResolver
**Then** ReviewMutation implements GraphQLMutationResolver
**Then** all resolvers delegate to ReviewService methods
**Then** DataLoader is used to optimize reviewer and transaction queries
**Then** mutations are protected with @PreAuthorize("isAuthenticated()")

**Technical Notes:**
- Follow PRD section 3.2.3 for Mutation structure
- Use DataLoader to prevent N+1 on reviewer queries
- Share ReviewService with REST (DRY)
- Implement custom fetcher for transaction validation

**Requirements:** FR31 (create), FR32 (update), FR33 (delete), FR34 (view), NFR2 (< 300ms)

---

### Story 6.3: Review REST API Enhancement

As a developer,
I want to create comprehensive REST endpoints for reviews,
So that review operations are accessible via HTTP.

**Acceptance Criteria:**

**Given** the ReviewService from Story 6.1
**When** I create ReviewController with comprehensive endpoints
**Then** POST /api/reviews accepts {transactionId, rating, comment}
**Then** POST /api/reviews requires authentication and valid transaction
**Then** POST /api/reviews validates transaction is COMPLETED
**Then** POST /api/reviews returns ReviewResponse with HTTP 201 on success
**Then** POST /api/reviews returns 400 if transaction not COMPLETED or duplicate review
**Then** GET /api/reviews/user/{userId} returns user's reviews with pagination
**Then** GET /api/reviews/seller/{sellerId} returns seller's reviews with pagination
**Then** GET /api/reviews/seller/{sellerId}/stats returns {averageRating, totalReviews}
**Then** GET /api/reviews/transaction/{transactionId} returns review for transaction
**Then** PUT /api/reviews/{id} accepts {rating, comment}
**Then** PUT /api/reviews/{id} requires ownership (only reviewer can update)
**Then** PUT /api/reviews/{id} returns HTTP 200 on success
**Then** DELETE /api/reviews/{id} requires ownership or admin role
**Then** DELETE /api/reviews/{id} returns HTTP 204 on success
**Then** all endpoints return proper HTTP status codes
**Then** GlobalExceptionHandler handles BusinessException and ResourceNotFoundException

**Technical Notes:**
- Use @PreAuthorize for role and ownership checks
- Return ReviewResponse DTOs not entities
- Use Pageable for pagination support
- Implement proper error messages for validation failures

**Requirements:** FR31-FR34, NFR1 (< 200ms REST)

---

### Story 6.4: Star Rating Component

As a developer,
I want to create the interactive star rating component,
So that users can select ratings visually.

**Acceptance Criteria:**

**Given** the review requirements from Epic 6
**When** I create StarRating component
**Then** StarRating displays 5 star icons
**Then** StarRating accepts rating value (1-5) as prop
**Then** StarRating is interactive if onEditMode prop is true
**Then** StarRating is read-only if onEditMode prop is false (default)
**Then** StarRating highlights stars up to rating value on hover
**Then** StarRating fills stars with gold color when active
**Then** StarRating shows outline stars for inactive state
**Then** StarRating supports half-star ratings (optional)
**Then** StarRating calls onChange(rating) callback when star is clicked
**Then** StarRating displays tooltip on hover ("1 star", "2 stars", etc.)
**Then** StarRating uses Tailwind CSS for styling
**Then** StarRating is accessible with keyboard navigation
**Then** StarRating has aria-label for screen readers

**Technical Notes:**
- Use react-icons or heroicons for star icons
- Implement controlled component pattern
- Use onMouseEnter and onMouseLeave for hover effects
- Use onKeyDown for accessibility

**Requirements:** FR31 (rating selection), NFR35 (WCAG 2.1 AA)

---

### Story 6.5: Review Form & Display

As a developer,
I want to create the review form and display components,
So that users can create and view reviews.

**Acceptance Criteria:**

**Given** the Review API from Story 6.3
**When** I create review components
**Then** CreateReviewForm has StarRating component (interactive)
**Then** CreateReviewForm has comment textarea with character limit (500 chars)
**Then** CreateReviewForm validates rating is selected
**Then** CreateReviewForm validates comment is not empty
**Then** CreateReviewForm has "Submit Review" button
**Then** CreateReviewForm shows loading state during submission
**Then** CreateReviewForm shows success message and clears form on submit
**Then** CreateReviewForm shows error message on failure
**Then** ReviewList displays paginated list of reviews
**Then** ReviewListItem displays reviewer name, avatar, rating (stars), comment, date
**Then** ReviewListItem has "Report" button (for moderation)
**Then** ReviewListItem displays "Edited" badge if review was updated
**Then** ReviewList has "Load More" button for pagination
**Then** SellerProfilePage displays average rating with large stars component
**Then** SellerProfilePage displays review count: "XX reviews"
**Then** SellerProfilePage displays rating breakdown (histogram)
**Then** all components use Tailwind CSS for styling

**Technical Notes:**
- Use react-hook-form for form validation
- Use formatRelative for dates ("2 days ago")
- Implement pagination with Apollo Client's fetchMore
- Cache review data in Apollo Client

**Requirements:** FR31 (create UI), FR32 (update UI), FR34 (view UI), NFR35 (accessibility)

---

### Story 6.6: Review Management Page

As a developer,
I want to create the review management page,
So that users can view and manage their reviews.

**Acceptance Criteria:**

**Given** the review components from Story 6.5
**When** I create MyReviewsPage
**Then** MyReviewsPage displays user's given reviews in list view
**Then** MyReviewsPage shows each review with: target seller name, rating, comment, transaction link, date
**Then** MyReviewsPage has "Edit" button for each review (if within 30 days)
**Then** MyReviewsPage has "Delete" button for each review
**Then** MyReviewsPage confirms deletion before proceeding
**Then** MyReviewsPage uses pagination (10 reviews per page)
**Then** MyReviewsPage shows empty state if no reviews
**Then** MyReviewsPage is protected route
**Then** EditReviewModal pre-fills form with existing review data
**Then** EditReviewModal validates all fields
**Then** DeleteReviewModal confirms: "Are you sure you want to delete this review?"
**Then** MyReviewsPage uses useQuery to fetch reviews
**Then** edit/delete uses useMutation with optimistic updates

**Technical Notes:**
- Use Apollo Client's useMutation with refetchQueries
- Implement optimistic UI updates
- Use Modal components from shadcn/ui or custom
- Use React Router's useNavigate for navigation

**Requirements:** FR32 (update UI), FR33 (delete UI), NFR4 (< 2s load)

---

## Epic 7: Platform Administration

**Goal:** Admins can manage users, listings, and platform statistics

**FRs covered:** FR35-FR43

**NFRs covered:** NFR1, NFR10 (rate limiting)

**User Value:** Platform operators need to maintain marketplace integrity. This epic enables administrative workflows - moderation, approvals, analytics, and reporting.

**Dependencies:** Uses Epic 1 (admin authentication), Epic 2 (listings to moderate)
**Standalone:** Complete admin dashboard - full platform management capabilities.

---

### Story 7.1: Admin Role & Security Setup

As a developer,
I want to configure admin role and secure admin endpoints,
So that only authorized users can access admin features.

**Acceptance Criteria:**

**Given** the User entity with Role enum from Epic 1
**When** I configure admin security
**Then** User.Role enum has ADMIN value
**Then** SecurityConfig permits /api/admin/** only for ROLE_ADMIN
**Then** SecurityConfig has custom AdminAuthorizationFilter
**Then** AdminAuthorizationFilter checks user role from SecurityContext
**Then** AdminAuthorizationFilter returns 403 for non-admin users
**Then** test admin user exists with role ADMIN and email admin@gameaccount.com
**Then** test admin user can access admin endpoints
**Then** test regular user cannot access admin endpoints
**Then** admin endpoints are documented in Swagger/OpenAPI

**Technical Notes:**
- Use @PreAuthorize("hasRole('ADMIN')") on admin controllers
- Create @AdminRole annotation for custom checks
- Implement custom AccessDeniedHandler
- No admin UI yet - just security infrastructure

**Requirements:** FR39 (ban), FR40 (unban), NFR9 (RBAC)

---

### Story 7.2: Admin Statistics Queries

As a developer,
I want to implement statistics queries for the admin dashboard,
So that admins can view platform metrics.

**Acceptance Criteria:**

**Given** the repositories from previous epics
**When** I implement StatisticsService
**Then** StatisticsService is annotated with @Service and @RequiredArgsConstructor
**Then** StatisticsService.getDashboardStats() returns: totalUsers, totalAccounts, totalTransactions, totalRevenue, pendingAccounts
**Then** StatisticsService.getRevenueStats() accepts date range parameters
**Then** StatisticsService.getRevenueStats() returns: dailyRevenue, totalRevenue, transactionCount, averageOrderValue
**Then** StatisticsService.getUserStats() returns: userGrowth, activeUsers, usersByRole
**Then** StatisticsService.getAccountStats() returns: accountsByStatus, accountsByGame, accountsByPriceRange
**Then** StatisticsService.getTransactionStats() returns: transactionsByStatus, transactionsByDate, paymentMethodBreakdown
**Then** StatisticsService uses @Cacheable for dashboard stats (5 min TTL)
**Then** StatisticsService uses COUNT, SUM, AVG JPA aggregations
**Then** StatisticsService formats dates and currency values
**Then** query performance is < 500ms for dashboard stats

**Technical Notes:**
- Use @Query with aggregations for performance
- Use LocalDate and LocalDateTime for date handling
- Consider materialized views for complex stats
- No frontend yet - just backend queries

**Requirements:** FR41 (statistics dashboard), FR42 (revenue reports), NFR1 (< 200ms REST)

---

### Story 7.3: Admin REST API Endpoints

As a developer,
I want to create admin REST endpoints for platform management,
So that admins can manage users and accounts via HTTP requests.

**Acceptance Criteria:**

**Given** the services from Story 7.2
**When** I create AdminController
**Then** AdminController is annotated with @RestController and @RequestMapping("/api/admin")
**Then** AdminController is annotated with @PreAuthorize("hasRole('ADMIN')")
**Then** GET /api/admin/statistics/dashboard returns DashboardStats
**Then** GET /api/admin/statistics/revenue accepts startDate, endDate parameters
**Then** GET /api/admin/statistics/revenue returns RevenueStats
**Then** GET /api/admin/accounts/pending returns all PENDING accounts with pagination
**Then** PUT /api/admin/accounts/{id}/approve calls AccountService.approveAccount()
**Then** PUT /api/admin/accounts/{id}/approve returns HTTP 200 on success
**Then** PUT /api/admin/accounts/{id}/reject accepts {reason} parameter
**Then** PUT /api/admin/accounts/{id}/reject calls AccountService.rejectAccount()
**Then** PUT /api/admin/accounts/{id}/feature toggles isFeatured flag
**Then** PUT /api/admin/users/{id}/ban bans user and sets status to BANNED
**Then** PUT /api/admin/users/{id}/unban unbans user and sets status to ACTIVE
**Then** GET /api/admin/users returns all users with pagination and filters
**Then** GET /api/admin/reports/transactions generates CSV report
**Then** GET /api/admin/reports/revenue generates PDF report
**Then** all endpoints return appropriate HTTP status codes
**Then** GlobalExceptionHandler handles admin-specific exceptions

**Technical Notes:**
- Use @PreAuthorize for role checks
- Return AdminResponse DTOs not entities
- Use OpenAPI annotations for Swagger documentation
- Use Spring Boot's CsvHelper for CSV generation
- Use iText or similar for PDF generation

**Requirements:** FR35-FR43, NFR1 (< 200ms REST)

---

### Story 7.4: Account Approval Workflow

As a developer,
I want to implement the admin account approval system,
So that all listings go through moderation before appearing in marketplace.

**Acceptance Criteria:**

**Given** the AccountService from Epic 2
**When** I implement admin approval endpoints
**Then** AccountService.approveAccount() validates account is PENDING
**Then** AccountService.approveAccount() changes status to APPROVED
**Then** AccountService.approveAccount() is annotated with @PreAuthorize("hasRole('ADMIN')")
**Then** AccountService.approveAccount() triggers notification to seller via NotificationService
**Then** AccountService.rejectAccount() accepts reason parameter
**Then** AccountService.rejectAccount() changes status to REJECTED
**Then** AccountService.rejectAccount() triggers notification to seller with reason
**Then** admin can view all pending accounts via GET /api/admin/accounts/pending
**Then** admin can filter pending accounts by game, price range, date
**Then** admin can bulk approve multiple accounts (optional feature)
**Then** admin actions are logged for audit trail
**Then** rejected accounts cannot be re-approved (creates new listing required)

**Technical Notes:**
- Create AuditLog entity to track admin actions
- Use @Async for notifications
- Implement soft delete or status history if needed
- Follow moderation workflow from PRD

**Requirements:** FR35 (view pending), FR36 (approve), FR37 (reject), FR38 (feature), FR29 (notifications)

---

### Story 7.5: User Management Backend

As a developer,
I want to implement user management for admins,
So that admins can moderate user behavior.

**Acceptance Criteria:**

**Given** the UserRepository from Epic 1
**When** I implement user management
**Then** AdminService.banUser() changes User.status to BANNED
**Then** AdminService.banUser() disables user's ability to login
**Then** AdminService.banUser() is annotated with @PreAuthorize("hasRole('ADMIN')")
**Then** AdminService.banUser() logs ban action with reason and timestamp
**Then** AdminService.unbanUser() changes User.status to ACTIVE
**Then** AdminService.unbanUser() restores user's login ability
**Then** AdminService.unbanUser() logs unban action
**Then** AdminService.changeUserRole() changes user's role
**Then** AdminService.changeUserRole() validates new role is valid
**Then** AdminService.getAllUsers() returns paginated user list with filters
**Then** AdminService.getAllUsers() supports filtering by: role, status, date range
**Then** AdminService.getAllUsers() supports sorting by: createdAt, email, name
**Then** AdminService.getUserDetails() returns user with: account count, transaction count, total spent, reviews given
**Then** banned users cannot login (checked in JwtAuthenticationFilter or UserDetailsService)
**Then** admin actions are logged for audit

**Technical Notes:**
- Update JwtAuthenticationFilter to check user status
- Create AuditLog entity for admin actions
- Implement reason parameter for ban/unban
- Use @Transactional for state changes

**Requirements:** FR39 (ban), FR40 (unban), admin role management, NFR10 (rate limiting for login)

---

### Story 7.6: Admin Dashboard UI

As a developer,
I want to create the admin dashboard interface,
So that admins can monitor and manage the platform.

**Acceptance Criteria:**

**Given** the Admin API from Story 7.3
**When** I create AdminDashboard component
**Then** AdminDashboard requires authentication and ADMIN role
**Then** AdminDashboard has sidebar navigation with: Dashboard, Accounts, Users, Reports, Settings
**Then** DashboardPage shows statistics cards: Total Users, Total Accounts, Total Transactions, Revenue, Pending Approvals
**Then** DashboardPage shows charts: Revenue trend (line chart), Accounts by Game (pie chart), Transaction Status (bar chart)
**Then** DashboardPage uses Recharts library for visualization
**Then** DashboardPage has date range filter (Last 7 days, 30 days, 90 days, custom)
**Then** DashboardPage shows loading skeleton while fetching stats
**Then** DashboardPage auto-refreshes statistics every 30 seconds
**Then** DashboardPage uses REST API calls via Axios
**Then** AdminDashboard layout is responsive with collapsible sidebar
**Then** AdminDashboard uses Tailwind CSS for styling
**Then** AdminDashboard is protected route with role check

**Technical Notes:**
- Use Recharts for charts (line, pie, bar)
- Use react-select for date range picker
- Implement role check in ProtectedRoute or custom hook
- Use useAdmin hook for admin-specific data fetching
- Use interval or setTimeout for auto-refresh

**Requirements:** FR41 (dashboard), FR42 (revenue reports), NFR1 (< 200ms REST), NFR4 (< 2s load)

---

### Story 7.7: Account Approval Interface

As a developer,
I want to create the admin account approval interface,
So that admins can moderate listings.

**Acceptance Criteria:**

**Given** the admin approval endpoints from Story 7.4
**When** I create the approval interface
**Then** PendingAccountsPage displays table of PENDING accounts
**Then** PendingAccountsPage shows columns: Account ID, Title, Seller, Game, Price, Level, Rank, Created At, Actions
**Then** PendingAccountsPage supports pagination (50 per page)
**Then** PendingAccountsPage has search/filter by game, seller, price range
**Then** PendingAccountsPage has "View" button to see full account details
**Then** PendingAccountsPage has "Approve" button (green checkmark)
**Then** PendingAccountsPage has "Reject" button (red X)
**Then** PendingAccountsPage has "Feature" button (star icon)
**Then** Approve action opens confirmation modal
**Then** Reject action opens modal with reason textarea
**Then** Reject action requires reason to be entered
**Then** Approve/Reject actions show loading state during API call
**Then** Approve/Reject actions remove account from pending list
**Then** AccountDetailModal (admin view) shows all account information
**Then** actions use useMutation with optimistic UI updates
**Then** success toast notifications appear after actions

**Technical Notes:**
- Use TanStack Table or custom table component
- Use react-hook-form for rejection reason validation
- Use react-hot-toast for notifications
- Use Apollo Client's refetchQueries to update lists

**Requirements:** FR35 (view pending), FR36 (approve), FR37 (reject), FR38 (feature), NFR4 (< 2s load)

---

### Story 7.8: User Management UI

As a developer,
I want to create the user management interface for admins,
So that admins can view and moderate users.

**Acceptance Criteria:**

**Given** the user management API from Story 7.5
**When** I create UserManagementPage
**Then** UserManagementPage displays table of all users
**Then** UserManagementPage shows columns: User ID, Email, Name, Role, Status, Balance, Rating, Created At, Actions
**Then** UserManagementPage supports pagination (20 per page)
**Then** UserManagementPage has search/filter by: email, name, role, status
**Then** UserManagementPage has "Ban" button for active users
**Then** UserManagementPage has "Unban" button for banned users
**Then** UserManagementPage has "Change Role" dropdown
**Then** UserManagementPage shows status badge: Active (green), Banned (red), Suspended (yellow)
**Then** BanModal confirms action and requires reason
**Then** BanModal shows "Are you sure?" confirmation
**Then** BanModal shows loading state during API call
**Then** actions remove user from list or update status in place
**Then** UserDetailsModal shows full user profile with: account count, transaction history, reviews given/received
**Then** all actions use REST API via Axios
**Then** all actions show toast notifications

**Technical Notes:**
- Use TanStack Table or custom table component
- Implement role change dropdown with select options
- Use useQuery for fetching users, useMutation for actions
- Use react-hot-toast for notifications

**Requirements:** FR39 (ban), FR40 (unban), user moderation, NFR4 (< 2s load)

---

### Story 7.9: Reports & Export

As a developer,
I want to implement report generation and export functionality,
So that admins can analyze platform data.

**Acceptance Criteria:**

**Given** the admin statistics from Story 7.2
**When** I implement reports and export
**Then** ReportsPage has "Generate Report" section
**Then** ReportsPage has report type selector: Transaction Report, Revenue Report, User Report
**Then** ReportsPage has date range picker
**Then** ReportsPage has "Generate CSV" button
**Then** ReportsPage has "Generate PDF" button
**Then** GET /api/admin/reports/transactions returns CSV file
**Then** GET /api/admin/reports/revenue returns PDF file
**Then** CSV export includes headers matching data fields
**Then** CSV export uses UTF-8 encoding for Vietnamese characters
**Then** PDF export includes: title, date range, data tables, charts
**Then** PDF export has platform branding (logo, name)
**Then** reports include summary statistics at top
**Then** reports are generated server-side to avoid client-side memory issues
**Then** file download is triggered via browser's download mechanism
**Then** ReportsPage shows loading state during generation

**Technical Notes:**
- Use OpenCSV or Apache Commons CSV for CSV generation
- Use iText or Apache PDFBox for PDF generation
- Use Spring's HttpServletResponse with Content-Disposition header
- Set Content-Type: text/csv for CSV, application/pdf for PDF
- Buffer large reports to avoid OOM errors

**Requirements:** FR43 (export reports), FR42 (revenue reports), NFR1 (< 200ms for small reports)

---

### Story 7.10: Platform Health Monitoring

As a developer,
I want to add platform health monitoring for admins,
So that admins can monitor system performance and issues.

**Acceptance Criteria:**

**Given** the Spring Boot Actuator from architecture
**When** I implement health monitoring
**Then** /actuator/health endpoint returns system health status
**Then** health check includes: MySQL (database status), Redis (cache status), Disk Space
**Then** /actuator/metrics endpoint exposes custom metrics
**Then** custom metrics track: active users, transactions per hour, API response times
**Then** /actuator/loggers endpoint allows dynamic log level configuration
**Then** AdminDashboard has "System Health" section
**Then** AdminDashboard shows status indicators: Database (UP/DOWN), Redis (UP/DOWN), Disk Usage (%)
**Then** AdminDashboard shows real-time metrics: Requests/sec, Average Response Time, Error Rate
**Then** AdminDashboard auto-refreshes metrics every 10 seconds
**Then** AdminDashboard shows alerts for health issues
**Then** metrics are displayed using charts and gauges
**Then** health checks are configured in application.yml (health.indicators)

**Technical Notes:**
- Use Spring Boot Actuator
- Use Micrometer for custom metrics
- Use @Timed annotation for method timing
- Implement custom HealthIndicator beans
- Expose actuator endpoints in SecurityConfig

**Requirements:** FR41 (monitoring), NFR24 (uptime monitoring), NFR25 (< 1s p99), NFR31 (Actuator metrics)

---
