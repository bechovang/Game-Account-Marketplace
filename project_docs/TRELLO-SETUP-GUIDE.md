# 📋 Trello Board Setup Guide
## Complete Trello Configuration for Game Account Marketplace

---

## 📊 Table of Contents

1. [Board Structure Overview](#board-structure-overview)
2. [Setting Up Your Master Sprint Board](#setting-up-your-master-sprint-board)
3. [Card Templates for Each Story Type](#card-templates-for-each-story-type)
4. [Labels & Custom Fields](#labels--custom-fields)
5. [Automation Rules](#automation-rules)
6. [Epic-Specific Boards](#epic-specific-boards)
7. [Best Practices](#best-practices)

---

## 1. Board Structure Overview

### 🎯 Recommended Board Setup

For a team of **1 Lead + 4 Junior Devs**, create:

1. **Master Sprint Board** (Primary board for current sprint)
2. **Backlog Board** (All future stories)
3. **Epic 1: Authentication** (Optional, if team needs focus)
4. **Epic 2: Listing Management** (Optional)
5. **Epic 3: Discovery** (Optional)

---

## 2. Setting Up Your Master Sprint Board

### Step-by-Step Setup

**Step 1: Create Board**
1. Go to Trello.com
2. Click "Create new board"
3. Name: "Sprint [Number] - [Start Date]"
   - Example: "Sprint 1 - Jan 2026"
4. Background: Choose a professional color (Blue or Green recommended)
5. Visibility: Workspace (so all team members can see)

**Step 2: Create Lists (Columns)**

Create these 7 lists in this exact order:

```
1. 📦 Backlog
2. 📋 Sprint Backlog
3. ✅ To Do
4. 🏃 In Progress (Max 2 per person)
5. 👀 Code Review
6. 🧪 Testing
7. ✅ Done
```

**Column Descriptions:**

| Column | Purpose | Who Adds Cards | Exit Criteria |
|--------|---------|----------------|---------------|
| 📦 **Backlog** | All future stories not in current sprint | Lead/PM | Story has acceptance criteria |
| 📋 **Sprint Backlog** | Stories committed to current sprint | Lead during Sprint Planning | Story selected for sprint |
| ✅ **To Do** | Ready to start coding | Lead assigns or Dev self-assigns | Dev understands requirements |
| 🏃 **In Progress** | Actively coding | Dev when starting work | PR created or blocked |
| 👀 **Code Review** | PR created, waiting for review | Dev after creating PR | PR approved by Lead |
| 🧪 **Testing** | Merged to develop, testing in staging | Lead/Reviewer after merge | Feature tested, no bugs |
| ✅ **Done** | Completed and verified | Lead/Tester | Feature works in staging |

**Step 3: Add Power-Ups**

Click "Power-Ups" in menu, then add:

1. **Custom Fields** (Free)
   - Allows adding story points, priority, etc.
   
2. **Card Aging** (Free)
   - Highlights old cards that haven't moved
   
3. **GitHub** (Free)
   - Links PRs to cards
   - Shows PR status on card
   
4. **Calendar** (Free)
   - Visualizes deadlines
   
5. **Butler Automation** (Free, built-in)
   - Auto-moves cards based on rules

**Step 4: Configure Custom Fields**

Add these custom fields to all cards:

| Field Name | Type | Options |
|------------|------|---------|
| **Story Points** | Number | 1, 2, 3, 5, 8 |
| **Priority** | Dropdown | 🔴 High, 🟡 Medium, 🟢 Low |
| **Blocked By** | Text | Free text (reference to blocker) |
| **Assigned Dev** | Dropdown | JuniorDev1, JuniorDev2, JuniorDev3, JuniorDev4 |
| **Epic** | Dropdown | Epic 1, Epic 2, Epic 3, Epic 4, Epic 5, Epic 6, Epic 7 |
| **Story ID** | Text | E.g., "1.4", "2.3" |

---

## 3. Card Templates for Each Story Type

### 🏗️ Infrastructure Story Template (Epic 1.1, 1.2, 1.3)

**Use this for:** Project setup, environment configuration, tooling

```markdown
## Story 1.1: Project Structure & Environment Setup

### 🎯 Goal
Initialize the project structure with monorepo layout and Docker environment for MySQL and Redis.

### 🔗 Epic Reference
- **Epic:** 1 - User Authentication & Identity
- **Story Number:** 1.1
- **Story Points:** 5
- **Dependencies:** None (First story!)

---

### ✅ Acceptance Criteria

**Infrastructure Setup:**
- [ ] Git repository created with proper `.gitignore`
- [ ] Root directory contains `backend-java/` and `frontend-react/` subdirectories
- [ ] `.gitignore` includes: `node_modules/`, `target/`, `.idea/`, `*.log`, `.env`

**Docker Configuration:**
- [ ] `docker-compose.yml` includes MySQL 8.0 service
- [ ] `docker-compose.yml` includes Redis 7.0 service
- [ ] MySQL service exposes port 3306 with volume persistence
- [ ] Redis service exposes port 6379 with volume persistence
- [ ] Health checks configured for both services
- [ ] `docker-compose up -d` starts both services successfully

**Documentation:**
- [ ] `README.md` created with setup instructions
- [ ] `README.md` includes prerequisites (Java 21, Node 18, Docker)
- [ ] `README.md` includes "Getting Started" section

---

### 🛠️ Technical Notes
- Use MySQL 8.0 (not 5.7) for better performance
- Use Redis 7.0 for latest features
- Volume names: `mysql_data` and `redis_data`
- Network name: `marketplace-network`

### 📚 Reference Documents
- [Architecture Doc - Section 2.1](../ARCHITECTURE.md#21-project-structure)
- [Docker Compose Template](https://github.com/example/docker-compose)

---

### 📋 Subtasks (Check off as you complete)
- [ ] Create Git repository
- [ ] Create folder structure
- [ ] Create `.gitignore`
- [ ] Create `docker-compose.yml`
- [ ] Test MySQL container starts
- [ ] Test Redis container starts
- [ ] Write README.md
- [ ] Commit and push to `main` branch

---

### 🧪 Testing Checklist
- [ ] `docker-compose up -d` succeeds
- [ ] `docker ps` shows both containers running
- [ ] Connect to MySQL: `docker exec -it mysql_container mysql -u root -p`
- [ ] Connect to Redis: `docker exec -it redis_container redis-cli`

---

### 🐛 Known Issues / Blockers
*Update this section if you encounter problems*

None yet.

---

### 💬 Comments / Questions
*Ask questions here, Lead will respond*

**@JuniorDev1:** Should we use MySQL 8.0.35 or 8.0.36?
**@Lead:** Use 8.0.35 (more stable).

---

### 🔗 Related Cards
- **Blocks:** Story 1.2 (Backend Skeleton)
- **Blocks:** Story 1.3 (Frontend Setup)

---

### ⏱️ Time Tracking
- **Estimated:** 2 days
- **Actual:** ___ days (fill in when done)

---

### ✅ Definition of Done (Must check all before moving to Code Review)
- [ ] All acceptance criteria met
- [ ] Code pushed to Git
- [ ] README.md updated
- [ ] Tested by developer
- [ ] No blockers remaining
- [ ] PR created (if applicable)
```

**How to use:**
1. Copy entire template
2. Paste into Trello card description
3. Update story number, dependencies, and criteria
4. Assign to developer

---

### 💾 Backend Story Template (Epic 1.4, 1.5, 2.1, etc.)

**Use this for:** JPA entities, services, controllers, repositories

```markdown
## Story 1.4: User Entity & Repository

### 🎯 Goal
Create the User JPA entity with UserRepository so that user data can be persisted and retrieved from the database.

### 🔗 Epic Reference
- **Epic:** 1 - User Authentication & Identity
- **Story Number:** 1.4
- **Story Points:** 3
- **Dependencies:** 
  - ✅ Story 1.1 (Project Setup) - DONE
  - ✅ Story 1.2 (Spring Boot Skeleton) - DONE

---

### ✅ Acceptance Criteria

**User Entity:**
- [ ] User entity created in `entity/User.java`
- [ ] User has fields: `id`, `email`, `password`, `fullName`, `avatar`, `role`, `status`, `balance`, `rating`, `totalReviews`, `createdAt`, `updatedAt`
- [ ] User has JPA annotations: `@Entity`, `@Table(name = "users")`, `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)`
- [ ] Email field has `@Column(unique = true, nullable = false)`
- [ ] Password field has `@Column(nullable = false, length = 255)`
- [ ] User has Lombok annotations: `@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`
- [ ] `createdAt` uses `@CreatedDate`
- [ ] `updatedAt` uses `@LastModifiedDate`
- [ ] Role enum created: `BUYER`, `SELLER`, `ADMIN`
- [ ] Status enum created: `ACTIVE`, `BANNED`, `SUSPENDED`

**UserRepository:**
- [ ] UserRepository created in `repository/UserRepository.java`
- [ ] Extends `JpaRepository<User, Long>`
- [ ] Has method: `Optional<User> findByEmail(String email)`
- [ ] Has method: `boolean existsByEmail(String email)`
- [ ] Has method: `List<User> findByRole(UserRole role)`
- [ ] Has method: `Page<User> findByRole(UserRole role, Pageable pageable)`

**Database Schema:**
- [ ] Application starts successfully
- [ ] MySQL `users` table is created automatically
- [ ] Table has all columns with correct types
- [ ] Foreign key constraints are correct (if any)

---

### 🛠️ Technical Notes
- Password field must be at least 255 chars to support BCrypt hashes (60 chars) and future algorithms
- Use `@Column(updatable = false)` on `createdAt`
- Enable JPA Auditing: Add `@EnableJpaAuditing` to main application class
- Index the `email` column for faster lookups

### 📚 Reference Documents
- [Epic 1 Documentation](_bmad-output/planning-artifacts/epics.md#story-14)
- [Architecture - Entity Pattern](ARCHITECTURE.md#entity-layer)
- [PRD - User Requirements](PRD.md#user-management)

---

### 📋 Subtasks
- [ ] Create `User.java` entity
- [ ] Add all fields with correct types
- [ ] Add JPA annotations
- [ ] Add Lombok annotations
- [ ] Create `UserRole` enum
- [ ] Create `UserStatus` enum
- [ ] Create `UserRepository.java` interface
- [ ] Add custom query methods
- [ ] Enable JPA Auditing in main class
- [ ] Test: Start application
- [ ] Test: Check MySQL schema
- [ ] Write unit tests for UserRepository methods
- [ ] Commit and push to feature branch
- [ ] Create Pull Request

---

### 🧪 Testing Checklist
- [ ] `mvn clean compile` - SUCCESS
- [ ] `mvn test` - All tests pass
- [ ] Application starts without errors
- [ ] MySQL table created: `SHOW TABLES;` → `users` exists
- [ ] Table schema correct: `DESCRIBE users;`
- [ ] Manual test: UserRepository.save(user) works
- [ ] Manual test: UserRepository.findByEmail() retrieves user
- [ ] Manual test: UserRepository.existsByEmail() returns true/false

---

### 🔗 GitHub Pull Request
*Link PR here once created*

PR #44: https://github.com/your-org/marketplace/pull/44

---

### 🐛 Known Issues / Blockers
*Update this if you get stuck*

None.

---

### 💬 Comments / Discussion
*Team discussion thread*

**@JuniorDev1 (Jan 9, 10:00):** Started working on this story.
**@JuniorDev1 (Jan 9, 14:30):** Question: Should `rating` be Float or Double?
**@Lead (Jan 9, 15:00):** Use Double for better precision.
**@JuniorDev1 (Jan 9, 17:00):** ✅ Completed! PR created.

---

### 🔗 Related Cards
- **Depends on:** Story 1.2 (Spring Boot Skeleton) ✅
- **Blocks:** Story 1.5 (JWT Implementation)
- **Related to:** Story 1.6 (AuthService)

---

### ⏱️ Time Tracking
- **Estimated:** 1 day
- **Started:** Jan 9, 9:00 AM
- **Completed:** Jan 9, 5:00 PM
- **Actual:** 6 hours (faster than expected!)

---

### ✅ Definition of Done
- [ ] All acceptance criteria checked
- [ ] Code compiles without errors
- [ ] All tests pass
- [ ] No linter warnings
- [ ] Code follows N-Layer architecture
- [ ] PR created and reviewed
- [ ] PR merged to `develop`
- [ ] Trello card moved to Done
```

---

### 🎨 Frontend Story Template (Epic 1.8, 2.6, 3.5, etc.)

**Use this for:** React components, pages, hooks, contexts

```markdown
## Story 1.8: Frontend Authentication Pages & Context

### 🎯 Goal
Create login/register pages with AuthContext and protected routes so that users can authenticate and access protected pages.

### 🔗 Epic Reference
- **Epic:** 1 - User Authentication & Identity
- **Story Number:** 1.8
- **Story Points:** 5
- **Dependencies:**
  - ✅ Story 1.3 (Frontend Setup) - DONE
  - ✅ Story 1.7 (REST API Endpoints) - DONE

---

### ✅ Acceptance Criteria

**AuthContext:**
- [ ] `AuthContext.tsx` created in `src/contexts/`
- [ ] Provides state: `user`, `token`, `isAuthenticated`, `isLoading`
- [ ] Provides methods: `login(email, password)`, `logout()`, `refreshUser()`
- [ ] Stores JWT token in `localStorage` under `'access_token'` key
- [ ] Retrieves user on app mount if token exists

**useAuth Hook:**
- [ ] `useAuth.ts` hook created in `src/hooks/`
- [ ] Returns all AuthContext values
- [ ] Throws error if used outside AuthProvider

**LoginPage:**
- [ ] `LoginPage.tsx` created in `src/pages/auth/`
- [ ] Has email input field with validation
- [ ] Has password input field with validation (min 6 chars)
- [ ] Has "Login" button
- [ ] Has link to RegisterPage
- [ ] Displays loading state during API call
- [ ] Displays error message on login failure
- [ ] Redirects to home page (`/`) on success

**RegisterPage:**
- [ ] `RegisterPage.tsx` created in `src/pages/auth/`
- [ ] Has email input with format validation
- [ ] Has password input with strength indicator
- [ ] Has fullName input (required)
- [ ] Has "Register" button
- [ ] Has link to LoginPage
- [ ] Displays validation errors inline
- [ ] Redirects to home page on success

**ProtectedRoute:**
- [ ] `ProtectedRoute.tsx` component created
- [ ] Checks `isAuthenticated` before rendering children
- [ ] Redirects to `/login` if not authenticated
- [ ] Stores original URL in state for redirect after login

**Routing:**
- [ ] React Router configured in `App.tsx`
- [ ] Route: `/login` → LoginPage
- [ ] Route: `/register` → RegisterPage
- [ ] Route: `/` → HomePage (protected)
- [ ] Route: `/accounts/:id` → AccountDetailPage (protected)

**Axios Integration:**
- [ ] Axios interceptor attaches JWT token to all requests
- [ ] Interceptor adds `Authorization: Bearer {token}` header
- [ ] Interceptor handles 401 errors (redirect to login)

---

### 🛠️ Technical Notes
- Use `react-hook-form` for form validation (easier than manual state)
- Use `React.Context` + `useContext` for auth state
- Use `localStorage` for token persistence (not sessionStorage)
- Use Axios interceptors (not fetch) for automatic header injection
- Tailwind CSS for styling

### 📚 Reference Documents
- [Epic 1.8 Details](_bmad-output/planning-artifacts/epics.md#story-18)
- [Architecture - Frontend Structure](ARCHITECTURE.md#frontend-architecture)
- [Figma Designs](https://figma.com/...) *(if available)*

---

### 📋 Subtasks
- [ ] Create `AuthContext.tsx`
- [ ] Implement login() method (calls `/api/auth/login`)
- [ ] Implement logout() method
- [ ] Implement token storage logic
- [ ] Create `useAuth.ts` hook
- [ ] Create `LoginPage.tsx` with form
- [ ] Add form validation (react-hook-form)
- [ ] Create `RegisterPage.tsx` with form
- [ ] Create `ProtectedRoute.tsx` wrapper
- [ ] Configure React Router in `App.tsx`
- [ ] Create Axios interceptor for JWT
- [ ] Test: Full auth flow (register → login → protected page)
- [ ] Test: Logout clears token
- [ ] Test: Accessing protected route without login redirects
- [ ] Write unit tests for AuthContext
- [ ] Commit and push
- [ ] Create PR

---

### 🧪 Testing Checklist
- [ ] `npm run lint` - No errors
- [ ] `npm run build` - Builds successfully
- [ ] `npm run dev` - Starts without errors
- [ ] Manual test: Register new user → redirected to home
- [ ] Manual test: Logout → redirected to login
- [ ] Manual test: Login with valid credentials → success
- [ ] Manual test: Login with invalid credentials → error shown
- [ ] Manual test: Access `/` without login → redirected to `/login`
- [ ] Manual test: Refresh page → user stays logged in (token persisted)
- [ ] Manual test: Token expired → redirect to login on API call
- [ ] Check localStorage: Token is stored correctly
- [ ] Check Network tab: JWT sent in Authorization header

---

### 🎨 Design Screenshots
*Attach design mockups or screenshots*

**LoginPage:**
![Login Page](https://i.imgur.com/login.png)

**RegisterPage:**
![Register Page](https://i.imgur.com/register.png)

---

### 🔗 GitHub Pull Request
*Link once created*

PR #52: https://github.com/your-org/marketplace/pull/52

---

### 🐛 Known Issues / Blockers
*Update if stuck*

**@JuniorDev2 (Jan 10):** Getting CORS error when calling `/api/auth/login`
**@Lead (Jan 10):** Backend needs CORS config. I'll add it to SecurityConfig.

---

### 💬 Comments
*Team discussion*

**@JuniorDev2 (Jan 9):** Starting work on this.
**@JuniorDev2 (Jan 10):** LoginPage done, working on RegisterPage.
**@JuniorDev2 (Jan 11):** All done! Testing now.
**@JuniorDev2 (Jan 11):** ✅ PR created! Ready for review.

---

### 🔗 Related Cards
- **Depends on:** Story 1.7 (REST API) ✅
- **Blocks:** Story 2.6 (Seller Listing Pages)
- **Related to:** Story 3.5 (Homepage)

---

### ⏱️ Time Tracking
- **Estimated:** 3 days
- **Started:** Jan 9
- **Completed:** Jan 11
- **Actual:** 3 days

---

### ✅ Definition of Done
- [ ] All acceptance criteria met
- [ ] Code compiles and runs
- [ ] All manual tests pass
- [ ] No console errors
- [ ] No linter warnings
- [ ] Responsive on mobile and desktop
- [ ] PR created and reviewed
- [ ] PR merged
- [ ] Feature tested in staging
```

---

## 4. Labels & Custom Fields

### 🏷️ Standard Labels

Create these labels (color-coded):

| Label | Color | Use Case | Example |
|-------|-------|----------|---------|
| `frontend` | Green | React/TypeScript work | Story 1.8, 2.6, 3.5 |
| `backend` | Blue | Java/Spring Boot work | Story 1.4, 1.5, 2.2 |
| `graphql` | Purple | GraphQL schema/resolvers | Story 2.3, 3.1 |
| `database` | Orange | Database entities/migrations | Story 1.4, 2.1, 4.1 |
| `devops` | Yellow | Docker, CI/CD, deployment | Story 1.1 |
| `bug` | Red | Bug fix task | Any hotfix |
| `blocked` | Black | Cannot proceed | When dependency not met |
| `needs-review` | Light Blue | Ready for code review | After PR created |
| `needs-info` | Pink | Waiting for clarification | When AC unclear |
| `high-priority` | Red | Must complete ASAP | Critical path stories |

### How to Apply Labels:
1. Open card
2. Click "Labels" in right sidebar
3. Select appropriate labels
4. Cards can have multiple labels (e.g., `backend` + `blocked`)

---

## 5. Automation Rules

### 🤖 Butler Automation (Free, Built-in)

**Rule 1: Auto-move to "Done" when checklist complete**
```
When all items in a checklist named "Acceptance Criteria" are complete, move the card to list "Done"
```

**Rule 2: Add "blocked" label when "Blocked By" field is filled**
```
When custom field "Blocked By" is set, add the "blocked" label to the card
```

**Rule 3: Notify team when card in "Code Review" for >24h**
```
Every day at 9:00 AM, for each card in list "Code Review" aged more than 24 hours, post comment "@Lead This PR needs review"
```

**Rule 4: Auto-assign due date based on story points**
```
When a card with custom field "Story Points" is moved to list "To Do", set due date to 3 days from now if Story Points is 3 or more
```

**Rule 5: Congratulations message when card moved to Done**
```
When a card is moved to list "Done", post comment "🎉 Great work @{username}! Story completed!"
```

---

## 6. Epic-Specific Boards

### When to Create Epic Boards

Create separate boards when:
- Team has 4+ developers
- Epic is large (>8 stories)
- Juniors need focused workspace

### Epic Board Structure

**Board Name:** "Epic 1 - User Authentication"

**Lists:**
```
1. Backlog (Epic 1 stories)
2. To Do
3. In Progress (Max 2 per person)
4. Code Review
5. Done
```

**Board Description:**
```
This board tracks all stories for Epic 1: User Authentication & Identity.

Goal: Users can register, login, and manage their profiles.

Total Stories: 8
Duration: 2-3 weeks
Dependencies: None (first epic)

Team:
- @Lead: Overall coordination
- @JuniorDev1: Stories 1.1, 1.2, 1.5
- @JuniorDev2: Stories 1.3, 1.8
- @JuniorDev3: Stories 1.4, 1.6
- @JuniorDev4: Story 1.7
```

---

## 7. Best Practices

### ✅ For Lead

**Daily:**
- [ ] Check "In Progress" column - help with blockers
- [ ] Review new cards in "Code Review"
- [ ] Update cards with feedback comments
- [ ] Move tested cards to "Done"

**Weekly (Sprint Planning):**
- [ ] Move completed stories to archive
- [ ] Create cards for next sprint stories
- [ ] Assign stories to developers
- [ ] Update sprint board name with new sprint number

**Weekly (Sprint Retro):**
- [ ] Review "Done" column - celebrate wins
- [ ] Identify bottlenecks (which column has most cards?)
- [ ] Adjust WIP limits if needed

---

### ✅ For Junior Developers

**Daily:**
- [ ] Update card with progress comment
- [ ] Move card to correct column
- [ ] Check off completed acceptance criteria
- [ ] Link PR when created
- [ ] Ask questions in card comments (not DM)

**When Starting Story:**
- [ ] Read entire card description
- [ ] Check dependencies are done
- [ ] Ask questions if unclear
- [ ] Move card to "In Progress"
- [ ] Add comment: "Started work on [date]"

**When Stuck:**
- [ ] Add "blocked" label
- [ ] Fill "Blocked By" field
- [ ] Add comment explaining blocker
- [ ] Ping Lead in #tech-discuss

**When Done:**
- [ ] Check all acceptance criteria
- [ ] Create PR and link in card
- [ ] Move to "Code Review"
- [ ] Add comment: "PR created, ready for review"

---

## 📝 Appendix: Quick Copy-Paste Templates

### Empty Story Card Template

```markdown
## Story X.Y: [Story Name]

### 🎯 Goal
[What this story achieves]

### 🔗 Epic Reference
- **Epic:** [Epic Number and Name]
- **Story Number:** X.Y
- **Story Points:** [1, 2, 3, 5, or 8]
- **Dependencies:** 
  - Story X.Y (Name) - [Status]

---

### ✅ Acceptance Criteria
- [ ] Criteria 1
- [ ] Criteria 2
- [ ] Criteria 3

---

### 🛠️ Technical Notes
[Implementation hints]

### 📚 Reference Documents
- [Link to epic]
- [Link to architecture]

---

### 📋 Subtasks
- [ ] Task 1
- [ ] Task 2
- [ ] Create PR

---

### 🧪 Testing Checklist
- [ ] Tests pass
- [ ] Manual testing done

---

### 🔗 GitHub Pull Request
PR #XX: [link]

---

### 🐛 Known Issues / Blockers
None.

---

### 💬 Comments
[Discussion thread]

---

### 🔗 Related Cards
- **Depends on:** [Card]
- **Blocks:** [Card]

---

### ⏱️ Time Tracking
- **Estimated:** X days
- **Actual:** ___ days

---

### ✅ Definition of Done
- [ ] All AC met
- [ ] Code reviewed
- [ ] PR merged
- [ ] Tested
```

---

## 🎯 Trello Workflow Summary

```
1. Lead creates card in "Backlog" with full description
   ↓
2. Sprint Planning: Move to "Sprint Backlog"
   ↓
3. Lead assigns to Junior, moves to "To Do"
   ↓
4. Junior reads card, asks questions, starts work
   ↓
5. Junior moves to "In Progress" (updates daily)
   ↓
6. Junior completes, creates PR, moves to "Code Review"
   ↓
7. Lead reviews PR, provides feedback
   ↓
8. Junior addresses feedback, Lead approves
   ↓
9. Lead merges PR, moves card to "Testing"
   ↓
10. Lead/Tester tests feature in staging
    ↓
11. If OK: Move to "Done" 🎉
    If bugs: Move back to "To Do" with bug label
```

---

**Document Version:** 1.0  
**Last Updated:** 2026-01-09  
**Questions?** Ask in #tech-discuss or check [Main Onboarding Guide](JUNIOR-DEV-ONBOARDING-GUIDE.md)

