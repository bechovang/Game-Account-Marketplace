# 🚨 URGENT Security Action Checklist

## ⏰ Do These NOW (Before Next Commit)

### 🔴 CRITICAL (Do in next 15 minutes)

- [ ] **1. Rotate PayOS API Keys**
  - Go to: https://my.payos.vn
  - Generate new Client ID, API Key, Checksum Key
  - Update your `.env` file (NOT `key-payos.txt`)
  - Old keys are compromised!

### 🟠 HIGH (Do today)

- [ ] **2. Remove PayOS keys from Git**
  ```bash
  git rm --cached -r docs/payos_docs/
  git commit -m "Remove PayOS credentials from repository"
  git push
  ```

- [ ] **3. Remove log file from Git**
  ```bash
  git rm --cached backend.log
  git commit -m "Remove log files from repository"
  git push
  ```

- [ ] **4. Create `.env` file for secrets**
  ```bash
  # In project root
  touch .env

  # Add to .env (use your NEW PayOS keys):
  echo 'PAYOS_CLIENT_ID=your-new-client-id' >> .env
  echo 'PAYOS_API_KEY=your-new-api-key' >> .env
  echo 'PAYOS_CHECKSUM_KEY=your-new-checksum-key' >> .env
  echo 'JWT_SECRET='$(openssl rand -base64 64) >> .env
  echo 'ENCRYPTION_SECRET_KEY='$(openssl rand -hex 32) >> .env
  echo 'DB_PASSWORD=your-db-password' >> .env
  ```

### 🟡 MEDIUM (Do this week)

- [ ] **5. Check if secrets were pushed to GitHub/GitLab**
  - Review commit history
  - If yes, rewrite history (see docs/SECURITY-GITIGNORE-AUDIT.md)

- [ ] **6. Update production secrets**
  - Generate strong JWT secret: `openssl rand -base64 64`
  - Generate strong encryption key: `openssl rand -hex 32`
  - Store in server environment variables (NOT in files)

- [ ] **7. Review all tracked files**
  ```bash
  git ls-files | grep -E '\.(env|log|key|pem)$'
  # Should return NOTHING
  ```

### 🟢 LOW (Do this month)

- [ ] **8. Setup pre-commit hooks** (prevents future leaks)
- [ ] **9. Team security training** (share SECURITY-GITIGNORE-AUDIT.md)
- [ ] **10. Document secret rotation schedule**

---

## ✅ Verification

After completing above:

```bash
# 1. Check .gitignore is working
git status
# Should NOT see: .env, *.log, docs/payos_docs/

# 2. Verify no secrets in tracked files
git grep -i 'payos.*key' -- '*.yml' '*.java'
# Should return NOTHING or only ${PAYOS_*} placeholders

# 3. Test with new PayOS keys
# Update .env with new keys, restart backend, test payment
```

---

## 📞 If You Already Pushed Secrets to Remote

**STOP!** Don't panic, but act quickly:

1. Rotate ALL compromised credentials immediately
2. Contact your team lead/architect
3. Follow "Git History Cleanup" in docs/SECURITY-GITIGNORE-AUDIT.md
4. Monitor for unauthorized access

---

**Check off each item as you complete it. Security is not optional! 🔒**

