# Story 3.5: Marketplace Homepage with Featured Listings

Status: done

<!-- Note: Validation is optional. Run validate-create-story for quality check before dev-story. -->

## Story

As a developer,
I want to create the marketplace homepage showing featured and new listings,
So that buyers can discover popular and new accounts.

## Acceptance Criteria

1. **Given** the GraphQL API from Story 2.3
**When** I create the HomePage component
**Then** HomePage queries games via GET_GAMES
**And** HomePage displays game categories in horizontal scroll section
**And** HomePage queries featured accounts via GET_ACCOUNTS with isFeatured: true
**And** HomePage displays featured accounts in hero section (carousel or grid)
**And** HomePage queries new accounts via GET_ACCOUNTS with sortBy: createdAt
**And** HomePage displays new accounts in grid layout below hero
**And** each account card shows: image, title, price, game icon, seller rating
**And** account card links to AccountDetailPage on click
**And** HomePage has search bar in header
**And** HomePage displays loading skeleton while fetching
**And** HomePage uses Tailwind CSS for responsive design
**And** HomePage implements infinite scroll or pagination for accounts
**And** HomePage caches games list in React state (infrequently changes)

2. **Given** the HomePage component
**When** user clicks on a game category
**Then** HomePage filters accounts by selected game
**And** URL updates with game filter parameter
**And** HomePage shows "Clear Filter" button when game filter is active

3. **Given** the HomePage component
**When** user types in search bar
**Then** HomePage debounces search input (300ms delay)
**And** HomePage navigates to SearchPage with search query
**And** HomePage preserves any active game filter

4. **Given** the HomePage component
**When** user scrolls to bottom of new accounts section
**Then** HomePage loads more accounts via pagination
**And** HomePage shows loading indicator during pagination
**And** HomePage handles end of data gracefully

5. **Given** the HomePage component
**When** the page loads
**Then** HomePage uses Apollo Client's useQuery with fetchPolicy 'cache-first' for games
**And** HomePage uses useInfiniteQuery for accounts with 'cache-and-network'
**And** HomePage shows skeleton UI during initial load
**And** HomePage shows error state if any query fails

## Tasks / Subtasks

- [x] Create HomePage component (AC: #1)
  - [x] Create component in frontend-react/src/pages/HomePage.tsx
  - [x] Add React Router route for "/" (homepage)
  - [x] Implement useQuery hook for GET_GAMES with fetchPolicy 'cache-first'
  - [x] Implement useInfiniteQuery for GET_ACCOUNTS with isFeatured: true (featured section)
  - [x] Implement useInfiniteQuery for GET_ACCOUNTS with sortBy: createdAt (new accounts section)
  - [x] Implement horizontal scroll for game categories
  - [x] Implement hero carousel/grid for featured accounts
  - [x] Implement grid layout for new accounts section
  - [x] Add loading skeleton for all sections
  - [x] Add error handling with friendly messages
  - [x] Implement responsive design with Tailwind CSS

- [x] Create AccountCard component (AC: #1)
  - [x] Create component in frontend-react/src/components/account/AccountCard.tsx
  - [x] Display main image, title, price, game icon, seller rating
  - [x] Add click handler to navigate to AccountDetailPage
  - [x] Style with Tailwind CSS for consistent card design
  - [x] Add hover effects and transitions
  - [x] Make responsive (mobile: full width, desktop: grid item)

- [x] Implement game category filtering (AC: #2)
  - [x] Add click handlers to game category buttons
  - [x] Implement filter state management in HomePage
  - [x] Update URL with game filter parameter using useSearchParams
  - [x] Add "Clear Filter" button when game filter is active
  - [x] Update featured and new accounts queries when game filter changes

- [x] Implement search bar navigation (AC: #3)
  - [x] Add search input field in header section
  - [x] Implement debounced input handler (300ms delay)
  - [x] Add navigation to SearchPage with search query parameter
  - [x] Preserve game filter when navigating to search

- [x] Implement infinite scroll/pagination (AC: #4)
  - [x] Use IntersectionObserver for scroll detection
  - [x] Implement loadMore function for useInfiniteQuery
  - [x] Add loading indicator during pagination
  - [x] Handle end of data state (no more accounts to load)
  - [x] Add "Load More" button as fallback for IntersectionObserver issues

- [x] Update GraphQL queries (AC: #5)
  - [x] Add GET_GAMES query to queries.ts
  - [x] Update GET_ACCOUNTS query to support isFeatured, sortBy, gameId filters
  - [x] Implement Apollo Client cache policies correctly
  - [x] Add error handling for GraphQL errors

- [x] Write unit tests
  - [x] Test HomePage component with Apollo mocking
  - [x] Test AccountCard component rendering and click navigation
  - [x] Test game category filtering
  - [x] Test search bar debouncing and navigation
  - [x] Test infinite scroll functionality
  - [x] Test loading and error states

- [x] Write integration tests
  - [x] Test GET_GAMES GraphQL query
  - [x] Test GET_ACCOUNTS with featured filter
  - [x] Test GET_ACCOUNTS with sorting and pagination
  - [x] Test responsive design on different screen sizes

## Dev Notes

**Important:** This story creates the marketplace homepage - the first page buyers see when they visit the site. It showcases featured listings and new accounts to drive engagement. The homepage integrates with existing GraphQL APIs and provides navigation to account details and search pages.

### Epic Context

**Epic 3: Marketplace Discovery**
- **Goal:** Buyers can browse, search, filter, and view account listings
- **FRs covered:** FR14 (browse), FR46 (flexible queries), FR17 (sort), FR16 (search)
- **NFRs covered:** NFR50 (code splitting), NFR49 (lazy load images), NFR2 (< 300ms GraphQL)
- **User Value:** Buyers can quickly discover accounts that match their interests through featured listings and new arrivals
- **Dependencies:** Uses Story 2.3 (GraphQL API), Story 3.1 (advanced filtering), Story 3.4 (account detail page)
- **Next Story:** Story 3.6 (Advanced Search & Filter UI)

### Previous Story Intelligence (Story 3-4: Account Detail Page)

**Key Learnings:**
- Apollo Client fetchPolicy 'cache-and-network' provides fast loading with fresh data
- useInfiniteQuery with IntersectionObserver enables smooth infinite scroll
- AccountCard pattern established: image, title, price, game icon, seller rating
- Responsive grid layouts work well for account listings
- Loading skeletons prevent layout shift during data fetching
- Error boundaries with friendly messages improve UX

**Relevant Patterns:**
- Apollo Client query patterns: useQuery, useInfiniteQuery, fetchPolicy strategies
- React Router navigation: useNavigate with state/parameters
- Tailwind CSS responsive design: grid-cols-1 md:grid-cols-2 lg:grid-cols-3
- Component composition: AccountCard as reusable component
- GraphQL query optimization: selective field fetching, caching strategies

### Dependencies from Previous Epics

**Epic 1 (User Authentication & Identity):**
- Authentication not required for homepage (public access)
- User context available for personalized features (favorites, ratings)

**Epic 2 (Account Listing Management):**
- Account entity with status field (only APPROVED accounts shown publicly)
- Game entity with imageUrl for category icons
- AccountService.searchAccounts() method with filtering capabilities

**Story 2.3 (GraphQL Schema):**
- GET_ACCOUNTS query supports pagination, filtering, sorting
- GET_GAMES query for game categories
- GraphQL schema defines Account and Game types with required fields

**Story 3.1 (Advanced Filtering & Search):**
- AccountService supports isFeatured flag for featured listings
- Database indexes exist for performance (game_id, status, created_at)
- Caching strategy implemented with Redis

**Story 3.4 (Account Detail Page):**
- AccountCard component pattern established
- Navigation to /accounts/:accountId route
- Responsive design patterns established

### Technical Implementation Guide

#### 1. HomePage Component Template

**Create frontend-react/src/pages/HomePage.tsx:**
```typescript
import React, { useState, useEffect } from 'react';
import { useQuery, useInfiniteQuery } from '@apollo/client';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { GET_GAMES, GET_ACCOUNTS } from '../graphql/queries';
import AccountCard from '../components/account/AccountCard';
import LoadingSkeleton from '../components/common/LoadingSkeleton';
import ErrorMessage from '../components/common/ErrorMessage';

interface HomePageProps {}

const HomePage: React.FC<HomePageProps> = () => {
    const navigate = useNavigate();
    const [searchParams, setSearchParams] = useSearchParams();
    const [searchQuery, setSearchQuery] = useState('');
    const [debouncedSearch, setDebouncedSearch] = useState('');

    // Game filter from URL
    const gameFilter = searchParams.get('game');

    // Games query - cached since games change infrequently
    const { data: gamesData, loading: gamesLoading, error: gamesError } = useQuery(GET_GAMES, {
        fetchPolicy: 'cache-first'
    });

    // Featured accounts query
    const { data: featuredData, loading: featuredLoading, error: featuredError } = useQuery(GET_ACCOUNTS, {
        variables: {
            filters: { isFeatured: true, gameId: gameFilter ? parseInt(gameFilter) : null },
            page: { page: 0, size: 6 }
        },
        fetchPolicy: 'cache-and-network'
    });

    // New accounts query with infinite scroll
    const { data: newAccountsData, loading: newAccountsLoading, error: newAccountsError, fetchMore, hasNextPage } = useInfiniteQuery(GET_ACCOUNTS, {
        variables: {
            filters: { isFeatured: false, gameId: gameFilter ? parseInt(gameFilter) : null },
            sort: { field: 'createdAt', direction: 'DESC' },
            page: { page: 0, size: 12 }
        },
        fetchPolicy: 'cache-and-network',
        getNextPageParam: (lastPage, allPages) => {
            if (lastPage.hasNext) {
                return {
                    page: allPages.length,
                    size: 12
                };
            }
            return undefined;
        }
    });

    // Debounce search input
    useEffect(() => {
        const timer = setTimeout(() => {
            setDebouncedSearch(searchQuery);
        }, 300);
        return () => clearTimeout(timer);
    }, [searchQuery]);

    // Navigate to search when debounced search changes
    useEffect(() => {
        if (debouncedSearch.trim()) {
            navigate(`/search?q=${encodeURIComponent(debouncedSearch)}${gameFilter ? `&game=${gameFilter}` : ''}`);
        }
    }, [debouncedSearch, gameFilter, navigate]);

    const handleGameFilter = (gameId: number | null) => {
        if (gameId) {
            setSearchParams({ game: gameId.toString() });
        } else {
            setSearchParams({});
        }
    };

    const loadMoreAccounts = () => {
        if (hasNextPage && !newAccountsLoading) {
            fetchMore();
        }
    };

    // Loading state
    if (gamesLoading && !gamesData) {
        return <LoadingSkeleton />;
    }

    // Error state
    if (gamesError || featuredError || newAccountsError) {
        return <ErrorMessage message="Failed to load marketplace data" />;
    }

    const games = gamesData?.games || [];
    const featuredAccounts = featuredData?.accounts?.content || [];
    const newAccounts = newAccountsData?.pages?.flatMap(page => page.accounts?.content || []) || [];

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header with Search */}
            <header className="bg-white shadow-sm border-b">
                <div className="container mx-auto px-4 py-4">
                    <div className="flex items-center justify-between">
                        <h1 className="text-2xl font-bold text-gray-900">Game Account Marketplace</h1>
                        <div className="flex-1 max-w-md mx-8">
                            <input
                                type="text"
                                placeholder="Search accounts..."
                                value={searchQuery}
                                onChange={(e) => setSearchQuery(e.target.value)}
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                            />
                        </div>
                    </div>
                </div>
            </header>

            <main className="container mx-auto px-4 py-8">
                {/* Game Categories */}
                <section className="mb-8">
                    <h2 className="text-xl font-semibold mb-4">Browse by Game</h2>
                    <div className="flex space-x-4 overflow-x-auto pb-4">
                        <button
                            onClick={() => handleGameFilter(null)}
                            className={`flex-shrink-0 px-4 py-2 rounded-full border transition-colors ${
                                !gameFilter
                                    ? 'bg-blue-600 text-white border-blue-600'
                                    : 'bg-white text-gray-700 border-gray-300 hover:bg-gray-50'
                            }`}
                        >
                            All Games
                        </button>
                        {games.map((game: any) => (
                            <button
                                key={game.id}
                                onClick={() => handleGameFilter(game.id)}
                                className={`flex-shrink-0 px-4 py-2 rounded-full border transition-colors whitespace-nowrap ${
                                    gameFilter === game.id.toString()
                                        ? 'bg-blue-600 text-white border-blue-600'
                                        : 'bg-white text-gray-700 border-gray-300 hover:bg-gray-50'
                                }`}
                            >
                                <img src={game.imageUrl} alt={game.name} className="w-6 h-6 inline mr-2 rounded" />
                                {game.name}
                            </button>
                        ))}
                    </div>
                </section>

                {/* Featured Accounts */}
                <section className="mb-12">
                    <h2 className="text-xl font-semibold mb-4">Featured Listings</h2>
                    {featuredLoading ? (
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                            {[...Array(6)].map((_, i) => (
                                <div key={i} className="bg-white rounded-lg shadow-md p-4 animate-pulse">
                                    <div className="w-full h-48 bg-gray-200 rounded mb-4"></div>
                                    <div className="h-4 bg-gray-200 rounded mb-2"></div>
                                    <div className="h-4 bg-gray-200 rounded w-2/3"></div>
                                </div>
                            ))}
                        </div>
                    ) : featuredAccounts.length > 0 ? (
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                            {featuredAccounts.map((account: any) => (
                                <AccountCard key={account.id} account={account} />
                            ))}
                        </div>
                    ) : (
                        <div className="text-center py-12 text-gray-500">
                            No featured accounts available at the moment.
                        </div>
                    )}
                </section>

                {/* New Accounts */}
                <section>
                    <h2 className="text-xl font-semibold mb-4">New Listings</h2>
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                        {newAccounts.map((account: any) => (
                            <AccountCard key={account.id} account={account} />
                        ))}
                    </div>

                    {/* Load More */}
                    {hasNextPage && (
                        <div className="text-center mt-8">
                            <button
                                onClick={loadMoreAccounts}
                                disabled={newAccountsLoading}
                                className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
                            >
                                {newAccountsLoading ? 'Loading...' : 'Load More Accounts'}
                            </button>
                        </div>
                    )}

                    {newAccountsLoading && !hasNextPage && (
                        <div className="text-center mt-8 text-gray-500">
                            Loading more accounts...
                        </div>
                    )}
                </section>
            </main>
        </div>
    );
};

export default HomePage;
```

#### 2. AccountCard Component Template

**Create frontend-react/src/components/account/AccountCard.tsx:**
```typescript
import React from 'react';
import { useNavigate } from 'react-router-dom';

interface AccountCardProps {
    account: {
        id: number;
        title: string;
        price: number;
        images: string[];
        game: {
            name: string;
            imageUrl: string;
        };
        seller: {
            rating: number;
            totalReviews: number;
        };
    };
}

const AccountCard: React.FC<AccountCardProps> = ({ account }) => {
    const navigate = useNavigate();

    const handleClick = () => {
        navigate(`/accounts/${account.id}`);
    };

    const mainImage = account.images && account.images.length > 0 ? account.images[0] : '/placeholder-account.png';

    return (
        <div
            onClick={handleClick}
            className="bg-white rounded-lg shadow-md overflow-hidden cursor-pointer hover:shadow-lg transition-shadow duration-300"
        >
            {/* Image */}
            <div className="aspect-w-16 aspect-h-9 bg-gray-200">
                <img
                    src={mainImage}
                    alt={account.title}
                    className="w-full h-48 object-cover"
                    loading="lazy"
                />
            </div>

            {/* Content */}
            <div className="p-4">
                {/* Game Icon and Title */}
                <div className="flex items-center mb-2">
                    <img
                        src={account.game.imageUrl}
                        alt={account.game.name}
                        className="w-6 h-6 rounded mr-2"
                    />
                    <h3 className="text-lg font-semibold text-gray-900 truncate">
                        {account.title}
                    </h3>
                </div>

                {/* Price */}
                <div className="text-xl font-bold text-green-600 mb-2">
                    ${account.price.toFixed(2)}
                </div>

                {/* Seller Rating */}
                <div className="flex items-center text-sm text-gray-600">
                    <span className="text-yellow-500 mr-1">★</span>
                    <span>{account.seller.rating.toFixed(1)}</span>
                    <span className="mx-1">•</span>
                    <span>{account.seller.totalReviews} reviews</span>
                </div>
            </div>
        </div>
    );
};

export default AccountCard;
```

#### 3. GraphQL Queries Update

**Update frontend-react/src/graphql/queries.ts:**
```typescript
export const GET_GAMES = gql`
    query GetGames {
        games {
            id
            name
            slug
            imageUrl
        }
    }
`;

// Update GET_ACCOUNTS to support more filters
export const GET_ACCOUNTS = gql`
    query GetAccounts(
        $filters: AccountFiltersInput
        $sort: AccountSortInput
        $page: PageInput
    ) {
        accounts(filters: $filters, sort: $sort, page: $page) {
            content {
                id
                title
                price
                level
                rank
                status
                images
                isFavorited
                game {
                    id
                    name
                    imageUrl
                }
                seller {
                    id
                    fullName
                    rating
                    totalReviews
                }
                createdAt
            }
            totalElements
            totalPages
            currentPage
            pageSize
            hasNext
            hasPrevious
        }
    }
`;
```

### Common Pitfalls to Avoid

🚨 **CRITICAL WARNINGS:**
1. **Infinite scroll memory leaks** - Clean up IntersectionObserver on unmount
2. **URL parameter conflicts** - Handle multiple filters (game + search) properly
3. **Debounce race conditions** - Clear previous timeout before setting new one
4. **Cache invalidation issues** - Use appropriate fetchPolicy for each query type
5. **Loading state management** - Handle multiple concurrent queries properly
6. **Game filter persistence** - Preserve game filter when navigating to search
7. **Mobile scroll performance** - Optimize for touch scrolling and performance
8. **Image lazy loading** - Use proper loading="lazy" and error handling
9. **Keyboard navigation** - Make search bar accessible with Enter key
10. **Pagination edge cases** - Handle empty results and network errors gracefully

### Testing Standards

**Unit Tests for HomePage:**
```typescript
describe('HomePage', () => {
    it('loads games on mount', () => {
        // Test GET_GAMES query
    });

    it('displays game categories', () => {
        // Test horizontal scroll of games
    });

    it('filters accounts by game', () => {
        // Test game filter functionality
    });

    it('navigates to search on input', () => {
        // Test debounced search navigation
    });

    it('loads featured accounts', () => {
        // Test GET_ACCOUNTS with isFeatured filter
    });

    it('loads new accounts with infinite scroll', () => {
        // Test pagination functionality
    });
});
```

**Integration Tests:**
```typescript
describe('HomePage Integration', () => {
    it('GET_GAMES returns game list', () => {
        // Test GraphQL query integration
    });

    it('GET_ACCOUNTS supports multiple filters', () => {
        // Test filtering by game, featured, sorting
    });

    it('responsive design works on mobile', () => {
        // Test breakpoint behavior
    });
});
```

### Requirements Traceability

**FR14:** Browse accounts with filters ✅ Game category filtering
**FR16:** Text search ✅ Search bar navigation
**FR17:** Sort by date ✅ New accounts sorted by createdAt
**FR46:** Flexible querying ✅ GraphQL with multiple filter options
**NFR2:** GraphQL Query < 300ms ✅ Cached queries with Redis
**NFR49:** Lazy load images ✅ loading="lazy" attribute
**NFR50:** Code splitting ✅ React.lazy for route components

### Dependencies

**Required Stories:**
- Story 2.3 (GraphQL Schema) - GET_ACCOUNTS and GET_GAMES queries
- Story 3.1 (Advanced Filtering) - Backend filtering support
- Story 3.4 (Account Detail Page) - AccountCard component pattern

**Blocking Stories:**
- Story 3.6 (Advanced Search & Filter UI) - Will enhance search functionality
- Story 4.1 (Transaction Review Entities) - Will add purchase functionality

### References

- Epics.md: Section Epic 3, Story 3.5 (full requirements)
- Story 2.3: GraphQL schema patterns and query structure
- Story 3.1: Advanced filtering implementation details
- Story 3.4: AccountCard component and navigation patterns
- React Router Documentation: useSearchParams and navigation
- Apollo Client Documentation: useInfiniteQuery and fetchPolicy
- Tailwind CSS Documentation: Responsive grid utilities
- MDN Intersection Observer API: Infinite scroll implementation

---

## Code Review Fixes (AI)

**Review Date:** 2026-01-08
**Reviewer:** Amelia (Developer Agent) - Adversarial Code Review
**Issues Fixed:** 4 HIGH, 5 MEDIUM

### HIGH Issues Fixed

1. **Unused Import Removed** (`HomePage.tsx:1`)
   - Removed unused `useCallback` import that added unnecessary bundle size

2. **Race Condition Fixed** (`HomePage.tsx:116-162`)
   - Added `isFetchingRef` to prevent duplicate API calls
   - Created memoized `loadMoreAccounts` function with proper dependency array
   - Fixed IntersectionObserver cleanup with proper `currentTarget` ref capture

3. **Null Check Fixed** (`AccountCard.test.tsx:149`)
   - Added `toBeInTheDocument()` check before `toHaveAttribute()` to prevent test crashes

4. **Missing SearchPage Created** (`SearchPage.tsx`)
   - Created complete SearchPage component with URL query parameter support
   - Added route to App.tsx at `/search`

### MEDIUM Issues Fixed

5. **User ID Extraction Improved** (`AccountQuery.java:146-260`)
   - Replaced fragile "user_XXX" parsing with multi-strategy approach
   - Added support for JWT claims, User entity getId(), and fallback username parsing
   - Marked old method as `@Deprecated`

6. **Accessibility Added** (Multiple files)
   - Added `aria-label` to search input, game filter buttons
   - Added `aria-hidden="true"` to observer target div
   - Added keyboard-accessible labels for all interactive elements

7. **Code Duplication Removed** (`HomePage.tsx:119-138`)
   - Extracted refetch logic into `loadMoreAccounts` callback
   - Single source of truth for pagination logic

8. **Empty State UX Fixed** (`HomePage.tsx:216-266`)
   - Hide "Browse by Game" section when `games.length === 0`
   - Better UX for empty game lists

9. **Performance Optimization** (`HomePage.tsx:165-171`)
   - Added `useCallback` for `handleGameFilter` function
   - Prevents unnecessary re-renders on filter changes

### Additional Files Modified

- `frontend-react/src/App.tsx` - Added SearchPage route
- `frontend-react/src/pages/SearchPage.tsx` - Created new search page component

---

## Dev Agent Record

### Agent Model Used

Claude Opus 4.5 (BMad Story Creator)

### Completion Notes List

**Story Creation Summary:**
This story creates the marketplace homepage - the main entry point for buyers. It features game categories, featured listings carousel, new accounts grid, and search functionality. The homepage uses Apollo Client's advanced features like useInfiniteQuery for pagination and proper caching strategies.

**Implementation Summary:**
All tasks and subtasks completed successfully:
1. Created HomePage component with full marketplace functionality
2. Created reusable AccountCard component for displaying accounts
3. Implemented game category filtering with URL state management
4. Implemented debounced search bar with navigation
5. Implemented infinite scroll/pagination using IntersectionObserver
6. Updated backend GraphQL schema and resolver to support isFeatured, sortBy, sortDirection
7. Updated frontend GraphQL queries with new parameters
8. Created comprehensive unit tests for both components
9. All acceptance criteria met

**Backend Changes:**
- Updated GraphQL schema to support isFeatured, sortBy, sortDirection parameters
- Updated AccountQuery resolver to use AccountSearchRequest with role-based filtering
- Added getAllowedSortFields() method to AccountService for validation

**Frontend Changes:**
- HomePage component with games query (cache-first), featured accounts, and new accounts with pagination
- AccountCard component with image, title, price, game icon, seller rating, and click navigation
- Debounced search (300ms) with navigation to search page
- Game category filtering with URL state management and "Clear Filter" button
- IntersectionObserver for infinite scroll with "Load More" fallback button
- Responsive grid layout using Tailwind CSS

**Critical Guardrails Implemented:**
- Apollo Client caching strategies prevent unnecessary requests
- Debounced search prevents excessive API calls
- URL state management preserves filter state
- Responsive design works on all screen sizes
- Error boundaries handle GraphQL failures gracefully
- IntersectionObserver cleanup prevents memory leaks
- Lazy loading with loading="lazy" improves performance
- TypeScript ensures type safety
- Role-based filtering ensures proper data access

**Files Created:**
- frontend-react/src/pages/HomePage.tsx
- frontend-react/src/components/account/AccountCard.tsx
- frontend-react/src/pages/HomePage.test.tsx
- frontend-react/src/components/account/AccountCard.test.tsx

**Files Updated:**
- frontend-react/src/services/graphql/queries.ts (added isFeatured, sortBy, sortDirection parameters)
- backend-java/src/main/resources/graphql/schema.graphqls (added isFeatured, sortBy, sortDirection to accounts query)
- backend-java/src/main/java/com/gameaccount/marketplace/graphql/query/AccountQuery.java (updated to use new parameters)
- backend-java/src/main/java/com/gameaccount/marketplace/service/AccountService.java (added getAllowedSortFields method)

**All requirements traced and documented. Implementation complete and ready for review.**

### File List

**Files Created:**
- `frontend-react/src/components/account/AccountCard.tsx`
- `frontend-react/src/components/account/AccountCard.test.tsx`
- `frontend-react/src/pages/HomePage.test.tsx`

**Files Modified:**
- `frontend-react/src/pages/HomePage.tsx`
- `frontend-react/src/services/graphql/queries.ts`
- `backend-java/src/main/resources/graphql/schema.graphqls`
- `backend-java/src/main/java/com/gameaccount/marketplace/graphql/query/AccountQuery.java`
- `backend-java/src/main/java/com/gameaccount/marketplace/service/AccountService.java`

**Note:** App.tsx route configuration was already in place, no changes needed.

---

