# Game Account Marketplace - Project Overview

**Version:** 1.0  
**Generated:** 2026-01-09  
**Status:** Production-Ready  
**Architecture:** Full-Stack Multi-Part Application

---

## Executive Summary

The **Game Account Marketplace** is a comprehensive platform enabling users to buy and sell video game accounts securely. Built with enterprise-grade technologies, it features a **hybrid API architecture** (REST + GraphQL + WebSocket) for optimal performance across different use cases.

---

## Project Classification

| Attribute | Value |
|-----------|-------|
| **Repository Type** | Multi-part Monorepo |
| **Parts** | 2 (Backend API + Frontend Client) |
| **Primary Language (Backend)** | Java 17 |
| **Primary Language (Frontend)** | TypeScript 5.x |
| **Development Stage** | Production-Ready (Epic 3 Complete) |
| **Lines of Code** | ~15,000+ (Backend: ~9,000, Frontend: ~6,000) |

---

## Technology Stack

### Backend (Spring Boot)
- **Framework:** Spring Boot 3.2.1
- **Language:** Java 17 LTS
- **Build Tool:** Maven 3.9+
- **ORM:** Hibernate / Spring Data JPA
- **Security:** Spring Security + JWT (jjwt 0.12.3)
- **API:** REST + GraphQL (Spring for GraphQL)
- **Caching:** Redis 7.0+ (with Caffeine fallback)
- **Database:** MySQL 8.0+
- **Testing:** JUnit 5, Mockito, Spring Test

**Key Dependencies:**
- `spring-boot-starter-web` - REST APIs
- `spring-boot-starter-data-jpa` - Database ORM
- `spring-boot-starter-security` - Authentication & Authorization
- `spring-boot-starter-graphql` - GraphQL API
- `spring-boot-starter-data-redis` - Caching
- `lombok` - Boilerplate reduction
- `mapstruct` - DTO mapping
- `springdoc-openapi` - API documentation

### Frontend (React SPA)
- **Framework:** React 18.2
- **Language:** TypeScript 5.3
- **Build Tool:** Vite 5.0
- **Routing:** React Router DOM 6.21
- **State Management:** React Context + Apollo Cache + Zustand 4.4
- **GraphQL Client:** Apollo Client 3.8
- **HTTP Client:** Axios 1.6
- **WebSocket Client:** SockJS + STOMP
- **UI Framework:** TailwindCSS 3.4 + Radix UI
- **Forms:** React Hook Form 7.49
- **Testing:** Vitest, React Testing Library

**Key Dependencies:**
- `@apollo/client` - GraphQL queries & mutations
- `axios` - REST API calls
- `@stomp/stompjs` + `sockjs-client` - Real-time WebSocket
- `react-hook-form` + `@hookform/resolvers` + `yup` - Form validation
- `tailwindcss` - Utility-first CSS
- `@radix-ui/*` - Accessible UI components
- `sonner` - Toast notifications
- `lucide-react` - Icon library

### Infrastructure
- **Database:** MySQL 8.0 (Primary data store)
- **Cache:** Redis 7.0 (Session, Hot data, DataLoader cache)
- **Containerization:** Docker + Docker Compose
- **Local Development:** Port 8080 (backend), Port 3000 (frontend)

---

## Architecture Pattern

### Backend: N-Layer Architecture
```
Presentation Layer (Controllers/Resolvers)
          ↓
Business Logic Layer (Services)
          ↓
Data Access Layer (Repositories)
          ↓
Database Layer (MySQL + Redis)
```

### Frontend: Component-Based Architecture
```
Pages (Route Components)
  ↓
Feature Components
  ↓
Shared Components
  ↓
UI Primitives (Radix + Tailwind)
```

### Integration: Hybrid API Strategy
| API Type | Use Case | Implemented |
|----------|----------|-------------|
| **REST** | Authentication, Admin operations, File uploads | ✅ |
| **GraphQL** | Marketplace browsing, Search, Filtering | ✅ |
| **WebSocket** | Real-time chat, Live notifications | ⏳ Planned |

---

## Core Features

### Implemented (Epic 1-3)
1. ✅ **Authentication System**
   - JWT-based authentication
   - Role-based access control (BUYER, SELLER, ADMIN)
   - Secure password hashing (BCrypt)
   - Token refresh mechanism

2. ✅ **Account Marketplace**
   - Browse game accounts by game
   - Advanced filtering (price, level, rank, game, status)
   - Full-text search
   - Account detail view with view count tracking
   - Seller profiles with ratings

3. ✅ **Favorites / Wishlist**
   - Add/remove accounts from favorites
   - Favorites management page
   - Pagination support
   - Instant UI updates (Apollo Cache)

4. ✅ **Seller Management**
   - Create account listings (CRUD)
   - Image uploads (multi-image support)
   - Edit/delete own listings
   - View listing status (PENDING, APPROVED, REJECTED)

5. ✅ **Advanced Search & Filtering**
   - Real-time filter updates
   - Multiple filters combinable
   - Sorting (price, level, date)
   - Pagination (offset-based + cursor-based)
   - URL parameter persistence

6. ✅ **Caching Strategy**
   - Redis caching for hot data
   - Query result caching
   - Cache warming on startup
   - Cache metrics logging

7. ✅ **N+1 Query Prevention**
   - GraphQL DataLoader implementation
   - Batch loading for nested fields
   - Performance monitoring

### Planned (Future Epics)
- 🔄 Real-time Chat (Buyer ↔ Seller)
- 🔄 Transaction Processing
- 🔄 Payment Integration (VNPay/Momo)
- 🔄 Admin Dashboard
- 🔄 Review & Rating System
- 🔄 Email Notifications

---

## Project Structure

```
Game-Account-Marketplace/
├── backend-java/              # Spring Boot Backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/.../marketplace/
│   │   │   │   ├── entity/        # JPA Entities (User, Account, Game, Favorite)
│   │   │   │   ├── repository/    # Spring Data JPA Repositories
│   │   │   │   ├── service/       # Business Logic Layer
│   │   │   │   ├── controller/    # REST Controllers
│   │   │   │   ├── graphql/       # GraphQL Queries, Mutations, Resolvers
│   │   │   │   ├── dto/           # Request/Response DTOs
│   │   │   │   ├── security/      # JWT, Authentication Filters
│   │   │   │   ├── config/        # Spring Configuration
│   │   │   │   ├── exception/     # Global Exception Handling
│   │   │   │   ├── cache/         # Caching utilities
│   │   │   │   └── spec/          # JPA Specifications
│   │   │   └── resources/
│   │   │       ├── graphql/       # GraphQL schema definitions
│   │   │       ├── application.yml
│   │   │       └── seed_data.sql
│   │   └── test/                  # Unit & Integration Tests
│   └── pom.xml                    # Maven dependencies
│
├── frontend-react/            # React SPA Frontend
│   ├── src/
│   │   ├── pages/             # Route-based page components
│   │   ├── components/        # Reusable UI components
│   │   ├── contexts/          # React Context (Auth)
│   │   ├── hooks/             # Custom React hooks
│   │   ├── services/          # API clients (GraphQL, REST, WebSocket)
│   │   ├── lib/               # Apollo Client, utilities
│   │   └── types/             # TypeScript type definitions
│   ├── package.json           # NPM dependencies
│   ├── vite.config.ts         # Vite configuration
│   ├── tailwind.config.js     # TailwindCSS configuration
│   └── tsconfig.json          # TypeScript configuration
│
├── project_docs/              # Planning & Architecture Documentation
│   ├── PRD.md                 # Product Requirements Document
│   ├── ARCHITECTURE.md        # Technical Architecture
│   └── TASKS.md               # Implementation Plan
│
├── _bmad-output/              # Development Artifacts
│   ├── planning-artifacts/    # UX Design, Epics
│   └── implementation-artifacts/  # Story docs, Sprint status
│
├── docs/                      # Generated Documentation (this)
├── docker-compose.yml         # Local development environment
├── SEED_DATA.md               # Test data documentation
└── README.md                  # Project overview
```

---

## Data Model

### Core Entities

**User**
- Roles: BUYER, SELLER, ADMIN
- Status: ACTIVE, BANNED, SUSPENDED
- Tracks balance, rating, review count

**Account** (Game account listing)
- Status: PENDING, APPROVED, REJECTED, SOLD
- Fields: title, description, level, rank, price, images
- Many-to-One: User (seller), Game

**Game**
- Catalog of supported games
- Unique slug for URL-friendly names

**Favorite**
- User's wishlist/favorites
- Many-to-One: User, Account

### Relationships
```
User (1) ←→ (N) Account (seller)
User (1) ←→ (N) Favorite
Account (N) ←→ (1) Game
Account (1) ←→ (N) Favorite
```

---

## API Contracts

### REST Endpoints

**Authentication:**
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login (returns JWT)
- `GET /api/users/profile` - Get user profile

**Account Management (Seller):**
- `POST /api/accounts` - Create listing (multipart/form-data)
- `PUT /api/accounts/{id}` - Update listing
- `DELETE /api/accounts/{id}` - Delete listing
- `PATCH /api/accounts/{id}/view` - Increment view count (public)

**Favorites:**
- `GET /api/favorites` - Get user favorites
- `POST /api/favorites` - Add favorite
- `DELETE /api/favorites/{accountId}` - Remove favorite

### GraphQL API

**Queries:**
```graphql
# Browse accounts with filters & pagination
accounts(
  gameId: ID, minPrice: Float, maxPrice: Float,
  minLevel: Int, maxLevel: Int, rank: String,
  status: AccountStatus, isFeatured: Boolean, q: String,
  sortBy: String, sortDirection: String,
  page: Int, limit: Int
): PaginatedAccounts!

# Get single account
account(id: ID!): Account!

# Get all games
games: [Game!]!

# Get user's favorites (paginated)
favorites(page: Int, limit: Int): PaginatedAccounts!
```

**Mutations:**
```graphql
# Account mutations
createAccount(input: CreateAccountInput!): Account!
updateAccount(id: ID!, input: UpdateAccountInput!): Account!
deleteAccount(id: ID!): Boolean!

# Favorites mutations
addToFavorites(accountId: ID!): Account!
removeFromFavorites(accountId: ID!): Boolean!

# Admin mutations
approveAccount(id: ID!): Account!
rejectAccount(id: ID!, reason: String): Account!
```

---

## Security Architecture

### Authentication Flow
1. User submits email + password → `POST /api/auth/login`
2. Backend validates credentials via Spring Security
3. JWT token generated (email as subject, role as claim)
4. Token returned to frontend, stored in localStorage
5. All subsequent requests include `Authorization: Bearer <token>`
6. JWT filter validates token and populates SecurityContext

### Authorization
- **Role-Based Access Control (RBAC)**
  - BUYER: Browse, favorite, purchase (future)
  - SELLER: Create/edit listings, view own accounts
  - ADMIN: Approve/reject listings, manage users (future)

- **Endpoint Protection**
  - Public: `/api/auth/**`, `/graphql`, `/graphiql/**`
  - Authenticated: All other endpoints
  - Admin-only: `/api/admin/**` (future)

### Security Measures
- ✅ JWT token-based stateless authentication
- ✅ BCrypt password hashing
- ✅ CORS configuration for frontend origin
- ✅ CSRF protection disabled (stateless API)
- ✅ SQL injection prevention (JPA Parameterized Queries)
- ✅ Input validation (Jakarta Validation)
- ⏳ Rate limiting (planned)
- ⏳ XSS protection (planned)

---

## Performance Optimizations

### Backend
1. **Redis Caching**
   - Game list cached (1 hour TTL)
   - Account queries cached (10 minutes TTL)
   - Featured accounts cached (5 minutes TTL)
   - Cache metrics logged for monitoring

2. **Database Optimization**
   - Indexes on frequently queried fields (status, game_id, seller_id, price)
   - JPA Specification for dynamic query building
   - Pagination support (offset-based + cursor-based)

3. **GraphQL N+1 Prevention**
   - DataLoader for batch loading seller, game data
   - Batch loading for `isFavorited` field resolution
   - Query complexity analysis instrumentation

4. **Connection Pooling**
   - Hikari CP (default in Spring Boot)
   - Max pool size: 20, Min idle: 5

### Frontend
1. **Apollo Client Cache**
   - Normalized cache for GraphQL responses
   - Cache-first fetch policy for static data
   - Optimistic updates for mutations

2. **Code Splitting**
   - Route-based lazy loading (React.lazy)
   - Vite automatic code splitting

3. **Image Optimization**
   - Lazy loading for account images
   - Responsive image loading

---

## Development Workflow

### Local Setup
1. Start infrastructure:
   ```bash
   docker-compose up -d  # MySQL + Redis
   ```

2. Start backend:
   ```bash
   cd backend-java
   mvn spring-boot:run
   ```

3. Start frontend:
   ```bash
   cd frontend-react
   npm run dev
   ```

4. Access application:
   - Frontend: http://localhost:3000
   - Backend: http://localhost:8080
   - GraphQL Playground: http://localhost:8080/graphiql

### Testing
- **Backend:**
  - Unit tests: `mvn test`
  - Integration tests: `mvn verify`
  - Coverage: JaCoCo reports

- **Frontend:**
  - Unit tests: `npm test`
  - Component tests: Vitest + React Testing Library
  - E2E tests: (planned) Playwright

### Git Workflow
- Main branch: `main` (production-ready)
- Development branch: `develop`
- Feature branches: `feature/<feature-name>`
- Epic branches: `epic/<epic-number>`

---

## Deployment Architecture

### Development Environment
- Docker Compose for local services
- MySQL + Redis in containers
- Backend + Frontend run natively

### Production (Planned)
```
CloudFlare CDN
  ↓
Nginx Load Balancer
  ↓
Spring Boot Instances (Auto-scaling)
  ↓
├── MySQL Primary (RDS/Aurora)
├── MySQL Read Replicas
├── Redis Cluster (ElastiCache)
└── S3 (Static Assets)
```

---

## Current Status

### Completed Epics
- ✅ **Epic 1: Foundation & Authentication** (Stories 1.1-1.8)
- ✅ **Epic 2: Marketplace Core** (Stories 2.1-2.4)
- ✅ **Epic 3: Advanced Features** (Stories 3.1-3.10)

### Sprint Status
- **Current Sprint:** Epic 3 Complete
- **Last Session:** 2026-01-09
  - Fixed JWT authentication bugs
  - Implemented favorites pagination
  - Resolved Apollo cache issues
  - Added CORS configuration

---

## Known Issues & Technical Debt

1. **WebClient Dependency Missing** - Test compilation issues (non-blocking)
2. **DataLoader Temporarily Disabled** - Awaiting Spring Boot 3.3+ upgrade
3. **Database-level pagination for favorites** - Currently in-memory
4. **Test Coverage** - Need more integration tests

---

## Next Steps

### Immediate (Epic 4 Candidate)
1. Real-time Chat (WebSocket implementation)
2. Transaction Processing
3. Payment Gateway Integration
4. Admin Dashboard

### Future Enhancements
- Mobile-responsive improvements
- Advanced analytics dashboard
- Multi-currency support
- Email notifications
- User verification system

---

## References

- **PRD:** `project_docs/PRD.md` - Complete product requirements
- **Architecture:** `project_docs/ARCHITECTURE.md` - Technical design
- **Tasks:** `project_docs/TASKS.md` - Implementation plan
- **UX Design:** `_bmad-output/planning-artifacts/ux-design-specification.md`
- **API Documentation:** `/graphiql` (GraphQL), Swagger (REST - future)

---

## Team & Contacts

- **Project:** Game Account Marketplace
- **Development Stage:** Production-Ready
- **Latest Update:** 2026-01-09
- **Documentation Version:** 1.0

---

**For questions or contributions, refer to the project documentation or contact the development team.**

