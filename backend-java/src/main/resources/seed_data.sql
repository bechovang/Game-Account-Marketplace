-- Game Account Marketplace - Test Database Seed
-- Run with: mysql -u appuser -papppassword gameaccount_marketplace < seed_data.sql

-- ============================================================
-- 1. USERS
-- ============================================================

DELETE FROM accounts WHERE seller_id IN (SELECT id FROM users WHERE email LIKE '%@test.com');
DELETE FROM users WHERE email LIKE '%@test.com';

INSERT INTO users (email, password, full_name, avatar, role, status, balance, rating, total_reviews, created_at, updated_at) VALUES
-- Admin
('admin@test.com', 'password123', 'Admin User', 'https://i.pravatar.cc/150?u=admin', 'ADMIN', 'ACTIVE', 0.0, 0.0, 0, NOW(), NOW()),
-- Sellers
('seller1@test.com', 'password123', 'Pro Gamer Seller', 'https://i.pravatar.cc/150?u=seller1', 'SELLER', 'ACTIVE', 1250.0, 4.5, 23, NOW(), NOW()),
('seller2@test.com', 'password123', 'Trusty Trader', 'https://i.pravatar.cc/150?u=seller2', 'SELLER', 'ACTIVE', 890.0, 4.8, 45, NOW(), NOW()),
('seller3@test.com', 'password123', 'Game Master', 'https://i.pravatar.cc/150?u=seller3', 'SELLER', 'ACTIVE', 2100.0, 4.2, 12, NOW(), NOW()),
-- Buyers
('buyer1@test.com', 'password123', 'John Buyer', 'https://i.pravatar.cc/150?u=buyer1', 'BUYER', 'ACTIVE', 150.0, 0.0, 0, NOW(), NOW()),
('buyer2@test.com', 'password123', 'Jane Gamer', 'https://i.pravatar.cc/150?u=buyer2', 'BUYER', 'ACTIVE', 75.0, 0.0, 0, NOW(), NOW()),
('buyer3@test.com', 'password123', 'Mike Player', 'https://i.pravatar.cc/150?u=buyer3', 'BUYER', 'ACTIVE', 200.0, 0.0, 0, NOW(), NOW());

-- ============================================================
-- 2. GAMES
-- ============================================================

DELETE FROM games;

INSERT INTO games (name, slug, description, icon_url, account_count, created_at) VALUES
('League of Legends', 'league-of-legends', 'MOBA by Riot Games', 'https://upload.wikimedia.org/wikipedia/en/d/d8/League_of_Legends_2019_vector_logo.svg', 0, NOW()),
('Valorant', 'valorant', 'Tactical FPS by Riot Games', 'https://upload.wikimedia.org/wikipedia/commons/f/fc/Valorant_logo_-_pink_color_version.svg', 0, NOW()),
('Mobile Legends', 'mobile-legends', 'Mobile MOBA', 'https://upload.wikimedia.org/wikipedia/commons/thumb/d/d2/Mobile_Legends_Bang_Bang_logo.svg/1200px-Mobile_Legends_Bang_Bang_logo.svg.png', 0, NOW()),
('Garena Free Fire', 'free-fire', 'Battle Royale mobile', 'https://upload.wikimedia.org/wikipedia/en/1/17/Free_Fire_logo.png', 0, NOW()),
('PUBG Mobile', 'pubg-mobile', 'Battle Royale mobile', 'https://upload.wikimedia.org/wikipedia/en/8/86/PUBG_Mobile_Logo.png', 0, NOW()),
('Fortnite', 'fortnite', 'Battle Royale by Epic', 'https://upload.wikimedia.org/wikipedia/en/7/7f/Fortnite_logo.svg.png', 0, NOW()),
('Apex Legends', 'apex-legends', 'Battle Royale by EA', 'https://upload.wikimedia.org/wikipedia/en/6/63/Apex_Legends_Logo.svg', 0, NOW()),
('Counter-Strike 2', 'cs2', 'Tactical FPS by Valve', 'https://upload.wikimedia.org/wikipedia/en/thumb/1/16/Counter-Strike_2_Logo.svg/1200px-Counter-Strike_2_Logo.svg.png', 0, NOW());

-- ============================================================
-- 3. ACCOUNTS
-- ============================================================

INSERT INTO accounts (seller_id, game_id, title, description, price, level, player_rank, status, is_featured, views_count, created_at, updated_at) VALUES
-- League of Legends (seller1)
((SELECT id FROM users WHERE email='seller1@test.com'), (SELECT id FROM games WHERE slug='league-of-legends'), 'Diamond 3 - 120 Skins', 'Diamond account with 120+ rare skins', 450.00, 120, 'DIAMOND', 'APPROVED', TRUE, 0, NOW(), NOW()),
((SELECT id FROM users WHERE email='seller1@test.com'), (SELECT id FROM games WHERE slug='league-of-legends'), 'Master Tier - 50 Skins', 'Master with 50+ legendary skins', 650.00, 150, 'MASTER', 'APPROVED', TRUE, 0, NOW(), NOW()),
((SELECT id FROM users WHERE email='seller1@test.com'), (SELECT id FROM games WHERE slug='league-of-legends'), 'Platinum 2 - All Champs', 'All champions unlocked', 120.00, 85, 'PLATINUM', 'APPROVED', FALSE, 0, NOW(), NOW()),
-- League of Legends (seller2)
((SELECT id FROM users WHERE email='seller2@test.com'), (SELECT id FROM games WHERE slug='league-of-legends'), 'Gold 1 Starter', 'Good starter account', 45.00, 45, 'GOLD', 'APPROVED', FALSE, 0, NOW(), NOW()),
((SELECT id FROM users WHERE email='seller2@test.com'), (SELECT id FROM games WHERE slug='league-of-legends'), 'Silver 2 Budget', 'Budget with 30 champs', 25.00, 30, 'SILVER', 'APPROVED', FALSE, 0, NOW(), NOW()),
-- Valorant (seller1)
((SELECT id FROM users WHERE email='seller1@test.com'), (SELECT id FROM games WHERE slug='valorant'), 'Immortal 2 - All Agents', 'All agents unlocked', 280.00, 200, 'IMMORTAL', 'APPROVED', TRUE, 0, NOW(), NOW()),
-- Valorant (seller2)
((SELECT id FROM users WHERE email='seller2@test.com'), (SELECT id FROM games WHERE slug='valorant'), 'Diamond 3 Premium', 'Diamond with Reaver skins', 150.00, 85, 'DIAMOND', 'APPROVED', TRUE, 0, NOW(), NOW()),
-- Valorant (seller3)
((SELECT id FROM users WHERE email='seller3@test.com'), (SELECT id FROM games WHERE slug='valorant'), 'Platinum 1 Account', 'Good for ranked', 75.00, 65, 'PLATINUM', 'APPROVED', FALSE, 0, NOW(), NOW()),
((SELECT id FROM users WHERE email='seller3@test.com'), (SELECT id FROM games WHERE slug='valorant'), 'Gold 2 Starter', 'All agents unlocked', 40.00, 45, 'GOLD', 'APPROVED', FALSE, 0, NOW(), NOW()),
-- Mobile Legends (seller1)
((SELECT id FROM users WHERE email='seller1@test.com'), (SELECT id FROM games WHERE slug='mobile-legends'), 'Mythic Glory 200 Skins', 'High tier with 200 skins', 180.00, 95, 'MYTHIC', 'APPROVED', TRUE, 0, NOW(), NOW()),
-- Mobile Legends (seller2)
((SELECT id FROM users WHERE email='seller2@test.com'), (SELECT id FROM games WHERE slug='mobile-legends'), 'Epic 50 Heroes', 'Epic rank, 50 heroes', 85.00, 65, 'EPIC', 'APPROVED', FALSE, 0, NOW(), NOW()),
-- Mobile Legends (seller3)
((SELECT id FROM users WHERE email='seller3@test.com'), (SELECT id FROM games WHERE slug='mobile-legends'), 'Legend 1 All Roles', 'All role heroes', 120.00, 75, 'LEGEND', 'APPROVED', FALSE, 0, NOW(), NOW()),
-- Free Fire (seller1)
((SELECT id FROM users WHERE email='seller1@test.com'), (SELECT id FROM games WHERE slug='free-fire'), 'Grandmaster 100 Skins', 'Grandmaster with elite pass', 95.00, 85, 'GRANDMASTER', 'APPROVED', TRUE, 0, NOW(), NOW()),
-- Free Fire (seller2)
((SELECT id FROM users WHERE email='seller2@test.com'), (SELECT id FROM games WHERE slug='free-fire'), 'Heroic 50 Skins', 'Heroic with good skins', 55.00, 60, 'HEROIC', 'APPROVED', FALSE, 0, NOW(), NOW()),
-- PUBG Mobile (seller1)
((SELECT id FROM users WHERE email='seller1@test.com'), (SELECT id FROM games WHERE slug='pubg-mobile'), 'Conqueror 1000 Matches', 'Season conqueror', 110.00, 75, 'CONQUEROR', 'APPROVED', TRUE, 0, NOW(), NOW()),
-- PUBG Mobile (seller3)
((SELECT id FROM users WHERE email='seller3@test.com'), (SELECT id FROM games WHERE slug='pubg-mobile'), 'Ace 500 Matches', 'Ace with good KD', 65.00, 55, 'ACE', 'APPROVED', FALSE, 0, NOW(), NOW()),
-- Fortnite (seller1)
((SELECT id FROM users WHERE email='seller1@test.com'), (SELECT id FROM games WHERE slug='fortnite'), 'Level 200 All Seasons', 'All BP completed', 150.00, 200, 'UNRANKED', 'APPROVED', TRUE, 0, NOW(), NOW()),
-- Fortnite (seller2)
((SELECT id FROM users WHERE email='seller2@test.com'), (SELECT id FROM games WHERE slug='fortnite'), 'Level 100 OG Skins', 'OG Chapter 1 skins', 200.00, 100, 'UNRANKED', 'APPROVED', TRUE, 0, NOW(), NOW()),
-- Apex Legends (seller1)
((SELECT id FROM users WHERE email='seller1@test.com'), (SELECT id FROM games WHERE slug='apex-legends'), 'Predator Bundle', 'All heirlooms', 175.00, 500, 'PREDATOR', 'APPROVED', TRUE, 0, NOW(), NOW()),
-- Apex Legends (seller3)
((SELECT id FROM users WHERE email='seller3@test.com'), (SELECT id FROM games WHERE slug='apex-legends'), 'Diamond 2 All Legends', 'All legends', 85.00, 120, 'DIAMOND', 'APPROVED', FALSE, 0, NOW(), NOW()),
-- CS2 (seller1)
((SELECT id FROM users WHERE email='seller1@test.com'), (SELECT id FROM games WHERE slug='cs2'), 'Global Elite Prime', 'Prime with 2000h', 350.00, 250, 'GLOBAL', 'APPROVED', TRUE, 0, NOW(), NOW()),
-- CS2 (seller2)
((SELECT id FROM users WHERE email='seller2@test.com'), (SELECT id FROM games WHERE slug='cs2'), 'Faceit LVL 10 5 Years', '5 years service', 200.00, 150, 'UNRANKED', 'APPROVED', TRUE, 0, NOW(), NOW()),
-- CS2 (seller3)
((SELECT id FROM users WHERE email='seller3@test.com'), (SELECT id FROM games WHERE slug='cs2'), 'DMG Good Inventory', 'Good skins', 90.00, 120, 'DMG', 'APPROVED', FALSE, 0, NOW(), NOW());

-- ============================================================
-- 4. FAVORITES
-- ============================================================

DELETE FROM favorites WHERE user_id IN (SELECT id FROM users WHERE email LIKE '%@test.com');

INSERT INTO favorites (user_id, account_id, created_at) VALUES
((SELECT id FROM users WHERE email='buyer1@test.com'), (SELECT id FROM accounts WHERE title LIKE '%Immortal%' LIMIT 1), NOW()),
((SELECT id FROM users WHERE email='buyer1@test.com'), (SELECT id FROM accounts WHERE title LIKE '%Mythic%' LIMIT 1), NOW()),
((SELECT id FROM users WHERE email='buyer2@test.com'), (SELECT id FROM accounts WHERE title LIKE '%Conqueror%' LIMIT 1), NOW());

-- ============================================================
-- 5. UPDATE GAME COUNTS
-- ============================================================

UPDATE games g SET account_count = (SELECT COUNT(*) FROM accounts a WHERE a.game_id = g.id AND a.status = 'APPROVED');

-- ============================================================
-- SUMMARY
-- ============================================================

SELECT 'Database seeded successfully!' AS Message;
SELECT COUNT(*) AS Total_Games FROM games;
SELECT COUNT(*) AS Total_Users FROM users;
SELECT COUNT(*) AS Total_Accounts FROM accounts;
SELECT COUNT(*) AS Total_Favorites FROM favorites;
