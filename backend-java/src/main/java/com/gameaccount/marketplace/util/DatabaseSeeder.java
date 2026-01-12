package com.gameaccount.marketplace.util;

import com.gameaccount.marketplace.entity.*;
import com.gameaccount.marketplace.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Database Seeder for generating large-scale test data
 *
 * Usage:
 * 1. Run with profile:
 *    mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=seed
 *
 * 2. To clean database before seeding:
 *    mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=seed --seed.clean=true
 *
 * Configuration:
 * - Change @Profile("seed") to control when this runs
 * - Adjust BATCH_SIZE for performance tuning
 * - Modify TARGET_* constants for different dataset sizes
 * - Set seed.clean=true to cleanup database before seeding
 *
 * Architectural Features:
 * - Uses Long[] IDs instead of full entities to reduce memory footprint
 * - Single seeded Random instance for reproducibility
 * - Named constants for magic numbers
 * - Externalized game/prefix/rank arrays for maintainability
 * - Cleanup mechanism for re-seeding with fresh data
 */
@Slf4j
@Component
@Profile("seed") // Only runs when --spring.profiles.active=seed
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final AccountRepository accountRepository;
    private final FavoriteRepository favoriteRepository;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionUtil encryptionUtil;

    // Configuration
    private static final int TARGET_USERS = 100;
    private static final int TARGET_GAMES = 50;
    private static final int TARGET_ACCOUNTS = 1000;
    private static final int TARGET_FAVORITES = 500;

    private static final int BATCH_SIZE = 50; // Batch insert for performance
    private static final long SEED = 12345L; // Fixed seed for reproducibility

    // Named constants for magic numbers
    private static final double APPROVED_RATIO = 0.70;
    private static final double PENDING_RATIO = 0.85;
    private static final double SOLD_RATIO = 0.95;
    private static final double FEATURED_RATIO = 0.05;
    private static final double SELLER_RATIO = 0.40;

    // Externalized game names as constant array
    private static final String[] POPULAR_GAMES = {
        "League of Legends", "Valorant", "Counter-Strike 2", "Dota 2", "Fortnite",
        "Apex Legends", "PUBG", "Call of Duty: Warzone", "Overwatch 2", "Rainbow Six Siege",
        "Rocket League", "FIFA 24", "NBA 2K24", "Minecraft", "Roblox",
        "Grand Theft Auto V", "Red Dead Redemption 2", "Elden Ring", "World of Warcraft", "Final Fantasy XIV",
        "Lost Ark", "Path of Exile", "Diablo IV", "Destiny 2", "Warframe",
        "Genshin Impact", "Honkai: Star Rail", "Black Desert Online", "New World", "Guild Wars 2",
        "The Elder Scrolls Online", "Star Wars: The Old Republic", "RuneScape", "Albion Online", "EVE Online",
        "Team Fortress 2", "Paladins", "Smite", "Heroes of the Storm", "Mobile Legends",
        "Arena of Valor", "Wild Rift", "Clash of Clans", "Clash Royale", "Brawl Stars",
        "Among Us", "Fall Guys", "Dead by Daylight", "Phasmophobia", "Rust"
    };

    private static final String[] ACCOUNT_RANKS = {
        "Bronze", "Silver", "Gold", "Platinum", "Diamond", "Master", "Grandmaster", "Challenger"
    };

    private static final String[] ACCOUNT_PREFIXES = {
        "Premium", "Rare", "Epic", "Legendary", "Pro", "High Level", "Max Rank"
    };

    // Single Random instance (consolidated)
    private final Random random = new Random(SEED);
    private final Faker faker = new Faker(this.random);

    // Configuration flag for cleanup
    @Value("${seed.clean:false}")
    private boolean cleanBeforeSeed;

    // Use Long[] IDs instead of full entities to reduce memory footprint
    private List<User> allUsers;
    private List<Game> allGames;
    private Long[] allAccountIds;  // Just IDs, not full Account entities

    @Override
    public void run(String... args) {
        log.info("🌱 Starting Database Seeding...");
        log.info("Target: {} users, {} games, {} accounts", TARGET_USERS, TARGET_GAMES, TARGET_ACCOUNTS);
        log.info("Clean before seed: {}", cleanBeforeSeed);

        long startTime = System.currentTimeMillis();

        try {
            // Cleanup mechanism
            if (cleanBeforeSeed) {
                cleanupDatabase();
            }

            seedGames();
            seedUsers();
            seedAccounts();
            seedFavorites();

            long duration = System.currentTimeMillis() - startTime;
            log.info("✅ Database seeding completed in {} seconds", duration / 1000);
            printStatistics();

        } catch (Exception e) {
            log.error("❌ Database seeding failed: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public void seedGames() {
        log.info("📦 Seeding games...");

        if (gameRepository.count() >= TARGET_GAMES) {
            log.info("⏭️  Games already seeded, loading from database...");
            allGames = gameRepository.findAll();
            return;
        }

        List<Game> games = new ArrayList<>();

        // Use externalized POPULAR_GAMES constant
        for (int i = 0; i < TARGET_GAMES && i < POPULAR_GAMES.length; i++) {
            Game game = Game.builder()
                    .name(POPULAR_GAMES[i])
                    .slug(POPULAR_GAMES[i].toLowerCase().replaceAll("[^a-z0-9]+", "-"))
                    .description(faker.lorem().paragraph(3))
                    .iconUrl("https://via.placeholder.com/64?text=" + POPULAR_GAMES[i].charAt(0))
                    .accountCount(0) // Will be updated by accounts
                    .build();
            games.add(game);
        }

        allGames = gameRepository.saveAll(games);
        log.info("✅ Created {} games", allGames.size());
    }

    @Transactional
    public void seedUsers() {
        log.info("👥 Seeding users...");

        if (userRepository.count() >= TARGET_USERS) {
            log.info("⏭️  Users already seeded, loading from database...");
            allUsers = userRepository.findAll();
            return;
        }

        List<User> users = new ArrayList<>();
        Set<String> usedEmails = new HashSet<>();

        // Create admin user
        User admin = createUser("admin@marketplace.com", "Admin", User.Role.ADMIN, usedEmails);
        if (admin != null) users.add(admin);

        // Create test users
        for (int i = 0; i < 10; i++) {
            User testUser = createUser(
                String.format("user%d@test.com", i),
                String.format("Test User %d", i),
                i % 3 == 0 ? User.Role.SELLER : User.Role.BUYER,
                usedEmails
            );
            if (testUser != null) users.add(testUser);
        }

        // Create realistic users in batches
        log.info("Creating {} realistic users (this may take a while)...", TARGET_USERS - users.size());

        for (int batch = 0; batch < (TARGET_USERS - users.size()) / BATCH_SIZE; batch++) {
            List<User> batchUsers = new ArrayList<>();

            for (int i = 0; i < BATCH_SIZE; i++) {
                User user = createRealisticUser(usedEmails);
                if (user != null) {
                    batchUsers.add(user);
                }
            }

            userRepository.saveAll(batchUsers);
            users.addAll(batchUsers);

            if (batch % 10 == 0) {
                log.info("Progress: {}/{} users created", users.size(), TARGET_USERS);
            }
        }

        // Reload all users from database to ensure they're attached to persistence context
        // This prevents TransientPropertyValueException when creating accounts
        allUsers = userRepository.findAll();
        log.info("✅ Created {} users", allUsers.size());
    }

    @Transactional
    public void seedAccounts() {
        log.info("🎮 Seeding accounts...");

        if (accountRepository.count() >= TARGET_ACCOUNTS) {
            log.info("⏭️  Accounts already seeded, loading from database...");
            // Load only IDs instead of full entities
            allAccountIds = accountRepository.findAll().stream()
                    .map(Account::getId)
                    .toArray(Long[]::new);
            return;
        }

        List<Account> accounts = new ArrayList<>();

        log.info("Creating {} accounts (this will take several minutes)...", TARGET_ACCOUNTS);

        for (int batch = 0; batch < TARGET_ACCOUNTS / BATCH_SIZE; batch++) {
            List<Account> batchAccounts = new ArrayList<>();

            for (int i = 0; i < BATCH_SIZE; i++) {
                // 70% sellers, 30% also buyers
                User seller = allUsers.get(random.nextInt(allUsers.size()));
                Game game = allGames.get(random.nextInt(allGames.size()));

                // Generate fake game credentials
                String fakeUsername = "gameuser" + (i + batch * BATCH_SIZE) + faker.random().nextInt(1000, 9999);
                String fakePassword = "pass" + faker.random().nextInt(100000, 999999);

                Account account = Account.builder()
                        .seller(seller)
                        .game(game)
                        .title(generateAccountTitle(game.getName()))
                        .description(faker.lorem().paragraph(2))
                        .level(random.nextInt(100) + 1)
                        .rank(generateRank())
                        .price(generatePrice())
                        .status(generateStatus())
                        .viewsCount(random.nextInt(1000))
                        .isFeatured(random.nextDouble() < FEATURED_RATIO)
                        .images(generateImageUrls())
                        .encryptedUsername(encryptionUtil.encrypt(fakeUsername))
                        .encryptedPassword(encryptionUtil.encrypt(fakePassword))
                        .createdAt(generatePastDate(365))
                        .build();

                batchAccounts.add(account);
            }

            accountRepository.saveAll(batchAccounts);
            accounts.addAll(batchAccounts);

            if (batch % 10 == 0) {
                log.info("Progress: {}/{} accounts created", accounts.size(), TARGET_ACCOUNTS);
            }
        }

        // Store only IDs, not full Account entities
        allAccountIds = accounts.stream()
                .map(Account::getId)
                .toArray(Long[]::new);

        log.info("✅ Created {} accounts", accounts.size());
    }

    @Transactional
    public void seedFavorites() {
        log.info("⭐ Seeding favorites...");

        if (favoriteRepository.count() >= TARGET_FAVORITES) {
            log.info("⏭️  Favorites already seeded");
            return;
        }

        List<Favorite> favorites = new ArrayList<>();
        Set<String> uniquePairs = new HashSet<>();

        for (int i = 0; i < TARGET_FAVORITES; i++) {
            User user = allUsers.get(random.nextInt(allUsers.size()));
            // Use getReferenceById() with ID instead of full entity
            Long accountId = allAccountIds[random.nextInt(allAccountIds.length)];
            Account account = accountRepository.getReferenceById(accountId);

            String pair = user.getId() + "-" + accountId;
            if (uniquePairs.contains(pair)) {
                continue; // Skip duplicates
            }
            uniquePairs.add(pair);

            Favorite favorite = Favorite.builder()
                    .user(user)
                    .account(account)
                    .createdAt(generatePastDate(180))
                    .build();

            favorites.add(favorite);

            if (favorites.size() % BATCH_SIZE == 0) {
                favoriteRepository.saveAll(favorites);
                favorites.clear();
                log.info("Progress: {}/{} favorites created", uniquePairs.size(), TARGET_FAVORITES);
            }
        }

        if (!favorites.isEmpty()) {
            favoriteRepository.saveAll(favorites);
        }

        log.info("✅ Created {} favorites", uniquePairs.size());
    }

    // Helper methods

    private User createUser(String email, String fullName, User.Role role, Set<String> usedEmails) {
        if (usedEmails.contains(email)) {
            return null;
        }
        usedEmails.add(email);

        return User.builder()
                .email(email)
                .password(passwordEncoder.encode("password123"))
                .fullName(fullName)
                .role(role)
                .status(User.UserStatus.ACTIVE)
                .balance(0.0)
                .rating(0.0)
                .totalReviews(0)
                .build();
    }

    private User createRealisticUser(Set<String> usedEmails) {
        String email;
        int attempts = 0;
        do {
            email = faker.internet().emailAddress();
            attempts++;
            if (attempts > 10) {
                email = faker.internet().emailAddress() + System.currentTimeMillis();
                break;
            }
        } while (usedEmails.contains(email));

        usedEmails.add(email);

        // Use the seeded Random instance instead of creating a new one
        // Use named constant SELLER_RATIO instead of magic number 0.4
        return User.builder()
                .email(email)
                .password(passwordEncoder.encode("password123"))
                .fullName(faker.name().fullName())
                .role(random.nextDouble() < SELLER_RATIO ? User.Role.SELLER : User.Role.BUYER)
                .status(User.UserStatus.ACTIVE)
                .balance(random.nextDouble() * 10000)
                .rating(random.nextDouble() * 5)
                .totalReviews(random.nextInt(50))
                .build();
    }

    private String generateAccountTitle(String gameName) {
        // Use externalized ACCOUNT_PREFIXES constant and consolidated random
        return ACCOUNT_PREFIXES[random.nextInt(ACCOUNT_PREFIXES.length)] + " " + gameName + " Account";
    }

    private String generateRank() {
        // Use externalized ACCOUNT_RANKS constant and consolidated random
        return ACCOUNT_RANKS[random.nextInt(ACCOUNT_RANKS.length)];
    }

    private Double generatePrice() {
        // Use consolidated random instance
        double price = 10 + (random.nextDouble() * 1990); // $10 - $2000
        return Math.round(price * 100.0) / 100.0;
    }

    private Account.AccountStatus generateStatus() {
        // Use named constants instead of magic numbers
        double roll = random.nextDouble();
        if (roll < APPROVED_RATIO) return Account.AccountStatus.APPROVED;
        if (roll < PENDING_RATIO) return Account.AccountStatus.PENDING;
        if (roll < SOLD_RATIO) return Account.AccountStatus.SOLD;
        return Account.AccountStatus.REJECTED;
    }

    private List<String> generateImageUrls() {
        // Use consolidated random instance
        int imageCount = random.nextInt(4) + 1; // 1-4 images
        List<String> urls = new ArrayList<>();
        for (int i = 0; i < imageCount; i++) {
            urls.add("https://via.placeholder.com/800x600?text=Screenshot" + (i + 1));
        }
        return urls;
    }

    private LocalDateTime generatePastDate(int maxDaysAgo) {
        // Use consolidated random instance
        int daysAgo = random.nextInt(maxDaysAgo);
        int hoursAgo = random.nextInt(24);
        return LocalDateTime.now().minusDays(daysAgo).minusHours(hoursAgo);
    }

    private void printStatistics() {
        log.info("📊 Database Statistics:");
        log.info("   Users: {}", userRepository.count());
        log.info("   Games: {}", gameRepository.count());
        log.info("   Accounts: {}", accountRepository.count());
        log.info("   Favorites: {}", favoriteRepository.count());
    }

    // Cleanup mechanism
    @Transactional
    public void cleanupDatabase() {
        log.warn("🧹 Cleaning database before seeding...");
        log.warn("⚠️  This will DELETE ALL existing data!");

        // Delete in reverse order of dependencies
        favoriteRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();
        gameRepository.deleteAll();

        log.info("✅ Database cleanup completed");
    }
}
