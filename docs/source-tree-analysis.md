# Source Tree Analysis - Game Account Marketplace

**Version:** 1.0  
**Generated:** 2026-01-09  
**Project Root:** `C:\Users\Admin\Desktop\GIT CLONE\Game-Account-Marketplace`

---

## Complete Directory Structure

```
Game-Account-Marketplace/
│
├── _bmad/                                    # BMAD Method Rules & Workflows
│   ├── bmm/                                  # Business Method Management
│   │   ├── agents/                           # Agent personas (9 agents)
│   │   ├── workflows/                        # 30+ workflows
│   │   ├── config.yaml                       # BMM configuration
│   │   └── data/                             # Templates & standards
│   └── core/                                 # Core BMAD utilities
│       ├── agents/                           # bmad-master agent
│       ├── tasks/                            # XML workflow tasks
│       └── workflows/                        # Core workflows
│
├── _bmad-output/                             # Generated Artifacts
│   ├── bugs/                                 # Bug reports
│   │   └── 2026-01-08-authentication-bugs.md
│   ├── implementation-artifacts/             # Story Implementation Docs
│   │   ├── 1-1-project-structure-environment-setup.md
│   │   ├── 1-2-backend-spring-boot-skeleton.md
│   │   ├── 1-3-frontend-vite-react-typescript-setup.md
│   │   ├── 1-4-user-entity-repository.md
│   │   ├── 1-5-security-configuration-jwt-implementation.md
│   │   ├── 1-6-authservice-authentication-logic.md
│   │   ├── 1-7-authentication-rest-api-endpoints.md
│   │   ├── 1-8-frontend-authentication-pages-context.md
│   │   ├── 2-1-game-account-entities-repositories.md
│   │   ├── 2-2-accountservice-business-logic.md
│   │   ├── 2-3-graphql-schema-resolvers-accounts.md
│   │   ├── 2-4-rest-controllers-seller-operations.md
│   │   ├── 3-1-advanced-filtering-search-implementation.md
│   │   ├── 3-2-favorites-wishlist-feature.md
│   │   ├── 3-3-favorites-rest-api-graphql-integration.md
│   │   ├── 3-4-account-detail-page-related-data.md
│   │   ├── 3-5-marketplace-homepage-featured-listings.md
│   │   ├── 3-6-advanced-search-filter-ui.md
│   │   ├── 3-7-favorites-management-page.md
│   │   ├── 3-8-redis-caching-strategy-implementation.md
│   │   ├── 3-9-dataloader-n-plus-1-query-prevention.md
│   │   ├── 3-10-pagination-infinite-scroll.md
│   │   ├── DataLoader-Integration-Guide.md
│   │   ├── epic-3-retro-2026-01-09.md
│   │   └── sprint-status.yaml              # Sprint tracking
│   └── planning-artifacts/                 # Planning Documents
│       ├── epics.md                        # Epic definitions
│       ├── implementation-readiness-report-2026-01-06.md
│       └── ux-design-specification.md      # 1,163 lines of UI/UX specs
│
├── backend-java/                            # ⚙️ BACKEND: Spring Boot Application
│   ├── pom.xml                             # Maven dependencies & build config
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/gameaccount/marketplace/
│   │   │   │   │
│   │   │   │   ├── MarketplaceApplication.java   # 🚀 Main entry point (@SpringBootApplication)
│   │   │   │   │
│   │   │   │   ├── cache/                        # Caching Utilities
│   │   │   │   │   ├── CacheMetricsLogger.java   # Cache performance logging
│   │   │   │   │   └── CacheWarmer.java          # Preload cache on startup
│   │   │   │   │
│   │   │   │   ├── config/                       # 🔧 Spring Configuration Classes
│   │   │   │   │   ├── CacheConfig.java          # Redis + Caffeine cache config
│   │   │   │   │   └── SecurityConfig.java       # JWT, CORS, endpoint protection
│   │   │   │   │
│   │   │   │   ├── controller/                   # 🌐 REST API Controllers
│   │   │   │   │   ├── AccountController.java    # PATCH /api/accounts/{id}/view
│   │   │   │   │   ├── FavoriteController.java   # GET/POST/DELETE /api/favorites
│   │   │   │   │   ├── auth/
│   │   │   │   │   │   └── AuthController.java   # POST /api/auth/login, register
│   │   │   │   │   └── user/
│   │   │   │   │       └── UserController.java   # GET /api/users/profile
│   │   │   │   │
│   │   │   │   ├── dto/                          # 📦 Data Transfer Objects
│   │   │   │   │   ├── request/                  # Request DTOs (incoming)
│   │   │   │   │   │   ├── AccountSearchRequest.java
│   │   │   │   │   │   ├── AddFavoriteRequest.java
│   │   │   │   │   │   ├── CreateAccountRequest.java
│   │   │   │   │   │   ├── LoginRequest.java
│   │   │   │   │   │   ├── RegisterRequest.java
│   │   │   │   │   │   ├── UpdateAccountRequest.java
│   │   │   │   │   │   └── UpdateProfileRequest.java
│   │   │   │   │   └── response/                 # Response DTOs (outgoing)
│   │   │   │   │       ├── AccountResponse.java
│   │   │   │   │       ├── AuthResponse.java     # JWT token + user info
│   │   │   │   │       ├── ErrorResponse.java    # Standard error format
│   │   │   │   │       └── UserResponse.java
│   │   │   │   │
│   │   │   │   ├── entity/                       # 🗃️ JPA Entities (Database Models)
│   │   │   │   │   ├── User.java                 # Users table (auth, profile)
│   │   │   │   │   ├── Account.java              # Accounts table (game listings)
│   │   │   │   │   ├── Game.java                 # Games table (catalog)
│   │   │   │   │   └── Favorite.java             # Favorites table (wishlist)
│   │   │   │   │
│   │   │   │   ├── exception/                    # ⚠️ Exception Handling
│   │   │   │   │   ├── BusinessException.java    # 400 Bad Request
│   │   │   │   │   ├── ResourceNotFoundException.java  # 404 Not Found
│   │   │   │   │   └── GlobalExceptionHandler.java     # @RestControllerAdvice
│   │   │   │   │
│   │   │   │   ├── graphql/                      # 🔷 GraphQL Implementation
│   │   │   │   │   │
│   │   │   │   │   ├── batchloader/              # DataLoader (N+1 prevention)
│   │   │   │   │   │   ├── FavoriteBatchLoader.java    # Batch load isFavorited
│   │   │   │   │   │   ├── GameBatchLoader.java        # Batch load games
│   │   │   │   │   │   └── UserBatchLoader.java        # Batch load sellers
│   │   │   │   │   │
│   │   │   │   │   ├── config/                   # GraphQL Configuration
│   │   │   │   │   │   ├── DataLoaderConfig.java       # DataLoader registry
│   │   │   │   │   │   ├── GraphQLConfig.java          # GraphQL setup
│   │   │   │   │   │   └── QueryComplexityInstrumentation.java  # Query cost limits
│   │   │   │   │   │
│   │   │   │   │   ├── dto/                      # GraphQL-specific DTOs
│   │   │   │   │   │   ├── AccountConnection.java      # Relay cursor pagination
│   │   │   │   │   │   ├── AccountEdge.java
│   │   │   │   │   │   ├── PageInfo.java
│   │   │   │   │   │   ├── PaginatedAccountResponse.java  # Offset pagination
│   │   │   │   │   │   ├── CreateAccountInput.java
│   │   │   │   │   │   └── UpdateAccountInput.java
│   │   │   │   │   │
│   │   │   │   │   ├── mutation/                 # GraphQL Mutations
│   │   │   │   │   │   ├── AccountMutation.java   # createAccount, updateAccount, deleteAccount
│   │   │   │   │   │   └── FavoriteMutation.java  # addToFavorites, removeFromFavorites
│   │   │   │   │   │
│   │   │   │   │   ├── query/                    # GraphQL Queries
│   │   │   │   │   │   ├── AccountQuery.java     # accounts, account
│   │   │   │   │   │   ├── FavoriteQuery.java    # favorites (paginated)
│   │   │   │   │   │   └── GameQuery.java        # games, game, gameBySlug
│   │   │   │   │   │
│   │   │   │   │   └── resolver/                 # Field Resolvers
│   │   │   │   │       └── AccountFieldResolver.java  # isFavorited field
│   │   │   │   │
│   │   │   │   ├── repository/                   # 🗄️ Data Access Layer (Spring Data JPA)
│   │   │   │   │   ├── UserRepository.java       # findByEmail, existsByEmail
│   │   │   │   │   ├── AccountRepository.java    # JPA Specification queries
│   │   │   │   │   ├── GameRepository.java       # findBySlug, findAll
│   │   │   │   │   └── FavoriteRepository.java   # findByUserId, existsByUserIdAndAccountId
│   │   │   │   │
│   │   │   │   ├── security/                     # 🔐 Security Components
│   │   │   │   │   ├── CustomUserDetailsService.java  # Load user for authentication
│   │   │   │   │   ├── JwtAuthenticationFilter.java   # Extract & validate JWT
│   │   │   │   │   └── JwtTokenProvider.java          # Generate & parse JWT tokens
│   │   │   │   │
│   │   │   │   ├── service/                      # 💼 Business Logic Layer
│   │   │   │   │   ├── AuthService.java          # login, register, profile management
│   │   │   │   │   ├── AccountService.java       # CRUD, search, filtering (cached)
│   │   │   │   │   ├── FavoriteService.java      # add/remove favorites
│   │   │   │   │   ├── GameService.java          # game catalog management
│   │   │   │   │   └── PaginationService.java    # cursor pagination utilities
│   │   │   │   │
│   │   │   │   ├── spec/                         # JPA Specifications (Dynamic Queries)
│   │   │   │   │   └── AccountSpecification.java # Build dynamic WHERE clauses
│   │   │   │   │
│   │   │   │   └── util/                         # 🛠️ Utility Classes
│   │   │   │       └── CursorUtil.java           # Base64 cursor encoding/decoding
│   │   │   │
│   │   │   └── resources/
│   │   │       ├── graphql/
│   │   │       │   └── schema.graphqls           # 🔷 GraphQL Schema Definition (284 lines)
│   │   │       ├── application.yml               # Spring Boot configuration
│   │   │       └── seed_data.sql                 # Test data (8 games, 9 users, 23 accounts)
│   │   │
│   │   └── test/                                 # 🧪 Unit & Integration Tests
│   │       └── java/.../marketplace/
│   │           ├── controller/                   # Controller tests
│   │           ├── graphql/                      # GraphQL tests
│   │           ├── repository/                   # Repository tests
│   │           └── service/                      # Service tests
│   │
│   └── target/                                   # Build output (not in version control)
│
├── frontend-react/                          # 🎨 FRONTEND: React SPA
│   ├── package.json                         # NPM dependencies
│   ├── vite.config.ts                       # Vite build & dev server config
│   ├── tsconfig.json                        # TypeScript configuration
│   ├── tailwind.config.js                   # TailwindCSS customization
│   ├── postcss.config.js                    # PostCSS plugins
│   ├── components.json                      # shadcn/ui component config
│   ├── eslint.config.js                     # ESLint rules
│   ├── index.html                           # HTML entry point
│   │
│   ├── public/                              # Static assets
│   │   └── vite.svg                         # App icon
│   │
│   ├── dist/                                # Build output (not in version control)
│   │
│   ├── src/
│   │   ├── main.tsx                         # 🚀 React app entry point (Apollo, Auth providers)
│   │   ├── App.tsx                          # Root component with routing
│   │   ├── App.css                          # Global app styles
│   │   ├── index.css                        # TailwindCSS imports
│   │   ├── vite-env.d.ts                    # Vite type declarations
│   │   │
│   │   ├── assets/                          # Static assets (images, icons)
│   │   │   └── react.svg
│   │   │
│   │   ├── components/                      # 🧩 Reusable UI Components
│   │   │   │
│   │   │   ├── account/                     # Account-related components
│   │   │   │   ├── AccountCard.tsx          # Account card in grid view
│   │   │   │   ├── AccountCard.test.tsx
│   │   │   │   ├── AccountCardNew.tsx       # Alternate card design
│   │   │   │   ├── SellerCard.tsx           # Seller info card
│   │   │   │   ├── SellerCard.test.tsx
│   │   │   │   ├── ImageGallery.tsx         # Multi-image viewer
│   │   │   │   └── ImageGallery.test.tsx
│   │   │   │
│   │   │   ├── common/                      # Common utilities
│   │   │   │   ├── ErrorMessage.tsx         # Error display component
│   │   │   │   ├── LoadingSkeleton.tsx      # Loading placeholders
│   │   │   │   ├── ProtectedRoute.tsx       # Auth guard for routes
│   │   │   │   └── SellerRoute.tsx          # Seller role guard
│   │   │   │
│   │   │   ├── favorites/                   # Favorites feature components
│   │   │   │   ├── RemoveFavoriteButton.tsx   # Remove from favorites (with cache update)
│   │   │   │   ├── RemoveFavoriteButton.test.tsx
│   │   │   │   ├── RemoveFavoriteModal.tsx    # Confirmation modal
│   │   │   │   └── RemoveFavoriteModal.test.tsx
│   │   │   │
│   │   │   ├── features/                    # Feature-specific components (empty)
│   │   │   │
│   │   │   ├── layout/                      # Layout components
│   │   │   │   └── AppHeader.tsx            # Main header with nav & auth
│   │   │   │
│   │   │   ├── modals/                      # Modal dialogs
│   │   │   │   └── DeleteAccountModal.tsx   # Delete listing confirmation
│   │   │   │
│   │   │   ├── search/                      # Search & filter components
│   │   │   │   ├── FilterSidebar.tsx        # Advanced filter panel
│   │   │   │   ├── FilterSidebar.test.tsx
│   │   │   │   ├── ActiveFilterChips.tsx    # Display active filters
│   │   │   │   ├── ActiveFilterChips.test.tsx
│   │   │   │   ├── SortDropdown.tsx         # Sort options dropdown
│   │   │   │   ├── SortDropdown.test.tsx
│   │   │   │   └── searchIntegration.test.tsx
│   │   │   │
│   │   │   └── ui/                          # 🎨 UI Primitives (Radix + Tailwind)
│   │   │       ├── avatar.tsx               # User avatar component
│   │   │       ├── badge.tsx                # Badge/tag component
│   │   │       ├── button.tsx               # Button variants
│   │   │       ├── card.tsx                 # Card container
│   │   │       ├── dropdown-menu.tsx        # Dropdown menu
│   │   │       ├── input.tsx                # Text input
│   │   │       ├── separator.tsx            # Divider line
│   │   │       ├── skeleton.tsx             # Loading skeleton
│   │   │       └── sonner.tsx               # Toast notification wrapper
│   │   │
│   │   ├── contexts/                        # ⚛️ React Context (Global State)
│   │   │   └── AuthContext.tsx              # Authentication state & actions
│   │   │
│   │   ├── hooks/                           # 🪝 Custom React Hooks
│   │   │   ├── use-graphql.ts               # GraphQL query hook utilities
│   │   │   ├── useFilters.ts                # Filter state management
│   │   │   └── useFilters.test.ts
│   │   │
│   │   ├── lib/                             # 📚 Libraries & Utilities
│   │   │   ├── apolloClient.ts              # Apollo Client setup (auth, error links)
│   │   │   └── utils.ts                     # clsx, cn utilities
│   │   │
│   │   ├── pages/                           # 📄 Page Components (Routes)
│   │   │   ├── HomePage.tsx                 # / - Marketplace homepage
│   │   │   ├── HomePage.test.tsx
│   │   │   ├── LoginPage.tsx                # /login
│   │   │   ├── RegisterPage.tsx             # /register
│   │   │   ├── ProfilePage.tsx              # /profile
│   │   │   ├── SearchPage.tsx               # /search
│   │   │   ├── SearchPage.test.tsx
│   │   │   ├── FavoritesPage.tsx            # /favorites
│   │   │   ├── FavoritesPage.test.tsx
│   │   │   ├── CreateListingPage.tsx        # /create-listing (seller)
│   │   │   ├── EditListingPage.tsx          # /edit-listing/:id (seller)
│   │   │   ├── MyListingsPage.tsx           # /my-listings (seller)
│   │   │   └── account/
│   │   │       ├── AccountDetailPage.tsx    # /account/:id
│   │   │       └── AccountDetailPage.test.tsx
│   │   │
│   │   ├── services/                        # 🌐 API Clients
│   │   │   ├── graphql/
│   │   │   │   ├── queries.ts               # GraphQL queries (GET_ACCOUNTS, GET_GAMES, etc.)
│   │   │   │   └── mutations.ts             # GraphQL mutations (addToFavorites, etc.)
│   │   │   ├── rest/
│   │   │   │   └── axiosInstance.ts         # Axios setup (auth interceptor)
│   │   │   └── websocket/                   # WebSocket (planned)
│   │   │
│   │   ├── styles/                          # Global styles
│   │   │   └── index.css                    # Additional global CSS
│   │   │
│   │   ├── types/                           # 📘 TypeScript Type Definitions
│   │   │   └── graphql.ts                   # GraphQL types (Account, User, Game, etc.)
│   │   │
│   │   └── utils/                           # Utility functions (empty)
│   │
│   └── node_modules/                        # NPM dependencies (not in version control)
│
├── project_docs/                            # 📑 Planning & Architecture Documentation
│   ├── PRD.md                               # Product Requirements Document (1,983 lines)
│   ├── ARCHITECTURE.md                      # Technical Architecture (2,310 lines)
│   └── TASKS.md                             # Implementation Plan (1,988 lines)
│
├── docs/                                    # 📚 Generated Documentation (this folder)
│   ├── project-scan-report.json             # Workflow state file
│   ├── project-overview.md                  # ✅ Just created
│   └── source-tree-analysis.md              # ✅ This file
│
├── docker-compose.yml                       # 🐳 Docker services (MySQL, Redis)
├── SEED_DATA.md                             # Test data documentation
├── backend.log                              # Backend application logs
├── verify-docker.sh                         # Docker verification script
└── payos_qrpayment_docs.txt                 # Payment integration docs

```

---

## Critical Directories Explained

### Backend Critical Paths

| Directory | Purpose | Key Files | Entry Point |
|-----------|---------|-----------|-------------|
| `entity/` | JPA database models | User, Account, Game, Favorite | Mapped to MySQL tables |
| `repository/` | Data access interfaces | Spring Data JPA repositories | Autowired into services |
| `service/` | Business logic layer | SHARED by REST + GraphQL | Main logic hub |
| `controller/` | REST API endpoints | `@RestController` classes | `@PostMapping`, `@GetMapping` |
| `graphql/` | GraphQL implementation | Queries, Mutations, Resolvers | `/graphql` endpoint |
| `security/` | Authentication & authorization | JWT filter, token provider | Spring Security filter chain |
| `config/` | Spring configuration | Cache, Security, DataLoader | Loaded at startup |

### Frontend Critical Paths

| Directory | Purpose | Key Files | Entry Point |
|-----------|---------|-----------|-------------|
| `pages/` | Route components | HomePage, AccountDetailPage | React Router routes |
| `components/` | Reusable UI | AccountCard, FilterSidebar | Imported into pages |
| `lib/` | Core utilities | apolloClient setup | App initialization |
| `contexts/` | Global state | AuthContext | Wrapped around app |
| `services/` | API clients | GraphQL queries, mutations | Called from hooks |
| `hooks/` | Custom hooks | useFilters, use-graphql | Encapsulate logic |

---

## Integration Points (Frontend ↔ Backend)

### REST API Calls
```
Frontend (Axios)             Backend (Spring REST)
─────────────────            ─────────────────────
POST /api/auth/login    →    AuthController.login()
GET /api/users/profile  →    UserController.getProfile()
POST /api/accounts      →    AccountController.create() (multipart)
DELETE /api/favorites   →    FavoriteController.remove()
```

### GraphQL Queries
```
Frontend (Apollo Client)     Backend (GraphQL)
────────────────────         ────────────────
GET_ACCOUNTS query      →    AccountQuery.accounts()
GET_GAMES query         →    GameQuery.games()
GET_FAVORITES query     →    FavoriteQuery.favorites()
```

### GraphQL Mutations
```
Frontend (useMutation)       Backend (GraphQL)
──────────────────────       ────────────────
addToFavorites          →    FavoriteMutation.addToFavorites()
createAccount           →    AccountMutation.createAccount()
updateAccount           →    AccountMutation.updateAccount()
```

---

## Entry Points

### Backend Entry Points

1. **Main Application**
   - File: `MarketplaceApplication.java`
   - Annotation: `@SpringBootApplication`
   - Port: 8080
   - Starts Spring Boot container

2. **REST API**
   - Base URL: `http://localhost:8080/api`
   - Controllers: `@RestController` classes in `controller/` package
   - Authentication: JWT via `JwtAuthenticationFilter`

3. **GraphQL API**
   - Endpoint: `http://localhost:8080/graphql`
   - Schema: `resources/graphql/schema.graphqls`
   - Playground: `http://localhost:8080/graphiql`

4. **Database**
   - Driver: MySQL JDBC
   - Connection: `application.yml` datasource config
   - ORM: Hibernate (via Spring Data JPA)
   - Schema: Auto-created/updated (`ddl-auto: update`)

### Frontend Entry Points

1. **Main Entry**
   - File: `main.tsx`
   - Mounts React app to DOM
   - Wraps with: ApolloProvider, AuthProvider, BrowserRouter

2. **Root Component**
   - File: `App.tsx`
   - Defines routes: /, /login, /register, /search, /favorites, /account/:id

3. **Development Server**
   - Port: 3000
   - Proxy: `/api`, `/graphql`, `/ws` → `http://localhost:8080`
   - Hot Module Replacement (HMR)

---

## Asset Locations

### Backend Assets
- **GraphQL Schema:** `src/main/resources/graphql/schema.graphqls`
- **Configuration:** `src/main/resources/application.yml`
- **Seed Data:** `src/main/resources/seed_data.sql`
- **Compiled Classes:** `target/classes/`
- **JAR Output:** `target/marketplace-backend-1.0.0.jar` (after `mvn package`)

### Frontend Assets
- **Static Files:** `public/` (vite.svg)
- **Images/Icons:** `src/assets/` (react.svg)
- **Build Output:** `dist/` (after `npm run build`)
- **Bundled JS/CSS:** `dist/assets/` (Vite code-split chunks)

---

## File Organization Patterns

### Backend Naming Conventions
- **Entities:** `{Entity}.java` (User.java, Account.java)
- **Repositories:** `{Entity}Repository.java` (UserRepository.java)
- **Services:** `{Entity}Service.java` (AccountService.java)
- **Controllers:** `{Feature}Controller.java` (AuthController.java)
- **DTOs:** `{Action}{Entity}Request/Response.java` (CreateAccountRequest.java)
- **GraphQL:** `{Entity}Query.java`, `{Entity}Mutation.java`

### Frontend Naming Conventions
- **Pages:** `{Feature}Page.tsx` (HomePage.tsx, LoginPage.tsx)
- **Components:** `{Feature}{Type}.tsx` (AccountCard.tsx, FilterSidebar.tsx)
- **Tests:** `{Component}.test.tsx` (AccountCard.test.tsx)
- **Hooks:** `use{Feature}.ts` (useFilters.ts)
- **Services:** `{type}.ts` in `services/` (queries.ts, mutations.ts)

---

## Build & Output Directories

### Backend
- **Source:** `src/main/java/`
- **Resources:** `src/main/resources/`
- **Test Source:** `src/test/java/`
- **Compiled Classes:** `target/classes/`
- **Test Classes:** `target/test-classes/`
- **JAR:** `target/marketplace-backend-1.0.0.jar`
- **Maven Local Repo:** `~/.m2/repository/`

### Frontend
- **Source:** `src/`
- **Dependencies:** `node_modules/` (13,000+ files)
- **Build Output:** `dist/`
  - `dist/index.html` - Entry HTML
  - `dist/assets/` - Bundled JS/CSS chunks
  - `dist/vite.svg` - Static assets

---

## Test File Locations

### Backend Tests
```
backend-java/src/test/java/.../marketplace/
├── controller/
│   ├── AccountControllerTest.java
│   └── auth/
│       └── AuthControllerTest.java
├── graphql/
│   ├── mutation/
│   │   └── AccountMutationTest.java
│   └── query/
│       └── AccountQueryTest.java
├── repository/
│   ├── AccountRepositoryTest.java
│   └── UserRepositoryTest.java
└── service/
    ├── AccountServiceTest.java
    └── AuthServiceTest.java
```

### Frontend Tests
```
frontend-react/src/
├── components/
│   ├── account/
│   │   ├── AccountCard.test.tsx
│   │   ├── ImageGallery.test.tsx
│   │   └── SellerCard.test.tsx
│   ├── favorites/
│   │   ├── RemoveFavoriteButton.test.tsx
│   │   └── RemoveFavoriteModal.test.tsx
│   └── search/
│       ├── ActiveFilterChips.test.tsx
│       ├── FilterSidebar.test.tsx
│       ├── SortDropdown.test.tsx
│       └── searchIntegration.test.tsx
├── hooks/
│   └── useFilters.test.ts
└── pages/
    ├── HomePage.test.tsx
    ├── FavoritesPage.test.tsx
    ├── SearchPage.test.tsx
    └── account/
        └── AccountDetailPage.test.tsx
```

---

## Configuration File Locations

### Backend Configuration
| File | Purpose |
|------|---------|
| `pom.xml` | Maven dependencies, build config, plugins |
| `application.yml` | Spring Boot app config (database, Redis, JWT) |
| `SecurityConfig.java` | Security rules, CORS, JWT filter |
| `CacheConfig.java` | Redis + Caffeine cache configuration |
| `GraphQLConfig.java` | GraphQL scalar types, instrumentation |
| `DataLoaderConfig.java` | DataLoader registry for N+1 prevention |

### Frontend Configuration
| File | Purpose |
|------|---------|
| `package.json` | NPM dependencies, scripts |
| `vite.config.ts` | Dev server, proxy, build settings |
| `tsconfig.json` | TypeScript compiler options |
| `tailwind.config.js` | TailwindCSS theme customization |
| `postcss.config.js` | PostCSS plugins (Tailwind, Autoprefixer) |
| `eslint.config.js` | Linting rules |
| `components.json` | shadcn/ui component configuration |

---

## Excluded from Version Control

**Backend:**
- `target/` - Maven build output
- `*.log` - Log files
- `.idea/` - IntelliJ IDEA settings
- `*.iml` - IntelliJ module files

**Frontend:**
- `node_modules/` - NPM dependencies (13,000+ files)
- `dist/` - Vite build output
- `.vite/` - Vite cache

**General:**
- `.env` - Environment variables
- `.DS_Store` - macOS metadata
- `*.class` - Compiled Java classes

---

## Documentation & Artifacts

### Planning Documentation
- `project_docs/PRD.md` - Product requirements
- `project_docs/ARCHITECTURE.md` - Technical architecture
- `project_docs/TASKS.md` - Implementation plan

### Implementation Artifacts
- `_bmad-output/implementation-artifacts/` - 23 story implementation docs
- `_bmad-output/planning-artifacts/` - UX specs, epics
- `_bmad-output/bugs/` - Bug reports

### Generated Documentation
- `docs/` - This documentation folder
- `SEED_DATA.md` - Test data documentation
- `docs/CHANGELOG-2026-01-09.md` - Latest session changes

---

## Summary Statistics

| Metric | Backend | Frontend | Total |
|--------|---------|----------|-------|
| **Source Files** | 59 Java | 50+ TypeScript | 109+ |
| **Test Files** | 21 Java | 15+ TypeScript | 36+ |
| **Configuration Files** | 6 | 7 | 13 |
| **Documentation Files** | 23 stories | - | 23 |
| **Lines of Code (est.)** | ~9,000 | ~6,000 | ~15,000 |

---

**End of Source Tree Analysis**  
**For detailed API documentation, see `api-documentation.md`** (to be generated)  
**For architecture details, see `architecture-backend.md` and `architecture-frontend.md`** (to be generated)

