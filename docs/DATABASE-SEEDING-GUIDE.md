# 🌱 Database Seeding Guide
## Large-Scale Test Data Generation for Performance Testing

---

## 📋 Overview

This guide explains how to generate **100,000+ realistic test records** to demonstrate the effectiveness of optimizations like:
- ✅ Redis caching
- ✅ DataLoader (N+1 prevention)
- ✅ Database indexing
- ✅ GraphQL query optimization

---

## 🚀 Quick Start

### **Step 1: Add Dependency**

Add Datafaker to your `pom.xml`:

```xml
<dependency>
    <groupId>net.datafaker</groupId>
    <artifactId>datafaker</artifactId>
    <version>2.1.0</version>
</dependency>
```

### **Step 2: Run Seeder**

```bash
# Option A: Maven (Normal seeding)
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=seed

# Option B: Maven (Clean + Seed - removes existing data first)
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=seed --seed.clean=true

# Option C: Gradle
./gradlew bootRun --args='--spring.profiles.active=seed'

# Option D: JAR
java -jar target/marketplace-0.0.1-SNAPSHOT.jar --spring.profiles.active=seed
```

### **Step 3: Wait & Monitor**

```
🌱 Starting Database Seeding...
Target: 10000 users, 50 games, 100000 accounts
📦 Seeding games...
✅ Created 50 games
👥 Seeding users...
Creating 9989 realistic users (this may take a while)...
Progress: 1000/10000 users created
Progress: 2000/10000 users created
...
✅ Created 10000 users
🎮 Seeding accounts...
Creating 100000 accounts (this will take several minutes)...
Progress: 10000/100000 accounts created
...
✅ Database seeding completed in 187 seconds

📊 Database Statistics:
   Users: 10000
   Games: 50
   Accounts: 100000
   Favorites: 50000
   Transactions: 20000
   Reviews: 15000
```

**Expected Time:**
- Small (10K accounts): ~30 seconds
- Medium (50K accounts): ~90 seconds
- Large (100K accounts): ~3-5 minutes
- XL (1M accounts): ~30-40 minutes

---

## 🛠️ New Features & Architectural Improvements

### **Cleanup Mechanism (v1.1)**

The seeder now supports cleaning the database before seeding:

```bash
# Enable cleanup mode
--seed.clean=true
```

This will:
- ✅ Delete all existing data in the correct order (respecting foreign key constraints)
- ✅ Start with a fresh dataset
- ✅ Useful when you want to change the random seed or dataset size

**Warning:** This will DELETE ALL data in the database!

---

### **Memory Optimization (v1.1)**

The seeder now uses **Long[] IDs** instead of full entities:

```java
// Before: Loaded 100,000 Account entities into memory (~200MB+)
private List<Account> allAccounts;

// After: Loads only IDs (~400KB)
private Long[] allAccountIds;
```

**Benefits:**
- ✅ **99.8% memory reduction** for account storage
- ✅ Can handle larger datasets (1M+ accounts)
- ✅ Uses JPA proxies for lazy loading when needed

---

### **Reproducibility Fixes (v1.1)**

All random generation now uses a **single seeded Random instance**:

```java
// Before: Random was recreated without seed (BROKEN)
Random random = new Random();  // ❌ Different every run

// After: Single seeded instance
private final Random random = new Random(SEED);  // ✅ Reproducible
```

**Benefits:**
- ✅ Same data every run with the same seed
- ✅ Reliable benchmarking
- ✅ Debuggable issues

---

### **Named Constants (v1.1)**

Magic numbers are now named constants:

```java
// Before: What does 0.70 mean?
if (roll < 0.70) return "APPROVED";

// After: Clear intent
private static final double APPROVED_RATIO = 0.70;
if (roll < APPROVED_RATIO) return "APPROVED";
```

**Available Constants:**
- `APPROVED_RATIO = 0.70` (70% of accounts approved)
- `PENDING_RATIO = 0.85` (15% pending)
- `SOLD_RATIO = 0.95` (10% sold)
- `FEATURED_RATIO = 0.05` (5% featured)
- `COMPLETED_TRANSACTION_RATIO = 0.80` (80% completed)
- `SELLER_RATIO = 0.40` (40% of users are sellers)

---

### **Externalized Arrays (v1.1)**

Game names, ranks, and prefixes are now constants:

```java
private static final String[] POPULAR_GAMES = { ... };
private static final String[] ACCOUNT_RANKS = { ... };
private static final String[] ACCOUNT_PREFIXES = { ... };
```

**Benefits:**
- ✅ Easy to customize
- ✅ Centralized maintenance
- ✅ Can be moved to config files if needed

---

## 📊 Generated Dataset Details

### **Data Distribution**

```
users (10,000 records)
├── 1 admin (admin@marketplace.com)
├── 10 test users (user0@test.com ... user9@test.com)
└── 9,989 realistic users (faker-generated names/emails)

games (50 records)
└── Popular games: League of Legends, Valorant, CS2, etc.

accounts (100,000 records)
├── 70,000 APPROVED (70%)
├── 15,000 PENDING (15%)
├── 10,000 SOLD (10%)
└── 5,000 REJECTED (5%)

favorites (50,000 records)
└── Random user-account pairs (no duplicates)

transactions (20,000 records)
├── 16,000 COMPLETED (80%)
└── 4,000 PENDING (20%)

reviews (15,000 records)
└── 75% of completed transactions have reviews
```

### **Realistic Data Characteristics**

- **Emails:** Unique, realistic format (faker-generated)
- **Names:** Realistic full names from Datafaker
- **Passwords:** All encrypted with BCrypt (password: "password123")
- **Prices:** $10 - $2,000 (realistic marketplace range)
- **Levels:** 1-100
- **Ranks:** Bronze, Silver, Gold, Platinum, Diamond, Master, Grandmaster, Challenger
- **Dates:** Spread over last 365 days (accounts) and 180 days (favorites/transactions)
- **Images:** 1-4 placeholder images per account

---

## 🎯 Customization

### **Adjust Dataset Size**

Edit `DatabaseSeeder.java`:

```java
// For smaller testing (faster)
private static final int TARGET_USERS = 1_000;
private static final int TARGET_ACCOUNTS = 10_000;

// For medium scale
private static final int TARGET_USERS = 5_000;
private static final int TARGET_ACCOUNTS = 50_000;

// For large scale (production-like)
private static final int TARGET_USERS = 50_000;
private static final int TARGET_ACCOUNTS = 500_000;

// For stress testing
private static final int TARGET_USERS = 100_000;
private static final int TARGET_ACCOUNTS = 1_000_000;
```

### **Adjust Batch Size**

```java
// Smaller batch = Less memory, slower
private static final int BATCH_SIZE = 500;

// Larger batch = More memory, faster
private static final int BATCH_SIZE = 2000;

// Recommended: 1000 for balanced performance
private static final int BATCH_SIZE = 1000;
```

### **Change Random Seed**

```java
// For reproducible data (same seed = same data)
private static final long SEED = 12345L;

// For different data each run
private static final long SEED = System.currentTimeMillis();
```

---

## 📈 Simple Performance Benchmarking

### **Quick Start (3 Steps)**

#### **Step 1: Seed the Database**

```bash
# Generate test data (~3-5 minutes)
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=seed
```

#### **Step 2: Run the Application**

```bash
# Start with benchmark profile enabled
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=benchmark
```

#### **Step 3: Run Benchmarks**

Open your browser or use curl:

```bash
# Run all benchmarks at once
curl http://localhost:8080/api/benchmark/run-all

# Or run individual tests
curl http://localhost:8080/api/benchmark/test-1-simple-query
curl http://localhost:8080/api/benchmark/test-2-filtered-query
curl http://localhost:8080/api/benchmark/test-3-nested-query
curl http://localhost:8080/api/benchmark/test-4-optimized-nested-query
curl http://localhost:8080/api/benchmark/test-5-cache-effectiveness
```

---

### **What the Tests Measure**

| Test | What It Tests | What to Look For |
|------|---------------|------------------|
| **Test 1: Simple Query** | Baseline performance | Should be < 100ms with indexes |
| **Test 2: Filtered Query** | Index effectiveness | Should be similar to Test 1 if indexed |
| **Test 3: Nested Query (N+1)** | Lazy loading issues | Slow! Should be > 1000ms without optimization |
| **Test 4: Optimized Nested** | JOIN FETCH effectiveness | Should be fast (< 200ms) |
| **Test 5: Cache Effectiveness** | Redis cache hit | Second call should be much faster |

---

### **Expected Results**

#### **Without Optimizations:**
```
Test 1 (Simple):           450ms
Test 2 (Filtered):         620ms
Test 3 (Nested N+1):       2,840ms  ← SLOW!
Test 4 (Optimized):        180ms    ← Better!
Test 5 (Cache):            450ms / 12ms (huge improvement!)
```

#### **With All Optimizations:**
```
Test 1 (Simple):           85ms     ← 5x faster
Test 2 (Filtered):         120ms    ← 5x faster
Test 3 (Nested N+1):       Still slow (test 4 is better)
Test 4 (Optimized):        85ms     ← Best!
Test 5 (Cache):            15ms     ← 37x faster!
```

---

### **How to Compare Before/After**

1. **Run benchmarks BEFORE optimizations:**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=benchmark
   curl http://localhost:8080/api/benchmark/run-all > before.txt
   ```

2. **Enable optimizations** (indexes, Redis, DataLoader)

3. **Run benchmarks AFTER optimizations:**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=benchmark
   curl http://localhost:8080/api/benchmark/run-all > after.txt
   ```

4. **Compare:**
   ```bash
   diff before.txt after.txt
   ```

---

# 🌱 Database Seeding Guide
## Large-Scale Test Data Generation for Performance Testing

---

## 📋 Overview

This guide explains how to generate **100,000+ realistic test records** to demonstrate the effectiveness of optimizations like:
- ✅ Redis caching
- ✅ Database indexing
- ✅ GraphQL query optimization

**Supported Entities:** User, Game, Account, Favorite

---

## 🚀 Quick Start

### **Step 1: Dependency Already Added**

The Datafaker dependency is already included in `pom.xml`:
```xml
<dependency>
    <groupId>net.datafaker</groupId>
    <artifactId>datafaker</artifactId>
    <version>2.1.0</version>
</dependency>
```

### **Step 2: Run Seeder**

```bash
# Option A: Maven (Normal seeding)
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=seed

# Option B: Maven (Clean + Seed - removes existing data first)
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=seed --seed.clean=true

# Option C: JAR
java -jar target/marketplace-0.0.1-SNAPSHOT.jar --spring.profiles.active=seed
```

### **Step 3: Wait & Monitor**

```
🌱 Starting Database Seeding...
Target: 10000 users, 50 games, 100000 accounts
📦 Seeding games...
✅ Created 50 games
👥 Seeding users...
Creating 9989 realistic users (this may take a while)...
Progress: 1000/10000 users created
Progress: 2000/10000 users created
...
✅ Created 10000 users
🎮 Seeding accounts...
Creating 100000 accounts (this will take several minutes)...
Progress: 10000/100000 accounts created
...
✅ Database seeding completed in 187 seconds

📊 Database Statistics:
   Users: 10000
   Games: 50
   Accounts: 100000
   Favorites: 50000
```

**Expected Time:**
- Small (10K accounts): ~30 seconds
- Medium (50K accounts): ~90 seconds
- Large (100K accounts): ~3-5 minutes
- XL (1M accounts): ~30-40 minutes

---

## 🛠️ Architectural Features

### **Cleanup Mechanism**

The seeder supports cleaning the database before seeding:

```bash
# Enable cleanup mode
--seed.clean=true
```

This will:
- ✅ Delete all existing data in the correct order (respecting foreign key constraints)
- ✅ Start with a fresh dataset
- ✅ Useful when you want to change the random seed or dataset size

**Warning:** This will DELETE ALL data in the database!

---

### **Memory Optimization**

The seeder uses **Long[] IDs** instead of full entities:

```java
// Loaded 100,000 Account entities into memory (~200MB+)
private List<Account> allAccounts;

// Loads only IDs (~400KB)
private Long[] allAccountIds;
```

**Benefits:**
- ✅ **99.8% memory reduction** for account storage
- ✅ Can handle larger datasets (1M+ accounts)
- ✅ Uses JPA proxies for lazy loading when needed

---

### **Reproducibility**

All random generation uses a **single seeded Random instance**:

```java
// Single seeded instance
private final Random random = new Random(SEED);  // ✅ Reproducible
```

**Benefits:**
- ✅ Same data every run with the same seed
- ✅ Reliable benchmarking
- ✅ Debuggable issues

---

### **Named Constants**

Magic numbers are now named constants:

```java
// Clear intent
private static final double APPROVED_RATIO = 0.70;
if (roll < APPROVED_RATIO) return Account.AccountStatus.APPROVED;
```

**Available Constants:**
- `APPROVED_RATIO = 0.70` (70% of accounts approved)
- `PENDING_RATIO = 0.85` (15% pending)
- `SOLD_RATIO = 0.95` (10% sold)
- `FEATURED_RATIO = 0.05` (5% featured)
- `SELLER_RATIO = 0.40` (40% of users are sellers)

---

### **Externalized Arrays**

Game names, ranks, and prefixes are now constants:

```java
private static final String[] POPULAR_GAMES = { ... };
private static final String[] ACCOUNT_RANKS = { ... };
private static final String[] ACCOUNT_PREFIXES = { ... };
```

**Benefits:**
- ✅ Easy to customize
- ✅ Centralized maintenance
- ✅ Can be moved to config files if needed

---

## 📊 Generated Dataset Details

### **Data Distribution**

```
users (10,000 records)
├── 1 admin (admin@marketplace.com / password123)
├── 10 test users (user0@test.com ... user9@test.com)
└── 9,989 realistic users (faker-generated names/emails)

games (50 records)
└── Popular games: League of Legends, Valorant, CS2, Dota 2, Fortnite, etc.

accounts (100,000 records)
├── 70,000 APPROVED (70%)
├── 15,000 PENDING (15%)
├── 10,000 SOLD (10%)
└── 5,000 REJECTED (5%)

favorites (50,000 records)
└── Random user-account pairs (no duplicates)
```

### **Realistic Data Characteristics**

- **Emails:** Unique, realistic format (faker-generated)
- **Names:** Realistic full names from Datafaker
- **Passwords:** All encrypted with BCrypt (default: "password123")
- **Prices:** $10 - $2,000 (realistic marketplace range)
- **Levels:** 1-100
- **Ranks:** Bronze, Silver, Gold, Platinum, Diamond, Master, Grandmaster, Challenger
- **Dates:** Spread over last 365 days (accounts) and 180 days (favorites)
- **Images:** 1-4 placeholder images per account

---

## 🎯 Customization

### **Adjust Dataset Size**

Edit `DatabaseSeeder.java`:

```java
// For smaller testing (faster)
private static final int TARGET_USERS = 1_000;
private static final int TARGET_ACCOUNTS = 10_000;

// For medium scale
private static final int TARGET_USERS = 5_000;
private static final int TARGET_ACCOUNTS = 50_000;

// For large scale (production-like)
private static final int TARGET_USERS = 50_000;
private static final int TARGET_ACCOUNTS = 500_000;

// For stress testing
private static final int TARGET_USERS = 100_000;
private static final int TARGET_ACCOUNTS = 1_000_000;
```

### **Adjust Batch Size**

```java
// Smaller batch = Less memory, slower
private static final int BATCH_SIZE = 500;

// Larger batch = More memory, faster
private static final int BATCH_SIZE = 2000;

// Recommended: 1000 for balanced performance
private static final int BATCH_SIZE = 1000;
```

### **Change Random Seed**

```java
// For reproducible data (same seed = same data)
private static final long SEED = 12345L;

// For different data each run
private static final long SEED = System.currentTimeMillis();
```

---

## 🔍 Verification & Debugging

### **1. Check Data Was Created**

```sql
-- MySQL
SELECT
    'users' as table_name, COUNT(*) as count FROM users
UNION ALL
SELECT 'games', COUNT(*) FROM games
UNION ALL
SELECT 'accounts', COUNT(*) FROM accounts
UNION ALL
SELECT 'favorites', COUNT(*) FROM favorites;
```

### **2. Check Data Quality**

```sql
-- Check account status distribution
SELECT status, COUNT(*) as count,
       ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM accounts), 2) as percentage
FROM accounts
GROUP BY status;

-- Check price distribution
SELECT
    CASE
        WHEN price < 50 THEN '< $50'
        WHEN price < 100 THEN '$50-100'
        WHEN price < 500 THEN '$100-500'
        WHEN price < 1000 THEN '$500-1000'
        ELSE '> $1000'
    END as price_range,
    COUNT(*) as count
FROM accounts
GROUP BY price_range
ORDER BY MIN(price);

-- Check date distribution (last 30 days)
SELECT DATE(created_at) as date, COUNT(*) as accounts_created
FROM accounts
WHERE created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY DATE(created_at)
ORDER BY date DESC;
```

### **3. Monitor Performance During Seeding**

```bash
# Watch MySQL process list
mysql -u appuser -p -e "SHOW PROCESSLIST;" --watch 1

# Check database size
SELECT
    table_name,
    ROUND(((data_length + index_length) / 1024 / 1024), 2) AS size_mb
FROM information_schema.TABLES
WHERE table_schema = 'gameaccount_marketplace'
ORDER BY size_mb DESC;
```

---

## 📝 Best Practices

### **1. Use Separate Database for Testing**

```yaml
# application-seed.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/marketplace_test
  jpa:
    hibernate:
      ddl-auto: create-drop  # Reset DB each time
```

### **2. Monitor Memory Usage**

The seeder is optimized with:
- Batch inserts (1000 records at a time)
- Long[] IDs instead of full entities
- Single Random instance

### **3. Batch Insert for Performance**

```java
// Already implemented in DatabaseSeeder
// Save in batches of 1000 instead of one-by-one
```

### **4. Use Transactions**

```java
// Already annotated with @Transactional
// Rollback if anything fails
```

---

## 🎯 Conclusion

With this seeder, you can:
- ✅ Generate **100,000+ realistic records** in minutes
- ✅ Prove optimizations work with **real metrics**
- ✅ Benchmark performance: **before vs after**
- ✅ Reproduce same dataset (fixed seed)
- ✅ Customize for different test scenarios

**No manual SQL writing. No external tools. Pure Java + Spring Boot!** 🚀

---

**Implementation location:** `src/main/java/com/gameaccount/marketplace/util/DatabaseSeeder.java`

**Questions?** Check the inline JavaDoc comments in `DatabaseSeeder.java`! 🏗️

