# Game Account Marketplace - Documentation Index

**Project:** Game Account Marketplace  
**Version:** 1.0  
**Generated:** 2026-01-09  
**Status:** Production-Ready  
**Documentation Type:** Comprehensive Project Documentation

---

## 📚 Documentation Overview

This folder contains complete, auto-generated documentation for the **Game Account Marketplace** project. The documentation covers both the backend (Spring Boot Java) and frontend (React TypeScript) parts of the application.

**Documentation Generation Method:** Exhaustive Scan  
**Coverage:** Full project state as of 2026-01-09

---

## 🗂️ Documentation Structure

### 1. **Project Overview**
**File:** [`project-overview.md`](./project-overview.md)

**Contents:**
- Executive summary
- Project classification (multi-part monorepo)
- Technology stack (backend + frontend)
- Architecture patterns
- Core features implemented
- Data model overview
- API contracts summary
- Security architecture
- Performance optimizations
- Development workflow
- Deployment architecture
- Current status & roadmap

**When to read:** Start here for a high-level understanding of the entire project.

---

### 2. **Source Tree Analysis**
**File:** [`source-tree-analysis.md`](./source-tree-analysis.md)

**Contents:**
- Complete directory structure (both backend & frontend)
- File organization patterns
- Critical directories explained
- Integration points (frontend ↔ backend)
- Entry points for both applications
- Asset locations
- Build & output directories
- Test file locations
- Configuration file locations
- Summary statistics

**When to read:** When you need to understand project structure or locate specific files.

---

### 3. **Development Guide**
**File:** [`development-guide.md`](./development-guide.md)

**Contents:**
- Prerequisites & required software
- Quick start guide (Docker recommended)
- Detailed setup instructions (MySQL, Redis, Backend, Frontend)
- Environment configuration
- Running tests (backend + frontend)
- Build process
- Code style & linting
- Common development tasks
- Debugging (backend + frontend)
- Database management
- Troubleshooting guide
- Performance monitoring
- Hot reload & developer experience
- Git workflow
- Deployment instructions

**When to read:** When setting up the project for the first time or troubleshooting development issues.

---

### 4. **API Documentation**
**File:** [`api-documentation.md`](./api-documentation.md)

**Contents:**
- API architecture overview (REST + GraphQL)
- Authentication (JWT)
- REST API endpoints:
  - Authentication (`/api/auth/*`)
  - User profile (`/api/users/*`)
  - Account management (`/api/accounts/*`)
  - Favorites (`/api/favorites/*`)
- GraphQL API:
  - Schema overview (types, enums)
  - Queries (`accounts`, `games`, `favorites`)
  - Mutations (`createAccount`, `addToFavorites`, etc.)
  - Pagination strategies
- Error responses (REST + GraphQL)
- Caching strategy
- CORS configuration
- API testing examples (cURL, GraphQL Playground)
- Best practices

**When to read:** When integrating with the API, building frontend features, or testing endpoints.

---

### 5. **State & Progress Tracking**
**File:** [`project-scan-report.json`](./project-scan-report.json)

**Contents:**
- Workflow execution state
- Scan level: exhaustive
- Project classification metadata
- Completed steps
- Existing documentation inventory
- Timestamps

**When to read:** For workflow metadata and documentation generation tracking.

---

## 🏗️ Architecture Documentation

### Backend Architecture

**Primary Documentation:** `project_docs/ARCHITECTURE.md` (2,310 lines)

**Key Topics:**
- N-Layer architecture (Presentation → Business Logic → Data Access)
- Hybrid API strategy (REST + GraphQL + WebSocket)
- Entity-relationship diagram
- Security architecture (JWT, RBAC)
- Caching strategy (Redis + Caffeine)
- GraphQL DataLoader for N+1 prevention
- Spring configuration breakdown
- Code examples for each layer

**Additional Backend Resources:**
- **Entity Models:** See `backend-java/src/main/java/.../entity/`
- **GraphQL Schema:** `backend-java/src/main/resources/graphql/schema.graphqls`
- **Configuration:** `backend-java/src/main/resources/application.yml`

### Frontend Architecture

**Primary Documentation:** `project_docs/ARCHITECTURE.md` (frontend section)

**Key Topics:**
- Component-based architecture
- Apollo Client setup (auth link, error link)
- React Context for global state
- React Router routing strategy
- Form management (react-hook-form)
- TailwindCSS + Radix UI components
- Code splitting & lazy loading

**Additional Frontend Resources:**
- **Component Structure:** See [`source-tree-analysis.md`](./source-tree-analysis.md) frontend section
- **GraphQL Queries:** `frontend-react/src/services/graphql/queries.ts`
- **GraphQL Mutations:** `frontend-react/src/services/graphql/mutations.ts`

---

## 📋 Planning & Requirements

### Product Requirements Document (PRD)
**File:** `project_docs/PRD.md` (1,983 lines)

**Contents:**
- Problem statement
- User personas
- Feature requirements (MVPs)
- User stories
- Success metrics
- Out of scope items

### Implementation Tasks
**File:** `project_docs/TASKS.md` (1,988 lines)

**Contents:**
- Phase-by-phase implementation plan
- Task breakdown (Epic 1-5)
- Acceptance criteria per task
- Dependencies
- Definition of Done

### UX Design Specification
**File:** `_bmad-output/planning-artifacts/ux-design-specification.md` (1,163 lines)

**Contents:**
- Complete UI/UX specifications
- Page-by-page wireframe descriptions
- Component design system
- User flows
- Interaction patterns

---

## 📦 Implementation Artifacts

### Story Implementation Docs
**Location:** `_bmad-output/implementation-artifacts/`

**Files (23 stories):**
- **Epic 1: Foundation** (Stories 1.1-1.8)
  - Project structure, backend skeleton, frontend setup
  - User entity, authentication (JWT), REST endpoints
  - Frontend auth pages & context

- **Epic 2: Marketplace Core** (Stories 2.1-2.4)
  - Game & Account entities
  - GraphQL schema & resolvers
  - REST controllers for seller operations

- **Epic 3: Advanced Features** (Stories 3.1-3.10)
  - Advanced filtering & search
  - Favorites/wishlist feature
  - Account detail page
  - Marketplace homepage
  - Advanced search UI
  - Redis caching
  - DataLoader N+1 prevention
  - Pagination & infinite scroll

### Sprint Status
**File:** `_bmad-output/implementation-artifacts/sprint-status.yaml`

**Contents:**
- Current sprint status
- Completed stories
- In-progress work
- Blocked items

### Retrospectives
**File:** `_bmad-output/implementation-artifacts/epic-3-retro-2026-01-09.md`

**Contents:**
- What went well
- What didn't go well
- Lessons learned
- Action items

---

## 🐛 Bug Reports & Changelogs

### Latest Changelog
**File:** `docs/CHANGELOG-2026-01-09.md`

**Contents:**
- Authentication fixes (JWT email parsing)
- Favorites pagination implementation
- CORS configuration updates
- Apollo cache fixes
- File modification summary
- Testing results

### Bug Reports
**Location:** `_bmad-output/bugs/`

**Files:**
- `2026-01-08-authentication-bugs.md` - JWT authentication issues

---

## 🔧 Development Resources

### Test Data
**File:** `SEED_DATA.md`

**Contents:**
- 8 games (League of Legends, Valorant, Mobile Legends, etc.)
- 9 users (admin, sellers, buyers) with test credentials
- 23 account listings across all games
- 3 favorites examples

**SQL File:** `backend-java/src/main/resources/seed_data.sql`

### Environment Setup
**Docker Compose:** `docker-compose.yml` (MySQL + Redis)

**Recommended Development Setup:**
1. Start services: `docker-compose up -d`
2. Start backend: `cd backend-java && mvn spring-boot:run`
3. Start frontend: `cd frontend-react && npm run dev`

---

## 🧪 Testing Documentation

### Backend Tests
**Location:** `backend-java/src/test/java/.../marketplace/`

**Test Coverage:**
- Unit tests for services
- Integration tests for controllers
- Repository tests
- GraphQL resolver tests

**Run Tests:**
```bash
cd backend-java
mvn test
```

### Frontend Tests
**Location:** `frontend-react/src/` (co-located with components)

**Test Coverage:**
- Component tests (React Testing Library)
- Hook tests (useFilters, etc.)
- Integration tests (search, favorites)

**Run Tests:**
```bash
cd frontend-react
npm test
```

---

## 📊 Project Statistics

| Metric | Backend | Frontend | Total |
|--------|---------|----------|-------|
| **Source Files** | 59 Java | 50+ TypeScript | 109+ |
| **Test Files** | 21 Java | 15+ TypeScript | 36+ |
| **Documentation Files** | 23 stories + 3 specs | - | 26+ |
| **Lines of Code (est.)** | ~9,000 | ~6,000 | ~15,000 |

**Completion Status:**
- ✅ Epic 1: Foundation & Authentication (Complete)
- ✅ Epic 2: Marketplace Core (Complete)
- ✅ Epic 3: Advanced Features (Complete)
- ⏳ Epic 4: Real-time Communication (Planned)
- ⏳ Epic 5: Payments & Transactions (Planned)

---

## 🔗 Quick Links

### Live Endpoints (Local Development)
- **Frontend:** http://localhost:3000
- **Backend REST API:** http://localhost:8080/api
- **GraphQL Playground:** http://localhost:8080/graphiql
- **Health Check:** http://localhost:8080/actuator/health

### Code Repositories
- **Backend Source:** `backend-java/src/main/java/com/gameaccount/marketplace/`
- **Frontend Source:** `frontend-react/src/`
- **GraphQL Schema:** `backend-java/src/main/resources/graphql/schema.graphqls`

### Key Configuration Files
- **Backend Config:** `backend-java/src/main/resources/application.yml`
- **Frontend Config:** `frontend-react/vite.config.ts`
- **Database:** `docker-compose.yml`

---

## 📖 How to Use This Documentation

### For New Developers
1. Read [`project-overview.md`](./project-overview.md) for context
2. Follow [`development-guide.md`](./development-guide.md) to set up locally
3. Review [`source-tree-analysis.md`](./source-tree-analysis.md) to understand structure
4. Reference [`api-documentation.md`](./api-documentation.md) for API integration

### For Frontend Developers
1. Start with frontend section of [`project-overview.md`](./project-overview.md)
2. Review [`api-documentation.md`](./api-documentation.md) GraphQL section
3. Check UX specs: `_bmad-output/planning-artifacts/ux-design-specification.md`
4. Reference implementation stories: `_bmad-output/implementation-artifacts/`

### For Backend Developers
1. Review [`source-tree-analysis.md`](./source-tree-analysis.md) backend structure
2. Study N-Layer architecture: `project_docs/ARCHITECTURE.md`
3. Reference GraphQL schema: `backend-java/src/main/resources/graphql/schema.graphqls`
4. Check implementation stories for context

### For DevOps/Deployment
1. Review deployment section of [`project-overview.md`](./project-overview.md)
2. Check `docker-compose.yml` for infrastructure
3. Review environment variables in [`development-guide.md`](./development-guide.md)

### For Project Managers
1. Review feature completion in [`project-overview.md`](./project-overview.md)
2. Check sprint status: `_bmad-output/implementation-artifacts/sprint-status.yaml`
3. Review PRD: `project_docs/PRD.md`
4. Check retrospectives: `_bmad-output/implementation-artifacts/epic-3-retro-2026-01-09.md`

---

## 🆘 Getting Help

**Troubleshooting:** See [`development-guide.md`](./development-guide.md) troubleshooting section

**Common Issues:**
- Database connection errors → Check MySQL is running
- CORS errors → Verify frontend URL in `SecurityConfig.java`
- Authentication errors → Check JWT token format and expiration
- GraphQL errors → Use GraphQL Playground to test queries

**Additional Resources:**
- Implementation stories: `_bmad-output/implementation-artifacts/`
- Bug reports: `_bmad-output/bugs/`
- Changelog: `docs/CHANGELOG-2026-01-09.md`

---

## 📝 Documentation Maintenance

**Generated By:** BMAD Document-Project Workflow  
**Scan Type:** Exhaustive  
**Generation Date:** 2026-01-09  
**Documentation Version:** 1.0

**To Regenerate Documentation:**
```
Use @bmad/bmm/workflows/document-project
```

**Last Updated:** 2026-01-09  
**Next Review:** After Epic 4 completion

---

## 🎯 Key Takeaways

1. **Multi-Part Architecture:** Backend (Spring Boot Java) + Frontend (React TypeScript)
2. **Hybrid API:** REST for admin/auth, GraphQL for marketplace, WebSocket for chat (planned)
3. **Production-Ready:** Authentication, marketplace, favorites, search, caching all implemented
4. **Well-Documented:** 26+ documentation files, code comments, comprehensive specs
5. **Test Coverage:** Unit + integration tests for both backend and frontend
6. **Modern Stack:** Java 17, Spring Boot 3.2, React 18, TypeScript 5, Vite 5, TailwindCSS 3

---

## 📄 Documentation Files

| File | Description | Lines |
|------|-------------|-------|
| [`index.md`](./index.md) | This file - master documentation index | - |
| [`project-overview.md`](./project-overview.md) | Executive summary & project overview | ~500 |
| [`source-tree-analysis.md`](./source-tree-analysis.md) | Complete file structure & organization | ~600 |
| [`development-guide.md`](./development-guide.md) | Setup, build, test, debug, troubleshoot | ~700 |
| [`api-documentation.md`](./api-documentation.md) | REST + GraphQL API reference | ~900 |
| [`project-scan-report.json`](./project-scan-report.json) | Workflow state & metadata | JSON |

**Additional Documentation (Project Root):**
- `project_docs/PRD.md` - Product requirements (1,983 lines)
- `project_docs/ARCHITECTURE.md` - Technical architecture (2,310 lines)
- `project_docs/TASKS.md` - Implementation plan (1,988 lines)
- `SEED_DATA.md` - Test data documentation
- `docs/CHANGELOG-2026-01-09.md` - Latest changes

---

**End of Documentation Index**

**Start Here:** [`project-overview.md`](./project-overview.md) → [`development-guide.md`](./development-guide.md)

**Need Help?** Check troubleshooting in [`development-guide.md`](./development-guide.md) or review implementation stories in `_bmad-output/implementation-artifacts/`

