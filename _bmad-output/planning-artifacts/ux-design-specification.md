# Game Account Marketplace - UX Design Specification

**Version:** 1.0
**Date:** 2026-01-09
**Status:** Draft
**Author:** Admin (Product Owner) + BMAD UX Design Workflow

---

## Table of Contents

1. [Executive Summary](#executive-summary)
2. [Current UI/UX Analysis](#current-uiux-analysis)
3. [Design Principles](#design-principles)
4. [Component Library Selection](#component-library-selection)
5. [Design System Specification](#design-system-specification)
6. [Component Inventory](#component-inventory)
7. [Page-by-Page Redesign](#page-by-page-redesign)
8. [Responsive Design Strategy](#responsive-design-strategy)
9. [Accessibility Requirements](#accessibility-requirements)
10. [Implementation Roadmap](#implementation-roadmap)

---

## Executive Summary

### Problem Statement

The Game Account Marketplace frontend has been built incrementally without a unified design system or component library. This has resulted in:
- Inconsistent UI patterns across pages
- Duplicated navigation code in each component
- Basic styling without a cohesive visual language
- Missing layout components (Header, Navigation, Footer)
- Suboptimal user experience for marketplace interactions

### Solution

Adopt **shadcn/ui** as the component library and create a comprehensive design system for the platform. This specification provides:
- Unified design system with consistent tokens
- Reusable component library built on Radix UI + Tailwind CSS
- Modern, accessible, and performant UI patterns
- Scalable architecture for future features (Epic 4-7)

### Goals

1. **Consistency** - Unified visual language across all pages
2. **Accessibility** - WCAG 2.1 AA compliant by default
3. **Performance** - Optimized rendering and bundle size
4. **Developer Experience** - Type-safe, composable components
5. **User Experience** - Intuitive, efficient marketplace interactions

---

## Current UI/UX Analysis

### Existing Components Inventory

| Component | Location | Issues | Priority |
|-----------|----------|--------|----------|
| Navigation (Inline) | HomePage, SearchPage, FavoritesPage | Duplicated code, no component | HIGH |
| Header (Inline) | HomePage | Not reusable, inconsistent | HIGH |
| AccountCard | components/account/AccountCard.tsx | Basic design, missing features | MEDIUM |
| Search Input | HomePage | No advanced search UI | MEDIUM |
| Filter Sidebar | components/search/FilterSidebar.tsx | Functional but basic | MEDIUM |
| Image Gallery | components/account/ImageGallery.tsx | Needs lightbox feature | LOW |
| Modals | components/modals/ | Custom implementation | MEDIUM |
| Loading Skeleton | components/common/LoadingSkeleton.tsx | Basic | LOW |
| Error Messages | components/common/ErrorMessage.tsx | Basic | LOW |

### Current Tech Stack

```
React 18.2.0
TypeScript 5.3.3
Tailwind CSS 3.4.0
React Router 6.21.1
Apollo Client 3.8.10
React Hook Form 7.49.2
Zustand 4.4.7
@headlessui/react 2.2.9 (will be replaced)
react-hot-toast 2.4.1
```

### Identified Issues

1. **No Layout System**
   - Navigation duplicated across pages
   - No shared Header component
   - No Footer component
   - Inconsistent page structures

2. **Component Gaps**
   - No Badge component for roles/status
   - No Tooltip component for help text
   - No Tabs component for account details
   - No Dialog/Sheet for modals
   - No Command palette for quick navigation
   - No Avatar group for multi-player accounts

3. **Design Inconsistencies**
   - Mixed button styles (blue-600 vs variants)
   - Inconsistent spacing (py-2 vs py-4)
   - No defined color palette
   - No typography scale
   - Inconsistent border radius values

4. **Missing Patterns**
   - No empty state illustrations
   - No loading states for cards
   - No error boundaries
   - No skeleton loaders for images
   - No optimistic UI feedback
   - No toast notifications for all actions

---

## Design Principles

### 1. Clarity Over Density
- **Rationale:** Game accounts have complex attributes (level, rank, skins, etc.)
- **Application:** Use generous spacing, clear hierarchy, scannable layouts
- **Avoid:** Information overload, cramped layouts

### 2. Visual Hierarchy
- **Rationale:** Users need to quickly scan listings and compare options
- **Application:** Size, color, and position guide attention to key information
- **Priority:** Price > Title > Game > Seller > Stats

### 3. Progressive Disclosure
- **Rationale:** Advanced filters and details clutter the interface
- **Application:** Show basics first, reveal details on interaction
- **Patterns:** Collapsible filters, expandable details, hover tooltips

### 4. Feedback & Affordance
- **Rationale:** Users need to know what's interactive and what happened
- **Application:** Hover states, active states, loading indicators, toasts
- **Avoid:** Static, unresponsive UI elements

### 5. Mobile-First Responsive
- **Rationale:** Many gamers browse on mobile devices
- **Application:** Touch targets (44px+), readable text, stacked layouts
- **Breakpoints:** 640px | 768px | 1024px | 1280px

### 6. Performance First
- **Rationale:** Image-heavy listings need fast rendering
- **Application:** Lazy loading, image optimization, code splitting
- **Target:** LCP < 2.5s, FID < 100ms, CLS < 0.1

---

## Component Library Selection

### Why shadcn/ui?

| Criteria | shadcn/ui | Alternatives | Winner |
|----------|-----------|--------------|--------|
| **Tailwind Native** | ✅ Built on Tailwind | ❌ Emotion (Chakra), CSS-in-JS (Mantine) | shadcn/ui |
| **Accessibility** | ✅ Radix UI primitives | ✅ Good (Chakra), ✅ Good (Mantine) | Tie |
| **Customization** | ✅ You own the code | ⚠️ Theme config only | shadcn/ui |
| **Bundle Size** | ✅ Tree-shakeable | ⚠️ Larger (Ant Design) | shadcn/ui |
| **TypeScript** | ✅ First-class support | ✅ Good support | Tie |
| **Documentation** | ✅ Excellent | ✅ Good | Tie |
| **Components** | ✅ 40+ components | ✅ More (Ant Design 200+) | Tie |
| **Copy-Paste** | ✅ Full control | ❌ Black box | shadcn/ui |
| **Updates** | ✅ Your pace | ❌ Breaking major versions | shadcn/ui |

### Key Benefits

1. **You Own the Code** - Components are copied to your project, full control
2. **Tailwind Native** - Works with existing Tailwind setup
3. **Radix UI Primitives** - Excellent accessibility out of the box
4. **Incremental Adoption** - Add components as needed
5. **No Runtime** - Compile-time optimizations
6. **Type-Safe** - Full TypeScript support
7. **Modern Design** - Contemporary aesthetics

### Required Dependencies

```bash
# Core
npm install class-variance-authority clsx tailwind-merge

# Icons (Lucide - modern, tree-shakeable)
npm install lucide-react

# Animations (optional, for smooth transitions)
npm install framer-motion

# Better toast notifications
npm install sonner

# Utilities
npm install @radix-ui/react-slot
npm install @radix-ui/react-dropdown-menu
npm install @radix-ui/react-dialog
npm install @radix-ui/react-toast
npm install @radix-ui/react-tooltip
npm install @radix-ui/react-tabs
npm install @radix-ui/react-avatar
npm install @radix-ui/react-select
npm install @radix-ui/react-separator
```

---

## Design System Specification

### Color Palette

#### Primary Colors (Gaming Theme)

```css
/* Light Mode */
--primary: 221 83% 53%;          /* Blue 600 - #2563EB */
--primary-foreground: 210 40% 98%;  /* White */

--secondary: 210 40% 96%;        /* Slate 100 - #F1F5F9 */
--secondary-foreground: 222 47% 11%;  /* Slate 900 - #0F172A */

--accent: 142 76% 36%;            /* Green 700 - #15803D */
--accent-foreground: 355 100% 97%;  /* Rose 50 - #FFF1F2 */

--muted: 210 40% 96%;             /* Slate 100 */
--muted-foreground: 215 16% 47%;  /* Slate 500 */

/* Dark Mode */
--primary: 217 91% 60%;           /* Blue 500 */
--primary-foreground: 222 47% 11%;

--secondary: 217 33% 17%;         /* Slate 800 */
--secondary-foreground: 210 40% 98%;

--accent: 142 71% 45%;            /* Green 600 */
--accent-foreground: 355 100% 97%;

--muted: 217 33% 17%;             /* Slate 800 */
--muted-foreground: 215 20% 65%;  /* Slate 400 */
```

#### Semantic Colors

```css
/* Status Colors */
--success: 142 76% 36%;           /* Green 700 - APPROVED */
--warning: 38 92% 50%;            /* Amber 500 - PENDING */
--destructive: 0 84% 60%;         /* Red 500 - REJECTED/SOLD */
--info: 199 89% 48%;              /* Sky 500 - INFO */

/* Role Colors */
--role-admin: 262 83% 58%;        /* Purple 600 */
--role-seller: 142 76% 36%;        /* Green 700 */
--role-buyer: 221 83% 53%;        /* Blue 600 */

/* Price Colors */
--price-low: 142 76% 36%;         /* Green */
--price-medium: 38 92% 50%;       /* Amber */
--price-high: 0 84% 60%;          /* Red */
```

### Typography

```css
/* Font Family */
--font-sans: 'Inter', system-ui, -apple-system, sans-serif;

/* Type Scale (Tailwind defaults) */
--text-xs: 0.75rem;    /* 12px - Labels, badges */
--text-sm: 0.875rem;   /* 14px - Secondary text */
--text-base: 1rem;     /* 16px - Body text */
--text-lg: 1.125rem;   /* 18px - Emphasized */
--text-xl: 1.25rem;    /* 20px - Subheadings */
--text-2xl: 1.5rem;    /* 24px - Section titles */
--text-3xl: 1.875rem;  /* 30px - Page headings */
--text-4xl: 2.25rem;   /* 36px - Hero text */

/* Font Weights */
--font-normal: 400;
--font-medium: 500;
--font-semibold: 600;
--font-bold: 700;
```

### Spacing

```css
/* Tailwind spacing scale (rem units) */
--spacing-1: 0.25rem;   /* 4px */
--spacing-2: 0.5rem;    /* 8px */
--spacing-3: 0.75rem;   /* 12px */
--spacing-4: 1rem;      /* 16px */
--spacing-5: 1.25rem;   /* 20px */
--spacing-6: 1.5rem;    /* 24px */
--spacing-8: 2rem;      /* 32px */
--spacing-10: 2.5rem;   /* 40px */
--spacing-12: 3rem;     /* 48px */
--spacing-16: 4rem;     /* 64px */
```

### Border Radius

```css
--radius-sm: 0.375rem;   /* 6px - Small elements */
--radius: 0.5rem;        /* 8px - Default cards, buttons */
--radius-md: 0.75rem;    /* 12px - Medium cards */
--radius-lg: 1rem;       /* 16px - Large cards, modals */
--radius-xl: 1.25rem;    /* 20px - Hero sections */
--radius-full: 9999px;   /* Pill buttons, badges */
```

### Shadows

```css
--shadow-sm: 0 1px 2px 0 rgb(0 0 0 / 0.05);
--shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1), 0 1px 2px -1px rgb(0 0 0 / 0.1);
--shadow-md: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
--shadow-lg: 0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1);
--shadow-xl: 0 20px 25px -5px rgb(0 0 0 / 0.1), 0 8px 10px -6px rgb(0 0 0 / 0.1);
```

---

## Component Inventory

### New Components to Add

#### Layout Components

| Component | shadcn/ui | Priority | Epic |
|-----------|-----------|----------|------|
| **AppHeader** | Custom + Sheet | HIGH | All |
| **Navigation** | Custom | HIGH | All |
| **Footer** | Custom | MEDIUM | All |
| **Sidebar** | Sheet (shadcn) | MEDIUM | Admin (E7) |

#### Form Components

| Component | shadcn/ui | Priority | Epic |
|-----------|-----------|----------|------|
| **Input** | ✅ Input | HIGH | E2, E4 |
| **Textarea** | ✅ Textarea | HIGH | E2 |
| **Select** | ✅ Select | HIGH | E2, E3 |
| **Checkbox** | ✅ Checkbox | MEDIUM | E3 |
| **RadioGroup** | ✅ Radio Group | MEDIUM | E3 |
| **Slider** | ✅ Slider | LOW | E3 |
| **Switch** | ✅ Switch | LOW | Settings |
| **DatePicker** | Custom (input + Popover) | MEDIUM | E4 |

#### Data Display

| Component | shadcn/ui | Priority | Epic |
|-----------|-----------|----------|------|
| **Badge** | ✅ Badge | HIGH | E2, E3 |
| **Card** | ✅ Card | HIGH | E2, E3 |
| **Table** | ✅ Table | MEDIUM | E5, E7 |
| **Tabs** | ✅ Tabs | HIGH | E3 |
| **Avatar** | ✅ Avatar | HIGH | E1, E2 |
| **AvatarGroup** | Custom | MEDIUM | E3 |
| **Skeleton** | ✅ Skeleton | HIGH | All |
| **EmptyState** | Custom | HIGH | All |
| **PriceDisplay** | Custom | HIGH | E2, E3 |

#### Feedback

| Component | shadcn/ui | Priority | Epic |
|-----------|-----------|----------|------|
| **Toast** | ✅ Sonner | HIGH | All |
| **Alert** | ✅ Alert | MEDIUM | All |
| **Dialog** | ✅ Dialog | HIGH | All |
| **AlertDialog** | ✅ Alert Dialog | MEDIUM | E2, E4 |
| **Tooltip** | ✅ Tooltip | MEDIUM | E3 |
| **Progress** | ✅ Progress | LOW | E4 |
| **Spinner** | Custom | HIGH | All |

#### Navigation

| Component | shadcn/ui | Priority | Epic |
|-----------|-----------|----------|------|
| **Button** | ✅ Button | HIGH | All |
| **DropdownMenu** | ✅ Dropdown Menu | HIGH | E2, E7 |
| **ContextMenu** | ✅ Context Menu | LOW | E7 |
| **Pagination** | ✅ Pagination | HIGH | E3 |
| **Breadcrumb** | ✅ Breadcrumb | MEDIUM | E3 |

#### Overlays

| Component | shadcn/ui | Priority | Epic |
|-----------|-----------|----------|------|
| **Sheet** | ✅ Sheet | HIGH | E4, E5 |
| **Popover** | ✅ Popover | MEDIUM | E3 |
| **HoverCard** | ✅ Hover Card | MEDIUM | E2 |
| **Command** | ✅ Command | LOW | Power users |

#### Enhanced AccountCard (Redesign)

| Feature | Priority | Epic |
|---------|----------|------|
| Price highlighting | HIGH | E2, E3 |
| Seller rating stars | HIGH | E2 |
| Favorite button (inline) | HIGH | E3 |
| Quick stats (level, rank) | MEDIUM | E3 |
| Status badges | HIGH | E2 |
| Image lazy loading | HIGH | E3 |
| Hover actions | MEDIUM | E3 |
| Wishlist indicator | HIGH | E3 |

---

## Page-by-Page Redesign

### 1. HomePage (/)

**Current Issues:**
- Inline header, not reusable
- Search input too basic
- Game filter buttons are functional but plain
- Featured/New sections use same card design
- No visual distinction between sections

**Redesign Specifications:**

#### Header Component
```tsx
<Header>
  <Logo />
  <SearchBar />
  <UserMenu />
  <Actions>
    <FavoritesButton />
    <NotificationsButton />
  </Actions>
</Header>
```

#### Hero Section (New)
```tsx
<Hero>
  <HeroContent>
    <Title>Buy & Sell Game Accounts</Title>
    <Subtitle>Secure marketplace with escrow protection</Subtitle>
    <CTAGroup>
      <Button variant="default">Browse Listings</Button>
      <Button variant="outline">Start Selling</Button>
    </CTAGroup>
  </HeroContent>
  <HeroImage> {/* Game account illustration */} </HeroImage>
</Hero>
```

#### Game Categories (Enhanced)
```tsx
<GameCategories>
  <CategoryCard highlighted>
    <GameIcon />
    <GameName>Valorant</GameName>
    <AccountCount>1,234 accounts</AccountCount>
  </CategoryCard>
</GameCategories>
```

#### Featured Section
```tsx
<Section>
  <SectionHeader>
    <Title>Featured Listings</Title>
    <Badge variant="secondary">Curated</Badge>
  </SectionHeader>
  <AccountGrid variant="featured">
    <AccountCard featured />
  </AccountGrid>
</Section>
```

#### New Listings (Infinite Scroll)
```tsx
<Section>
  <SectionHeader>
    <Title>New Listings</Title>
    <SortDropdown />
  </SectionHeader>
  <InfiniteScrollGrid>
    <AccountCard />
    <LoadingSentinel />
  </InfiniteScrollGrid>
</Section>
```

---

### 2. SearchPage (/search)

**Current Issues:**
- Filter sidebar is functional but basic
- No saved searches
- No search history
- Active filter chips are basic
- No sort options UI

**Redesign Specifications:**

#### Search Header
```tsx
<SearchHeader>
  <SearchInput />
  <FilterToggle />
  <SortDropdown />
</SearchHeader>
```

#### Filter Sidebar (Enhanced)
```tsx
<FilterSheet>
  <FilterSection title="Game">
    <GameFilterGroup />
  </FilterSection>
  <FilterSection title="Price Range">
    <PriceSlider />
  </FilterSection>
  <FilterSection title="Level">
    <LevelRangeInput />
  </FilterSection>
  <FilterSection title="Rank">
    <RankFilterGroup />
  </FilterSection>
  <FilterActions>
    <Button variant="outline">Clear All</Button>
    <Button>Apply Filters</Button>
  </FilterActions>
</FilterSheet>
```

#### Active Filters
```tsx
<ActiveFilterBar>
  <FilterChip removable>Valorant</FilterChip>
  <FilterChip removable>$50 - $200</FilterChip>
  <FilterChip removable>Diamond+</FilterChip>
  <ClearAllButton />
</ActiveFilterBar>
```

#### Results Grid
```tsx
<ResultsHeader>
  <ResultCount>1,234 listings found</ResultCount>
  <ToggleGroup>
    <ToggleItem>Grid</ToggleItem>
    <ToggleItem>List</ToggleItem>
  </ToggleGroup>
</ResultsHeader>

<ResultsGrid>
  <AccountCard />
</ResultsGrid>

<NoResultsState>
  <Illustration />
  <Title>No listings found</Title>
  <Description>Try adjusting your filters</Description>
  <Button>Clear Filters</Button>
</NoResultsState>
```

---

### 3. AccountDetailPage (/accounts/:id)

**Current Issues:**
- Image gallery needs lightbox
- Seller info separated from actions
- No quick purchase flow
- Tabs not used for details
- No related listings

**Redesign Specifications:**

#### Page Header
```tsx
<PageHeader>
  <Breadcrumb>
    <BreadcrumbItem href="/">Home</BreadcrumbItem>
    <BreadcrumbItem href={`/search?game=${game.slug}`}>{game.name}</BreadcrumbItem>
    <BreadcrumbItem current>{title}</BreadcrumbItem>
  </BreadcrumbHeader>
  <StatusBadge>{status}</StatusBadge>
</PageHeader>
```

#### Main Content Grid
```tsx
<TwoColumnGrid>
  {/* Left Column */}
  <LeftColumn>
    <ImageGalleryWithLightbox>
      <MainImage />
      <ThumbnailStrip />
    </ImageGalleryWithLightbox>

    <Tabs defaultValue="details">
      <TabsList>
        <TabsTrigger value="details">Details</TabsTrigger>
        <TabsTrigger value="stats">Stats</TabsTrigger>
        <TabsTrigger value="history">History</TabsTrigger>
      </TabsList>
      <TabsContent value="details">
        <DetailsPanel />
      </TabsContent>
      <TabsContent value="stats">
        <StatsPanel />
      </TabsContent>
      <TabsContent value="history">
        <HistoryPanel />
      </TabsContent>
    </Tabs>
  </LeftColumn>

  {/* Right Column */}
  <RightColumn>
    <StickyContainer>
      <PurchaseCard>
        <PriceDisplay>${price}</PriceDisplay>
        <SellerCard compact />
        <ActionButtons>
          <Button size="lg" variant="default">Buy Now</Button>
          <Button size="lg" variant="outline">Make Offer</Button>
          <FavoriteButton />
        </ActionButtons>
        <TrustBadges>
          <Badge icon="shield">Escrow Protected</Badge>
          <Badge icon="clock">Instant Delivery</Badge>
        </TrustBadges>
      </PurchaseCard>
      <SellerCard expanded />
    </StickyContainer>
  </RightColumn>
</TwoColumnGrid>
```

#### Related Listings
```tsx
<Section>
  <SectionHeader>
    <Title>More from {game.name}</Title>
    <Link href={`/search?game=${game.slug}`}>View All →</Link>
  </SectionHeader>
  <AccountGrid variant="compact">
    <AccountCard compact />
  </AccountGrid>
</Section>
```

---

### 4. FavoritesPage (/favorites)

**Current Issues:**
- Basic grid layout
- No bulk actions
- No organization (folders/tags)
- Remove button is modal-based (slow)

**Redesign Specifications:**

#### Page Header
```tsx
<PageHeader>
  <Title>My Favorites</Title>
  <Actions>
    <Button variant="ghost" size="sm">
      <FolderPlus />
      Create Folder
    </Button>
    <DropdownMenu>
      <DropdownMenuTrigger>
        <MoreVertical />
      </DropdownMenuTrigger>
      <DropdownMenuContent>
        <DropdownMenuItem>Sort by Date Added</DropdownMenuItem>
        <DropdownMenuItem>Sort by Price</DropdownMenuItem>
        <DropdownMenuSeparator />
        <DropdownMenuItem variant="destructive">
          <Trash2 />
          Clear All Favorites
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  </Actions>
</PageHeader>
```

#### Favorites Grid with Actions
```tsx
<FavoritesGrid>
  <AccountCard
    variant="favorite"
    actions={{
      primary: <MoveToFolder />,
      secondary: <RemoveFavorite inline />
    }}
  />
</FavoritesGrid>

<EmptyFavoritesState>
  <Heart />
  <Title>No favorites yet</Title>
  <Description>Save listings you're interested in</Description>
  <Button href="/search">Browse Listings</Button>
</EmptyFavoritesState>
```

---

### 5. CreateListingPage (/seller/create)

**Current Issues:**
- Form is functional but basic
- No preview
- No image upload with crop
- No validation feedback

**Redesign Specifications:**

#### Multi-Step Form
```tsx
<FormWizard>
  <Step current={1} total={4}>
    <WizardProgress />

    <form>
      {/* Step 1: Basic Info */}
      <FormFieldSet>
        <FormLabel>Game</FormLabel>
        <Select />
        <FormDescription>Select the game for this account</FormDescription>
      </FormFieldSet>

      {/* ... more fields */}

      <FormActions>
        <Button variant="outline" disabled>Back</Button>
        <Button type="submit">Next Step</Button>
      </FormActions>
    </form>
  </Step>
</FormWizard>
```

---

### 6. ProfilePage (/profile)

**Redesign Specifications:**

```tsx
<ProfileLayout>
  <ProfileSidebar>
    <Avatar size="xl" />
    <UserName />
    <RoleBadge />
    <Stats />
    <Navigation />
  </ProfileSidebar>

  <ProfileContent>
    <Tabs defaultValue="listings">
      <TabsList>
        <TabsTrigger value="listings">My Listings</TabsTrigger>
        <TabsTrigger value="purchases">Purchases</TabsTrigger>
        <TabsTrigger value="sales">Sales</TabsTrigger>
        <TabsTrigger value="settings">Settings</TabsTrigger>
      </TabsList>
      <TabsContent value="listings">
        <MyListings />
      </TabsContent>
    </Tabs>
  </ProfileContent>
</ProfileLayout>
```

---

## Responsive Design Strategy

### Breakpoints

```css
/* Mobile First Approach */
sm: 640px   /* Small devices (landscape phones) */
md: 768px   /* Medium devices (tablets) */
lg: 1024px  /* Large devices (desktops) */
xl: 1280px  /* Extra large devices */
2xl: 1536px /* 2X large devices */
```

### Layout Patterns

#### Mobile (< 640px)
- Single column layouts
- Bottom navigation for key actions
- Full-width cards
- Stacked forms
- Collapsible filters
- Touch-optimized (44px targets)

#### Tablet (640px - 1024px)
- Two-column grids (2x2)
- Side-by-side forms on desktop
- Persistent filter sidebar
- Expanded navigation

#### Desktop (> 1024px)
- Three-column grids (3x3)
- Max-width containers (1280px)
- Hover interactions
- Multi-column layouts
- Sticky sidebars

### Component Responsiveness

```tsx
/* Account Card */
<AccountCard
  variant={{
    default: "full",      /* Mobile */
    sm: "compact",        /* Tablet */
    lg: "expanded"        /* Desktop */
  }}
/>

/* Navigation */
<Navigation
  layout={{
    default: "bottom",    /* Mobile */
    md: "top",            /* Desktop */
  }}
/>

/* Filters */
<Filters
  presentation={{
    default: "sheet",     /* Mobile */
    md: "sidebar"         /* Desktop */
  }}
/>
```

---

## Accessibility Requirements

### WCAG 2.1 AA Compliance

#### Color Contrast
- Normal text: 4.5:1 minimum
- Large text (18px+): 3:1 minimum
- UI components: 3:1 minimum

#### Keyboard Navigation
- All interactive elements reachable via Tab
- Visible focus indicators (2px solid offset)
- Skip links for main content
- Logical tab order
- No keyboard traps

#### Screen Reader Support
- Semantic HTML elements
- ARIA labels where needed
- Alt text for images
- Error announcements
- Live regions for dynamic content

#### Touch Targets
- Minimum 44x44px for interactive elements
- Spacing between touch targets
- No hover-only interactions

### Implementation Checklist

- [ ] All images have alt text
- [ ] Form inputs have associated labels
- [ ] Buttons have accessible names
- [ ] Color not the only indicator
- [ ] Focus visible on all interactive elements
- [ ] Skip to main content link
- [ ] Proper heading hierarchy (h1 → h2 → h3)
- [ ] ARIA live regions for toasts/errors
- [ ] Keyboard traps avoided
- [ ] Modal focus management

---

## Implementation Roadmap

### Phase 1: Foundation (Day 1)

**Setup shadcn/ui:**
```bash
# Initialize shadcn/ui
npx shadcn-ui@latest init

# Install core utilities
npm install class-variance-authority clsx tailwind-merge lucide-react sonner

# Create lib/utils.ts
```

**Create Design Tokens:**
- [ ] Update `tailwind.config.js` with custom colors
- [ ] Create `components.json` for shadcn config
- [ ] Add Inter font family
- [ ] Define CSS custom properties

**Add Essential Components:**
- [ ] Button (shadcn)
- [ ] Input (shadcn)
- [ ] Card (shadcn)
- [ ] Badge (shadcn)
- [ ] Toast/Sonner (shadcn)
- [ ] Skeleton (shadcn)

### Phase 2: Layout Components (Day 2)

**Navigation System:**
- [ ] Create `AppHeader` component
- [ ] Create `Navigation` component
- [ ] Create `Footer` component
- [ ] Create `Sidebar` component (Sheet-based)
- [ ] Update `App.tsx` with new layout

**User Menu:**
- [ ] Create `UserMenu` dropdown
- [ ] Create `Avatar` component (shadcn)
- [ ] Add role badges
- [ ] Add user stats

**Search:**
- [ ] Create `SearchBar` component
- [ ] Create `Command` palette for quick search
- [ ] Add search history

### Phase 3: Core Components (Day 3)

**Account Card (Redesign):**
- [ ] Enhanced `AccountCard` with variants
- [ ] Price highlighting
- [ ] Seller rating display
- [ ] Inline favorite button
- [ ] Status badges
- [ ] Hover actions
- [ ] Image lazy loading

**Data Display:**
- [ ] `PriceDisplay` component
- [ ] `Rating` component (stars)
- [ ] `EmptyState` component
- [ ] `LoadingState` component
- [ ] `ErrorBoundary` component

**Feedback:**
- [ ] Toast notifications (Sonner)
- [ ] Alert banners
- [ ] Progress indicators
- [ ] Loading spinners

### Phase 4: Page Redesigns (Day 4-5)

**HomePage:**
- [ ] Hero section
- [ ] Game categories (enhanced)
- [ ] Featured listings
- [ ] New listings (infinite scroll)

**SearchPage:**
- [ ] Enhanced filter sidebar (Sheet)
- [ ] Active filter chips
- [ ] Sort dropdown
- [ ] Results grid/list toggle

**AccountDetailPage:**
- [ ] Image gallery with lightbox
- [ ] Tabs for details
- [ ] Sticky purchase card
- [ ] Related listings

**FavoritesPage:**
- [ ] Bulk actions
- [ ] Folder organization
- [ ] Inline remove

### Phase 5: Forms & Modals (Day 6)

**Form Components:**
- [ ] Enhanced form fields
- [ ] Form validation
- [ ] Multi-step forms
- [ ] Form wizards

**Modals:**
- [ ] Dialog (shadcn)
- [ ] AlertDialog (shadcn)
- [ ] Sheet (shadcn)
- [ ] Popover (shadcn)

### Phase 6: Polish & Testing (Day 7)

**Optimization:**
- [ ] Image optimization
- [ ] Code splitting
- [ ] Lazy loading
- [ ] Bundle analysis

**Testing:**
- [ ] Accessibility audit
- [ ] Cross-browser testing
- [ ] Mobile testing
- [ ] Performance testing

**Documentation:**
- [ ] Component storybook (optional)
- [ ] Usage documentation
- [ ] Design tokens documentation

---

## Migration Strategy

### Incremental Approach

1. **Setup First** - Install shadcn/ui without breaking existing UI
2. **Create New Components** - Build alongside existing ones
3. **Page by Page** - Migrate one page at a time
4. **Test Thoroughly** - Ensure no regressions
5. **Remove Old Code** - Delete unused components

### Component Migration Order

1. ✅ Layout components (Header, Navigation)
2. ✅ Core components (Button, Input, Card)
3. ✅ HomePage (simplest, most visible)
4. ✅ AccountCard (used everywhere)
5. ✅ SearchPage
6. ✅ AccountDetailPage
7. ✅ FavoritesPage
8. ✅ Form pages

### Backward Compatibility

- Keep old components during migration
- Use feature flags if needed
- Test in staging environment
- Gradual rollout to users

---

## Success Metrics

### Performance Targets

| Metric | Target | Measurement |
|--------|--------|-------------|
| First Contentful Paint (FCP) | < 1.8s | Lighthouse |
| Largest Contentful Paint (LCP) | < 2.5s | Lighthouse |
| First Input Delay (FID) | < 100ms | Lighthouse |
| Cumulative Layout Shift (CLS) | < 0.1 | Lighthouse |
| Time to Interactive (TTI) | < 3.8s | Lighthouse |

### User Experience Metrics

| Metric | Target | Measurement |
|--------|--------|-------------|
| Time to first listing view | < 2s | Analytics |
| Search completion rate | > 80% | Analytics |
| Favorite add/remove | < 500ms perceived | User testing |
| Mobile touch accuracy | 100% | User testing |

### Developer Experience Metrics

| Metric | Target | Measurement |
|--------|--------|-------------|
| Component reusability | > 80% | Code analysis |
| TypeScript coverage | 100% | tsc --noEmit |
| Build time | < 30s | CI/CD |
| Bundle size | < 500KB (gzipped) | Bundle report |

---

## Appendix

### A. Component Variants

#### Button Variants
```tsx
<Button variant="default" | "outline" | "ghost" | "link" />
<Button size="default" | "sm" | "lg" | "icon" />
```

#### Card Variants
```tsx
<Card variant="default" | "featured" | "compact" | "elevated" />
<Card size="sm" | "md" | "lg" />
```

#### Badge Variants
```tsx
<Badge variant="default" | "secondary" | "destructive" | "outline" />
<Badge size="sm" | "md" />
```

### B. Color Tokens Reference

```css
/* Usage in Tailwind */
bg-primary text-primary-foreground
bg-secondary text-secondary-foreground
bg-accent text-accent-foreground
bg-muted text-muted-foreground

/* Status */
bg-success text-success-foreground
bg-warning text-warning-foreground
bg-destructive text-destructive-foreground

/* Roles */
bg-role-admin text-role-admin-foreground
bg-role-seller text-role-seller-foreground
bg-role-buyer text-role-buyer-foreground
```

### C. Icon Library (Lucide)

```typescript
// Navigation Icons
import { Home, Search, Heart, User, Settings } from 'lucide-react';

// Action Icons
import { Plus, Minus, Edit, Trash, Check, X } from 'lucide-react';

// Status Icons
import { Shield, Clock, AlertCircle, CheckCircle } from 'lucide-react';

// Gaming Icons
import { Gamepad2, Trophy, Star, Zap } from 'lucide-react';
```

---

## Changelog

| Version | Date | Changes | Author |
|---------|------|---------|--------|
| 1.0 | 2026-01-09 | Initial UX design specification | Admin + BMAD Workflow |

---

**Next Steps:**
1. Review and approve this specification
2. Begin Phase 1: Foundation setup
3. Create design system tokens
4. Start component migration

---

*This specification is a living document and will be updated as the design evolves.*
