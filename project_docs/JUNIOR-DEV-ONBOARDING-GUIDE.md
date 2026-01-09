# 🎓 Junior Developer Onboarding & Management Guide
## Game Account Marketplace - Epic 1-3 Implementation

---

## 📋 Table of Contents

1. [Project Overview](#project-overview)
2. [Tooling Setup](#tooling-setup)
3. [Trello Board Structure](#trello-board-structure)
4. [Git Workflow & Branching Strategy](#git-workflow--branching-strategy)
5. [Code Review Process](#code-review-process)
6. [Task Breakdown for Epic 1-3](#task-breakdown-for-epic-1-3)
7. [Daily Workflow](#daily-workflow)
8. [Best Practices for Juniors](#best-practices-for-juniors)
9. [Common Pitfalls & Solutions](#common-pitfalls--solutions)
10. [Learning Resources](#learning-resources)

---

## 1. Project Overview

**Project:** Game Account Marketplace  
**Tech Stack:**
- **Backend:** Java 21 + Spring Boot 3.x + GraphQL + REST API
- **Frontend:** React 18 + TypeScript + Vite + Tailwind CSS
- **Database:** MySQL 8.0
- **Cache:** Redis 7.0
- **Build Tools:** Maven (Backend), npm (Frontend)

**Architecture:** Monorepo with N-Layer architecture
```
game-account-marketplace/
├── backend-java/          # Spring Boot application
├── frontend-react/        # React + Vite application
├── docker-compose.yml     # MySQL + Redis containers
└── project_docs/          # Documentation
```

**Completed Epics (Reference for Future Work):**
- ✅ Epic 1: User Authentication & Identity (8 stories)
- ✅ Epic 2: Account Listing Management (6 stories)
- ✅ Epic 3: Marketplace Discovery (10 stories)

**Current Phase:** Epic 1-3 completed, Epic 4+ in planning

---

## 2. Tooling Setup

### 🎯 2.1 Trello (Task Management)

**Purpose:** Visual kanban board for tracking all development tasks

**Setup Steps:**
1. Create a new Trello workspace: "Game Account Marketplace"
2. Create one board per epic (e.g., "Epic 1: Authentication")
3. For team-wide view, create "Master Sprint Board"

**Required Trello Power-Ups:**
- **Custom Fields:** Add story points, priority, blocked status
- **Card Aging:** Highlight stale cards
- **GitHub Integration:** Link PRs to cards
- **Calendar:** Visualize deadlines

---

### 💬 2.2 Discord/Slack (Communication)

**Channel Structure:**

```
📢 #announcements          → Lead-only important updates
💬 #general                → Team discussions, casual chat
🔧 #tech-discuss           → Code questions, architecture decisions
📝 #daily-standup          → Daily progress updates (async)
🐛 #bug-reports            → Bug tracking and triage
🎉 #wins                   → Celebrate completed features
🔗 #git-notifications      → GitHub webhook (commits, PRs)
📚 #resources              → Learning materials, docs
```

**Daily Standup Format (in #daily-standup):**
```markdown
**Date:** 2026-01-09
**Name:** @JuniorDev1

✅ **Yesterday:**
- Completed Story 1.4 (User Entity & Repository)
- Fixed validation bug in RegisterRequest

⏳ **Today:**
- Starting Story 1.5 (JWT Implementation)
- Code review for @JuniorDev2's PR

🚧 **Blockers:**
- Need clarification on JWT expiration time (should it be 24h or 7 days?)
```

---

### 🔧 2.3 Git (Version Control)

**Repository Setup:**
```bash
# Clone repository
git clone https://github.com/your-team/game-account-marketplace.git
cd game-account-marketplace

# Set up Git config
git config user.name "Your Name"
git config user.email "your.email@example.com"

# Install Git hooks (if available)
chmod +x .git/hooks/*
```

**Required Branch Protection Rules (on GitHub):**
- ❌ Direct commits to `main` and `develop` disabled
- ✅ Require pull request reviews (min 1 approval)
- ✅ Require status checks to pass (build, tests)
- ✅ Require branches to be up to date before merging
- ✅ Require linear history (no merge commits, use rebase)

---

## 3. Trello Board Structure

### 📊 Master Sprint Board Layout

This is the primary board that the Lead uses to track the entire sprint.

**Columns (Kanban Flow):**

```
┌──────────────────┬──────────────────┬──────────────┬──────────────┬──────────────┬──────────────┬──────────────┐
│   📦 Backlog     │ 📋 Sprint        │  ✅ To Do    │ 🏃 In        │  👀 Code     │  🧪 Testing  │  ✅ Done     │
│                  │    Backlog       │              │   Progress   │    Review    │              │              │
│  All future      │ This sprint's    │ Ready to     │ Actively     │ PR created,  │ Merged to    │ Completed    │
│  stories         │ committed tasks  │ start        │ coding       │ needs review │ dev, testing │ & deployed   │
└──────────────────┴──────────────────┴──────────────┴──────────────┴──────────────┴──────────────┴──────────────┘
```

**Column Rules:**

| Column | Who Moves Cards Here | Max Cards per Person | Exit Criteria |
|--------|----------------------|----------------------|---------------|
| **Backlog** | Lead/PM | N/A | Story has clear acceptance criteria |
| **Sprint Backlog** | Lead (during Sprint Planning) | N/A | Story selected for current sprint |
| **To Do** | Lead assigns, Dev can self-assign | N/A | Dev has read story and confirmed understanding |
| **In Progress** | Dev | **Max 2 per person** | Dev starts coding on feature branch |
| **Code Review** | Dev (after PR created) | N/A | PR created, all CI checks pass, self-review done |
| **Testing** | Lead/Reviewer (after merge) | N/A | PR approved & merged to `develop` |
| **Done** | Lead/Tester | N/A | Feature tested in staging, no bugs found |

---

### 🎴 Trello Card Template

Every task card must follow this structure:

**Card Title Format:**
```
[Epic-Story] Short Task Description
Example: [1.4] User Entity & Repository
```

**Card Description Template:**
```markdown
## 📝 Story: User Entity & Repository

### 🎯 Goal
Create the User JPA entity with UserRepository so user data can be persisted.

### 🔗 Epic Reference
- **Epic:** 1 - User Authentication & Identity
- **Story Number:** 1.4
- **Dependencies:** Story 1.2 (Spring Boot Skeleton) ✅

### ✅ Acceptance Criteria (Copy-paste from epic doc)
- [ ] User entity has fields: id, email, password, fullName, avatar, role, status, balance, rating, totalReviews, createdAt, updatedAt
- [ ] User entity has JPA annotations: @Entity, @Table, @Id, @GeneratedValue
- [ ] User entity uses Lombok: @Getter, @Setter, @Builder
- [ ] UserRepository extends JpaRepository<User, Long>
- [ ] UserRepository has methods: findByEmail(), existsByEmail()
- [ ] Application starts successfully and MySQL `users` table is created

### 🛠️ Technical Notes
- Password field must support BCrypt (60+ chars)
- Use @CreatedDate for createdAt, @LastModifiedDate for updatedAt
- Role enum: BUYER, SELLER, ADMIN
- Status enum: ACTIVE, BANNED, SUSPENDED

### 📌 PR Requirements
- [ ] All tests pass (`mvn test`)
- [ ] No linter errors
- [ ] Code follows N-Layer architecture
- [ ] PR description references this card

### 🔗 Related Links
- [Epic 1 Document](_bmad-output/planning-artifacts/epics.md#story-14)
- [Architecture Doc](project_docs/ARCHITECTURE.md)
- [Entity Template](docs/development-guide.md#entity-pattern)

### 📊 Metadata
- **Story Points:** 3
- **Priority:** High
- **Assigned To:** @JuniorDev1
- **Deadline:** Wednesday 17:00
```

**Card Labels:**
- 🟢 `frontend` - React/TypeScript work
- 🔵 `backend` - Java/Spring Boot work
- 🟣 `graphql` - GraphQL schema or resolvers
- 🟠 `database` - Database migrations or entities
- 🔴 `blocked` - Cannot proceed (waiting on something)
- ⚪ `needs-review` - Ready for code review
- 🟡 `bug` - Bug fix task

---

### 📈 Epic-Specific Boards (Optional, for Large Teams)

For teams with 4+ developers, create separate boards per epic:

**Example: "Epic 1 - Authentication Board"**
```
Columns: Backlog → To Do → In Progress → Code Review → Done
Cards: Story 1.1, 1.2, 1.3, ..., 1.8
```

This allows each pair of juniors to focus on one epic without overwhelming them.

---

## 4. Git Workflow & Branching Strategy

### 🌳 4.1 Branching Model (Simplified Git Flow)

```
main (production-ready code)
  ↓
develop (integration branch)
  ↓
feature/1.4-user-entity (story branches)
  ↓
fix/1.4-fix-email-validation (hotfix branches)
```

**Branch Types:**

| Branch Type | Naming Convention | Created From | Merges Into | Who Can Create |
|-------------|-------------------|--------------|-------------|----------------|
| `main` | `main` | N/A | N/A | Lead only |
| `develop` | `develop` | `main` | `main` | Lead only |
| `feature/` | `feature/<epic>.<story>-<description>` | `develop` | `develop` | Any Dev |
| `fix/` | `fix/<epic>.<story>-<bug-description>` | `develop` | `develop` | Any Dev |
| `hotfix/` | `hotfix/<critical-bug>` | `main` | `main` + `develop` | Lead only |

**Example Branch Names:**
```bash
feature/1.4-user-entity-repository
feature/1.5-jwt-authentication
feature/2.3-graphql-schema
fix/1.4-email-validation-bug
```

---

### 🚀 4.2 Daily Git Workflow for Juniors

**Step 1: Start a new story**
```bash
# Always start from latest develop
git checkout develop
git pull origin develop

# Create feature branch
git checkout -b feature/1.4-user-entity-repository

# Verify branch
git branch
```

**Step 2: Work on the story**
```bash
# Make changes to files
# backend-java/src/main/java/com/gameaccount/marketplace/entity/User.java

# Check what changed
git status
git diff

# Stage changes
git add backend-java/src/main/java/com/gameaccount/marketplace/entity/User.java
git add backend-java/src/main/java/com/gameaccount/marketplace/repository/UserRepository.java

# Commit with descriptive message
git commit -m "[1.4] Add User entity with JPA annotations and UserRepository"

# Push to remote (first time)
git push -u origin feature/1.4-user-entity-repository

# Push subsequent commits
git push
```

**Step 3: Keep branch updated (if working for multiple days)**
```bash
# Get latest changes from develop
git checkout develop
git pull origin develop

# Go back to your feature branch
git checkout feature/1.4-user-entity-repository

# Rebase your changes on top of develop
git rebase develop

# If conflicts occur, resolve them, then:
git add .
git rebase --continue

# Force push (since rebase rewrites history)
git push --force-with-lease
```

**Step 4: Create Pull Request**
```bash
# Before creating PR, ensure code is clean
mvn clean test          # For backend
npm run lint            # For frontend
npm run build           # For frontend

# If all pass, push final changes
git push

# Then create PR on GitHub (see section 5)
```

---

### 📝 4.3 Commit Message Standards

**Format:**
```
[Story-ID] Short summary (max 50 chars)

- Detailed explanation of what changed (if needed)
- Why the change was necessary
- Reference to issue/ticket

Example:
[1.4] Add User entity with JPA annotations

- Created User.java with all required fields
- Added UserRepository with findByEmail() method
- Configured Lombok and JPA relationships
- Fixes issue with email uniqueness validation
```

**Commit Message Types:**
- `[1.4] Add` - New feature
- `[1.4] Fix` - Bug fix
- `[1.4] Refactor` - Code restructure (no behavior change)
- `[1.4] Update` - Modify existing feature
- `[1.4] Remove` - Delete code
- `[1.4] Docs` - Documentation only

**❌ Bad Commit Messages:**
```
"fix bug"
"update"
"asdf"
"WIP"
"change things"
```

**✅ Good Commit Messages:**
```
"[1.4] Add User entity with JPA annotations and Lombok"
"[1.5] Implement JWT token generation in JwtTokenProvider"
"[2.3] Fix N+1 query issue in GraphQL account resolver"
"[3.1] Add Redis caching to AccountService.searchAccounts()"
```

---

## 5. Code Review Process

### 👀 5.1 Creating a Pull Request (PR)

**When to create PR:**
- ✅ All acceptance criteria completed
- ✅ Code compiles without errors
- ✅ All tests pass (`mvn test` or `npm test`)
- ✅ No linter warnings
- ✅ You've self-reviewed the diff

**PR Template (on GitHub):**
```markdown
## [1.4] User Entity & Repository

### 📋 Story Reference
- **Trello Card:** [Link to Trello card]
- **Epic:** 1 - User Authentication & Identity
- **Story:** 1.4 - User Entity & Repository

### 🎯 What Changed?
Created User JPA entity with:
- All required fields (id, email, password, fullName, role, status, etc.)
- JPA annotations for database mapping
- Lombok annotations for boilerplate reduction
- UserRepository with custom query methods

### ✅ Acceptance Criteria Checklist
- [x] User entity has all required fields
- [x] JPA annotations configured (@Entity, @Table, @Id, @GeneratedValue)
- [x] Lombok annotations applied (@Getter, @Setter, @Builder)
- [x] UserRepository extends JpaRepository
- [x] Custom methods: findByEmail(), existsByEmail()
- [x] Application starts successfully
- [x] MySQL `users` table created automatically

### 🧪 Testing Done
- [x] mvn clean compile - SUCCESS
- [x] mvn test - All tests pass
- [x] Application starts without errors
- [x] MySQL table created with correct schema
- [x] Manual test: UserRepository.save() works

### 📸 Screenshots (if applicable)
![Database Schema](screenshots/users-table-schema.png)

### 🔍 Self-Review Checklist
- [x] Code follows N-Layer architecture
- [x] No hardcoded values (used application.yml)
- [x] No System.out.println (used SLF4J logger)
- [x] Followed Java naming conventions
- [x] Added JavaDoc comments for public methods

### 🤔 Questions for Reviewer
- Is the password field size (255) sufficient for BCrypt hashes?
- Should `rating` be Float or Double?

### 📦 Related PRs
- Depends on: #42 (Spring Boot Skeleton)
- Blocks: #44 (JWT Implementation)
```

---

### 🔍 5.2 Code Review Checklist (for Lead/Reviewers)

**Level 1: Automated Checks (CI/CD)**
- [ ] Build succeeds
- [ ] All tests pass
- [ ] No linter errors
- [ ] Code coverage meets threshold (>80%)

**Level 2: Code Quality (Manual Review)**

**Architecture & Design:**
- [ ] Follows N-Layer architecture (Controller → Service → Repository)
- [ ] No business logic in controllers (thin controllers)
- [ ] Service methods are transactional where needed
- [ ] DTOs used for request/response (not entities)
- [ ] Proper use of dependency injection

**Code Standards:**
- [ ] Variable/method names are descriptive (no `temp`, `data`, `x`)
- [ ] No magic numbers (use constants or config)
- [ ] No commented-out code
- [ ] Error handling is comprehensive (try-catch, custom exceptions)
- [ ] Logging is appropriate (INFO for important events, DEBUG for details)

**Security:**
- [ ] No hardcoded passwords or secrets
- [ ] SQL queries use parameterized statements (no string concatenation)
- [ ] User input is validated
- [ ] Authentication/authorization checks are present

**Performance:**
- [ ] No N+1 query issues
- [ ] Appropriate use of caching
- [ ] Database indexes defined where needed
- [ ] No unnecessary API calls in loops

**Testing:**
- [ ] Unit tests cover critical logic
- [ ] Test names are descriptive
- [ ] Edge cases are tested (null, empty, boundary values)

**Documentation:**
- [ ] Complex logic has comments explaining "why"
- [ ] Public APIs have JavaDoc
- [ ] README updated if needed

---

### 💬 5.3 Giving Feedback (for Lead)

**Feedback Guidelines:**

✅ **DO:**
- Be specific: "Line 45: Move this validation to the Service layer"
- Explain the "why": "This violates SRP because the controller is doing business logic"
- Suggest solutions: "Consider extracting this to a separate method"
- Praise good code: "Great use of Optional<> here!"
- Ask questions: "Could we use a Set instead of List here for better performance?"

❌ **DON'T:**
- Be vague: "This looks wrong"
- Be dismissive: "Why did you do it this way?"
- Fix it yourself without explanation
- Approve without reading

**Feedback Tone Examples:**

**❌ Bad:**
```
This is wrong. Fix it.
```

**✅ Good:**
```
On line 45, we're directly returning the User entity to the frontend. 
This exposes the password field. Could you create a UserResponse DTO 
and use MapStruct to map it? See AccountController.java for an example.
```

---

### ✅ 5.4 Approval & Merge Process

**Approval Levels:**

| Reviewer | Can Approve | Can Merge | Typical Stories |
|----------|-------------|-----------|-----------------|
| Lead | ✅ Yes | ✅ Yes | All stories |
| Senior Dev | ✅ Yes | ⚠️ Lead approval required first | Complex stories |
| Junior Dev | ⚠️ Can comment | ❌ No | Peer review only |

**Merge Requirements:**
1. ✅ At least 1 approval from Lead or Senior
2. ✅ All CI checks pass
3. ✅ All review comments resolved or acknowledged
4. ✅ Branch is up to date with `develop`

**Merge Strategy:**
```bash
# Use "Squash and Merge" for feature branches
# This keeps develop history clean

# GitHub will automatically:
# 1. Squash all commits into one
# 2. Use PR title as commit message
# 3. Delete feature branch after merge
```

**After Merge:**
```bash
# Junior dev should:
1. Move Trello card to "Testing" column
2. Update #git-notifications in Discord
3. Delete local branch:
   git checkout develop
   git pull origin develop
   git branch -d feature/1.4-user-entity-repository
4. Notify tester/QA in #general
```

---

## 6. Task Breakdown for Epic 1-3

### 📦 Epic 1: User Authentication & Identity

**Overview:** Foundation for the entire application. Users can register, login, and manage profiles.

**Total Stories:** 8  
**Estimated Duration:** 2-3 weeks (for 4 juniors)  
**Dependencies:** None

---

#### **Week 1: Infrastructure Setup**

**Story 1.1: Project Structure & Environment Setup**
- **Who:** Junior Dev 1 (strongest dev, as this blocks everyone)
- **Duration:** 2 days
- **Trello Tasks:**
  1. Create Git repository with proper `.gitignore`
  2. Set up monorepo structure (`backend-java/`, `frontend-react/`)
  3. Create `docker-compose.yml` with MySQL 8.0 and Redis 7.0
  4. Test Docker containers start successfully
  5. Document setup in `README.md`

**Story 1.2: Backend Spring Boot Skeleton**
- **Who:** Junior Dev 1 (continues from 1.1)
- **Duration:** 2 days
- **Trello Tasks:**
  1. Create `pom.xml` with all dependencies
  2. Configure `application.yml` (datasource, JPA, Redis, port)
  3. Create folder structure: config/, controller/, service/, repository/, entity/, dto/
  4. Create `MarketplaceApplication.java` main class
  5. Test: `mvn clean install` succeeds

**Story 1.3: Frontend Vite + React + TypeScript Setup**
- **Who:** Junior Dev 2 (can work parallel to 1.2)
- **Duration:** 2 days
- **Trello Tasks:**
  1. Initialize Vite project with React + TypeScript template
  2. Install dependencies: Apollo Client, Axios, React Router, Tailwind CSS
  3. Configure `vite.config.ts` with proxy to backend
  4. Configure `tailwind.config.js`
  5. Create folder structure: components/, pages/, services/, hooks/, contexts/, types/
  6. Test: `npm run dev` starts on port 3000

**Story 1.4: User Entity & Repository**
- **Who:** Junior Dev 3 (starts after 1.2 done)
- **Duration:** 1 day
- **Trello Tasks:**
  1. Create `User.java` entity with all fields (id, email, password, role, status, etc.)
  2. Add JPA annotations (@Entity, @Table, @Id, @GeneratedValue, @Column)
  3. Add Lombok annotations (@Getter, @Setter, @Builder, @NoArgsConstructor, @AllArgsConstructor)
  4. Create `UserRepository.java` extending JpaRepository
  5. Add custom query methods: findByEmail(), existsByEmail()
  6. Test: Start application, verify `users` table created in MySQL

---

#### **Week 2: Authentication Logic**

**Story 1.5: Security Configuration & JWT Implementation**
- **Who:** Junior Dev 1 (Lead should pair with junior on this - critical security feature)
- **Duration:** 2 days
- **Trello Tasks:**
  1. Create `SecurityConfig.java` with Spring Security configuration
  2. Implement `JwtTokenProvider.java` (generateToken, validateToken, extractEmail)
  3. Implement `JwtAuthenticationFilter.java` to extract and validate JWT
  4. Configure BCrypt password encoder bean
  5. Create `CustomUserDetailsService.java` to load users
  6. Test: Generate JWT token manually and validate it

**Story 1.6: AuthService & Authentication Logic**
- **Who:** Junior Dev 3 (continues from 1.4)
- **Duration:** 2 days
- **Trello Tasks:**
  1. Create `AuthService.java` with @Service annotation
  2. Implement `register()` method (validate email uniqueness, hash password, save user, generate JWT)
  3. Implement `login()` method (authenticate, generate JWT)
  4. Implement `getProfile()` method
  5. Implement `updateProfile()` method
  6. Create custom exceptions: BusinessException, ResourceNotFoundException
  7. Test: Write unit tests for all methods

**Story 1.7: Authentication REST API Endpoints**
- **Who:** Junior Dev 4 (can work parallel to 1.6, starts after 1.5)
- **Duration:** 2 days
- **Trello Tasks:**
  1. Create DTOs: RegisterRequest, LoginRequest, AuthResponse, UserResponse
  2. Create `AuthController.java` with @RestController
  3. Implement POST /api/auth/register endpoint
  4. Implement POST /api/auth/login endpoint
  5. Implement GET /api/auth/me endpoint
  6. Add validation annotations (@Valid, @NotNull, @Email)
  7. Test: Use Postman to test all endpoints

**Story 1.8: Frontend Authentication Pages & Context**
- **Who:** Junior Dev 2 (continues from 1.3)
- **Duration:** 3 days
- **Trello Tasks:**
  1. Create `AuthContext.tsx` with login(), logout(), user state
  2. Create `useAuth.tsx` hook
  3. Create `LoginPage.tsx` with form (email, password)
  4. Create `RegisterPage.tsx` with form (email, password, fullName)
  5. Create `ProtectedRoute.tsx` component
  6. Configure React Router with routes
  7. Create Axios interceptor to attach JWT token
  8. Test: Full flow - register → login → protected page

---

### 📦 Epic 2: Account Listing Management

**Overview:** Sellers can create, edit, and manage game account listings.

**Total Stories:** 6  
**Estimated Duration:** 2 weeks (for 4 juniors)  
**Dependencies:** Epic 1 completed

---

#### **Week 3: Backend Listing Management**

**Story 2.1: Game & Account Entities with Repositories**
- **Who:** Junior Dev 1 + Junior Dev 3 (pair programming recommended)
- **Duration:** 2 days
- **Trello Tasks:**
  1. Create `Game.java` entity (id, name, slug, description, iconUrl, accountCount)
  2. Create `GameRepository.java` with custom methods
  3. Create `Account.java` entity (id, seller, game, title, description, level, rank, price, status, etc.)
  4. Add @ManyToOne relationships (seller → User, game → Game)
  5. Add @ElementCollection for images list
  6. Create `AccountRepository.java` with custom queries
  7. Create seed data script (insert 5-10 games)
  8. Test: Verify tables created, foreign keys set

**Story 2.2: AccountService Business Logic**
- **Who:** Junior Dev 1 (continues from 2.1)
- **Duration:** 2 days
- **Trello Tasks:**
  1. Create `AccountService.java` with @Service
  2. Implement createAccount() with validation
  3. Implement updateAccount() with ownership check
  4. Implement deleteAccount() with authorization
  5. Implement approveAccount() with @PreAuthorize("ADMIN")
  6. Implement rejectAccount() with reason
  7. Implement searchAccounts() with filtering
  8. Add @Cacheable for searchAccounts()
  9. Test: Unit tests for all methods

---

#### **Week 4: GraphQL & Frontend**

**Story 2.3: GraphQL Schema & Resolvers for Accounts**
- **Who:** Junior Dev 4 (Lead should review schema carefully)
- **Duration:** 3 days
- **Trello Tasks:**
  1. Create `schema.graphqls` file in resources/graphql/
  2. Define types: Account, Game, User, AccountStatus enum
  3. Define inputs: CreateAccountInput, UpdateAccountInput
  4. Define Query: accounts(), account(), games()
  5. Define Mutation: createAccount(), updateAccount(), deleteAccount()
  6. Create `AccountQuery.java` with @QueryMapping
  7. Create `AccountMutation.java` with @MutationMapping
  8. Configure DataLoader for N+1 prevention
  9. Test: Use GraphiQL to test all queries/mutations

**Story 2.4: REST Controllers for Seller Operations**
- **Who:** Junior Dev 3 (parallel to 2.3)
- **Duration:** 2 days
- **Trello Tasks:**
  1. Create DTOs: CreateAccountRequest, UpdateAccountRequest, AccountResponse
  2. Create `AccountController.java` for sellers
  3. Implement POST /api/accounts (with multipart file upload)
  4. Implement PUT /api/accounts/{id} with ownership check
  5. Implement DELETE /api/accounts/{id}
  6. Implement GET /api/seller/my-accounts
  7. Add file validation (type, size)
  8. Test: Postman with file upload

**Story 2.5: Frontend GraphQL Queries & Mutations**
- **Who:** Junior Dev 2 (starts after 2.3)
- **Duration:** 1 day
- **Trello Tasks:**
  1. Create `services/graphql/queries.ts`
  2. Define GET_ACCOUNTS query with variables
  3. Define GET_ACCOUNT query
  4. Define GET_GAMES query
  5. Create `services/graphql/mutations.ts`
  6. Define CREATE_ACCOUNT mutation
  7. Define UPDATE_ACCOUNT mutation
  8. Define DELETE_ACCOUNT mutation
  9. Configure Apollo Client with auth link
  10. Test: Console.log query results

**Story 2.6: Seller Account Listing Pages**
- **Who:** Junior Dev 2 (continues from 2.5)
- **Duration:** 3 days
- **Trello Tasks:**
  1. Create `CreateListingPage.tsx` with form
  2. Add react-hook-form for validation
  3. Add react-dropzone for image upload
  4. Create `EditListingPage.tsx` pre-filled with data
  5. Create `MyListingsPage.tsx` with grid layout
  6. Create `AccountCard.tsx` component
  7. Add status badges (PENDING, APPROVED, REJECTED, SOLD)
  8. Add edit/delete buttons
  9. Create `DeleteAccountModal.tsx` for confirmation
  10. Test: Full CRUD flow

---

### 📦 Epic 3: Marketplace Discovery

**Overview:** Buyers can browse, search, filter, and favorite accounts.

**Total Stories:** 10  
**Estimated Duration:** 3 weeks (for 4 juniors)  
**Dependencies:** Epic 1, Epic 2 completed

---

#### **Week 5: Advanced Backend Features**

**Story 3.1: Advanced Filtering & Search Implementation**
- **Who:** Junior Dev 1 + Junior Dev 3 (pair programming for complex queries)
- **Duration:** 3 days
- **Trello Tasks:**
  1. Enhance `AccountService.searchAccounts()` with JPA Specifications
  2. Add filters: gameId, minPrice, maxPrice, minLevel, maxLevel, rank, isFeatured
  3. Add full-text search on title and description
  4. Add sorting: price ASC/DESC, level DESC, createdAt DESC
  5. Add pagination (page, limit)
  6. Return Page<Account> with total count
  7. Add database indexes (game_id, seller_id, status, price, level, created_at)
  8. Update @Cacheable to include all filter params in key
  9. Test: Performance test with 1000+ accounts

**Story 3.2: Favorites/Wishlist Feature**
- **Who:** Junior Dev 4
- **Duration:** 2 days
- **Trello Tasks:**
  1. Create `Favorite.java` entity (id, user, account, createdAt)
  2. Add @Table(uniqueConstraints) to prevent duplicates
  3. Create `FavoriteRepository.java`
  4. Create `FavoriteService.java`
  5. Implement addToFavorites() with duplicate check
  6. Implement removeFromFavorites()
  7. Implement getUserFavorites()
  8. Test: Unit tests for all methods

**Story 3.3: Favorites REST API & GraphQL Integration**
- **Who:** Junior Dev 4 (continues from 3.2)
- **Duration:** 2 days
- **Trello Tasks:**
  1. Create `FavoriteController.java`
  2. Implement POST /api/favorites
  3. Implement GET /api/favorites
  4. Implement DELETE /api/favorites/{accountId}
  5. Add GraphQL Query: favorites()
  6. Add GraphQL Mutation: addToFavorites(), removeFromFavorites()
  7. Add `isFavorited` computed field to Account type
  8. Configure DataLoader for favorites
  9. Test: Postman + GraphiQL

---

#### **Week 6-7: Frontend Discovery Features**

**Story 3.4: Account Detail Page with Related Data**
- **Who:** Junior Dev 2
- **Duration:** 2 days
- **Trello Tasks:**
  1. Create `AccountDetailPage.tsx`
  2. Use GET_ACCOUNT query with useQuery hook
  3. Display image gallery with thumbnails
  4. Display title, price, level, rank in header
  5. Display description with markdown support
  6. Display seller card (avatar, name, rating)
  7. Add "Chat with Seller" button (placeholder)
  8. Add "Add to Favorites" button (toggle)
  9. Add "Buy Now" button
  10. Show loading skeleton
  11. Test: Click account from list → detail page loads

**Story 3.5: Marketplace Homepage with Featured Listings**
- **Who:** Junior Dev 2 (parallel to others)
- **Duration:** 3 days
- **Trello Tasks:**
  1. Create `HomePage.tsx`
  2. Query GET_GAMES and display as horizontal scroll
  3. Query GET_ACCOUNTS with isFeatured: true
  4. Display featured accounts in hero section
  5. Query GET_ACCOUNTS with sortBy: createdAt
  6. Display new accounts in grid
  7. Create `AccountCard.tsx` reusable component
  8. Add search bar in header
  9. Implement infinite scroll with IntersectionObserver
  10. Add loading skeleton
  11. Test: Homepage loads with featured + new accounts

**Story 3.6: Advanced Search & Filter UI**
- **Who:** Junior Dev 2 (continues from 3.5)
- **Duration:** 3 days
- **Trello Tasks:**
  1. Create `SearchPage.tsx`
  2. Create `FilterSidebar.tsx` component
  3. Add game dropdown filter
  4. Add price range slider filter
  5. Add level inputs filter
  6. Add rank dropdown filter
  7. Create custom hook `useFilters.ts` for state management
  8. Persist filters in URL query params (useSearchParams)
  9. Debounce search input (300ms)
  10. Display active filters as chips
  11. Add "Clear Filters" button
  12. Add sort dropdown (Price Low/High, Level, Newest)
  13. Make sidebar collapsible on mobile
  14. Test: Apply filters → URL updates → results refresh

**Story 3.7: Favorites Management Page**
- **Who:** Junior Dev 3 (after 3.3 done)
- **Duration:** 2 days
- **Trello Tasks:**
  1. Create `FavoritesPage.tsx`
  2. Query GET_FAVORITES
  3. Display favorites in grid layout
  4. Add remove button on each card
  5. Create empty state (icon + message)
  6. Use optimistic UI for remove action
  7. Add confirmation modal before removing
  8. Implement pagination (20 per page)
  9. Test: Add favorite → view on favorites page → remove

---

#### **Week 7: Performance Optimization**

**Story 3.8: Redis Caching Strategy Implementation**
- **Who:** Junior Dev 1 (Lead should review cache strategy)
- **Duration:** 2 days
- **Trello Tasks:**
  1. Create `RedisConfig.java` with @EnableCaching
  2. Configure RedisCacheManager with JSON serialization
  3. Add @Cacheable to AccountService.searchAccounts() (TTL 10 min)
  4. Add @Cacheable to GameService.getAllGames() (TTL 1 hour)
  5. Add @Cacheable to AccountService.getFeaturedAccounts() (TTL 5 min)
  6. Add @CacheEvict to create/update/delete methods
  7. Configure cache key format: "accounts:gameId:123:minPrice:100"
  8. Add cache statistics logging
  9. Test: Query accounts → check Redis → same query uses cache

**Story 3.9: DataLoader for N+1 Query Prevention**
- **Who:** Junior Dev 4 (Lead should review GraphQL optimization)
- **Duration:** 2 days
- **Trello Tasks:**
  1. Add graphql-java-dataloader dependency
  2. Create `DataLoaderConfig.java`
  3. Implement UserDataLoader for batch loading sellers
  4. Implement GameDataLoader for batch loading games
  5. Configure DataLoaderRegistry in GraphQL context
  6. Update AccountQuery resolvers to use DataLoader
  7. Configure query complexity analyzer
  8. Set max query complexity: 1000
  9. Set max query depth: 10
  10. Test: Query 50 accounts → verify only 2-3 DB queries (not 50+)

**Story 3.10: Pagination & Infinite Scroll**
- **Who:** Junior Dev 2 (frontend expert)
- **Duration:** 2 days
- **Trello Tasks:**
  1. Define AccountConnection, AccountEdge, PageInfo types in GraphQL schema
  2. Update GET_ACCOUNTS query to return AccountConnection
  3. Implement cursor encoding (base64 of ID + timestamp)
  4. Implement hasNextPage, hasPreviousPage logic
  5. Create `useInfiniteScroll.ts` custom hook
  6. Update HomePage to use infinite scroll
  7. Add "Load More" button as fallback
  8. Configure Apollo Client cache merge function
  9. Test: Scroll down → fetch next page → results append

---

## 7. Daily Workflow

### ⏰ 7.1 Daily Schedule for Juniors

**9:00 - 9:15 AM: Daily Standup**
- Post update in #daily-standup channel
- Read others' updates
- Identify blockers

**9:15 - 12:00 PM: Deep Work (Morning)**
- No meetings, focus on coding
- Use Pomodoro Technique (25 min work, 5 min break)
- Commit frequently (at least 2-3 commits per morning)

**12:00 - 1:00 PM: Lunch Break**

**1:00 - 3:00 PM: Deep Work (Afternoon)**
- Continue coding
- Write tests for completed features
- Self-review code before pushing

**3:00 - 4:00 PM: Code Review & Collaboration**
- Review others' PRs
- Answer questions in #tech-discuss
- Pair programming session if needed

**4:00 - 5:00 PM: Admin & Learning**
- Update Trello cards
- Push final commits
- Create PR if story done
- Read documentation
- Learn new concepts

**End of Day:**
- Commit all work (even WIP)
- Update Trello card status
- Notify Lead if blocked

---

### 📅 7.2 Weekly Rituals

**Monday (Sprint Planning) - 1 hour**
- **Attendees:** Lead + All Juniors
- **Agenda:**
  1. Review last sprint's completed stories
  2. Demo completed features
  3. Select stories for this sprint
  4. Assign stories to team members
  5. Clarify acceptance criteria
  6. Identify dependencies

**Wednesday (Mid-Sprint Check-in) - 30 min**
- **Attendees:** Lead + All Juniors
- **Agenda:**
  1. Quick progress update
  2. Address blockers
  3. Re-assign stories if needed

**Friday (Sprint Review & Retrospective) - 1 hour**
- **Attendees:** Lead + All Juniors
- **Agenda:**
  1. Demo completed stories (15 min)
  2. Move completed stories to Done
  3. Retrospective discussion (30 min):
     - What went well?
     - What didn't go well?
     - What can we improve?
  4. Celebrate wins! 🎉

---

## 8. Best Practices for Juniors

### 💡 8.1 Coding Best Practices

**1. Read Before You Write**
```java
// ❌ Bad: Copy-paste without understanding
public User findUser(String email) {
    return userRepository.findByEmail(email); // What if null?
}

// ✅ Good: Understand the API and handle edge cases
public User findUser(String email) {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
}
```

**2. Follow SOLID Principles**
```java
// ❌ Bad: Violates Single Responsibility Principle
@RestController
public class AuthController {
    public AuthResponse register(RegisterRequest request) {
        // Validate email
        if (!request.getEmail().contains("@")) {
            throw new ValidationException("Invalid email");
        }
        // Hash password
        String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());
        // Save to database
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(hashedPassword);
        userRepository.save(user);
        // Generate JWT
        String token = JWT.create().withSubject(user.getEmail()).sign(Algorithm.HMAC256("secret"));
        return new AuthResponse(token);
    }
}

// ✅ Good: Separation of concerns
@RestController
public class AuthController {
    private final AuthService authService; // Delegates business logic
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
}

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    public AuthResponse register(RegisterRequest request) {
        validateEmail(request.getEmail());
        User user = createUser(request);
        String token = jwtTokenProvider.generateToken(user.getEmail());
        return new AuthResponse(token, user.getId(), user.getEmail());
    }
}
```

**3. Don't Repeat Yourself (DRY)**
```typescript
// ❌ Bad: Duplicated error handling
function createAccount() {
  try {
    // ... logic
  } catch (error) {
    console.error(error);
    alert("Failed to create account");
  }
}

function updateAccount() {
  try {
    // ... logic
  } catch (error) {
    console.error(error);
    alert("Failed to update account");
  }
}

// ✅ Good: Centralized error handling
const handleError = (error: Error, context: string) => {
  console.error(`Error in ${context}:`, error);
  toast.error(`Failed to ${context}`);
};

function createAccount() {
  try {
    // ... logic
  } catch (error) {
    handleError(error, "create account");
  }
}
```

**4. Use Meaningful Names**
```java
// ❌ Bad
List<Account> list = accountRepository.findAll();
for (Account a : list) {
    if (a.getStatus() == 1) { // Magic number
        System.out.println(a.getTitle()); // Don't use System.out
    }
}

// ✅ Good
List<Account> approvedAccounts = accountRepository.findByStatus(AccountStatus.APPROVED);
for (Account account : approvedAccounts) {
    log.info("Approved account: {}", account.getTitle());
}
```

---

### 🚫 8.2 Common Pitfalls & Solutions

**Pitfall 1: Not Reading Error Messages**
```bash
# ❌ Junior sees error, immediately asks for help
ERROR: java.lang.NullPointerException at UserService.java:45

# ✅ Junior should first:
1. Read the stack trace completely
2. Go to UserService.java line 45
3. Identify which variable is null
4. Trace back to where that variable should be initialized
5. Try to fix it
6. THEN ask for help if still stuck (with detailed context)
```

**Pitfall 2: Committing Without Testing**
```bash
# ❌ Bad workflow
git add .
git commit -m "fix bug"
git push
# (CI fails, PR rejected)

# ✅ Good workflow
mvn clean test           # Ensure tests pass
npm run lint             # Ensure no lint errors
mvn spring-boot:run      # Manually test the feature
# Only after everything works:
git add .
git commit -m "[1.4] Fix email validation bug in UserRepository"
git push
```

**Pitfall 3: Working in Isolation (Fear of Looking Dumb)**
```markdown
# ❌ Junior is stuck for 4 hours, doesn't ask for help

# ✅ Junior asks after 30 minutes:
"Hey @Lead, I'm working on Story 1.4 (User Entity). I'm stuck on this error:
[paste error]

I've tried:
1. Checked if MySQL is running (it is)
2. Verified application.yml datasource config (looks correct)
3. Googled the error (found similar issues but solutions didn't work)

Could you help me debug this? I've been stuck for 30 minutes."
```

**Pitfall 4: Over-Engineering**
```java
// ❌ Junior tries to make "perfect" code
public class UserService {
    // Implements caching, retry logic, circuit breaker, event sourcing, CQRS
    // ... for a simple CRUD operation (Story 1.4 doesn't ask for this!)
}

// ✅ Junior follows acceptance criteria exactly
public class UserService {
    private final UserRepository userRepository;
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
// (Caching will be added in Story 3.8, not now)
```

**Pitfall 5: Not Updating Trello**
```markdown
# ❌ Bad: Trello card stays in "In Progress" for 5 days
# Lead has no idea if junior is stuck or just slow

# ✅ Good: Junior updates card daily
Day 1: Moved to "In Progress", added comment "Started work"
Day 2: Comment "Completed User entity, working on UserRepository"
Day 3: Comment "Finished code, writing tests"
Day 4: Moved to "Code Review", PR created
```

---

## 9. Common Pitfalls & Solutions

### 🐛 9.1 Technical Issues

**Issue:** MySQL container won't start
```bash
# Error: "port 3306 already in use"

# Solution:
# 1. Check if MySQL is already running on host
sudo service mysql stop   # Linux
brew services stop mysql  # Mac

# 2. Or change port in docker-compose.yml
ports:
  - "3307:3306"  # Map to 3307 instead
```

**Issue:** JWT token validation fails
```java
// Error: "JWT signature does not match locally computed signature"

// Solution:
// 1. Ensure JWT secret is the same in JwtTokenProvider and SecurityConfig
// 2. Check application.yml:
jwt:
  secret: ${JWT_SECRET:default-secret-key-change-in-production}
  expiration: 86400000
```

**Issue:** GraphQL query returns null for nested fields
```graphql
# Query:
{
  accounts {
    id
    title
    seller {  # Returns null
      fullName
    }
  }
}

# Solution: Add DataLoader to prevent lazy loading issues
# See Story 3.9
```

---

### 🤝 9.2 Team Collaboration Issues

**Issue:** Merge conflicts
```bash
# Error: "CONFLICT (content): Merge conflict in User.java"

# Solution:
# 1. Don't panic! Conflicts are normal.
# 2. Open User.java, look for:
<<<<<<< HEAD
your changes
=======
their changes
>>>>>>> develop

# 3. Keep the correct version (or merge both)
# 4. Remove markers (<<<<<<<, =======, >>>>>>>)
# 5. Test the merged code
mvn test
# 6. Complete merge
git add User.java
git commit
```

**Issue:** Junior doesn't understand acceptance criteria
```markdown
# ❌ Bad: Junior guesses and implements wrong thing

# ✅ Good: Junior asks for clarification immediately
"@Lead, in Story 1.4, the acceptance criteria says 'User entity has role field'. 
Should this be a String or an enum? If enum, what are the possible values?"
```

**Issue:** PR taking too long to review
```markdown
# If PR is pending for >24 hours:
# 1. Junior should ping Lead in #general:
"@Lead, my PR #42 has been waiting for review for 2 days. Could you please review it when you have time? It's blocking Story 1.6."

# 2. Lead should set expectation:
"Will review by EOD today. In future, mention me directly in PR description."
```

---

## 10. Learning Resources

### 📚 10.1 Documentation (Required Reading)

**Before Starting Epic 1:**
- [ ] Read `project_docs/PRD.md` (Sections 1-3)
- [ ] Read `project_docs/ARCHITECTURE.md` (Sections 1-3)
- [ ] Read `_bmad-output/planning-artifacts/epics.md` (Epic 1)

**Before Starting Each Story:**
- [ ] Read the specific story section in epics.md
- [ ] Review technical notes and requirements
- [ ] Check linked architecture templates

---

### 🎥 10.2 Video Tutorials (For Learning)

**Spring Boot:**
- Spring Boot Crash Course (YouTube)
- Spring Data JPA Tutorial
- Spring Security + JWT Tutorial

**GraphQL:**
- GraphQL Java Kickstart Tutorial
- DataLoader Pattern Explained

**React + TypeScript:**
- React TypeScript Tutorial
- Apollo Client + React
- React Hook Form Tutorial

---

### 📖 10.3 Books (Optional, for Deep Learning)

**Backend:**
- "Spring Boot in Action" by Craig Walls
- "Effective Java" by Joshua Bloch (Java best practices)

**Frontend:**
- "Learning React" by Alex Banks & Eve Porcello
- "TypeScript Deep Dive" (free online)

**Architecture:**
- "Clean Architecture" by Robert C. Martin
- "Domain-Driven Design" by Eric Evans

---

## 📝 Appendix A: Quick Reference Commands

### Backend (Java)
```bash
# Build project
mvn clean install

# Run tests
mvn test

# Run application
mvn spring-boot:run

# Skip tests
mvn install -DskipTests

# Run specific test class
mvn test -Dtest=UserServiceTest

# Check code coverage
mvn jacoco:report
```

### Frontend (React)
```bash
# Install dependencies
npm install

# Run dev server
npm run dev

# Build for production
npm run build

# Run linter
npm run lint

# Fix lint errors
npm run lint:fix

# Run tests
npm test
```

### Docker
```bash
# Start containers
docker-compose up -d

# Stop containers
docker-compose down

# View logs
docker-compose logs -f mysql

# Restart container
docker-compose restart mysql

# Access MySQL shell
docker exec -it <container_id> mysql -u root -p
```

### Git
```bash
# Check status
git status

# View diff
git diff

# View commit history
git log --oneline --graph

# Amend last commit
git commit --amend

# Undo last commit (keep changes)
git reset --soft HEAD~1

# Discard local changes
git checkout -- <file>

# View branches
git branch -a

# Delete local branch
git branch -d <branch-name>
```

---

## 📞 Appendix B: Who to Ask for Help

| Question Type | Ask | Example |
|---------------|-----|---------|
| Acceptance criteria unclear | Lead | "Is the email field required?" |
| Tech stack question | Lead or Senior Dev | "Should I use JPA or QueryDSL?" |
| Bug in code | #tech-discuss first, then Lead | "Getting NullPointer at line 45" |
| Stuck for >30 min | Lead | "Tried X, Y, Z but still stuck" |
| Git merge conflict | Senior Dev or Lead | "How do I resolve this conflict?" |
| Architecture decision | Lead | "Should this go in Service or Controller?" |
| Trello process question | Lead | "When do I move card to Testing?" |
| General learning | #resources channel | "Best GraphQL tutorial?" |

---

## ✅ Appendix C: Definition of Done (DoD)

A story is only "Done" when ALL of these are checked:

**Code Quality:**
- [ ] Code compiles without errors
- [ ] All acceptance criteria met
- [ ] No linter warnings
- [ ] Follows N-Layer architecture
- [ ] Follows Java/TypeScript naming conventions
- [ ] No hardcoded values (use config)
- [ ] Proper error handling

**Testing:**
- [ ] All tests pass (`mvn test` or `npm test`)
- [ ] New unit tests written for new features
- [ ] Manual testing completed
- [ ] Edge cases tested (null, empty, boundary)

**Documentation:**
- [ ] Complex logic has comments
- [ ] Public methods have JavaDoc (Java) or JSDoc (TypeScript)
- [ ] README updated if needed

**Review:**
- [ ] PR created with proper description
- [ ] Self-review done (reviewed own diff)
- [ ] CI checks pass (build, tests, lint)
- [ ] Code reviewed by Lead or Senior Dev
- [ ] All review comments addressed

**Integration:**
- [ ] PR merged to `develop` branch
- [ ] Trello card moved to "Done"
- [ ] Feature tested in staging environment
- [ ] No new bugs introduced

---

## 🎉 Conclusion

This guide provides everything a Lead needs to successfully teach and manage 4 junior developers building the Game Account Marketplace from scratch.

**Key Takeaways:**
1. **Structure is critical** - Use Trello, Git workflow, and clear stories
2. **Communication is key** - Daily standups, code reviews, and pair programming
3. **Teach, don't just assign** - Explain the "why", not just the "what"
4. **Iterate and improve** - Use retrospectives to get better every sprint

**For Leads:**
- Be patient - juniors will make mistakes
- Be available - unblock them quickly
- Be consistent - enforce standards equally
- Be encouraging - celebrate small wins

**For Juniors:**
- Ask questions early and often
- Read error messages carefully
- Commit frequently
- Learn from code reviews

Good luck! 🚀

---

**Document Version:** 1.0  
**Last Updated:** 2026-01-09  
**Maintained By:** Lead Developer  
**Contact:** #tech-discuss on Discord

