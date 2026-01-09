# Game Account Marketplace - Test Database Seed Data

This document contains information about the test data seeded in the database for development and testing purposes.

## Database Statistics

| Entity | Count |
|--------|-------|
| **Games** | 8 |
| **Users** | 9 |
| **Accounts** | 23 |
| **Favorites** | 3 |

## Test Credentials

All test accounts share the same password: **`password123`**

### Admin Account

| Email | Role | Balance |
|-------|------|---------|
| admin@test.com | ADMIN | $0.00 |

### Seller Accounts

| Email | Role | Balance | Rating | Reviews |
|-------|------|---------|--------|---------|
| seller1@test.com | SELLER | $1,250.00 | 4.5 | 23 |
| seller2@test.com | SELLER | $890.00 | 4.8 | 45 |
| seller3@test.com | SELLER | $2,100.00 | 4.2 | 12 |

### Buyer Accounts

| Email | Role | Balance |
|-------|------|---------|
| buyer1@test.com | BUYER | $150.00 |
| buyer2@test.com | BUYER | $75.00 |
| buyer3@test.com | BUYER | $200.00 |

## Available Games

1. **League of Legends** - MOBA by Riot Games
2. **Valorant** - Tactical FPS by Riot Games
3. **Mobile Legends** - Mobile MOBA
4. **Garena Free Fire** - Battle Royale mobile
5. **PUBG Mobile** - Battle Royale mobile
6. **Fortnite** - Battle Royale by Epic
7. **Apex Legends** - Battle Royale by EA
8. **Counter-Strike 2** - Tactical FPS by Valve

## Sample Account Listings

### League of Legends Accounts
| Title | Rank | Level | Price | Seller | Featured |
|-------|------|-------|-------|--------|----------|
| Diamond 3 - 120 Skins | DIAMOND | 120 | $450.00 | seller1 | Yes |
| Master Tier - 50 Skins | MASTER | 150 | $650.00 | seller1 | Yes |
| Platinum 2 - All Champs | PLATINUM | 85 | $120.00 | seller1 | No |
| Gold 1 Starter | GOLD | 45 | $45.00 | seller2 | No |
| Silver 2 Budget | SILVER | 30 | $25.00 | seller2 | No |

### Valorant Accounts
| Title | Rank | Level | Price | Seller | Featured |
|-------|------|-------|-------|--------|----------|
| Immortal 2 - All Agents | IMMORTAL | 200 | $280.00 | seller1 | Yes |
| Diamond 3 Premium | DIAMOND | 85 | $150.00 | seller2 | Yes |
| Platinum 1 Account | PLATINUM | 65 | $75.00 | seller3 | No |
| Gold 2 Starter | GOLD | 45 | $40.00 | seller3 | No |

### Mobile Legends Accounts
| Title | Rank | Level | Price | Seller | Featured |
|-------|------|-------|-------|--------|----------|
| Mythic Glory 200 Skins | MYTHIC | 95 | $180.00 | seller1 | Yes |
| Epic 50 Heroes | EPIC | 65 | $85.00 | seller2 | No |
| Legend 1 All Roles | LEGEND | 75 | $120.00 | seller3 | No |

### Other Games
| Game | Title | Rank | Level | Price | Seller | Featured |
|------|-------|------|-------|-------|--------|----------|
| Free Fire | Grandmaster 100 Skins | GRANDMASTER | 85 | $95.00 | seller1 | Yes |
| Free Fire | Heroic 50 Skins | HEROIC | 60 | $55.00 | seller2 | No |
| PUBG Mobile | Conqueror 1000 Matches | CONQUEROR | 75 | $110.00 | seller1 | Yes |
| PUBG Mobile | Ace 500 Matches | ACE | 55 | $65.00 | seller3 | No |
| Fortnite | Level 200 All Seasons | UNRANKED | 200 | $150.00 | seller1 | Yes |
| Fortnite | Level 100 OG Skins | UNRANKED | 100 | $200.00 | seller2 | Yes |
| Apex Legends | Predator Bundle | PREDATOR | 500 | $175.00 | seller1 | Yes |
| Apex Legends | Diamond 2 All Legends | DIAMOND | 120 | $85.00 | seller3 | No |
| CS2 | Global Elite Prime | GLOBAL | 250 | $350.00 | seller1 | Yes |
| CS2 | Faceit LVL 10 5 Years | UNRANKED | 150 | $200.00 | seller2 | Yes |
| CS2 | DMG Good Inventory | DMG | 120 | $90.00 | seller3 | No |

## Favorites

| User | Favorited Account |
|------|-------------------|
| buyer1@test.com | Immortal 2 - All Agents (Valorant) |
| buyer1@test.com | Mythic Glory 200 Skins (Mobile Legends) |
| buyer2@test.com | Conqueror 1000 Matches (PUBG Mobile) |

## How to Use the Seed Data

### 1. Reset/Reseed the Database

```bash
cd backend-java
mysql -u appuser -papppassword gameaccount_marketplace < src/main/resources/seed_data.sql
```

### 2. Start the Application

```bash
# Backend
cd backend-java
mvn spring-boot:run

# Frontend (in another terminal)
cd frontend-react
npm run dev
```

### 3. Test the Application

1. **Browse Marketplace**: Visit `http://localhost:5173`
2. **Login**: Use any of the test credentials above
3. **Filter by Game**: Click on game buttons to filter listings
4. **View Details**: Click on any account card to see details
5. **Add to Favorites**: Click the heart icon on account cards
6. **Become a Seller**:
   - Login as a buyer (e.g., buyer1@test.com)
   - Go to Profile page
   - Click "Become Seller" button
7. **Create Listings** (requires Seller role):
   - Go to "Create New Listing" from profile or directly at `/seller/create`
   - Fill in account details and submit

## Testing Workflows

### Buy Flow Test
1. Login as `buyer1@test.com`
2. Browse accounts and add favorites
3. View account details
4. (Future: Add to cart and checkout)

### Sell Flow Test
1. Login as `buyer1@test.com`
2. Go to Profile → Click "Become Seller"
3. Go to "Create New Listing"
4. Fill out the form with game, title, description, price, etc.
5. Submit the listing
6. View in "My Listings"

### Admin Flow Test
1. Login as `admin@test.com`
2. Browse all accounts (full access)
3. View all user profiles
4. (Future: Admin panel for managing listings and users)

## Notes

- All passwords are set to `password123` for easy testing
- All accounts have status `APPROVED` and are visible in the marketplace
- Featured accounts are highlighted on the home page
- Seed data is cleared and re-inserted each time the script runs
- The script updates game account counts automatically after seeding

## Database Schema Reference

### Users Table Columns
- id, email, password, full_name, avatar, role, status, balance, rating, total_reviews, created_at, updated_at

### Games Table Columns
- id, name, slug, description, icon_url, account_count, created_at, updated_at

### Accounts Table Columns
- id, seller_id, game_id, title, description, price, level, player_rank, status, is_featured, views_count, created_at, updated_at

### Favorites Table Columns
- id, user_id, account_id, created_at
