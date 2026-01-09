# 🔀 Git Workflow Cheat Sheet
## Quick Reference for Junior Developers

---

## 📊 Visual Git Flow

```
                    main (production)
                      │
                      │ (hotfix only)
                      │
                    develop (integration)
                      │
                      ├── feature/1.4-user-entity
                      │   │
                      │   ├── commit: Add User entity
                      │   ├── commit: Add UserRepository
                      │   └── commit: Add unit tests
                      │   
                      ├── feature/1.5-jwt-auth
                      │   │
                      │   ├── commit: Add JwtTokenProvider
                      │   └── commit: Add JWT filter
                      │   
                      └── feature/2.3-graphql-schema
                          │
                          └── commit: Add GraphQL schema
```

---

## 🎬 Scenario 1: Starting a New Story

```bash
# Step 1: Ensure you're on develop and up-to-date
git checkout develop
git pull origin develop

# Step 2: Create feature branch (naming convention: feature/<epic>.<story>-<short-description>)
git checkout -b feature/1.4-user-entity-repository

# Step 3: Verify you're on the new branch
git branch
# Should show: * feature/1.4-user-entity-repository

# Step 4: Make your changes to files
# ... edit User.java, UserRepository.java ...

# Step 5: Check what changed
git status
# Shows modified files in red

# Step 6: Stage changes
git add backend-java/src/main/java/com/gameaccount/marketplace/entity/User.java
git add backend-java/src/main/java/com/gameaccount/marketplace/repository/UserRepository.java

# Or stage all changes:
git add .

# Step 7: Commit with clear message
git commit -m "[1.4] Add User entity with JPA annotations and UserRepository"

# Step 8: Push to remote (first time)
git push -u origin feature/1.4-user-entity-repository

# Step 9: Continue working, commit frequently
# ... make more changes ...
git add .
git commit -m "[1.4] Add validation annotations to User entity"
git push
```

**Output Example:**
```
On branch feature/1.4-user-entity-repository
Your branch is up to date with 'origin/feature/1.4-user-entity-repository'.

Changes not staged for commit:
  modified:   backend-java/src/main/java/com/gameaccount/marketplace/entity/User.java

Untracked files:
  backend-java/src/main/java/com/gameaccount/marketplace/repository/UserRepository.java
```

---

## 🔄 Scenario 2: Keeping Your Branch Updated (Working for Multiple Days)

**Problem:** You're working on `feature/1.4-user-entity-repository` for 3 days. Meanwhile, others merge their PRs to `develop`. Your branch is now behind.

**Solution: Rebase**

```bash
# Step 1: Commit your current work
git add .
git commit -m "[1.4] WIP: Adding unit tests for UserRepository"

# Step 2: Switch to develop and pull latest changes
git checkout develop
git pull origin develop
# This downloads all new commits from others

# Step 3: Switch back to your feature branch
git checkout feature/1.4-user-entity-repository

# Step 4: Rebase your commits on top of develop
git rebase develop
# This replays your commits on top of the latest develop

# If no conflicts:
# ✅ Rebase successful!

# If conflicts occur:
# ❌ See "Scenario 6: Resolving Merge Conflicts"
```

**Visual Representation:**
```
BEFORE REBASE:
develop:  A---B---C---D (others' work)
                │
feature:        E---F (your work, based on old develop)

AFTER REBASE:
develop:  A---B---C---D
                      │
feature:              E'---F' (your work, rebased on latest develop)
```

**Step 5: Force push (required after rebase)**
```bash
git push --force-with-lease origin feature/1.4-user-entity-repository
```

**⚠️ Important:** Use `--force-with-lease` instead of `--force`. It's safer because it checks if someone else pushed to your branch.

---

## ✅ Scenario 3: Creating a Pull Request

**Step 1: Ensure everything is ready**
```bash
# Run tests
mvn clean test           # Backend
npm test                 # Frontend

# Run linter
npm run lint             # Frontend

# Build to ensure no compile errors
mvn clean install        # Backend
npm run build            # Frontend
```

**Step 2: Commit and push final changes**
```bash
git add .
git commit -m "[1.4] Complete User entity and repository with tests"
git push
```

**Step 3: Create PR on GitHub**
1. Go to your repository on GitHub
2. Click "Pull requests" tab
3. Click "New pull request"
4. Base: `develop` ← Compare: `feature/1.4-user-entity-repository`
5. Fill in PR template (see below)
6. Click "Create pull request"

**Step 4: Self-review your PR**
- Go through the "Files changed" tab
- Review every line you changed
- Add comments for complex logic
- Ensure no debug code (`console.log`, `System.out.println`)

**Step 5: Link Trello card**
- In Trello, attach GitHub PR link to card
- Move Trello card to "Code Review" column
- Add comment: "PR created: [link]"

---

## 📝 Pull Request Template (Copy-Paste This)

```markdown
## [1.4] User Entity & Repository

### 📋 Story Reference
- **Trello Card:** [Link to card](https://trello.com/c/xxx)
- **Epic:** 1 - User Authentication & Identity
- **Story:** 1.4 - User Entity & Repository

### 🎯 What Changed?
Created User JPA entity with:
- All required fields (id, email, password, fullName, role, status, balance, rating, totalReviews, createdAt, updatedAt)
- JPA annotations for database mapping (@Entity, @Table, @Id, @GeneratedValue, @Column, @Enumerated)
- Lombok annotations for boilerplate reduction (@Getter, @Setter, @Builder, @NoArgsConstructor, @AllArgsConstructor)
- UserRepository with custom query methods (findByEmail, existsByEmail, findByRole)

### ✅ Acceptance Criteria Checklist
- [x] User entity has all required fields
- [x] JPA annotations configured correctly
- [x] Lombok annotations applied
- [x] UserRepository extends JpaRepository<User, Long>
- [x] Custom query methods implemented
- [x] Application starts successfully
- [x] MySQL `users` table created with correct schema

### 🧪 Testing Done
- [x] `mvn clean compile` - SUCCESS
- [x] `mvn test` - All tests pass
- [x] Application starts without errors
- [x] Verified MySQL table schema in HeidiSQL
- [x] Manual test: UserRepository.save() persists user
- [x] Manual test: UserRepository.findByEmail() retrieves user

### 📸 Screenshots
![MySQL Users Table](https://i.imgur.com/xxx.png)

### 🔍 Self-Review Checklist
- [x] Code follows N-Layer architecture
- [x] No hardcoded values (used application.yml for config)
- [x] No System.out.println (used SLF4J logger)
- [x] Followed Java naming conventions (camelCase for fields, PascalCase for classes)
- [x] Added JavaDoc comments for public methods
- [x] No unused imports
- [x] Password field size (255) supports BCrypt hashes

### 🤔 Questions for Reviewer
- Should `rating` field be `Float` or `Double`? Currently using `Double`.
- Should we add `@Index` annotation on `email` column for faster lookups?

### 📦 Related PRs
- Depends on: #42 (Story 1.2 - Spring Boot Skeleton) ✅ Merged
- Blocks: #45 (Story 1.5 - JWT Implementation)

### 📊 Metadata
- **Story Points:** 3
- **Estimated Time:** 1 day
- **Actual Time:** 6 hours
```

---

## 🔍 Scenario 4: Reviewing Someone Else's PR

**As a Junior, you should:**
1. **Understand the story first**
   - Read the story in epics.md
   - Understand the acceptance criteria

2. **Check out their branch locally**
```bash
# Fetch all remote branches
git fetch origin

# Check out their branch
git checkout feature/1.5-jwt-authentication

# Run tests
mvn test

# Run the application
mvn spring-boot:run

# Test the feature manually
```

3. **Review the code on GitHub**
   - Go to "Files changed" tab
   - Click on a line to add comment
   - Ask questions if you don't understand
   - Suggest improvements

4. **Leave feedback**
```markdown
# Example comment on line 45 of JwtTokenProvider.java:
"Great job on implementing token validation! 

One suggestion: Should we also check if the token is expired here? 
I see we validate the signature, but not the expiration time.

See: https://github.com/jwtk/jjwt#jws-read"
```

5. **Approve if everything looks good**
   - Click "Review changes"
   - Select "Approve"
   - Add summary comment: "LGTM! Great work on handling edge cases."

---

## 🚧 Scenario 5: Updating Your PR Based on Review Feedback

**Example: Reviewer said "Move validation logic from Controller to Service"**

**Step 1: Make the requested changes**
```bash
# Ensure you're on your feature branch
git checkout feature/1.4-user-entity-repository

# Make changes to files
# ... move validation from AuthController to AuthService ...

# Test the changes
mvn test

# Stage and commit
git add .
git commit -m "[1.4] Move email validation from controller to service layer"

# Push to update the PR
git push
```

**Step 2: Respond to review comment**
- On GitHub, go to the PR
- Find the reviewer's comment
- Click "Reply"
```markdown
"✅ Fixed! Moved validation to AuthService.validateEmail() method.

See commit: abc1234"
```

**Step 3: Re-request review**
- Click "Re-request review" button next to reviewer's name

---

## ⚔️ Scenario 6: Resolving Merge Conflicts

**Conflict Example:**
```bash
git rebase develop
# Output:
CONFLICT (content): Merge conflict in backend-java/src/main/java/com/gameaccount/marketplace/entity/User.java
```

**Step-by-Step Resolution:**

**Step 1: Open the conflicted file**
```java
// User.java (conflicted)
public class User {
    private Long id;
    private String email;
    
<<<<<<< HEAD (your changes)
    private String fullName;
    private String phoneNumber;
=======
    private String name;
    private String phone;
>>>>>>> develop (their changes)
    
    private String password;
}
```

**Step 2: Decide which version to keep**

**Option A: Keep your version**
```java
public class User {
    private Long id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String password;
}
```

**Option B: Keep their version**
```java
public class User {
    private Long id;
    private String email;
    private String name;
    private String phone;
    private String password;
}
```

**Option C: Merge both (if compatible)**
```java
public class User {
    private Long id;
    private String email;
    private String fullName;  // Keeping this from your version
    private String phone;     // Keeping this from their version
    private String password;
}
```

**Step 3: Remove conflict markers**
Remove these lines:
```
<<<<<<< HEAD
=======
>>>>>>> develop
```

**Step 4: Test the merged code**
```bash
mvn test
# Ensure everything still works
```

**Step 5: Mark as resolved and continue rebase**
```bash
git add backend-java/src/main/java/com/gameaccount/marketplace/entity/User.java
git rebase --continue
```

**Step 6: Force push**
```bash
git push --force-with-lease origin feature/1.4-user-entity-repository
```

---

## 🆘 Scenario 7: Emergency - "I Broke Everything!"

### Problem 1: "I committed to develop by mistake!"

```bash
# DON'T PANIC! This can be fixed.

# Step 1: Check current branch
git branch
# Shows: * develop (OH NO!)

# Step 2: Undo the commit (keep changes)
git reset --soft HEAD~1

# Step 3: Create the correct branch
git checkout -b feature/1.4-user-entity-repository

# Step 4: Commit on the correct branch
git add .
git commit -m "[1.4] Add User entity (moved from develop)"

# Step 5: Push to new branch
git push -u origin feature/1.4-user-entity-repository

# Step 6: Notify Lead immediately
# Post in #tech-discuss:
# "Accidentally committed to develop (not pushed). Fixed by moving to feature branch."
```

---

### Problem 2: "I pushed bad code to my branch!"

```bash
# Step 1: Identify the bad commit
git log --oneline
# Shows:
# abc1234 [1.4] Add broken code  ← Bad commit
# def5678 [1.4] Add User entity  ← Good commit

# Step 2: Revert to before the bad commit
git reset --hard def5678

# Step 3: Force push to overwrite remote
git push --force origin feature/1.4-user-entity-repository

# ⚠️ Only do this on YOUR feature branch, NEVER on develop or main!
```

---

### Problem 3: "I deleted a file by mistake!"

```bash
# If not committed yet:
git checkout -- path/to/deleted/file.java

# If already committed:
git log --oneline
# Find the commit before deletion
git checkout abc1234 -- path/to/deleted/file.java
git commit -m "[1.4] Restore accidentally deleted file"
```

---

### Problem 4: "My branch is completely messed up!"

```bash
# Nuclear option: Start over from develop

# Step 1: Save your work first!
cp -r backend-java /tmp/backup-backend

# Step 2: Delete your branch locally
git checkout develop
git branch -D feature/1.4-user-entity-repository

# Step 3: Create fresh branch
git checkout -b feature/1.4-user-entity-repository

# Step 4: Copy back your good changes
cp /tmp/backup-backend/specific-files backend-java/

# Step 5: Commit fresh
git add .
git commit -m "[1.4] Add User entity (fresh start)"
git push -u origin feature/1.4-user-entity-repository --force

# ⚠️ Ask Lead before doing this!
```

---

## 🎓 Git Command Reference

### Daily Commands (Use These Often)

| Command | Purpose | Example |
|---------|---------|---------|
| `git status` | Check what changed | `git status` |
| `git diff` | See exact changes | `git diff User.java` |
| `git log` | View commit history | `git log --oneline -10` |
| `git branch` | List branches | `git branch -a` |
| `git checkout` | Switch branches | `git checkout develop` |
| `git pull` | Get latest changes | `git pull origin develop` |
| `git add` | Stage changes | `git add .` |
| `git commit` | Commit changes | `git commit -m "message"` |
| `git push` | Upload to remote | `git push` |

---

### Advanced Commands (Use Carefully)

| Command | Purpose | Example | ⚠️ Warning |
|---------|---------|---------|-----------|
| `git rebase` | Replay commits | `git rebase develop` | Changes history |
| `git reset` | Undo commits | `git reset --soft HEAD~1` | Can lose work |
| `git push --force` | Overwrite remote | `git push --force-with-lease` | Dangerous on shared branches |
| `git cherry-pick` | Copy specific commit | `git cherry-pick abc1234` | Can cause conflicts |
| `git stash` | Temporarily save work | `git stash save "WIP"` | Easy to forget |
| `git reflog` | View all actions | `git reflog` | Advanced recovery |

---

### Emergency Commands (Ask Lead First!)

```bash
# View what Git is about to do (dry run)
git merge --no-commit --no-ff develop

# Abort a merge
git merge --abort

# Abort a rebase
git rebase --abort

# Undo last commit (keep files)
git reset --soft HEAD~1

# Undo last commit (delete files)
git reset --hard HEAD~1

# View deleted branches
git reflog

# Recover deleted branch
git checkout -b feature/1.4-user-entity-repository abc1234
```

---

## 📋 Git Workflow Checklist

**Starting a Story:**
- [ ] `git checkout develop`
- [ ] `git pull origin develop`
- [ ] `git checkout -b feature/X.Y-description`
- [ ] Move Trello card to "In Progress"

**During Development:**
- [ ] Commit frequently (at least 2-3 per day)
- [ ] Use clear commit messages `[X.Y] What changed`
- [ ] Push at end of day: `git push`
- [ ] Update Trello card with progress

**Before Creating PR:**
- [ ] `git checkout develop && git pull`
- [ ] `git checkout feature/X.Y-description`
- [ ] `git rebase develop` (update branch)
- [ ] `mvn clean test` or `npm test` (ensure tests pass)
- [ ] `npm run lint` (check code style)
- [ ] `git push --force-with-lease`
- [ ] Self-review changes on GitHub
- [ ] Fill PR template completely

**After PR Approved:**
- [ ] Lead merges PR (not junior)
- [ ] `git checkout develop`
- [ ] `git pull origin develop`
- [ ] `git branch -d feature/X.Y-description` (delete local branch)
- [ ] Move Trello card to "Done"

---

## 🆘 When to Ask for Help

**Ask Lead immediately if:**
- ❌ You pushed to `main` or `develop` by mistake
- ❌ You're in a merge conflict you don't understand
- ❌ You're stuck in rebase hell (multiple conflicts)
- ❌ You accidentally deleted important files
- ❌ You need to undo a merged PR

**Try to solve yourself first (30 min max):**
- ⚠️ Merge conflicts in your own code
- ⚠️ Need to update branch with develop
- ⚠️ Forgot to push before leaving for the day
- ⚠️ Made wrong commit message

---

## 🎯 Best Practices

✅ **DO:**
- Commit frequently (every hour)
- Write clear commit messages
- Pull develop daily
- Create PR when story is 100% done
- Test before pushing
- Ask questions early

❌ **DON'T:**
- Commit to develop/main directly
- Force push to shared branches
- Work on multiple stories in one branch
- Leave uncommitted work overnight
- Push broken code
- Ignore CI failures

---

## 📚 Learning Resources

**Interactive Git Tutorial:**
- https://learngitbranching.js.org/

**Git Cheat Sheet:**
- https://education.github.com/git-cheat-sheet-education.pdf

**Understanding Git Rebase:**
- https://www.atlassian.com/git/tutorials/rewriting-history/git-rebase

**Resolving Merge Conflicts:**
- https://www.atlassian.com/git/tutorials/using-branches/merge-conflicts

---

**Document Version:** 1.0  
**Last Updated:** 2026-01-09  
**Quick Help:** Ask in #tech-discuss or ping @Lead

