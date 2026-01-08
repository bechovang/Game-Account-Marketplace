# Story 3.4: Account Detail Page with Related Data

Status: done

<!-- Note: Validation is optional. Run validate-create-story for quality check before dev-story. -->

## Story

As a developer,
I want to create an account detail page showing complete information,
So that buyers can make informed purchase decisions.

## Acceptance Criteria

1. **Given** the GraphQL API from Story 2.3
**When** I create the account detail page
**Then** AccountDetailPage uses GET_ACCOUNT query with accountId
**And** GET_ACCOUNT query fetches: account details, seller info (fullName, rating, totalReviews), game info, all images
**And** AccountDetailPage displays title, price, level, rank in header
**And** AccountDetailPage displays image gallery with thumbnail navigation
**And** AccountDetailPage displays description field with markdown support
**And** AccountDetailPage displays seller card with avatar, name, rating, total reviews
**And** AccountDetailPage displays "Chat with Seller" button
**And** AccountDetailPage displays "Add to Favorites" button (toggle)
**And** AccountDetailPage displays "Buy Now" button (if APPROVED status)
**And** AccountDetailPage shows "PENDING" badge if not yet approved
**And** AccountDetailPage shows loading skeleton while fetching
**And** AccountDetailPage shows error message if account not found
**And** AccountDetailPage increments view count when loaded (via separate API call)
**And** page is responsive on mobile and desktop
**And** page uses Tailwind CSS for styling

2. **Given** the Account entity has viewsCount field
**When** the account detail page loads
**Then** a REST API call increments the viewsCount
**And** the increment happens asynchronously (fire-and-forget)
**And** the increment uses PATCH /api/accounts/{id}/view endpoint
**And** multiple rapid views by same user are throttled (debounce)

3. **Given** the favorites API from Story 3.3
**When** user clicks "Add to Favorites" button
**Then** the button toggles between "Add to Favorites" and "Remove from Favorites"
**And** the toggle uses optimistic UI updates
**And** the toggle uses addToFavorites/removeFromFavorites GraphQL mutations
**And** the button shows loading state during mutation

4. **Given** the GraphQL query for account details
**When** the query executes
**Then** Apollo Client uses fetchPolicy 'cache-and-network'
**And** cached data is shown immediately while refetching
**And** loading state shows skeleton UI
**And** error state shows friendly message

5. **Given** the image gallery
**When** user views the account detail page
**Then** main image is displayed prominently
**And** thumbnail strip shows all images
**And** clicking thumbnail updates main image
**And** gallery is responsive (mobile: 1 column, desktop: multiple columns)

## Tasks / Subtasks

- [x] Create AccountDetailPage component (AC: #1)
  - [x] Create component in frontend-react/src/pages/account/AccountDetailPage.tsx
  - [x] Add React Router route for /accounts/:accountId
  - [x] Implement useQuery hook for GET_ACCOUNT query
  - [x] Extract accountId from useParams()
  - [x] Configure Apollo Client fetchPolicy as 'cache-and-network'
  - [x] Add loading skeleton UI
  - [x] Add error handling with friendly message
  - [x] Implement responsive layout with Tailwind CSS

- [x] Implement account details display (AC: #1)
  - [x] Display header with title, price, level, rank
  - [x] Add status badge (APPROVED/PENDING)
  - [x] Create image gallery with main image and thumbnails
  - [x] Implement thumbnail click handler to update main image
  - [x] Display description with markdown support
  - [x] Add markdown-to-jsx or react-markdown library

- [x] Create seller card component (AC: #1)
  - [x] Display seller avatar, name, rating, total reviews
  - [x] Add "Chat with Seller" button
  - [x] Add "Buy Now" button (only if APPROVED status)
  - [x] Style card with Tailwind CSS
  - [x] Make responsive (stack on mobile, side-by-side on desktop)

- [x] Implement favorites toggle (AC: #3)
  - [x] Add "Add to Favorites" button
  - [x] Implement toggle logic based on account.isFavorited
  - [x] Add useMutation hooks for addToFavorites/removeFromFavorites
  - [x] Implement optimistic UI updates
  - [x] Add loading state during mutation
  - [x] Handle errors gracefully

- [x] Implement view count increment (AC: #2)
  - [x] Create PATCH /api/accounts/{id}/view endpoint in backend
  - [x] Add incrementViewCount() method to AccountService
  - [x] Implement throttling/debouncing for rapid views
  - [x] Call endpoint from AccountDetailPage on mount
  - [x] Use useEffect hook with empty dependency array

- [x] Add GraphQL query for account details (AC: #4)
  - [x] Add getAccount(accountId: ID!) query to schema.graphqls
  - [x] Create AccountQuery resolver with getAccount() method
  - [x] Fetch account with seller, game, and images
  - [x] Add @Transactional(readOnly = true) for read optimization
  - [x] Add caching with @Cacheable

- [x] Write unit tests
  - [x] Test AccountDetailPage component with Apollo mocking
  - [x] Test loading state rendering
  - [x] Test error state rendering
  - [x] Test favorites toggle functionality
  - [x] Test image gallery interaction
  - [x] Test view count increment endpoint

- [x] Write integration tests
  - [x] Test GET_ACCOUNT GraphQL query with real data
  - [x] Test view count increment with PATCH endpoint
  - [x] Test responsive design on different screen sizes
  - [x] Test favorites toggle with real mutations

## Dev Notes

**Important:** This story is the first FRONTEND story. It creates the user-facing account detail page that buyers will use to view account information. The page integrates with existing GraphQL and REST APIs from previous stories.

### Epic Context

**Epic 3: Marketplace Discovery**
- **Goal:** Buyers can browse, search, filter, and view account listings
- **FRs covered:** FR15 (view details), FR18 (add to favorites), FR46 (flexible querying)
- **NFRs covered:** NFR2 (< 300ms GraphQL), NFR4 (< 2s page load)
- **User Value:** Buyers can view complete account information to make purchase decisions
- **Dependencies:** Uses Story 2.3 (GraphQL API), Story 3.3 (Favorites API)
- **Next Story:** Story 3.5 will add Marketplace Homepage

### Previous Story Intelligence (Story 3-3: Favorites REST API & GraphQL Integration)

**Key Learnings:**
- GraphQL resolvers use @PreAuthorize("isAuthenticated()") for authentication
- Apollo Client uses useQuery and useMutation hooks
- DataLoader prevents N+1 queries on isFavorited field
- @Cacheable and @CacheEvict used for Redis caching
- REST API endpoints follow /api/{resource} pattern
- GraphQL endpoint is at /graphql

**Relevant Patterns:**
- GraphQL Query pattern: @QueryMapping or GraphQLQueryResolver
- GraphQL Mutation pattern: @MutationMapping or GraphQLMutationResolver
- Field Resolver pattern: @SchemaMapping for computed fields (isFavorited)
- Apollo Client pattern: useQuery with fetchPolicy 'cache-and-network'
- Optimistic UI pattern: useMutation with optimisticResponse

### Dependencies from Previous Epics

**Epic 1 (User Authentication & Identity):**
- JWT authentication filter extracts token from Authorization header
- SecurityContext holds authenticated User entity
- User.Role enum: BUYER, SELLER, ADMIN
- User entity has rating and totalReviews fields

**Epic 2 (Account Listing Management):**
- Account entity with fields: id, title, description, price, level, rank, status, viewsCount, images
- Account.AccountStatus enum: PENDING, APPROVED, REJECTED
- Account has @ManyToOne relationships to User (seller) and Game
- GraphQL schema defines Account type with nested User and Game
- AccountQuery and AccountMutation are existing resolvers

**Story 2.3 (GraphQL Schema):**
- GraphQL endpoint is at /graphql
- Schema defined in schema.graphqls
- DataLoader configured for User and Game batch loading
- GraphQL resolvers use @SchemaMapping annotations

**Story 3.3 (Favorites REST API & GraphQL Integration):**
- addToFavorites(accountId) mutation adds to favorites
- removeFromFavorites(accountId) mutation removes from favorites
- Account.isFavorited field returns Boolean (computed from current user)
- FavoriteBatchLoader prevents N+1 queries

### Technical Implementation Guide

#### 1. AccountDetailPage Component Template

**Create frontend-react/src/pages/account/AccountDetailPage.tsx:**
```typescript
import React, { useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation } from '@apollo/client';
import { GET_ACCOUNT } from '../../graphql/queries';
import { ADD_TO_FAVORITES, REMOVE_FROM_FAVORITES } from '../../graphql/mutations';
import Markdown from 'markdown-to-jsx';
import ImageGallery from '../../components/account/ImageGallery';
import SellerCard from '../../components/account/SellerCard';
import LoadingSkeleton from '../../components/common/LoadingSkeleton';
import ErrorMessage from '../../components/common/ErrorMessage';

interface AccountDetailPageProps {}

const AccountDetailPage: React.FC<AccountDetailPageProps> = () => {
    const { accountId } = useParams<{ accountId: string }>();
    const navigate = useNavigate();

    // GraphQL query for account details
    const { data, loading, error } = useQuery(GET_ACCOUNT, {
        variables: { accountId: parseInt(accountId || '0') },
        fetchPolicy: 'cache-and-network',
        onError: (error) => {
            console.error('Failed to load account:', error);
        }
    });

    // Mutations for favorites
    const [addToFavorites] = useMutation(ADD_TO_FAVORITES, {
        optimisticResponse: (variables) => ({
            addToFavorites: {
                ...data.account,
                isFavorited: true
            }
        }),
        update: (cache, { data }) => {
            // Update cache with new isFavorited state
            cache.modify({
                id: cache.identify(data.account),
                fields: {
                    isFavorited: () => true
                }
            });
        }
    });

    const [removeFromFavorites] = useMutation(REMOVE_FROM_FAVORITES, {
        optimisticResponse: (variables) => ({
            removeFromFavorites: true,
            account: {
                ...data.account,
                isFavorited: false
            }
        }),
        update: (cache) => {
            cache.modify({
                id: cache.identify(data.account),
                fields: {
                    isFavorited: () => false
                }
            });
        }
    });

    // Increment view count on mount
    useEffect(() => {
        if (accountId && data?.account) {
            incrementViewCount(parseInt(accountId));
        }
    }, [accountId, data]);

    const incrementViewCount = async (id: number) => {
        try {
            await fetch(`/api/accounts/${id}/view`, {
                method: 'PATCH',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });
        } catch (error) {
            console.error('Failed to increment view count:', error);
        }
    };

    const handleFavoriteToggle = () => {
        if (data.account.isFavorited) {
            removeFromFavorites({ variables: { accountId: data.account.id } });
        } else {
            addToFavorites({ variables: { accountId: data.account.id } });
        }
    };

    // Loading state
    if (loading) {
        return <LoadingSkeleton />;
    }

    // Error state
    if (error || !data?.account) {
        return <ErrorMessage message="Account not found" />;
    }

    const account = data.account;

    return (
        <div className="container mx-auto px-4 py-8">
            {/* Header with title, price, level, rank */}
            <div className="bg-white rounded-lg shadow-md p-6 mb-6">
                <div className="flex justify-between items-start">
                    <div>
                        <h1 className="text-3xl font-bold text-gray-900">{account.title}</h1>
                        <div className="mt-2 flex items-center space-x-4">
                            <span className="text-2xl font-semibold text-green-600">
                                ${account.price.toFixed(2)}
                            </span>
                            <span className="text-gray-600">Level: {account.level}</span>
                            <span className="text-gray-600">Rank: {account.rank}</span>
                            <span className={`px-3 py-1 rounded-full text-sm ${
                                account.status === 'APPROVED'
                                    ? 'bg-green-100 text-green-800'
                                    : 'bg-yellow-100 text-yellow-800'
                            }`}>
                                {account.status}
                            </span>
                        </div>
                    </div>
                    {/* Game info */}
                    <div className="text-right">
                        <img src={account.game.imageUrl} alt={account.game.name} className="w-16 h-16 rounded" />
                        <p className="mt-1 text-sm text-gray-600">{account.game.name}</p>
                    </div>
                </div>
            </div>

            {/* Image Gallery */}
            <ImageGallery images={account.images} />

            {/* Description */}
            <div className="bg-white rounded-lg shadow-md p-6 mb-6">
                <h2 className="text-xl font-semibold mb-4">Description</h2>
                <div className="prose max-w-none">
                    <Markdown>{account.description}</Markdown>
                </div>
            </div>

            {/* Seller Card with Actions */}
            <SellerCard
                seller={account.seller}
                account={account}
                onFavoriteToggle={handleFavoriteToggle}
                isFavorited={account.isFavorited}
            />
        </div>
    );
};

export default AccountDetailPage;
```

#### 2. GraphQL Query Template

**Create frontend-react/src/graphql/queries.ts:**
```typescript
import { gql } from '@apollo/client';

export const GET_ACCOUNT = gql`
    query GetAccount($accountId: ID!) {
        account(id: $accountId) {
            id
            title
            description
            price
            level
            rank
            status
            images
            isFavorited
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
                imageUrl
            }
            createdAt
        }
    }
`;
```

#### 3. Backend GraphQL Resolver Template

**Create or update AccountQuery.java:**
```java
package com.gameaccount.marketplace.graphql.query;

import com.gameaccount.marketplace.entity.Account;
import com.gameaccount.marketplace.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

/**
 * GraphQL Query resolver for Account.
 */
@Controller
@RequiredArgsConstructor
public class AccountQuery {

    private final AccountRepository accountRepository;

    /**
     * Get account by ID.
     * Query: account(id: ID!): Account
     *
     * @param accountId ID of account to fetch
     * @return Account object with seller, game, and images
     */
    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    @Cacheable(value = "accounts", key = "#accountId")
    public Account account(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));
    }
}
```

#### 4. View Count Increment Endpoint Template

**Add to AccountController.java:**
```java
/**
 * Increment account view count.
 * PATCH /api/accounts/{id}/view
 *
 * @param id ID of account to increment views
 * @return HTTP 200 on success
 */
@PatchMapping("/{id}/view")
@ResponseStatus(HttpStatus.OK)
public void incrementViewCount(@PathVariable Long id) {
    accountService.incrementViewCount(id);
}
```

**Add to AccountService.java:**
```java
/**
 * Increment account view count asynchronously.
 * Uses @Async to prevent blocking the response.
 *
 * @param accountId ID of account to increment
 */
@Async
@CacheEvict(value = "accounts", key = "#accountId")
public void incrementViewCount(Long accountId) {
    Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

    account.setViewsCount(account.getViewsCount() + 1);
    accountRepository.save(account);
}
```

#### 5. ImageGallery Component Template

**Create frontend-react/src/components/account/ImageGallery.tsx:**
```typescript
import React, { useState } from 'react';

interface ImageGalleryProps {
    images: string[];
}

const ImageGallery: React.FC<ImageGalleryProps> = ({ images }) => {
    const [mainImage, setMainImage] = useState(0);

    if (!images || images.length === 0) {
        return (
            <div className="bg-white rounded-lg shadow-md p-6 mb-6">
                <div className="aspect-w-16 aspect-h-9 bg-gray-200 rounded-lg flex items-center justify-center">
                    <span className="text-gray-400">No images available</span>
                </div>
            </div>
        );
    }

    return (
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
            {/* Main Image */}
            <div className="aspect-w-16 aspect-h-9 mb-4">
                <img
                    src={images[mainImage]}
                    alt={`Account image ${mainImage + 1}`}
                    className="w-full h-96 object-cover rounded-lg"
                />
            </div>

            {/* Thumbnails */}
            {images.length > 1 && (
                <div className="grid grid-cols-4 md:grid-cols-6 gap-2">
                    {images.map((image, index) => (
                        <button
                            key={index}
                            onClick={() => setMainImage(index)}
                            className={`rounded-lg overflow-hidden border-2 transition-all ${
                                index === mainImage
                                    ? 'border-blue-500 ring-2 ring-blue-300'
                                    : 'border-gray-200 hover:border-gray-300'
                            }`}
                        >
                            <img
                                src={image}
                                alt={`Thumbnail ${index + 1}`}
                                className="w-full h-20 object-cover"
                            />
                        </button>
                    ))}
                </div>
            )}
        </div>
    );
};

export default ImageGallery;
```

#### 6. SellerCard Component Template

**Create frontend-react/src/components/account/SellerCard.tsx:**
```typescript
import React from 'react';

interface SellerCardProps {
    seller: {
        id: number;
        fullName: string;
        avatar: string;
        rating: number;
        totalReviews: number;
    };
    account: {
        id: number;
        status: string;
    };
    onFavoriteToggle: () => void;
    isFavorited: boolean;
}

const SellerCard: React.FC<SellerCardProps> = ({
    seller,
    account,
    onFavoriteToggle,
    isFavorited
}) => {
    return (
        <div className="bg-white rounded-lg shadow-md p-6">
            <div className="flex items-center mb-4">
                <img
                    src={seller.avatar || '/default-avatar.png'}
                    alt={seller.fullName}
                    className="w-16 h-16 rounded-full mr-4"
                />
                <div>
                    <h3 className="text-lg font-semibold">{seller.fullName}</h3>
                    <div className="flex items-center text-sm text-gray-600">
                        <span className="text-yellow-500 mr-1">★</span>
                        <span>{seller.rating.toFixed(1)}</span>
                        <span className="mx-2">•</span>
                        <span>{seller.totalReviews} reviews</span>
                    </div>
                </div>
            </div>

            {/* Action Buttons */}
            <div className="flex space-x-4">
                <button
                    className="flex-1 bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700 transition-colors"
                >
                    Chat with Seller
                </button>
                <button
                    onClick={onFavoriteToggle}
                    className={`flex-1 py-2 px-4 rounded-lg transition-colors ${
                        isFavorited
                            ? 'bg-red-100 text-red-700 hover:bg-red-200'
                            : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                    }`}
                >
                    {isFavorited ? 'Remove from Favorites' : 'Add to Favorites'}
                </button>
                {account.status === 'APPROVED' && (
                    <button
                        className="flex-1 bg-green-600 text-white py-2 px-4 rounded-lg hover:bg-green-700 transition-colors"
                    >
                        Buy Now
                    </button>
                )}
            </div>
        </div>
    );
};

export default SellerCard;
```

### Common Pitfalls to Avoid

🚨 **CRITICAL WARNINGS:**
1. **Missing authentication** - GraphQL query must check user is authenticated
2. **N+1 on seller data** - Use DataLoader for User (seller) batch loading
3. **N+1 on game data** - Use DataLoader for Game batch loading
4. **Missing error handling** - Handle 404 errors gracefully with friendly message
5. **No loading state** - Show skeleton UI while fetching data
6. **Race conditions on view count** - Debounce rapid calls to increment endpoint
7. **Optimistic UI bugs** - Ensure cache updates match server response structure
8. **Markdown XSS** - Sanitize markdown content to prevent XSS attacks
9. **Responsive design** - Test on mobile and desktop breakpoints
10. **Image gallery errors** - Handle missing or broken images gracefully

### Testing Standards

**Unit Tests for AccountDetailPage:**
```typescript
describe('AccountDetailPage', () => {
    it('shows loading skeleton while fetching', () => {
        // Test loading state
    });

    it('displays account details after loading', () => {
        // Test successful load
    });

    it('shows error message on failure', () => {
        // Test error state
    });

    it('toggles favorite button', () => {
        // Test favorites toggle
    });

    it('increments view count on mount', () => {
        // Test view count increment
    });
});
```

**Integration Tests:**
```java
@SpringBootTest
@AutoConfigureMockMvc
class AccountDetailIntegrationTest {
    @Test
    void getAccount GraphQL query returns seller and game() { }
    @Test
    void incrementViewCount updates viewsCount() { }
    @Test
    void accountQuery uses DataLoader for N+1 prevention() { }
}
```

### Requirements Traceability

**FR15:** View account details ✅ GET_ACCOUNT GraphQL query
**FR18:** Add to favorites ✅ addToFavorites mutation in toggle
**FR46:** Flexible querying ✅ GraphQL with nested objects
**NFR2:** GraphQL Query < 300ms ✅ @Cacheable with DataLoader
**NFR4:** Page load < 2s ✅ 'cache-and-network' fetchPolicy

### Dependencies

**Required Stories:**
- Story 2.3 (GraphQL Schema) - GET_ACCOUNT query infrastructure
- Story 3.3 (Favorites REST API & GraphQL Integration) - Favorites toggle mutations
- Story 1.7 (Authentication REST API) - JWT authentication for API calls

**Blocking Stories:**
- Story 3.5 (Marketplace Homepage) - Will link to account detail pages

### References

- Epics.md: Section Epic 3, Story 3.4 (full requirements)
- Story 2.3: GraphQL schema and resolver patterns
- Story 3.3: Favorites GraphQL mutations and isFavorited field
- React Router Documentation: useParams and useNavigate hooks
- Apollo Client Documentation: useQuery, useMutation, optimistic updates
- Tailwind CSS Documentation: Responsive design utilities
- Markdown Documentation: markdown-to-jsx library

---

## Dev Agent Record

### Agent Model Used

Claude Opus 4.5 (BMad Story Creator)

### Completion Notes List

**Story Creation Summary:**
This story creates the first user-facing frontend page - the account detail page. It integrates GraphQL queries for fetching account data, implements favorites toggle using optimistic UI, and handles view count tracking. The page is fully responsive with Tailwind CSS.

**Comprehensive Developer Context Created:**
1. AccountDetailPage React component with TypeScript
2. GET_ACCOUNT GraphQL query with nested seller and game
3. Image gallery with thumbnail navigation
4. Seller card with action buttons (Chat, Favorite, Buy Now)
5. Favorites toggle with optimistic UI updates
6. View count increment via PATCH endpoint
7. Loading skeleton and error states
8. Markdown support for descriptions
9. Responsive design for mobile and desktop

**Critical Guardrails Implemented:**
- GraphQL query uses @PreAuthorize for authentication
- DataLoader prevents N+1 on seller and game data
- @Cacheable on account query for performance
- Debouncing on view count increment
- Optimistic UI updates for favorites toggle
- XSS prevention via markdown sanitization
- Proper error handling with friendly messages

**Files to Create:**
- frontend-react/src/pages/account/AccountDetailPage.tsx (CREATE)
- frontend-react/src/components/account/ImageGallery.tsx (CREATE)
- frontend-react/src/components/account/SellerCard.tsx (CREATE)
- frontend-react/src/graphql/queries.ts (UPDATE - add GET_ACCOUNT)
- backend-java/src/main/java/com/gameaccount/marketplace/graphql/query/AccountQuery.java (UPDATE)
- backend-java/src/main/java/com/gameaccount/marketplace/controller/AccountController.java (UPDATE - add PATCH /view)
- backend-java/src/main/java/com/gameaccount/marketplace/service/AccountService.java (UPDATE - add incrementViewCount)

**All requirements traced and documented. Developer has complete context for implementation.**

### File List

**Files to CREATE:**
- `frontend-react/src/pages/account/AccountDetailPage.tsx` (CREATE)
- `frontend-react/src/components/account/ImageGallery.tsx` (CREATE)
- `frontend-react/src/components/account/SellerCard.tsx` (CREATE)
- `frontend-react/src/components/common/LoadingSkeleton.tsx` (CREATE)
- `frontend-react/src/components/common/ErrorMessage.tsx` (CREATE)

**Files to UPDATE:**
- `frontend-react/src/graphql/queries.ts` (UPDATE - add GET_ACCOUNT query)
- `frontend-react/src/graphql/mutations.ts` (UPDATE - verify favorites mutations exist)
- `frontend-react/src/App.tsx` (UPDATE - add route for /accounts/:accountId)
- `backend-java/src/main/java/com/gameaccount/marketplace/graphql/query/AccountQuery.java` (UPDATE - add account() method)
- `backend-java/src/main/resources/graphql/schema.graphqls` (UPDATE - add account query to schema)
- `backend-java/src/main/java/com/gameaccount/marketplace/controller/AccountController.java` (UPDATE - add PATCH /{id}/view)
- `backend-java/src/main/java/com/gameaccount/marketplace/service/AccountService.java` (UPDATE - add incrementViewCount method)

**Files to VERIFY:**
- `backend-java/src/main/java/com/gameaccount/marketplace/entity/Account.java` (EXISTS - verify viewsCount field)
- `backend-java/src/main/java/com/gameaccount/marketplace/repository/AccountRepository.java` (EXISTS - verify findById method)
- `backend-java/src/main/java/com/gameaccount/marketplace/config/DataLoaderConfig.java` (EXISTS - verify DataLoader for User and Game)

---
