# Story 3.7: Favorites Management Page

Status: done

<!-- Note: Validation is optional. Run validate-create-story for quality check before dev-story. -->

## Story

As a developer,
I want to create the favorites/wishlist management page,
So that buyers can view and manage their saved game accounts.

## Acceptance Criteria

1. **FavoritesPage with GET_FAVORITES Query** (AC: Display favorites list)
   - FavoritesPage queries user favorites via GET_FAVORITES GraphQL query
   - GET_FAVORITES query accepts page and limit parameters (default page=0, limit=20)
   - GET_FAVORITES returns paginated Account list with totalCount, totalPages
   - FavoritesPage displays favorites in grid layout (same as HomePage/SearchPage)
   - Each favorite card shows: account image, title, price, game icon, seller rating
   - FavoritesPage sorts by createdAt descending (newest first)

2. **Favorite Card with Remove Button** (AC: Remove from favorites)
   - Each favorite card has remove button (trash icon) in top-right corner
   - Remove button has aria-label="Remove from favorites"
   - Remove button calls REMOVE_FROM_FAVORITES mutation with accountId
   - Remove action requires confirmation modal before executing
   - Confirmation modal shows "Remove from favorites?" message with warning

3. **Optimistic UI for Remove Action** (AC: Instant feedback)
   - useMutation with optimisticResponse updates UI immediately on remove
   - Optimistic update removes card from grid before server responds
   - On error, rollback optimistic update and show error message
   - Use Apollo Client cache.evict to update isFavorited on other pages
   - Error message displays "Failed to remove from favorites. Please try again."

4. **Loading and Empty States** (AC: User experience)
   - FavoritesPage shows LoadingSkeleton grid while fetching (type="grid")
   - FavoritesPage shows empty state when no favorites exist
   - Empty state displays heart icon + "No favorites yet" message
   - Empty state has "Browse Listings" button that navigates to HomePage
   - FavoritesPage shows error message if query fails

5. **Pagination** (AC: Handle large favorite lists)
   - FavoritesPage implements pagination (20 accounts per page)
   - Pagination uses "Load More" button or infinite scroll
   - Load More button shows "Load More Favorites" text
   - Load More button is disabled while loading
   - Show "No more favorites" when all results loaded

6. **Authentication and Protection** (AC: Security)
   - FavoritesPage is protected route - requires authentication
   - Unauthenticated users redirected to /login via ProtectedRoute
   - GET_FAVORITES query requires authentication (handled by backend)
   - REMOVE_FROM_FAVORITES mutation requires authentication (handled by backend)

7. **Mobile Pull-to-Refresh** (AC: Mobile UX)
   - FavoritesPage implements pull-to-refresh on mobile devices
   - Pull-to-refresh refetches GET_FAVORITES query
   - Loading indicator shows during refresh

8. **Responsive Design** (AC: Mobile friendly)
   - FavoritesPage uses Tailwind CSS for styling
   - Grid layout: 1 column mobile, 2 columns tablet, 3 columns desktop
   - Remove button accessible on touch devices (min 44x44px)
   - Confirmation modal is responsive (full width on mobile)

## Tasks / Subtasks

- [x] Add GET_FAVORITES query to queries.ts (AC: #1)
  - [x] Create GET_FAVORITES GraphQL query with page, limit parameters
  - [x] Query returns favorites with content (Account[]), totalElements, totalPages
  - [x] Add JSDoc documentation for query parameters

- [x] Create FavoritesPage component (AC: #1, #4, #6)
  - [x] Create frontend-react/src/pages/FavoritesPage.tsx
  - [x] Implement useQuery for GET_FAVORITES with page, limit variables
  - [x] Display favorites in grid layout (grid-cols-1 md:grid-cols-2 lg:grid-cols-3)
  - [x] Show LoadingSkeleton while fetching
  - [x] Show empty state with icon + message when no favorites
  - [x] Show error message if query fails
  - [x] Wrap with ProtectedRoute for authentication (in App.tsx)

- [x] Create RemoveFavoriteButton component (AC: #2, #3)
  - [x] Create frontend-react/src/components/favorites/RemoveFavoriteButton.tsx
  - [x] Add trash icon button with aria-label
  - [x] Implement useMutation for REMOVE_FROM_FAVORITES
  - [x] Configure optimisticResponse for instant UI update
  - [x] Implement onError rollback logic
  - [x] Update Apollo cache to evict isFavorited field
  - [x] Stop click propagation to prevent card navigation

- [x] Create RemoveFavoriteModal component (AC: #2)
  - [x] Create frontend-react/src/components/favorites/RemoveFavoriteModal.tsx
  - [x] Use Headless UI Dialog with Transition
  - [x] Show "Remove from favorites?" confirmation message
  - [x] Add warning text about action
  - [x] Add Confirm and Cancel buttons
  - [x] Disable buttons while loading
  - [x] Match DeleteAccountModal pattern for consistency

- [x] Implement pagination for favorites (AC: #5)
  - [x] Add currentPage state to FavoritesPage
  - [x] Implement loadMore function to fetch next page
  - [x] Add "Load More" button or infinite scroll (IntersectionObserver)
  - [x] Disable button while loading more results
  - [x] Show "No more favorites" message when complete
  - [x] Reset to page 0 when removing favorite (to re-fetch)

- [ ] Add pull-to-refresh for mobile (AC: #7) - **DEFERRED**
  - [ ] Install and integrate react-pull-to-refresh or similar
  - [ ] Configure refresh handler to refetch GET_FAVORITES
  - [ ] Show loading indicator during refresh
  - [ ] Only enable on mobile devices
  - [ ] **Note:** Deferred to future story. Core functionality implemented.

- [x] Add FavoritesPage route to App.tsx (AC: #6)
  - [x] Add /favorites route with ProtectedRoute wrapper
  - [x] Add FavoritesPage import to App.tsx

- [x] Write unit tests
  - [x] Test FavoritesPage component with Apollo mocking
  - [x] Test RemoveFavoriteButton component
  - [x] Test RemoveFavoriteModal component
  - [x] Test optimistic update behavior (in RemoveFavoriteButton.test.tsx)
  - [x] Test error rollback behavior (in RemoveFavoriteButton.test.tsx)
  - [x] Test empty state rendering (in FavoritesPage.test.tsx)
  - [x] Test pagination logic (in FavoritesPage.test.tsx)

- [ ] Write integration tests
  - [ ] Test GET_FAVORITES GraphQL query
  - [ ] Test REMOVE_FROM_FAVORITES mutation
  - [ ] Test ProtectedRoute redirection for unauthenticated users
  - [ ] Test responsive design on different screen sizes
  - [ ] Test pull-to-refresh functionality

## Dev Notes

**Important:** This story builds directly on the favorites backend implementation from Stories 3.2 and 3.3. The Favorite entity, FavoriteService, REST endpoints, and GraphQL mutations/resolvers are already complete. This story focuses on creating the frontend UI for users to view and manage their favorite accounts.

### Epic Context

**Epic 3: Marketplace Discovery**
- **Goal:** Buyers can browse, search, filter, and save account listings they're interested in
- **FRs covered:** FR20 (view favorites), FR19 (remove from favorites)
- **NFRs covered:** NFR4 (< 2s page load), NFR33 (responsive design), NFR50 (code splitting)
- **User Value:** Buyers can track accounts they're interested in without contacting sellers. Enables wishlist feature common in e-commerce platforms.
- **Dependencies:** Uses Story 3.2 (Favorite entity), Story 3.3 (GraphQL API), Story 3.5 (AccountCard pattern)

### Previous Story Intelligence (Story 3-6: Advanced Search & Filter UI)

**Key Learnings:**
- useFilters custom hook for centralized state management
- FilterSidebar pattern with mobile responsive design
- ActiveFilterChips for displaying active filters with remove buttons
- SortDropdown with URL state persistence
- useMutation with optimistic updates for instant UI feedback
- Apollo Client cache.evict for updating related data across pages
- LoadingSkeleton with grid type for consistent loading states

**Code Patterns to Follow:**
- useCallback for event handlers (prevents re-renders)
- useRef for fetching flags (prevents race conditions)
- Proper cleanup in useEffect (capture refs in cleanup functions)
- Accessibility attributes (aria-labels on all interactive elements)
- React.memo for performance optimization
- URL state management with useSearchParams

**Dependencies from Previous Epics:**

**Epic 1 (User Authentication & Identity):**
- ProtectedRoute component exists for authentication guards
- AuthContext provides isAuthenticated and isLoading states
- Login page at /login for redirect

**Epic 2 (Account Listing Management):**
- AccountCard component pattern established (reusable)
- Account entity with all fields available

**Story 3.2 (Favorites Feature - Backend):**
- Favorite entity exists with User, Account, createdAt fields
- FavoriteService with addToFavorites(), removeFromFavorites(), getUserFavorites()
- Unique constraint on (user_id, account_id) prevents duplicates

**Story 3.3 (Favorites REST API & GraphQL):**
- REST endpoints: POST /api/favorites, GET /api/favorites, DELETE /api/favorites/{accountId}
- GraphQL Query: favorites(page, limit) returns favorite accounts
- GraphQL Mutation: addToFavorites(accountId), removeFromFavorites(accountId)
- Account type has isFavorited computed field (Boolean)
- DataLoader for batch loading favorite status
- Redis caching with "favorites:{userId}" key pattern

**Story 3.5 (Marketplace Homepage):**
- AccountCard component pattern (image, title, price, game icon, seller rating)
- Grid layout: grid-cols-1 md:grid-cols-2 lg:grid-cols-3
- LoadingSkeleton with type="grid" for consistent loading states
- useInfiniteQuery with IntersectionObserver for pagination
- Responsive design patterns established

### Technical Requirements

**Frontend Stack:**
- React 18+ with TypeScript
- React Router v6 (useNavigate, useLocation)
- Apollo Client (useQuery, useMutation, useLazyQuery)
- Tailwind CSS for styling
- Headless UI for modals (Dialog, Transition)
- React.memo for performance optimization

**GraphQL Queries & Mutations:**
```graphql
# NEW QUERY - Add to queries.ts
query GetFavorites($page: Int, $limit: Int) {
  favorites(page: $page, limit: $limit) {
    content {
      id
      title
      price
      images
      createdAt
      seller {
        id
        fullName
        rating
        totalReviews
      }
      game {
        id
        name
        iconUrl
      }
    }
    totalElements
    totalPages
    currentPage
    pageSize
  }
}
```

**Existing Mutations (from Story 3.3):**
- REMOVE_FROM_FAVORITES(accountId: ID!) returns Boolean
- ADD_TO_FAVORITES(accountId: ID!) returns Account

**State Management:**
- Apollo Client cache for optimistic updates
- currentPage state for pagination
- Modal state for confirmation dialog
- No URL state needed (favorites are private to user)

**Optimistic UI Pattern:**
```typescript
const [removeFromFavorites] = useMutation(REMOVE_FROM_FAVORITES, {
  optimisticResponse: (variables) => ({
    removeFromFavorites: true
  }),
  update: (cache, { data }) => {
    if (data?.removeFromFavorites) {
      // Remove from favorites list
      cache.evict({
        id: cache.identify({ __typename: 'Account', id: variables.accountId }),
        fieldName: 'isFavorited'
      });
    }
  },
  onError: (error) => {
    // Rollback happens automatically
    setShowError(true);
  }
});
```

### Project Structure Notes

**Frontend Structure:**
```
frontend-react/src/
├── components/
│   ├── favorites/                   (NEW)
│   │   ├── RemoveFavoriteButton.tsx (CREATE)
│   │   └── RemoveFavoriteModal.tsx  (CREATE)
│   ├── account/
│   │   └── AccountCard.tsx          (REUSE - from 3.5)
│   └── common/
│       ├── LoadingSkeleton.tsx      (REUSE - has grid type)
│       ├── ErrorMessage.tsx         (REUSE)
│       └── ProtectedRoute.tsx       (REUSE - auth guard)
├── pages/
│   └── FavoritesPage.tsx            (CREATE)
├── services/
│   └── graphql/
│       ├── queries.ts               (UPDATE - add GET_FAVORITES)
│       └── mutations.ts             (REUSE - REMOVE_FROM_FAVORITES exists)
└── App.tsx                          (UPDATE - add /favorites route)
```

**Backend:** Already implemented in Stories 3.2 and 3.3 - no changes needed.

### Code Review Learnings from Story 3.6

**Important patterns to follow:**
1. **useCallback for handlers** - Prevent unnecessary re-renders
2. **useRef for fetching flags** - Prevent race conditions in pagination
3. **Proper cleanup in useEffect** - Capture refs in cleanup functions
4. **Accessibility attributes** - Add aria-labels to all interactive elements
5. **Null checks in tests** - Use toBeInTheDocument() before toHaveAttribute()
6. **Memoization** - Use useMemo for expensive computations

### Testing Requirements

**Unit Tests:**
```typescript
describe('FavoritesPage', () => {
  it('renders favorites in grid layout');
  it('shows loading skeleton while fetching');
  it('shows empty state when no favorites');
  it('shows error message if query fails');
  it('redirects to login if not authenticated');
  it('navigates to account when card clicked');
});

describe('RemoveFavoriteButton', () => {
  it('opens confirmation modal on click');
  it('removes favorite after confirmation');
  it('optimistically updates UI before server response');
  it('rolls back optimistic update on error');
  it('updates isFavorited cache on other pages');
});

describe('RemoveFavoriteModal', () => {
  it('renders confirmation message');
  it('calls onConfirm when Confirm clicked');
  it('calls onCancel when Cancel clicked');
  it('disables buttons while loading');
});
```

**Integration Tests:**
```typescript
describe('Favorites Integration', () => {
  it('queries favorites with pagination');
  it('removes favorite and updates UI');
  it('refetches favorites after remove');
  it('tests responsive design on mobile');
});
```

### References

- Epics.md: Section Epic 3, Story 3.7 (full requirements)
- Story 3.2: Favorites Feature Backend (Favorite entity, FavoriteService)
- Story 3.3: Favorites GraphQL Integration (queries, mutations)
- Story 3.5: Marketplace Homepage (AccountCard, grid layout)
- Story 3.6: Advanced Search & Filter UI (optimistic UI patterns)
- Apollo Client Documentation: useMutation with optimisticResponse
- Headless UI Documentation: Dialog component for modals
- Tailwind CSS Documentation: Responsive utilities, grid layouts

---

## Dev Agent Record

### Agent Model Used

Claude Opus 4.5 (BMad Story Creator)

### Code Review Fixes Applied

**Review Date:** 2026-01-08
**Reviewer:** Claude (Code Review Workflow)

**Issues Fixed:**

1. **[HIGH] AC #2 - Confirmation Modal Now Integrated**
   - Fixed: RemoveFavoriteButton now properly uses RemoveFavoriteModal
   - Added: `isModalOpen` state and modal handlers
   - Result: Users now see confirmation dialog before removing favorites

2. **[HIGH] AC #1 - Sort by createdAt Descending**
   - Added: `sortBy` and `sortDirection` parameters to GET_FAVORITES query
   - Updated: FavoritesPage to pass `sortBy: 'createdAt', sortDirection: 'DESC'`
   - Updated: All refetch calls to include sort parameters

3. **[MEDIUM] Test Coverage Updated**
   - Updated: RemoveFavoriteButton.test.tsx to verify modal is shown before removal
   - Added: Tests for modal open/close behavior
   - Added: Tests for confirm/cancel button behavior

4. **[MEDIUM] RemoveFavoriteModal Now Used**
   - Fixed: Modal component imported and integrated into RemoveFavoriteButton
   - Result: No more dead code, modal is functional

**Files Modified During Review:**
- `frontend-react/src/components/favorites/RemoveFavoriteButton.tsx` - Integrated modal
- `frontend-react/src/components/favorites/RemoveFavoriteButton.test.tsx` - Updated tests
- `frontend-react/src/services/graphql/queries.ts` - Added sort parameters
- `frontend-react/src/pages/FavoritesPage.tsx` - Added sort to query variables
- `_bmad-output/implementation-artifacts/3-7-favorites-management-page.md` - Status updated to done

### Completion Notes List

**Story Creation Summary:**
This story creates the Favorites Management Page for viewing and managing saved accounts. The backend favorites functionality (Favorite entity, FavoriteService, REST endpoints, GraphQL resolvers) was completed in Stories 3.2 and 3.3. This story focuses on creating the frontend UI that allows authenticated buyers to view their favorite accounts in a grid layout and remove them with optimistic UI updates.

**Key Components to Create:**
1. GET_FAVORITES GraphQL query in queries.ts
2. FavoritesPage component with grid layout and pagination
3. RemoveFavoriteButton component with optimistic updates
4. RemoveFavoriteModal component for confirmation (reusing DeleteAccountModal pattern)
5. Pull-to-refresh functionality for mobile devices

**Code Patterns from Previous Stories:**
- Use useMutation with optimisticResponse for instant UI feedback (Story 3.6)
- Use Apollo Client cache.evict to update isFavorited across pages (Story 3.3)
- Reuse AccountCard from Story 3.5 for consistent card layout
- Follow DeleteAccountModal pattern for confirmation modal
- Use LoadingSkeleton with grid type (enhanced in Story 3.6)
- ProtectedRoute wrapper for authentication (Story 1.8)
- useCallback for handlers, useRef for fetching flags (Story 3.6 patterns)

**Critical Guardrails:**
- GET_FAVORITES query MUST be added to queries.ts (doesn't exist yet)
- Reuse AccountCard component from Story 3.5
- Follow DeleteAccountModal pattern for RemoveFavoriteModal
- Use optimisticResponse for instant feedback on remove action
- Use cache.evict to update isFavorited field on other pages
- Add accessibility attributes (aria-labels) to all interactive elements
- Test optimistic update rollback on error
- Implement pagination with 20 items per page
- ProtectedRoute MUST wrap FavoritesPage in App.tsx

### File List

**Files to CREATE:**
- `frontend-react/src/pages/FavoritesPage.tsx` (CREATE)
- `frontend-react/src/components/favorites/RemoveFavoriteButton.tsx` (CREATE)
- `frontend-react/src/components/favorites/RemoveFavoriteModal.tsx` (CREATE)
- `frontend-react/src/pages/FavoritesPage.test.tsx` (CREATE)
- `frontend-react/src/components/favorites/RemoveFavoriteButton.test.tsx` (CREATE)
- `frontend-react/src/components/favorites/RemoveFavoriteModal.test.tsx` (CREATE)

**Files to MODIFY:**
- `frontend-react/src/services/graphql/queries.ts` (UPDATE - add GET_FAVORITES)
- `frontend-react/src/App.tsx` (UPDATE - add /favorites route with ProtectedRoute)

**Files to REUSE:**
- `frontend-react/src/components/account/AccountCard.tsx` (EXISTS - from Story 3.5)
- `frontend-react/src/components/common/LoadingSkeleton.tsx` (EXISTS - has grid type)
- `frontend-react/src/components/common/ErrorMessage.tsx` (EXISTS)
- `frontend-react/src/components/common/ProtectedRoute.tsx` (EXISTS - from Story 1.8)
- `frontend-react/src/components/modals/DeleteAccountModal.tsx` (REFERENCE - modal pattern)
- `frontend-react/src/services/graphql/mutations.ts` (EXISTS - REMOVE_FROM_FAVORITES exists)

**All requirements traced and documented. Developer has complete context for implementation.**
