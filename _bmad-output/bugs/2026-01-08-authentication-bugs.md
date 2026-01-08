# Bug Report: Authentication & GraphQL Issues
**Date:** 2026-01-08  
**Severity:** Critical  
**Status:** Fixed  
**Discovered During:** Epic 2 - Account Listing Management Testing  
**Reporter:** Admin (with Dev Agent Amelia)

---

## Summary
Multiple critical authentication bugs discovered during integration testing of seller account listing features. Root cause: JWT implementation stores email as subject, but all authentication methods attempted to parse it as Long user ID.

---

## Bug 1: JWT Authentication - Email Parsed as Long ID
**Severity:** Critical  
**Impact:** All authenticated endpoints failing with "Invalid authentication token"

### Root Cause
JWT token stores email in subject claim, but `getAuthenticatedUserId()` methods across codebase attempted `Long.parseLong(authentication.getName())`.

### Affected Files (8 files)
#### GraphQL Layer:
1. `backend-java/src/main/java/com/gameaccount/marketplace/graphql/mutation/AccountMutation.java`
   - Method: `getAuthenticatedUserId()` line 160
   
2. `backend-java/src/main/java/com/gameaccount/marketplace/graphql/query/FavoriteQuery.java`
   - Method: `getAuthenticatedUserId()` line 63

#### REST Layer:
3. `backend-java/src/main/java/com/gameaccount/marketplace/controller/AccountController.java`
   - Method: `getAuthenticatedUserId()` line 445
   
4. `backend-java/src/main/java/com/gameaccount/marketplace/controller/FavoriteController.java`
   - Method: `getAuthenticatedUserId()` line 180

#### DataLoaders:
5. `backend-java/src/main/java/com/gameaccount/marketplace/graphql/batchloader/FavoriteBatchLoader.java`
   - Method: `getAuthenticatedUserId()` line 106

6. `backend-java/src/main/java/com/gameaccount/marketplace/graphql/resolver/AccountFieldResolver.java`
   - Method: `getAuthenticatedUserId()` line 66

7. `backend-java/src/main/java/com/gameaccount/marketplace/graphql/mutation/FavoriteMutation.java`
   - Method: `getAuthenticatedUserId()` line 83

8. `backend-java/src/main/java/com/gameaccount/marketplace/graphql/query/FavoriteQuery.java` (duplicate)
   - Method: `getAuthenticatedUserId()` line 65

### Fix Applied
Changed all `getAuthenticatedUserId()` implementations from:
```java
// OLD - WRONG
try {
    return Long.parseLong(authentication.getName());
} catch (NumberFormatException e) {
    throw new BusinessException("Invalid authentication token");
}
```

To:
```java
// NEW - CORRECT
String email = authentication.getName();
User user = userRepository.findByEmail(email)
    .orElseThrow(() -> new BusinessException("User not found: " + email));
return user.getId();
```

**Note:** Required adding `UserRepository` dependency to all affected classes.

---

## Bug 2: Missing @Argument Annotations in GraphQL
**Severity:** Critical  
**Impact:** All GraphQL queries/mutations failing with "No suitable resolver"

### Root Cause
Spring GraphQL 1.2.4 requires explicit `@Argument` annotations on query/mutation parameters. Missing annotations caused parameter resolution failures.

### Error Message
```
IllegalStateException: Could not resolve parameter [0] in public 
PaginatedAccountResponse AccountQuery.accounts(Long,Double,Double,String,Integer,Integer): 
No suitable resolver
```

### Affected Files (3 files)
1. `backend-java/src/main/java/com/gameaccount/marketplace/graphql/query/AccountQuery.java`
   - Method: `accounts()` - 6 parameters missing `@Argument`
   - Method: `account()` - 1 parameter missing `@Argument`

2. `backend-java/src/main/java/com/gameaccount/marketplace/graphql/query/FavoriteQuery.java`
   - Method: `favorites()` - 2 parameters missing `@Argument`

3. `backend-java/src/main/java/com/gameaccount/marketplace/graphql/query/GameQuery.java`
   - Method: `game()` - 1 parameter missing `@Argument`
   - Method: `gameBySlug()` - 1 parameter missing `@Argument`

### Fix Applied
Added `@Argument` annotation to all GraphQL query/mutation parameters:
```java
// OLD
public PaginatedAccountResponse accounts(Long gameId, Double minPrice, ...) {

// NEW
public PaginatedAccountResponse accounts(@Argument Long gameId, 
                                         @Argument Double minPrice, ...) {
```

---

## Bug 3: Frontend Endpoint Mismatch
**Severity:** High  
**Impact:** User profile fetch failing after login

### Root Cause
Frontend calling `/api/users/me` but backend endpoint is `/api/auth/me`.

### Affected File
- `frontend-react/src/contexts/AuthContext.tsx` line 39

### Fix Applied
```typescript
// OLD
const response = await apiClient.get<User>('/api/users/me');

// NEW
const response = await apiClient.get<User>('/api/auth/me');
```

---

## Bug 4: Status Filter Preventing PENDING Accounts Display
**Severity:** High  
**Impact:** Seller's My Listings page showing empty despite accounts existing

### Root Cause
REST endpoint `/api/accounts/seller/my-accounts` hardcoded status filter to `AccountStatus.APPROVED`, but test accounts were `PENDING`.

### Affected File
- `backend-java/src/main/java/com/gameaccount/marketplace/controller/AccountController.java` line 334

### Fix Applied
```java
// OLD - Only APPROVED accounts
Page<Account> accountsPage = accountService.getSellerAccounts(
    userId, AccountStatus.APPROVED, pageable);

// NEW - All seller's accounts regardless of status
Page<Account> accountsPage = accountService.getSellerAccounts(
    userId, null, pageable);
```

### Rationale
Sellers should see ALL their listings (PENDING, APPROVED, REJECTED, SOLD) to manage them effectively. Status filtering is appropriate for public marketplace, not seller's own dashboard.

---

## Bug 5: Frontend GraphQL Query Variables Double-Wrapping
**Severity:** Medium  
**Impact:** Account creation mutation failing with "Null value for NonNull type"

### Root Cause
Frontend calling `createAccount({ variables: { input } })` but hook already wraps with `{ variables }`, causing `{ variables: { variables: { input } } }`.

### Affected File
- `frontend-react/src/pages/CreateListingPage.tsx` line 129

### Fix Applied
```typescript
// OLD
const result = await createAccount({ variables: { input } });

// NEW
const result = await createAccount({ input });
```

---

## Testing Impact
All bugs prevented basic seller workflow:
1. ❌ Login → Failed (auth parsing bug)
2. ❌ Create listing → Failed (GraphQL annotations + double-wrapping)
3. ❌ View listings → Failed (status filter bug)

After fixes:
1. ✅ Login with seller@test.com
2. ✅ Create account listings
3. ✅ View all listings in My Listings page

---

## Prevention Recommendations

### 1. Unit Tests
Add unit tests for `getAuthenticatedUserId()` helper methods with mock JWT tokens containing email strings.

### 2. Integration Tests
Add integration tests for:
- GraphQL query/mutation parameter resolution
- REST endpoint authentication flows
- Frontend-backend API contract validation

### 3. Code Review Checklist
- Verify JWT subject usage matches authentication.getName() usage
- Check all GraphQL parameters have `@Argument` annotations
- Validate frontend API endpoint paths match backend routes
- Review status filters in seller-facing vs public-facing endpoints

### 4. Documentation
Document JWT token structure:
```
JWT Subject = User Email (string)
Authentication.getName() = Email (NOT user ID)
```

---

## Related Stories
- Epic 1, Story 1-5: Security Configuration & JWT Implementation
- Epic 2, Story 2-4: REST Controllers - Seller Operations
- Epic 2, Story 2-6: Seller Account Listing Pages

---

## Notes
These bugs existed in "completed" stories but were only discovered during integration testing. Highlights importance of end-to-end testing before marking epics complete.

**Lesson Learned:** Unit tests passing doesn't mean integration works. Need E2E tests for authentication flows.

