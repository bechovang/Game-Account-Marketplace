# 📋 Junior Developer Stories - Simplified

**For:** Junior Developers learning to build the Game Account Marketplace
**Focus:** REST API, GraphQL, Role-Based Access Control
**Copy these cards directly to Trello!**

---

## 🎯 How to Use These Stories

1. **Copy entire card** to Trello
2. **Assign to junior** developer
3. **Junior follows checklist** from top to bottom
4. **Mark checkboxes** as they complete tasks
5. **Move card** through columns: To Do → In Progress → Code Review → Done

---

## 📦 Epic 1: Authentication (8 Stories)

### Story 1.1: Project Setup

```
## Story 1.1: Project Structure & Docker Setup

### 🎯 What to Build
- Git repository with backend/ and frontend/ folders
- Docker containers for MySQL and Redis

### ✅ Checklist
- [ ] Create GitHub repository
- [ ] Create folders: backend-java/, frontend-react/
- [ ] Create docker-compose.yml with MySQL 8.0 and Redis 7.0
- [ ] Create .gitignore file
- [ ] Create README.md with setup instructions
- [ ] Test: docker-compose up -d works
- [ ] Test: MySQL accessible on port 3306
- [ ] Test: Redis accessible on port 6379

### 📁 docker-compose.yml Template
```yaml
services:
  mysql:
    image: mysql:8.0
    ports:
      - "3306:3306"
    environment:
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_DATABASE: marketplace
    volumes:
      - mysql_data:/var/lib/mysql

  redis:
    image: redis:7.0
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data

volumes:
  mysql_data:
  redis_data:
```

**Time:** 1 day | **Assigned:** @JuniorDev1
**Blocks:** All other stories (do this first!)
```

---

### Story 1.2: Backend Setup

```
## Story 1.2: Spring Boot Backend Setup

### 🎯 What to Build
Spring Boot 3 project with MySQL connection

### ✅ Checklist
- [ ] Create pom.xml with Spring Boot dependencies
- [ ] Create application.yml with MySQL config
- [ ] Create folder structure: controller/, service/, repository/, entity/
- [ ] Create MarketplaceApplication.java main class
- [ ] Test: mvn clean install works
- [ ] Test: mvn spring-boot:run starts application
- [ ] Test: Application connects to MySQL (check logs)

### 📦 Required Dependencies (pom.xml)
```xml
<dependencies>
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
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>
</dependencies>
```

### ⚙️ application.yml Template
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/marketplace
    username: root
    password: root123
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

server:
  port: 8080
```

**Time:** 1 day | **Assigned:** @JuniorDev1
**Depends:** Story 1.1 ✅
```

---

### Story 1.3: Frontend Setup

```
## Story 1.3: React + TypeScript Frontend Setup

### 🎯 What to Build
React 18 app with TypeScript and Tailwind CSS

### ✅ Checklist
- [ ] Run: npm create vite@latest frontend-react -- --template react-ts
- [ ] Install dependencies: npm install
- [ ] Install: npm install react-router-dom @apollo/client graphql axios tailwindcss
- [ ] Configure Tailwind CSS
- [ ] Create folder structure: components/, pages/, contexts/, hooks/
- [ ] Test: npm run dev starts on port 3000
- [ ] Test: Tailwind CSS works (add className="text-red-500")

### 📁 Folder Structure
```
src/
├── components/     # Reusable components
├── pages/          # Page components
├── contexts/       # React contexts
├── hooks/          # Custom hooks
├── services/       # API calls
└── types/          # TypeScript types
```

**Time:** 1 day | **Assigned:** @JuniorDev2
**Depends:** Story 1.1 ✅
```

---

### Story 1.4: User Entity

```
## Story 1.4: User Database Entity

### 🎯 What to Build
User.java that saves users to MySQL database

### ✅ Checklist
- [ ] Create User.java in entity/ folder
- [ ] Add fields: id, email, password, fullName, role, status
- [ ] Add JPA annotations: @Entity, @Table, @Id
- [ ] Create UserRole enum: BUYER, SELLER, ADMIN
- [ ] Create UserStatus enum: ACTIVE, BANNED
- [ ] Create UserRepository.java interface
- [ ] Add method: findByEmail(String email)
- [ ] Enable JPA Auditing: @EnableJpaAuditing
- [ ] Test: Application starts, creates "users" table in MySQL

### 👤 User Entity Template
```java
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String fullName;

    @Enumerated(EnumType.STRING)
    private UserRole role;  // BUYER, SELLER, ADMIN

    @Enumerated(EnumType.STRING)
    private UserStatus status;  // ACTIVE, BANNED

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```

### 📋 Role Meanings
- **BUYER**: Can buy game accounts
- **SELLER**: Can sell game accounts
- **ADMIN**: Can approve accounts and ban users

**Time:** 1 day | **Assigned:** @JuniorDev3
**Depends:** Story 1.2 ✅
```

---

### Story 1.5: Security + JWT

```
## Story 1.5: JWT Authentication

### 🎯 What to Build
JWT token generation and security configuration

### ✅ Checklist
- [ ] Create SecurityConfig.java in config/ folder
- [ ] Create JwtTokenProvider.java in util/ folder
- [ ] Create JwtAuthenticationFilter.java
- [ ] Add password encoder bean (BCrypt)
- [ ] Configure public endpoints: /api/auth/**
- [ ] Configure private endpoints: /api/**
- [ ] Test: Can access /api/auth/login without token
- [ ] Test: Cannot access /api/accounts without token

### 🔐 Public vs Private Endpoints
```
PUBLIC (No Token Needed):
  POST /api/auth/register
  POST /api/auth/login
  GET /api/accounts

PRIVATE (Need JWT Token):
  GET /api/auth/me
  POST /api/accounts
  PUT /api/accounts/{id}
  DELETE /api/accounts/{id}
```

### 🎫 JWT Token Flow
1. User logs in → Server generates token
2. Client saves token in localStorage
3. Client sends token in header: `Authorization: Bearer {token}`
4. Server validates token → allows access

### 🔧 JwtTokenProvider Methods
```java
// Generate token from email
String generateToken(String email);

// Validate token
boolean validateToken(String token);

// Get email from token
String extractEmail(String token);
```

**Time:** 2 days | **Assigned:** @JuniorDev1
**Depends:** Story 1.2 ✅, Story 1.4 ✅
```

---

### Story 1.6: AuthService

```
## Story 1.6: Authentication Service

### 🎯 What to Build
Service layer for login, register, profile operations

### ✅ Checklist
- [ ] Create AuthService.java in service/ folder
- [ ] Create DTOs: RegisterRequest, LoginRequest, AuthResponse
- [ ] Implement register() method
- [ ] Implement login() method
- [ ] Implement getProfile() method
- [ ] Implement updateProfile() method
- [ ] Add password hashing (BCrypt)
- [ ] Add email validation
- [ ] Test: Register creates new user
- [ ] Test: Login returns JWT token

### 📝 AuthService Methods
```java
@Service
public class AuthService {

    // Register new user
    public AuthResponse register(RegisterRequest request) {
        // 1. Check if email exists
        // 2. Hash password
        // 3. Create user
        // 4. Generate JWT token
        // 5. Return token + user info
    }

    // Login user
    public AuthResponse login(LoginRequest request) {
        // 1. Find user by email
        // 2. Verify password
        // 3. Generate JWT token
        // 4. Return token + user info
    }

    // Get current user profile
    public UserResponse getProfile(String email) {
        // 1. Find user by email
        // 2. Return user info (no password)
    }
}
```

### 🔒 Password Hashing
```java
// Hash password before saving
String hashedPassword = passwordEncoder.encode(rawPassword);

// Verify password when logging in
boolean matches = passwordEncoder.matches(rawPassword, hashedPassword);
```

**Time:** 2 days | **Assigned:** @JuniorDev3
**Depends:** Story 1.4 ✅, Story 1.5 ✅
```

---

### Story 1.7: REST API - Auth

```
## Story 1.7: Authentication REST API

### 🎯 What to Build
REST endpoints for authentication

### ✅ Checklist
- [ ] Create AuthController.java in controller/ folder
- [ ] Implement POST /api/auth/register
- [ ] Implement POST /api/auth/login
- [ ] Implement GET /api/auth/me (needs token)
- [ ] Implement PUT /api/auth/me (needs token)
- [ ] Add validation: @Valid, @Email, @NotNull
- [ ] Test with Postman: All endpoints work

### 📡 API Endpoints

**Register**
```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "fullName": "John Doe"
}

Response 201:
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "fullName": "John Doe",
    "role": "BUYER"
  }
}
```

**Login**
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}

Response 200:
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "user": {...}
}
```

**Get Profile** (Requires Token)
```http
GET /api/auth/me
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...

Response 200:
{
  "id": 1,
  "email": "user@example.com",
  "fullName": "John Doe",
  "role": "BUYER",
  "status": "ACTIVE"
}
```

### 🚫 Error Responses
- **400 Bad Request**: Invalid input
- **401 Unauthorized**: Wrong password or no token
- **409 Conflict**: Email already exists

**Time:** 2 days | **Assigned:** @JuniorDev4
**Depends:** Story 1.5 ✅, Story 1.6 ✅
```

---

### Story 1.8: GraphQL - User Schema

```
## Story 1.8: GraphQL User Schema

### 🎯 What to Build
GraphQL schema and resolvers for users

### ✅ Checklist
- [ ] Create schema.graphqls in resources/graphql/
- [ ] Define User type
- [ ] Define user queries: users, user
- [ ] Define user mutations: createUser, updateUser
- [ ] Create UserQuery.java resolver
- [ ] Create UserMutation.java resolver
- [ ] Test in GraphiQL: All queries work

### 📊 GraphQL Schema
```graphql
type User {
  id: ID!
  email: String!
  fullName: String
  role: String!      # BUYER, SELLER, ADMIN
  status: String!    # ACTIVE, BANNED
  createdAt: String!
}

type Query {
  users: [User!]!
  user(id: ID!): User
}

type Mutation {
  createUser(email: String!, password: String!, fullName: String!): User!
  updateUser(id: ID!, fullName: String): User!
}
```

### 🔍 Example Queries
```graphql
# Get all users (ADMIN only)
query {
  users {
    id
    email
    fullName
    role
  }
}

# Get single user
query {
  user(id: "1") {
    id
    email
    fullName
    role
  }
}

# Create user
mutation {
  createUser(
    email: "test@example.com"
    password: "pass123"
    fullName: "Test User"
  ) {
    id
    email
    role
  }
}
```

### 🔒 Role-Based Access
- **ADMIN**: Can query all users
- **BUYER/SELLER**: Can only query own user

**Time:** 2 days | **Assigned:** @JuniorDev4
**Depends:** Story 1.4 ✅
```

---

## 📦 Epic 2: Account Listings (6 Stories)

### Story 2.1: Account Entity

```
## Story 2.1: Game & Account Entities

### 🎯 What to Build
Game and Account entities for marketplace listings

### ✅ Checklist
- [ ] Create Game.java entity
- [ ] Create GameRepository
- [ ] Create Account.java entity
- [ ] Create AccountRepository
- [ ] Add @ManyToOne relationships (seller, game)
- [ ] Test: Entities save to MySQL correctly

### 🎮 Game Entity
```java
@Entity
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;           // "Liên Quân", "Valorant"
    private String slug;           // "lien-quan", "valorant"
    private String iconUrl;

    @OneToMany(mappedBy = "game")
    private List<Account> accounts;
}
```

### 🎴 Account Entity
```java
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private BigDecimal price;
    private Integer level;
    private String rank;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;  // PENDING, APPROVED, SOLD

    @ManyToOne
    private User seller;

    @ManyToOne
    private Game game;

    @ElementCollection
    private List<String> images;
}
```

**Time:** 2 days | **Assigned:** @JuniorDev3
**Depends:** Story 1.4 ✅
```

---

### Story 2.2: AccountService

```
## Story 2.2: Account Business Logic

### 🎯 What to Build
Service layer for account CRUD operations

### ✅ Checklist
- [ ] Create AccountService.java
- [ ] Implement createAccount()
- [ ] Implement updateAccount()
- [ ] Implement deleteAccount()
- [ ] Implement getAccount()
- [ ] Implement searchAccounts() with filters
- [ ] Add ownership check (only owner can edit)
- [ ] Add @Cacheable for searchAccounts()

### 📝 Service Methods
```java
@Service
public class AccountService {

    // Create new account (SELLER only)
    public Account createAccount(AccountRequest request, String sellerEmail) {
        // 1. Validate seller is SELLER
        // 2. Create account with PENDING status
        // 3. Save to database
        // 4. Return account
    }

    // Update account (owner only)
    public Account updateAccount(Long id, AccountRequest request, String email) {
        // 1. Find account
        // 2. Check ownership (seller.email == email)
        // 3. Update fields
        // 4. Save and return
    }

    // Delete account (owner or ADMIN only)
    public void deleteAccount(Long id, String email) {
        // 1. Find account
        // 2. Check permission
        // 3. Delete
    }

    // Search with filters
    @Cacheable("accounts")
    public Page<Account> searchAccounts(
        String gameSlug,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Pageable pageable
    ) {
        // Build query with filters
        // Return paginated results
    }
}
```

**Time:** 2 days | **Assigned:** @JuniorDev1
**Depends:** Story 2.1 ✅
```

---

### Story 2.3: GraphQL - Accounts

```
## Story 2.3: Account GraphQL API

### 🎯 What to Build
GraphQL queries and mutations for accounts

### ✅ Checklist
- [ ] Add Account type to schema.graphqls
- [ ] Add Game type to schema.graphqls
- [ ] Create AccountQuery resolver
- [ ] Create AccountMutation resolver
- [ ] Implement DataLoader for N+1 prevention
- [ ] Test in GraphiQL

### 📊 GraphQL Schema
```graphql
type Account {
  id: ID!
  title: String!
  description: String
  price: Float!
  level: Int
  rank: String
  status: String!      # PENDING, APPROVED, SOLD
  seller: User!
  game: Game!
  images: [String!]
  createdAt: String!
}

type Game {
  id: ID!
  name: String!
  slug: String!
  iconUrl: String
}

type Query {
  accounts(first: Int, after: String): AccountConnection!
  account(id: ID!): Account
  games: [Game!]!
}

type Mutation {
  createAccount(input: CreateAccountInput!): Account!
  updateAccount(id: ID!, input: UpdateAccountInput!): Account!
  deleteAccount(id: ID!): Boolean!
}
```

### 🔍 Example Query
```graphql
query GetAccounts {
  accounts(first: 10) {
    edges {
      node {
        id
        title
        price
        seller {
          fullName
        }
        game {
          name
        }
      }
    }
  }
}
```

### 🔒 Role Rules
- **BUYER**: Can view all accounts
- **SELLER**: Can create, update own accounts
- **ADMIN**: Can delete any account

**Time:** 2 days | **Assigned:** @JuniorDev4
**Depends:** Story 2.1 ✅
```

---

### Story 2.4: REST API - Accounts

```
## Story 2.4: Account REST Endpoints

### 🎯 What to Build
REST API for seller account operations

### ✅ Checklist
- [ ] Create AccountController.java
- [ ] POST /api/accounts (SELLER only)
- [ ] PUT /api/accounts/{id} (owner only)
- [ ] DELETE /api/accounts/{id} (owner only)
- [ ] GET /api/seller/my-accounts (SELLER only)
- [ ] GET /api/accounts (public)
- [ ] Add file upload for images
- [ ] Test with Postman

### 📡 REST Endpoints

**Create Account** (SELLER only)
```http
POST /api/accounts
Authorization: Bearer {token}
Content-Type: multipart/form-data

title: "Rank Kim Cương Liên Quân"
description: "Account rank KC, 50 tướng..."
price: 500000
gameId: 1
images: [file1.jpg, file2.jpg]

Response 201:
{
  "id": 1,
  "title": "Rank Kim Cương...",
  "price": 500000,
  "status": "PENDING",
  "seller": {...},
  "game": {...}
}
```

**Update Account** (Owner only)
```http
PUT /api/accounts/1
Authorization: Bearer {token}

{
  "title": "Updated title",
  "price": 600000
}

Response 200:
{
  "id": 1,
  "title": "Updated title",
  ...
}
```

**Get My Accounts** (SELLER only)
```http
GET /api/seller/my-accounts
Authorization: Bearer {token}

Response 200:
[
  {
    "id": 1,
    "title": "...",
    "status": "PENDING"
  }
]
```

### 🔒 Security Checks
```java
// In controller:
@PreAuthorize("hasRole('SELLER')")
public Account createAccount(...) { }

// In service:
if (!account.getSeller().getEmail().equals(currentEmail)) {
    throw new ForbiddenException("You can only edit your own accounts");
}
```

**Time:** 2 days | **Assigned:** @JuniorDev3
**Depends:** Story 2.2 ✅, Story 1.7 ✅
```

---

### Story 2.5: Frontend - Account Pages

```
## Story 2.5: Frontend Account Pages

### 🎯 What to Build
React pages for browsing and managing accounts

### ✅ Checklist
- [ ] Create HomePage.tsx (browse accounts)
- [ ] Create AccountDetailPage.tsx (view single account)
- [ ] Create CreateListingPage.tsx (seller creates listing)
- [ ] Create MyListingsPage.tsx (seller's listings)
- [ ] Add Apollo GraphQL queries
- [ ] Add forms with validation
- [ ] Test: User can browse accounts
- [ ] Test: Seller can create listing

### 📄 Page Components

**HomePage.tsx**
```typescript
// GraphQL Query
const GET_ACCOUNTS = gql`
  query GetAccounts($gameSlug: String, $minPrice: Float) {
    accounts(gameSlug: $gameSlug, minPrice: $minPrice) {
      id
      title
      price
      game { name }
      seller { fullName }
    }
  }
`;

// Display accounts in grid
// Add filter sidebar
// Add search bar
```

**AccountDetailPage.tsx**
```typescript
// GraphQL Query
const GET_ACCOUNT = gql`
  query GetAccount($id: ID!) {
    account(id: $id) {
      id
      title
      description
      price
      seller { fullName, rating }
      game { name }
      images
    }
  }
`;

// Display full account details
// Add "Buy Now" button
// Add "Add to Favorites" button
```

**CreateListingPage.tsx**
```typescript
// Form with fields:
// - Title
// - Description
// - Price
// - Game (dropdown)
// - Level
// - Rank
// - Images (upload)

// GraphQL Mutation:
const CREATE_ACCOUNT = gql`
  mutation CreateAccount($input: CreateAccountInput!) {
    createAccount(input: $input) {
      id
      title
    }
  }
`;
```

**Time:** 3 days | **Assigned:** @JuniorDev2
**Depends:** Story 1.3 ✅, Story 2.3 ✅, Story 2.4 ✅
```

---

## 📦 Epic 4: Admin Features (3 Stories)

### Story 4.1: Admin REST API

```
## Story 4.1: Admin Management API

### 🎯 What to Build
REST endpoints for admin operations

### ✅ Checklist
- [ ] Create AdminController.java
- [ ] GET /api/admin/users (get all users)
- [ ] PUT /api/admin/users/{id}/status (ban/unban)
- [ ] PUT /api/admin/accounts/{id}/status (approve/reject)
- [ ] GET /api/admin/stats (platform statistics)
- [ ] Add @PreAuthorize("hasRole('ADMIN')") to all methods
- [ ] Test with Postman

### 📡 Admin Endpoints

**Get All Users** (ADMIN only)
```http
GET /api/admin/users
Authorization: Bearer {admin-token}

Response 200:
[
  {
    "id": 1,
    "email": "user@example.com",
    "role": "BUYER",
    "status": "ACTIVE"
  }
]
```

**Ban User** (ADMIN only)
```http
PUT /api/admin/users/1/status
Authorization: Bearer {admin-token}

{
  "status": "BANNED"
}

Response 200:
{
  "id": 1,
  "status": "BANNED"
}
```

**Approve Account** (ADMIN only)
```http
PUT /api/admin/accounts/1/status
Authorization: Bearer {admin-token}

{
  "status": "APPROVED"
}

Response 200:
{
  "id": 1,
  "status": "APPROVED"
}
```

**Platform Statistics** (ADMIN only)
```http
GET /api/admin/stats
Authorization: Bearer {admin-token}

Response 200:
{
  "totalUsers": 150,
  "totalAccounts": 500,
  "pendingApprovals": 12,
  "totalRevenue": 25000000
}
```

### 🔒 Role Check
```java
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @GetMapping("/users")
    public List<User> getAllUsers() { ... }

    @PutMapping("/users/{id}/status")
    public User updateUserStatus(@PathVariable Long id, @RequestParam String status) {
        // BAN or ACTIVE
    }
}
```

**Time:** 2 days | **Assigned:** @JuniorDev3
**Depends:** Story 1.7 ✅, Story 2.4 ✅
```

---

### Story 4.2: Role-Based Security

```
## Story 4.2: Complete Role-Based Access Control

### 🎯 What to Build
Ensure all endpoints have proper role checks

### ✅ Checklist
- [ ] Review all REST endpoints
- [ ] Add @PreAuthorize to controllers
- [ ] Add role checks in services
- [ ] Test each endpoint with different roles
- [ ] Document role permissions

### 🔒 Role Matrix

| Endpoint | BUYER | SELLER | ADMIN |
|----------|-------|--------|-------|
| Auth | ✅ | ✅ | ✅ |
| View Accounts | ✅ | ✅ | ✅ |
| Create Account | ❌ | ✅ | ✅ |
| Edit Own Account | ❌ | ✅ | ✅ |
| Edit Any Account | ❌ | ❌ | ✅ |
| Ban User | ❌ | ❌ | ✅ |
| Approve Account | ❌ | ❌ | ✅ |
| View Stats | ❌ | ❌ | ✅ |

### 🛡️ Implementation Examples

**Controller Level:**
```java
// Only sellers can create
@PreAuthorize("hasRole('SELLER')")
public Account createAccount(...) { }

// Only admins can ban users
@PreAuthorize("hasRole('ADMIN')")
public User banUser(@PathVariable Long id) { }

// Anyone authenticated can view
@PreAuthorize("isAuthenticated()")
public Account getAccount(@PathVariable Long id) { }
```

**Service Level:**
```java
public Account updateAccount(Long id, AccountRequest request, String email) {
    Account account = findById(id);

    // Check ownership
    if (!account.getSeller().getEmail().equals(email) &&
        !currentUserIsAdmin()) {
        throw new ForbiddenException("Access denied");
    }

    return accountRepository.save(account);
}
```

**Time:** 2 days | **Assigned:** @JuniorDev1
**Depends:** Story 1.5 ✅, Story 4.1 ✅
```

---

## 📚 Quick Reference for Juniors

### Common Patterns

**1. Create Entity**
```java
@Entity
@Table(name = "table_name")
public class EntityName {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // ... fields
}
```

**2. Create Repository**
```java
public interface EntityRepository extends JpaRepository<Entity, Long> {
    Optional<Entity> findBySomeField(String field);
}
```

**3. Create Service**
```java
@Service
public class EntityService {
    @Autowired
    private EntityRepository repository;

    public Entity create(Request request) {
        Entity entity = new Entity();
        // ... set fields
        return repository.save(entity);
    }
}
```

**4. Create REST Controller**
```java
@RestController
@RequestMapping("/api/entities")
public class EntityController {

    @PostMapping
    public ResponseEntity<Entity> create(@RequestBody Request request) {
        return ResponseEntity.ok(service.create(request));
    }
}
```

**5. Create GraphQL Resolver**
```java
@Controller
public class EntityQuery implements GraphQLQueryResolver {
    public List<Entity> entities() {
        return repository.findAll();
    }
}
```

### Git Commands (Daily Use)
```bash
# Start new story
git checkout develop
git pull origin develop
git checkout -b feature/1.4-user-entity

# Work and commit
git add .
git commit -m "[1.4] Add User entity"

# Push and create PR
git push -u origin feature/1.4-user-entity
# Create PR on GitHub
```

### Testing Commands
```bash
# Backend
mvn clean install        # Compile
mvn test                 # Run tests
mvn spring-boot:run      # Start app

# Frontend
npm run dev             # Start dev server
npm run build           # Build for production
npm run lint            # Check code
```

---

## ✅ Definition of Done

A story is done when:
- [ ] All checkboxes checked
- [ ] Code compiles without errors
- [ ] All tests pass (`mvn test` or `npm test`)
- [ ] Manual testing completed (Postman or browser)
- [ ] Pull request created
- [ ] Code reviewed by Lead
- [ ] PR merged to develop

---

**Good luck! Ask questions early. Don't struggle for >30 minutes alone!** 🚀
