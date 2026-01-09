# API Documentation - Game Account Marketplace

**Version:** 1.0  
**Generated:** 2026-01-09  
**Base URL:** `http://localhost:8080`

---

## API Architecture Overview

The Game Account Marketplace uses a **hybrid API strategy**:

| API Type | Purpose | Base Path | Format |
|----------|---------|-----------|--------|
| **REST API** | Authentication, Admin operations, File uploads | `/api/*` | JSON |
| **GraphQL API** | Marketplace queries, Complex filtering | `/graphql` | GraphQL |
| **WebSocket** | Real-time chat, Notifications (planned) | `/ws` | STOMP/SockJS |

---

## Authentication

All authenticated endpoints require a **JWT token** in the `Authorization` header:

```http
Authorization: Bearer <jwt_token>
```

### Obtaining a Token

**Login:**
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "email": "user@example.com",
  "role": "BUYER"
}
```

**Token Expiration:** 24 hours (86400000ms)  
**Token Subject:** User's email address  
**Token Claims:** Role (BUYER/SELLER/ADMIN)

---

## REST API Endpoints

### Authentication Endpoints

#### POST /api/auth/register
Register a new user account.

**Request:**
```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "newuser@example.com",
  "password": "securepassword",
  "fullName": "John Doe"
}
```

**Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 5,
  "email": "newuser@example.com",
  "role": "BUYER"
}
```

**Validation Rules:**
- Email: Valid email format, unique
- Password: Minimum 6 characters
- Full Name: Required

#### POST /api/auth/login
Authenticate existing user.

**Request:**
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "email": "user@example.com",
  "role": "SELLER"
}
```

**Error Responses:**
- `400 Bad Request`: Invalid credentials
- `401 Unauthorized`: Authentication failed

---

### User Profile Endpoints

#### GET /api/users/profile
Get authenticated user's profile.

**Request:**
```http
GET /api/users/profile
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "id": 1,
  "email": "user@example.com",
  "fullName": "John Doe",
  "avatar": "https://...",
  "role": "SELLER",
  "balance": 1250.00,
  "rating": 4.5,
  "totalReviews": 23,
  "createdAt": "2026-01-05T10:30:00Z"
}
```

---

### Account Management Endpoints (Seller)

#### POST /api/accounts
Create a new account listing. (Multipart form data for images)

**Request:**
```http
POST /api/accounts
Authorization: Bearer <token>
Content-Type: multipart/form-data

gameId: 1
title: "Diamond 3 - 120 Skins"
description: "High-quality account with rare skins"
level: 120
rank: "DIAMOND"
price: 450.00
images: [file1.jpg, file2.jpg, file3.jpg]
```

**Response (201 Created):**
```json
{
  "id": 24,
  "gameId": 1,
  "title": "Diamond 3 - 120 Skins",
  "description": "High-quality account with rare skins",
  "level": 120,
  "rank": "DIAMOND",
  "price": 450.00,
  "status": "PENDING",
  "images": [
    "https://storage.../image1.jpg",
    "https://storage.../image2.jpg"
  ],
  "createdAt": "2026-01-09T15:00:00Z"
}
```

#### PUT /api/accounts/{id}
Update an existing account listing (seller must own the account).

**Request:**
```http
PUT /api/accounts/24
Authorization: Bearer <token>
Content-Type: multipart/form-data

title: "Diamond 3 - 130 Skins (Updated)"
price: 475.00
```

**Response (200 OK):** Updated account object

#### DELETE /api/accounts/{id}
Delete an account listing (seller must own the account).

**Request:**
```http
DELETE /api/accounts/24
Authorization: Bearer <token>
```

**Response (204 No Content)**

#### PATCH /api/accounts/{id}/view
Increment view count (public endpoint - no authentication required).

**Request:**
```http
PATCH /api/accounts/24/view
```

**Response (200 OK):**
```json
{
  "success": true,
  "newViewCount": 156
}
```

---

### Favorites Endpoints

#### GET /api/favorites
Get authenticated user's favorite accounts.

**Request:**
```http
GET /api/favorites
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "favorites": [
    {
      "id": 1,
      "accountId": 5,
      "account": {
        "id": 5,
        "title": "Immortal 2 - All Agents",
        "price": 280.00,
        "game": { "name": "Valorant" }
      },
      "createdAt": "2026-01-08T12:00:00Z"
    }
  ]
}
```

#### POST /api/favorites
Add an account to favorites.

**Request:**
```http
POST /api/favorites
Authorization: Bearer <token>
Content-Type: application/json

{
  "accountId": 7
}
```

**Response (201 Created):**
```json
{
  "id": 4,
  "accountId": 7,
  "userId": 1,
  "createdAt": "2026-01-09T15:30:00Z"
}
```

#### DELETE /api/favorites/{accountId}
Remove an account from favorites.

**Request:**
```http
DELETE /api/favorites/7
Authorization: Bearer <token>
```

**Response (204 No Content)**

---

## GraphQL API

**Endpoint:** `/graphql`  
**Playground:** `/graphiql` (http://localhost:8080/graphiql)

### Authentication

GraphQL requests also require JWT token:
```http
POST /graphql
Authorization: Bearer <token>
Content-Type: application/json
```

### Schema Overview

#### Core Types

**User:**
```graphql
type User {
  id: ID!
  email: String!
  fullName: String
  avatar: String
  role: Role!
  status: UserStatus!
  rating: Float!
  totalReviews: Int!
  balance: Float!
  createdAt: String!
  updatedAt: String!
}
```

**Account:**
```graphql
type Account {
  id: ID!
  seller: User!
  game: Game!
  title: String!
  description: String
  level: Int
  rank: String
  price: Float!
  status: AccountStatus!
  viewsCount: Int!
  isFeatured: Boolean!
  isFavorited: Boolean!        # User-specific field (requires auth)
  images: [String!]!
  createdAt: String!
  updatedAt: String!
}
```

**Game:**
```graphql
type Game {
  id: ID!
  name: String!
  slug: String!
  description: String
  iconUrl: String
  accountCount: Int!
  createdAt: String!
}
```

**Paginated Response:**
```graphql
type PaginatedAccounts {
  content: [Account!]!
  totalElements: Int!
  totalPages: Int!
  currentPage: Int!
  pageSize: Int!
}
```

#### Enums

```graphql
enum AccountStatus {
  PENDING
  APPROVED
  REJECTED
  SOLD
}

enum Role {
  BUYER
  SELLER
  ADMIN
}

enum UserStatus {
  ACTIVE
  BANNED
  SUSPENDED
}
```

---

### Queries

#### accounts - Browse marketplace

Search and filter game accounts with pagination.

**Query:**
```graphql
query GetAccounts($gameId: ID, $minPrice: Float, $maxPrice: Float, $status: AccountStatus, $page: Int, $limit: Int) {
  accounts(
    gameId: $gameId
    minPrice: $minPrice
    maxPrice: $maxPrice
    status: $status
    page: $page
    limit: $limit
  ) {
    content {
      id
      title
      price
      level
      rank
      images
      status
      isFavorited
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
    totalElements
    totalPages
    currentPage
    pageSize
  }
}
```

**Variables:**
```json
{
  "gameId": "1",
  "minPrice": 50.0,
  "maxPrice": 500.0,
  "status": "APPROVED",
  "page": 0,
  "limit": 20
}
```

**Response:**
```json
{
  "data": {
    "accounts": {
      "content": [
        {
          "id": "5",
          "title": "Diamond 3 - 120 Skins",
          "price": 450.0,
          "level": 120,
          "rank": "DIAMOND",
          "images": ["https://..."],
          "status": "APPROVED",
          "isFavorited": true,
          "seller": {
            "id": "2",
            "fullName": "John Seller",
            "rating": 4.5
          },
          "game": {
            "id": "1",
            "name": "League of Legends",
            "slug": "league-of-legends"
          }
        }
      ],
      "totalElements": 15,
      "totalPages": 1,
      "currentPage": 0,
      "pageSize": 20
    }
  }
}
```

**Supported Filters:**
- `gameId`: Filter by game
- `minPrice`, `maxPrice`: Price range
- `minLevel`, `maxLevel`: Level range
- `rank`: Filter by player rank
- `status`: Filter by approval status
- `isFeatured`: Show only featured accounts
- `q`: Full-text search in title and description
- `sortBy`: Sort field (price, level, createdAt)
- `sortDirection`: ASC or DESC
- `page`: Page number (0-indexed)
- `limit`: Results per page (max 100)

#### account - Get single account

**Query:**
```graphql
query GetAccount($id: ID!) {
  account(id: $id) {
    id
    title
    description
    price
    level
    rank
    images
    status
    viewsCount
    isFeatured
    isFavorited
    createdAt
    seller {
      id
      fullName
      avatar
      rating
      totalReviews
    }
    game {
      id
      name
      slug
      iconUrl
    }
  }
}
```

**Variables:**
```json
{
  "id": "5"
}
```

#### games - Get all games

**Query:**
```graphql
query GetGames {
  games {
    id
    name
    slug
    description
    iconUrl
    accountCount
  }
}
```

**Response:**
```json
{
  "data": {
    "games": [
      {
        "id": "1",
        "name": "League of Legends",
        "slug": "league-of-legends",
        "description": "MOBA game by Riot Games",
        "iconUrl": "https://.../lol.png",
        "accountCount": 5
      },
      {
        "id": "2",
        "name": "Valorant",
        "slug": "valorant",
        "description": "Tactical FPS by Riot Games",
        "iconUrl": "https://.../valorant.png",
        "accountCount": 4
      }
    ]
  }
}
```

#### favorites - Get user's favorites

**Query:**
```graphql
query GetFavorites($page: Int, $limit: Int) {
  favorites(page: $page, limit: $limit) {
    content {
      id
      title
      price
      level
      rank
      images
      isFavorited
      game {
        name
      }
      seller {
        fullName
        rating
      }
    }
    totalElements
    totalPages
    currentPage
    pageSize
  }
}
```

**Authentication Required:** Yes

**Variables:**
```json
{
  "page": 0,
  "limit": 20
}
```

---

### Mutations

#### createAccount - Create listing

**Mutation:**
```graphql
mutation CreateAccount($input: CreateAccountInput!) {
  createAccount(input: $input) {
    id
    title
    price
    status
    createdAt
  }
}
```

**Variables:**
```json
{
  "input": {
    "gameId": "1",
    "title": "Gold 1 Starter Account",
    "description": "Great starter account",
    "level": 45,
    "rank": "GOLD",
    "price": 45.0,
    "images": ["https://storage.../img1.jpg"]
  }
}
```

**Authentication Required:** Yes (SELLER or ADMIN role)

#### updateAccount - Update listing

**Mutation:**
```graphql
mutation UpdateAccount($id: ID!, $input: UpdateAccountInput!) {
  updateAccount(id: $id, input: $input) {
    id
    title
    price
    updatedAt
  }
}
```

**Variables:**
```json
{
  "id": "24",
  "input": {
    "title": "Gold 1 Starter Account (Updated)",
    "price": 50.0
  }
}
```

**Authentication Required:** Yes (must own account or be ADMIN)

#### deleteAccount - Delete listing

**Mutation:**
```graphql
mutation DeleteAccount($id: ID!) {
  deleteAccount(id: $id)
}
```

**Variables:**
```json
{
  "id": "24"
}
```

**Response:**
```json
{
  "data": {
    "deleteAccount": true
  }
}
```

**Authentication Required:** Yes (must own account or be ADMIN)

#### addToFavorites - Add to wishlist

**Mutation:**
```graphql
mutation AddToFavorites($accountId: ID!) {
  addToFavorites(accountId: $accountId) {
    id
    title
    isFavorited
  }
}
```

**Variables:**
```json
{
  "accountId": "7"
}
```

**Response:**
```json
{
  "data": {
    "addToFavorites": {
      "id": "7",
      "title": "Platinum 1 Account",
      "isFavorited": true
    }
  }
}
```

**Authentication Required:** Yes

#### removeFromFavorites - Remove from wishlist

**Mutation:**
```graphql
mutation RemoveFromFavorites($accountId: ID!) {
  removeFromFavorites(accountId: $accountId)
}
```

**Variables:**
```json
{
  "accountId": "7"
}
```

**Response:**
```json
{
  "data": {
    "removeFromFavorites": true
  }
}
```

**Authentication Required:** Yes

---

## Error Responses

### REST API Errors

**Format:**
```json
{
  "timestamp": "2026-01-09T15:45:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Email already registered",
  "path": "/api/auth/register"
}
```

**HTTP Status Codes:**
- `200 OK`: Success
- `201 Created`: Resource created successfully
- `204 No Content`: Success with no response body
- `400 Bad Request`: Invalid request data
- `401 Unauthorized`: Authentication required or failed
- `403 Forbidden`: Insufficient permissions
- `404 Not Found`: Resource not found
- `500 Internal Server Error`: Server error

### GraphQL Errors

**Format:**
```json
{
  "errors": [
    {
      "message": "Account not found with ID: 999",
      "locations": [{"line": 2, "column": 3}],
      "path": ["account"],
      "extensions": {
        "classification": "DataFetchingException"
      }
    }
  ],
  "data": null
}
```

**Common GraphQL Errors:**
- `ValidationError`: Invalid input data
- `DataFetchingException`: Error fetching data from database
- `UnauthorizedException`: Authentication required
- `ForbiddenException`: Insufficient permissions

---

## Rate Limiting

**Status:** Planned (not yet implemented)

**Planned Limits:**
- Authentication endpoints: 10 requests/minute
- GraphQL queries: 100 requests/minute
- GraphQL mutations: 50 requests/minute

---

## Caching

### Backend Caching

**Redis Cache:**
- Game list: 1 hour TTL
- Account queries: 10 minutes TTL
- Featured accounts: 5 minutes TTL

**Cache Headers:**
```http
Cache-Control: public, max-age=600
ETag: "33a64df551425fcc55e4d42a148795d9f25f89d4"
```

### Client-Side Caching

**Apollo Client:**
- Normalized cache for GraphQL responses
- Cache-first fetch policy for games
- Network-only for account queries (real-time data)

---

## CORS Configuration

**Allowed Origins:**
- Development: `http://localhost:3000`
- Production: TBD

**Allowed Methods:**
- GET, POST, PUT, DELETE, PATCH, OPTIONS

**Allowed Headers:**
- Authorization, Content-Type, Accept

**Exposed Headers:**
- All headers (`*`)

**Credentials:** Allowed

---

## WebSocket API (Planned)

**Endpoint:** `ws://localhost:8080/ws`

**Authentication:** JWT token in handshake

**Topics:**
- `/topic/chat/{accountId}` - Chat messages
- `/topic/notifications/{userId}` - User notifications
- `/queue/typing/{accountId}` - Typing indicators

---

## Testing the API

### Using cURL

**Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"phuchcm2006@gmail.com","password":"phuc2006"}'
```

**GraphQL Query:**
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"query":"{ games { id name } }"}'
```

### Using GraphQL Playground

1. Open http://localhost:8080/graphiql
2. Add authorization header:
   ```json
   {
     "Authorization": "Bearer <token>"
   }
   ```
3. Write and execute queries

---

## API Best Practices

### For REST API
1. Always include `Content-Type: application/json`
2. Use JWT token for authenticated endpoints
3. Handle errors gracefully (check status codes)
4. Use appropriate HTTP methods (GET, POST, PUT, DELETE, PATCH)

### For GraphQL API
1. Request only the fields you need
2. Use variables for dynamic queries
3. Batch multiple queries using aliases
4. Handle errors in `errors` array
5. Use DataLoader-friendly query patterns (avoid nested loops)

---

## API Versioning

**Current Version:** v1 (implicit)

**Future Versioning Strategy:**
- REST: Path versioning (`/api/v2/...`)
- GraphQL: Field deprecation with `@deprecated` directive

---

**End of API Documentation**  
**Last Updated:** 2026-01-09  
**Version:** 1.0

---

**For interactive API exploration, visit:**
- **GraphQL Playground:** http://localhost:8080/graphiql
- **Swagger UI:** (planned) http://localhost:8080/swagger-ui.html

