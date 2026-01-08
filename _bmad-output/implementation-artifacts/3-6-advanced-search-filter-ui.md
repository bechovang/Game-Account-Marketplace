# Story 3.6: Advanced Search & Filter UI

Status: done

<!-- Note: Validation is optional. Run validate-create-story for quality check before dev-story. -->

## Story

As a developer,
I want to create advanced search and filter components,
so that buyers can find accounts matching specific criteria.

## Acceptance Criteria

1. **SearchPage with Search Input** (AC: Full-text search UI)
   - SearchPage has search input field in header
   - SearchPage debounces search input (300ms delay)
   - Search input supports title and description search
   - GET_ACCOUNTS query accepts filter variables from URL params

2. **Filter Sidebar Component** (AC: Multi-criteria filtering)
   - FilterSidebar displays filter controls in sidebar layout
   - FilterSidebar includes: Game dropdown, Price range slider, Level inputs, Rank dropdown, Status toggle
   - FilterSidebar persists filter state in URL query params
   - FilterSidebar has "Clear Filters" button that resets all filters
   - Filters are collapsible on mobile (responsive design)

3. **Filter State Management** (AC: URL-based state)
   - FilterSidebar uses react-router-dom's useSearchParams for URL state
   - Active filters are displayed as chips with remove button
   - Filter validation (minPrice < maxPrice, minLevel < maxLevel)
   - GET_ACCOUNTS query refetches when filters change

4. **Search Results Display** (AC: Results grid and empty state)
   - SearchPage displays results in grid layout (same as HomePage)
   - Search results show "No accounts found" message when empty
   - Search results show result count ("X results found")
   - AccountCard component is reused from Story 3.5

5. **Sort Dropdown Component** (AC: Sorting UI)
   - SortDropdown allows sorting by: Price (Low/High), Price (High/Low), Level, Newest
   - SortDropdown persists selection in URL query params (sortBy, sortDirection)
   - Active sort is visually indicated

6. **Advanced Search Features** (AC: Enhanced UX)
   - AccountSearchBar has autocomplete suggestions for game names
   - Use custom hook useFilters for filter state management
   - Use React.memo for performance optimization
   - Filters are collapsible on mobile (responsive design)

## Tasks / Subtasks

- [x] Create useFilters custom hook (AC: #2, #3)
  - [x] Create hook in frontend-react/src/hooks/useFilters.ts
  - [x] Implement filter state management with URL params
  - [x] Implement filter validation (minPrice < maxPrice, minLevel < maxLevel)
  - [x] Implement clearFilters function
  - [x] Export filter state and setters

- [x] Create FilterSidebar component (AC: #2, #3)
  - [x] Create component in frontend-react/src/components/search/FilterSidebar.tsx
  - [x] Add Game dropdown (multi-select or single-select based on design)
  - [x] Add Price range slider (min/max inputs)
  - [x] Add Level inputs (min/max)
  - [x] Add Rank dropdown
  - [x] Add Status toggle (APPROVED/PENDING/ALL)
  - [x] Add "Clear Filters" button
  - [x] Implement collapsible filters on mobile
  - [x] Style with Tailwind CSS

- [x] Create SortDropdown component (AC: #5)
  - [x] Create component in frontend-react/src/components/search/SortDropdown.tsx
  - [x] Add sort options: Price Low/High, Price High/Low, Level, Newest
  - [x] Persist selection in URL (sortBy, sortDirection)
  - [x] Visual indication of active sort
  - [x] Style with Tailwind CSS

- [x] Create ActiveFilterChips component (AC: #3)
  - [x] Create component in frontend-react/src/components/search/ActiveFilterChips.tsx
  - [x] Display active filters as removable chips
  - [x] Each chip has remove button (X icon)
  - [x] Clicking chip removes that filter from URL
  - [x] Style with Tailwind CSS

- [x] Enhance SearchPage component (AC: #1, #4)
  - [x] Add FilterSidebar to existing SearchPage
  - [x] Add SortDropdown to header
  - [x] Add ActiveFilterChips below header
  - [x] Display result count
  - [x] Display empty state with helpful message
  - [x] Update GET_ACCOUNTS query to use filter variables
  - [x] Implement responsive layout (sidebar collapses on mobile)

- [ ] Add autocomplete to search input (AC: #6) - **DEFERRED**
  - [ ] Create Autocomplete component or use existing library
  - [ ] Fetch game names from GET_GAMES query
  - [ ] Display suggestions on input
  - [ ] Handle suggestion selection
  - **Note:** Deferred to future story. Search input with debouncing is implemented.

- [x] Write unit tests
  - [x] Test useFilters hook
  - [x] Test FilterSidebar component
  - [x] Test SortDropdown component
  - [x] Test ActiveFilterChips component
  - [x] Test SearchPage integration

- [x] Write integration tests
  - [x] Test filter state changes with URL
  - [x] Test GET_ACCOUNTS with all filter combinations
  - [x] Test responsive design on mobile

## Dev Notes

**Important:** This story builds upon Story 3.1 (backend filtering) and Story 3.5 (SearchPage foundation). The filtering logic is already implemented on the backend - this story focuses on creating the UI components for buyers to access those filters.

**Story 3.1 provides:**
- Backend searchAccounts() method with all filters (gameId, minPrice, maxPrice, minLevel, maxLevel, rank, status, isFeatured)
- Full-text search on title and description
- Sorting by price, level, createdAt
- Pagination support
- < 300ms query performance with proper indexing

**Story 3.5 provides:**
- Basic SearchPage component
- AccountCard component (reusable)
- GET_ACCOUNTS GraphQL query with basic filtering
- useSearchParams for URL state management pattern

**This story adds:**
- Rich filter UI components
- Advanced filter combinations
- Better UX for filter management
- Autocomplete for game names

### Epic Context

**Epic 3: Marketplace Discovery**
- **Goal:** Buyers can browse, search, filter, and view account listings
- **FRs covered:** FR14-FR20, FR46-FR47
- **NFRs covered:** NFR2 (< 300ms queries), NFR33 (responsive design), NFR49 (lazy loading), NFR50 (code splitting)
- **User Value:** Buyers need to find accounts that match their criteria efficiently

### Project Structure Notes

**Frontend Structure:**
```
frontend-react/src/
├── components/
│   ├── search/
│   │   ├── FilterSidebar.tsx      (NEW - filter controls)
│   │   ├── SortDropdown.tsx        (NEW - sort options)
│   │   └── ActiveFilterChips.tsx   (NEW - active filters display)
│   ├── account/
│   │   └── AccountCard.tsx         (EXISTS - reuse from 3.5)
│   └── common/
│       └── LoadingSkeleton.tsx    (EXISTS - reuse)
├── pages/
│   └── SearchPage.tsx              (EXISTS - enhance)
├── hooks/
│   └── useFilters.ts               (NEW - filter state management)
└── services/graphql/
    └── queries.ts                  (EXISTS - may need updates)
```

**Backend:** Already implemented in Story 3.1 - no changes needed.

### Dependencies

**Required Stories:**
- Story 3.1 (Advanced Filtering) - Backend filtering implementation
- Story 3.5 (Marketplace Homepage) - SearchPage foundation, AccountCard component
- Story 2.3 (GraphQL Schema) - GET_ACCOUNTS query structure

**Related Files:**
- `frontend-react/src/pages/SearchPage.tsx` - Created in Story 3.5 code review
- `frontend-react/src/pages/HomePage.tsx` - Reference for filter patterns
- `backend-java/src/main/java/com/gameaccount/marketplace/service/AccountService.java` - searchAccounts() method
- `frontend-react/src/services/graphql/queries.ts` - GET_ACCOUNTS query

### Code Review Learnings from Story 3.5

**Important patterns to follow:**
1. **useCallback for handlers** - Prevent unnecessary re-renders
2. **useRef for fetching flags** - Prevent race conditions in pagination
3. **Proper cleanup in useEffect** - Capture refs in cleanup functions
4. **Accessibility attributes** - Add aria-labels to all interactive elements
5. **Null checks in tests** - Use toBeInTheDocument() before toHaveAttribute()
6. **URL state management** - Use useSearchParams pattern from HomePage

### Technical Requirements

**Frontend Stack:**
- React 18+ with TypeScript
- React Router v6 (useSearchParams, useNavigate)
- Apollo Client (useQuery, useLazyQuery)
- Tailwind CSS for styling
- React.memo for performance optimization
- lodash.debounce for search input (300ms delay)

**State Management:**
- URL query params for filter persistence
- useSearchParams hook for reading/writing URL state
- Custom useFilters hook for centralized filter logic

**Filter Parameters:**
- `gameId` - Single game ID for filtering
- `minPrice` - Minimum price (number)
- `maxPrice` - Maximum price (number)
- `minLevel` - Minimum level (number)
- `maxLevel` - Maximum level (number)
- `rank` - Account rank (string)
- `status` - Account status (APPROVED/PENDING/ALL)
- `sortBy` - Sort field (price/level/createdAt)
- `sortDirection` - Sort direction (ASC/DESC)
- `q` - Full-text search query

**Responsive Design:**
- Desktop: Sidebar visible on left, results on right
- Tablet: Sidebar collapsible
- Mobile: Filters in drawer/accordion, full width when open

### Testing Requirements

**Unit Tests:**
```typescript
describe('FilterSidebar', () => {
  it('renders all filter controls');
  it('updates URL params when filters change');
  it('clears all filters on Clear button click');
  it('validates minPrice < maxPrice');
  it('collapses on mobile');
});

describe('SortDropdown', () => {
  it('displays all sort options');
  it('updates URL params when sort changes');
  it('shows active sort selection');
});

describe('useFilters', () => {
  it('reads initial filters from URL');
  it('updates URL when filters change');
  it('validates filter values');
  it('clears all filters');
});

describe('ActiveFilterChips', () => {
  it('displays active filters as chips');
  it('removes filter when chip X is clicked');
  it('updates URL when filter is removed');
});
```

**Integration Tests:**
```typescript
describe('SearchPage Integration', () => {
  it('filters by game selection');
  it('filters by price range');
  it('sorts by price ascending');
  it('displays result count');
  it('shows empty state when no results');
  it('persists filters in URL');
});
```

### References

- Epics.md: Section Epic 3, Story 3.6 (full requirements)
- Story 3.1: Backend filtering implementation (AccountService.searchAccounts)
- Story 3.5: SearchPage foundation, code review fixes
- React Router Documentation: useSearchParams API
- Apollo Client Documentation: useQuery with variables
- Tailwind CSS Documentation: Responsive utilities, form elements
- lodash.debounce: Debouncing function for search input

---

## Dev Agent Record

### Agent Model Used

Claude Opus 4.5 (BMad Story Creator)

### Completion Notes List

**Story Creation Summary:**
This story creates the advanced search and filter UI components for the marketplace. The backend filtering (Story 3.1) is complete, and the basic SearchPage was created in Story 3.5 code review. This story focuses on creating rich UI components that allow buyers to easily filter and sort accounts.

**Key Components to Create:**
1. useFilters custom hook for centralized filter state management
2. FilterSidebar component with all filter controls
3. SortDropdown component for sorting options
4. ActiveFilterChips component for displaying/removing active filters
5. Enhanced SearchPage with sidebar layout and result count

**Code Patterns from Story 3.5:**
- Use useSearchParams for URL state persistence
- Use useCallback for event handlers
- Use useRef for fetching flags to prevent race conditions
- Add aria-labels for accessibility
- Use React.memo for performance optimization

**Critical Guardrails:**
- Reuse AccountCard component from Story 3.5
- Follow useSearchParams pattern from HomePage.tsx
- Add accessibility attributes to all interactive elements
- Validate filter values (minPrice < maxPrice, etc.)
- Test responsive design (sidebar collapses on mobile)
- Use lodash.debounce for search input (300ms delay)

### File List

**Files to CREATE:**
- `frontend-react/src/hooks/useFilters.ts` (CREATE)
- `frontend-react/src/components/search/FilterSidebar.tsx` (CREATE)
- `frontend-react/src/components/search/SortDropdown.tsx` (CREATE)
- `frontend-react/src/components/search/ActiveFilterChips.tsx` (CREATE)
- `frontend-react/src/components/search/FilterSidebar.test.tsx` (CREATE)
- `frontend-react/src/components/search/SortDropdown.test.tsx` (CREATE)
- `frontend-react/src/components/search/ActiveFilterChips.test.tsx` (CREATE)
- `frontend-react/src/hooks/useFilters.test.ts` (CREATE)

**Files to MODIFY:**
- `frontend-react/src/pages/SearchPage.tsx` (UPDATE - add sidebar, sort, chips)
- `frontend-react/src/services/graphql/queries.ts` (UPDATE - may need new queries for autocomplete)

**Files to REUSE:**
- `frontend-react/src/components/account/AccountCard.tsx` (EXISTS - from Story 3.5)
- `frontend-react/src/components/common/LoadingSkeleton.tsx` (EXISTS - enhanced with grid type)
- `frontend-react/src/components/common/ErrorMessage.tsx` (EXISTS)
- `frontend-react/src/pages/HomePage.tsx` (REFERENCE - filter patterns)
- `backend-java/.../AccountService.java` (EXISTS - no changes needed)

**All requirements traced and documented. Developer has complete context for implementation.**

---

## Code Review Fixes

**Date:** 2026-01-08
**Reviewer:** Claude Code Review Agent

### Issues Fixed

#### HIGH Severity
1. ✅ **Missing Debounced Search Input** - Added search input field with lodash.debounce (300ms delay) to SearchPage header
2. ✅ **useEffect Dependency Issues** - Fixed SearchPage useEffect to use URL params directly via useMemo instead of filters object, preventing infinite re-render loops
3. ✅ **Missing Common Components** - Enhanced LoadingSkeleton.tsx to support grid layout type used in SearchPage

#### MEDIUM Severity
4. ✅ **useFilters Type Mismatch** - Updated setFilter interface to accept `null` value in addition to `undefined`
5. ✅ **Inconsistent Filter State Management** - Removed manual URL parsing, use useFilters as single source of truth, derive all values via useMemo from searchParams
6. ✅ **FilterSidebar Active Filter Count Bug** - Changed to use `activeFilterCount` from useFilters hook instead of manual calculation

#### Files Modified During Review
- `frontend-react/src/hooks/useFilters.ts` - Fixed type definition
- `frontend-react/src/components/search/FilterSidebar.tsx` - Use activeFilterCount from hook
- `frontend-react/src/components/common/LoadingSkeleton.tsx` - Added grid type support
- `frontend-react/src/pages/SearchPage.tsx` - Major refactor:
  - Added debounced search input with lodash.debounce
  - Fixed useEffect dependencies using useMemo
  - Added search input state management
  - Consolidated filter state to use useFilters as single source

### Deferred Features
- **Autocomplete for search input (AC #6)** - Deferred to future story. Search input with debouncing is fully implemented.
