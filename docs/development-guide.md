# Development Guide - Game Account Marketplace

**Version:** 1.0  
**Generated:** 2026-01-09  
**Target Audience:** Developers setting up local development environment

---

## Prerequisites

### Required Software

| Software | Version | Purpose | Download |
|----------|---------|---------|----------|
| **Java JDK** | 17 LTS | Backend runtime | https://adoptium.net/ |
| **Maven** | 3.9+ | Backend build tool | https://maven.apache.org/ |
| **Node.js** | 18.x or 20.x LTS | Frontend runtime | https://nodejs.org/ |
| **NPM** | 9.x+ | Frontend package manager | Included with Node.js |
| **MySQL** | 8.0+ | Primary database | https://www.mysql.com/ or Docker |
| **Redis** | 7.0+ | Caching layer | https://redis.io/ or Docker |
| **Docker** | Latest | Container orchestration (optional but recommended) | https://www.docker.com/ |
| **Git** | 2.x+ | Version control | https://git-scm.com/ |

### Optional Tools
- **IntelliJ IDEA** or **VS Code** - Recommended IDEs
- **Postman** or **Insomnia** - API testing
- **MySQL Workbench** or **DBeaver** - Database management
- **Redis Commander** - Redis GUI (optional)

---

## Quick Start (Docker Recommended)

### Option 1: Using Docker Compose (Easiest)

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd Game-Account-Marketplace
   ```

2. **Start infrastructure services (MySQL + Redis):**
   ```bash
   docker-compose up -d
   ```
   This starts:
   - MySQL on port 3306
   - Redis on port 6379

3. **Verify services are running:**
   ```bash
   docker-compose ps
   ```
   You should see both `gameaccount-mysql` and `gameaccount-redis` running.

4. **Start the backend:**
   ```bash
   cd backend-java
   mvn spring-boot:run
   ```
   Backend will start on `http://localhost:8080`

5. **Start the frontend (new terminal):**
   ```bash
   cd frontend-react
   npm install          # First time only
   npm run dev
   ```
   Frontend will start on `http://localhost:3000`

6. **Access the application:**
   - **Frontend:** http://localhost:3000
   - **Backend API:** http://localhost:8080/api
   - **GraphQL Playground:** http://localhost:8080/graphiql

---

## Detailed Setup Instructions

### Step 1: Database Setup (MySQL)

#### Using Docker (Recommended)
```bash
docker-compose up -d mysql
```

#### Manual Installation
1. Install MySQL 8.0+
2. Create database and user:
   ```sql
   CREATE DATABASE gameaccount_marketplace;
   CREATE USER 'appuser'@'localhost' IDENTIFIED BY 'apppassword';
   GRANT ALL PRIVILEGES ON gameaccount_marketplace.* TO 'appuser'@'localhost';
   FLUSH PRIVILEGES;
   ```

#### Load Seed Data
```bash
cd backend-java
mysql -u appuser -p gameaccount_marketplace < src/main/resources/seed_data.sql
```

**Seed Data Includes:**
- 8 games (League of Legends, Valorant, Mobile Legends, etc.)
- 9 test users (admin, 3 sellers, 3 buyers)
- 23 account listings
- 3 favorites

**Test Credentials:**
- Admin: `admin@test.com` / `password123`
- Seller: `seller1@test.com` / `password123`
- Buyer: `phuchcm2006@gmail.com` / `phuc2006`

### Step 2: Cache Setup (Redis)

#### Using Docker (Recommended)
```bash
docker-compose up -d redis
```

#### Manual Installation
1. Install Redis 7.0+
2. Start Redis server:
   ```bash
   redis-server
   ```
3. Verify Redis is running:
   ```bash
   redis-cli ping
   # Should return: PONG
   ```

### Step 3: Backend Setup (Spring Boot)

1. **Navigate to backend directory:**
   ```bash
   cd backend-java
   ```

2. **Configure environment variables (optional):**
   Create `.env` file or set system environment variables:
   ```bash
   DB_HOST=localhost
   DB_PORT=3306
   DB_NAME=gameaccount_marketplace
   DB_USERNAME=appuser
   DB_PASSWORD=apppassword
   REDIS_HOST=localhost
   REDIS_PORT=6379
   JWT_SECRET=MyVerySecretKeyForJWTTokenGenerationPleaseChangeThisInProduction
   JWT_EXPIRATION=86400000
   FRONTEND_URL=http://localhost:3000
   ```

3. **Build the project:**
   ```bash
   mvn clean install
   ```
   This will:
   - Download all dependencies
   - Compile source code
   - Run unit tests
   - Package JAR file

4. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```
   Or run the JAR directly:
   ```bash
   java -jar target/marketplace-backend-1.0.0.jar
   ```

5. **Verify backend is running:**
   ```bash
   curl http://localhost:8080/actuator/health
   ```
   Should return: `{"status":"UP"}`

6. **Access GraphQL Playground:**
   Open browser: http://localhost:8080/graphiql

### Step 4: Frontend Setup (React + Vite)

1. **Navigate to frontend directory:**
   ```bash
   cd frontend-react
   ```

2. **Install dependencies:**
   ```bash
   npm install
   ```
   This installs all packages from `package.json` (~13,000 files in `node_modules/`)

3. **Configure environment variables (optional):**
   Create `.env` file:
   ```bash
   VITE_API_BASE_URL=http://localhost:8080
   VITE_GRAPHQL_URL=http://localhost:8080/graphql
   VITE_WEBSOCKET_URL=ws://localhost:8080/ws
   ```

4. **Run development server:**
   ```bash
   npm run dev
   ```
   Vite dev server starts on `http://localhost:3000` with HMR (Hot Module Replacement)

5. **Build for production (optional):**
   ```bash
   npm run build
   ```
   Output: `dist/` folder with optimized bundles

6. **Preview production build:**
   ```bash
   npm run preview
   ```

---

## Environment Configuration

### Backend Configuration (`application.yml`)

**Default values:**
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/gameaccount_marketplace
    username: appuser
    password: apppassword
  jpa:
    hibernate:
      ddl-auto: update    # Auto-create/update schema
  data:
    redis:
      host: localhost
      port: 6379
server:
  port: 8080
jwt:
  secret: MyVerySecretKeyForJWTTokenGenerationPleaseChangeThisInProduction
  expiration: 86400000   # 24 hours
frontend:
  url: http://localhost:3000
```

**Override with environment variables:**
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`
- `REDIS_HOST`, `REDIS_PORT`
- `JWT_SECRET`, `JWT_EXPIRATION`
- `FRONTEND_URL`

### Frontend Configuration (`vite.config.ts`)

**Proxy configuration:**
```typescript
server: {
  port: 3000,
  proxy: {
    '/api': 'http://localhost:8080',
    '/graphql': 'http://localhost:8080',
    '/ws': {
      target: 'ws://localhost:8080',
      ws: true,
    },
  },
}
```

---

## Running Tests

### Backend Tests

**Run all tests:**
```bash
cd backend-java
mvn test
```

**Run specific test class:**
```bash
mvn test -Dtest=AccountServiceTest
```

**Run tests with coverage (JaCoCo):**
```bash
mvn clean test jacoco:report
```
Report: `target/site/jacoco/index.html`

**Skip tests during build:**
```bash
mvn clean install -DskipTests
# or
mvn clean install -Dmaven.test.skip=true
```

### Frontend Tests

**Run all tests:**
```bash
cd frontend-react
npm test
```

**Run tests in watch mode:**
```bash
npm run test:watch
```

**Run tests with coverage:**
```bash
npm run test:coverage
```

**Run specific test file:**
```bash
npm test AccountCard.test.tsx
```

---

## Build Process

### Backend Build

**Development build:**
```bash
mvn clean compile
```

**Production build:**
```bash
mvn clean package
```
Output: `target/marketplace-backend-1.0.0.jar`

**Run production JAR:**
```bash
java -jar target/marketplace-backend-1.0.0.jar
```

**Skip tests during build:**
```bash
mvn clean package -DskipTests
```

### Frontend Build

**Development mode:**
```bash
npm run dev    # Hot reload, source maps
```

**Production build:**
```bash
npm run build
```
Output: `dist/` folder with:
- Minified JS/CSS bundles
- Code-split chunks
- Optimized assets
- Source maps (optional)

**Analyze bundle size:**
```bash
npm run build -- --mode analyze
```

---

## Code Style & Linting

### Backend (Java)

**IntelliJ IDEA Code Style:**
- Java: Google Java Style Guide (modified)
- Indentation: 4 spaces
- Line width: 120 characters

**Lombok:**
- Use `@Data`, `@Builder`, `@RequiredArgsConstructor` where applicable
- Avoid `@AllArgsConstructor` + `@NoArgsConstructor` unless needed

**Naming Conventions:**
- Classes: PascalCase (e.g., `AccountService`)
- Methods: camelCase (e.g., `getUserById`)
- Constants: UPPER_SNAKE_CASE (e.g., `MAX_PAGE_SIZE`)

### Frontend (TypeScript/React)

**ESLint Configuration:**
```bash
npm run lint          # Check for issues
npm run lint -- --fix # Auto-fix issues
```

**Prettier (if configured):**
```bash
npm run format
```

**Naming Conventions:**
- Components: PascalCase (e.g., `AccountCard.tsx`)
- Hooks: camelCase with `use` prefix (e.g., `useFilters.ts`)
- Utilities: camelCase (e.g., `formatPrice.ts`)
- Types/Interfaces: PascalCase (e.g., `Account`, `User`)

**Component Structure:**
```typescript
// Imports
import React from 'react';

// Types
interface AccountCardProps {
  account: Account;
}

// Component
export function AccountCard({ account }: AccountCardProps) {
  // Hooks
  const [loading, setLoading] = useState(false);

  // Handlers
  const handleClick = () => { ... };

  // Render
  return <div>...</div>;
}
```

---

## Common Development Tasks

### Adding a New Entity (Backend)

1. **Create entity class:** `entity/NewEntity.java`
2. **Create repository:** `repository/NewEntityRepository.java`
3. **Create service:** `service/NewEntityService.java`
4. **Create DTOs:** `dto/request/`, `dto/response/`
5. **Create controller/resolver:** `controller/` or `graphql/`
6. **Write tests:** `test/.../service/`, `test/.../controller/`

### Adding a New Page (Frontend)

1. **Create page component:** `pages/NewPage.tsx`
2. **Add route:** Update `App.tsx` with new route
3. **Create feature components:** `components/feature/`
4. **Add GraphQL queries/mutations:** `services/graphql/`
5. **Write tests:** `NewPage.test.tsx`

### Adding a New GraphQL Query

**Backend:**
1. Update `schema.graphqls` with new query definition
2. Create query resolver: `graphql/query/NewQuery.java`
3. Implement service logic if needed

**Frontend:**
1. Add query to `services/graphql/queries.ts`
2. Use with `useQuery` hook in component

### Adding a New REST Endpoint

**Backend:**
1. Create DTO: `dto/request/NewRequest.java`, `dto/response/NewResponse.java`
2. Add service method: `service/SomeService.java`
3. Create controller method: `controller/SomeController.java` with `@PostMapping`, etc.
4. Update `SecurityConfig.java` if endpoint needs special auth

**Frontend:**
1. Add API call to `services/rest/axiosInstance.ts` or new file
2. Call from component or hook

---

## Debugging

### Backend Debugging

**IntelliJ IDEA:**
1. Set breakpoints in code
2. Click "Debug" button or `Shift+F9`
3. Application starts in debug mode on port 8080

**Remote Debugging:**
```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```
Then attach debugger to port 5005.

**Logging:**
- Logs printed to console (stdout)
- Log file: `backend.log` (if configured)
- Adjust log levels in `application.yml`:
  ```yaml
  logging:
    level:
      com.gameaccount.marketplace: DEBUG
      org.springframework.security: DEBUG
  ```

### Frontend Debugging

**Browser DevTools:**
- React DevTools extension
- Apollo Client DevTools extension
- Console logs: `console.log()`, `console.error()`

**VS Code Debugging:**
1. Install "Debugger for Chrome" extension
2. Add launch configuration:
   ```json
   {
     "type": "chrome",
     "request": "launch",
     "name": "Launch Chrome",
     "url": "http://localhost:3000",
     "webRoot": "${workspaceFolder}/frontend-react/src"
   }
   ```
3. Set breakpoints in `.tsx` files
4. Press `F5` to start debugging

---

## Database Management

### Accessing MySQL

**Via Docker:**
```bash
docker exec -it gameaccount-mysql mysql -u appuser -p
# Password: apppassword
```

**Via MySQL CLI:**
```bash
mysql -u appuser -p gameaccount_marketplace
```

**Common queries:**
```sql
-- Show tables
SHOW TABLES;

-- Describe table structure
DESCRIBE users;
DESCRIBE accounts;

-- Query data
SELECT * FROM users;
SELECT * FROM accounts WHERE status = 'APPROVED';
SELECT * FROM games;
SELECT * FROM favorites;

-- Check foreign keys
SELECT 
  CONSTRAINT_NAME, TABLE_NAME, COLUMN_NAME, 
  REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'gameaccount_marketplace'
  AND REFERENCED_TABLE_NAME IS NOT NULL;
```

### Accessing Redis

**Via Redis CLI:**
```bash
redis-cli
> PING
PONG
> KEYS *                    # List all keys
> GET someKey               # Get value
> TTL someKey               # Check expiration
> FLUSHALL                  # Clear all keys (careful!)
```

**Via Docker:**
```bash
docker exec -it gameaccount-redis redis-cli
```

---

## Troubleshooting

### Backend Issues

**Problem: `java.lang.ClassNotFoundException: com.mysql.cj.jdbc.Driver`**
- **Solution:** Ensure MySQL connector dependency is in `pom.xml` and run `mvn clean install`

**Problem: `Could not create connection to database server`**
- **Solution:** Check MySQL is running (`docker-compose ps` or `systemctl status mysql`)
- Verify connection details in `application.yml`

**Problem: `Redis connection refused`**
- **Solution:** Start Redis (`docker-compose up -d redis` or `redis-server`)
- Check Redis is on port 6379: `redis-cli ping`

**Problem: `Port 8080 already in use`**
- **Solution:** Kill process using port:
  ```bash
  # Windows
  netstat -ano | findstr :8080
  taskkill /PID <PID> /F

  # Linux/Mac
  lsof -i :8080
  kill -9 <PID>
  ```

**Problem: `JWT token invalid`**
- **Solution:** Check JWT secret matches between backend `application.yml` and frontend localStorage token

### Frontend Issues

**Problem: `npm install` fails**
- **Solution:** Clear cache and retry:
  ```bash
  npm cache clean --force
  rm -rf node_modules package-lock.json
  npm install
  ```

**Problem: `CORS error`**
- **Solution:** Check backend `SecurityConfig.java` CORS configuration includes `http://localhost:3000`

**Problem: `GraphQL query returns null`**
- **Solution:** Check network tab, verify backend is running, check authentication token is present

**Problem: `Module not found: Can't resolve '@/...'`**
- **Solution:** Check `tsconfig.json` paths configuration and ensure file exists

---

## Performance Monitoring

### Backend Monitoring

**Spring Boot Actuator:**
- Health: http://localhost:8080/actuator/health
- Metrics: http://localhost:8080/actuator/metrics
- Cache metrics: http://localhost:8080/actuator/caches

**JVM Monitoring:**
```bash
# Monitor heap usage
jmap -heap <PID>

# GC logs
java -Xlog:gc* -jar target/marketplace-backend-1.0.0.jar
```

### Frontend Monitoring

**Vite Build Analysis:**
```bash
npm run build -- --mode analyze
```

**Bundle size:**
```bash
npm run build
ls -lh dist/assets/
```

**Lighthouse Audit:**
1. Open Chrome DevTools
2. Navigate to "Lighthouse" tab
3. Run audit on http://localhost:3000

---

## Hot Reload & Development Experience

### Backend Hot Reload

**Spring Boot DevTools (if configured):**
- Auto-restart on class changes
- LiveReload browser extension support

**Manual reload:**
- Stop application (`Ctrl+C`)
- Recompile: `mvn compile`
- Restart: `mvn spring-boot:run`

### Frontend Hot Reload

**Vite HMR (automatic):**
- Edit `.tsx`, `.ts`, `.css` files
- Changes reflect instantly in browser
- State preserved where possible

---

## Git Workflow

### Branch Strategy
- **main:** Production-ready code
- **develop:** Integration branch
- **feature/***:** New features
- **epic/***:** Epic branches

### Commit Message Format
```
type(scope): subject

body (optional)

footer (optional)
```

**Types:** `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

**Examples:**
```
feat(auth): add JWT refresh token mechanism

fix(favorites): resolve Apollo cache update issue

docs(readme): update setup instructions
```

### Before Committing
```bash
# Backend
cd backend-java
mvn clean test         # Run tests
mvn clean package      # Ensure build passes

# Frontend
cd frontend-react
npm test               # Run tests
npm run lint           # Check linting
npm run build          # Ensure build passes
```

---

## Deployment

### Docker Deployment (Future)

**Build Docker images:**
```bash
# Backend
cd backend-java
docker build -t gameaccount-backend:latest .

# Frontend
cd frontend-react
docker build -t gameaccount-frontend:latest .
```

**Run with Docker Compose:**
```bash
docker-compose up -d
```

---

## Additional Resources

- **PRD:** `project_docs/PRD.md`
- **Architecture:** `project_docs/ARCHITECTURE.md`
- **Tasks:** `project_docs/TASKS.md`
- **API Documentation:** `/graphiql` for GraphQL
- **Seed Data:** `SEED_DATA.md`
- **Changelog:** `docs/CHANGELOG-2026-01-09.md`

---

## Getting Help

1. Check existing documentation in `project_docs/` and `docs/`
2. Review implementation artifacts in `_bmad-output/`
3. Inspect code comments and JavaDoc/JSDoc
4. Use GraphQL Playground for API exploration
5. Check logs (`backend.log` and browser console)

---

**End of Development Guide**  
**Last Updated:** 2026-01-09  
**Version:** 1.0

