# 🎓 Hướng Dẫn Làm Việc Cho Junior Developer
## Game Account Marketplace - Dành Cho Bạn Mới Vào Nghề

---

## 👋 Chào Mừng Bạn!

Chào bạn! Nếu bạn đang đọc tài liệu này, có nghĩa là bạn đã là một phần của team chúng mình rồi. Đừng lo lắng nếu bạn chưa biết nhiều - tất cả mọi người đều đã từng là junior như bạn. Tài liệu này sẽ hướng dẫn bạn từng bước một, từ ngày đầu tiên cho đến khi bạn tự tin code một mình.

**Mục tiêu của tài liệu này:**
- ✅ Giúp bạn hiểu quy trình làm việc của team
- ✅ Hướng dẫn cách sử dụng Git (không sợ conflict nữa!)
- ✅ Hướng dẫn cách sử dụng Trello
- ✅ Cách làm việc nhóm hiệu quả
- ✅ Tips & tricks từ kinh nghiệm thực tế

**Đọc tài liệu này mất bao lâu?**
- Đọc lướt: 30 phút
- Đọc kỹ: 2 giờ
- Thành thạo: 1-2 tuần làm việc thực tế

---

## 📚 Mục Lục

1. [Ngày Đầu Tiên - Setup Môi Trường](#1-ngày-đầu-tiên---setup-môi-trường)
2. [Hiểu Dự Án Chúng Ta Đang Làm](#2-hiểu-dự-án-chúng-ta-đang-làm)
3. [Cách Sử Dụng Trello](#3-cách-sử-dụng-trello)
4. [Cách Sử Dụng Git (Phần Quan Trọng Nhất!)](#4-cách-sử-dụng-git)
5. [Quy Trình Làm Một Story](#5-quy-trình-làm-một-story)
6. [Cách Làm Việc Nhóm](#6-cách-làm-việc-nhóm)
7. [Khi Nào Thì Hỏi? Hỏi Ai?](#7-khi-nào-thì-hỏi-hỏi-ai)
8. [Tình Huống Thường Gặp & Cách Xử Lý](#8-tình-huống-thường-gặp--cách-xử-lý)
9. [Tips Từ Anh/Chị Senior](#9-tips-từ-anhchị-senior)

---

## 1. Ngày Đầu Tiên - Setup Môi Trường

### 📋 Checklist Ngày Đầu Tiên

#### **Sáng (9:00 - 12:00): Làm Quen & Setup**

**Bước 1: Tạo tài khoản các tool (30 phút)**
- [ ] Tạo tài khoản GitHub (nếu chưa có)
- [ ] Tạo tài khoản Trello (nếu chưa có)
- [ ] Join workspace Discord/Slack của team
- [ ] Add email vào danh sách team

**Bước 2: Cài đặt phần mềm cần thiết (1 giờ)**

```bash
# Kiểm tra phiên bản đã cài
java -version        # Cần Java 21
node -v              # Cần Node.js 18+
git --version        # Cần Git 2.30+
docker --version     # Cần Docker 20+

# Nếu chưa có, tải về:
# - Java: https://adoptium.net/
# - Node.js: https://nodejs.org/
# - Git: https://git-scm.com/
# - Docker: https://www.docker.com/
# - IntelliJ IDEA (hoặc VS Code)
```

**Bước 3: Clone dự án (15 phút)**

```bash
# Tạo thư mục làm việc
mkdir C:\Projects
cd C:\Projects

# Clone repository
git clone https://github.com/your-team/game-account-marketplace.git
cd game-account-marketplace

# Xem cấu trúc thư mục
dir  # (Windows) hoặc ls (Mac/Linux)
```

**Bước 4: Chạy Docker (MySQL + Redis) (15 phút)**

```bash
# Start Docker Desktop trước

# Chạy MySQL và Redis
docker-compose up -d

# Kiểm tra containers đang chạy
docker ps

# Kết quả mong đợi: 2 containers (mysql, redis) đang chạy
```

#### **Chiều (1:00 - 5:00): Chạy Dự Án**

**Bước 5: Chạy Backend (30 phút)**

```bash
# Mở terminal, đi vào thư mục backend
cd backend-java

# Build project lần đầu (sẽ download dependencies, hơi lâu)
mvn clean install

# Chạy application
mvn spring-boot:run

# Nếu thấy dòng này = thành công!
# "Started MarketplaceApplication in X seconds"
```

**Mở trình duyệt, test:**
- Backend chạy tại: http://localhost:8080
- GraphQL Playground: http://localhost:8080/graphiql
- API test: http://localhost:8080/api/auth/test

**Bước 6: Chạy Frontend (30 phút)**

```bash
# Mở terminal MỚI (giữ backend chạy)
cd frontend-react

# Cài dependencies (lần đầu)
npm install

# Chạy dev server
npm run dev

# Nếu thấy dòng này = thành công!
# "Local: http://localhost:3000"
```

**Mở trình duyệt:**
- Frontend chạy tại: http://localhost:3000

**Bước 7: Test đăng nhập (15 phút)**

Mở http://localhost:3000:
1. Click "Register" (đăng ký tài khoản mới)
2. Nhập email, password, tên
3. Click "Login"
4. Nếu vào được trang chủ = thành công! 🎉

**Bước 8: Đọc tài liệu (còn lại thời gian)**

Đọc 3 file này (đọc nhanh thôi, không cần nhớ hết):
- [ ] `README.md` - Giới thiệu dự án
- [ ] `project_docs/ARCHITECTURE.md` - Kiến trúc (đọc phần 1-3)
- [ ] `_bmad-output/planning-artifacts/epics.md` - Epic 1 (phần bạn sẽ làm)

---

### 🆘 Nếu Gặp Lỗi Khi Setup

**Lỗi 1: "Port 8080 already in use"**
```bash
# Có thể MySQL hoặc app khác đang chạy port 8080

# Cách 1: Tắt app đang dùng port 8080
# Windows: 
netstat -ano | findstr :8080
taskkill /PID <process_id> /F

# Cách 2: Đổi port trong application.yml
# backend-java/src/main/resources/application.yml
server:
  port: 8081  # Đổi thành 8081
```

**Lỗi 2: "Cannot connect to MySQL"**
```bash
# Kiểm tra Docker container có chạy không
docker ps

# Nếu không thấy mysql, start lại
docker-compose up -d mysql

# Xem logs để debug
docker logs mysql_container
```

**Lỗi 3: "npm install failed"**
```bash
# Xóa cache và cài lại
rm -rf node_modules
rm package-lock.json
npm cache clean --force
npm install
```

**⚠️ Quan trọng:** Nếu mắc kẹt >30 phút, hỏi ngay Lead/Senior. Đừng ngồi debug một mình cả ngày!

---

## 2. Hiểu Dự Án Chúng Ta Đang Làm

### 🎮 Dự Án Là Gì?

**Game Account Marketplace** = Website mua bán tài khoản game.

**Ví dụ thực tế:**
- Bạn chơi Liên Quân, có tài khoản rank Kim Cương, muốn bán.
- Bạn lên website này, đăng tin "Bán acc LQ rank KC, level 50, giá 500k".
- Người khác thấy, mua, chuyển tiền, nhận tài khoản.

**Tính năng chính:**
1. **Đăng ký/Đăng nhập** (Epic 1 - bạn sẽ làm cái này trước)
2. **Đăng tin bán tài khoản** (Epic 2)
3. **Tìm kiếm, lọc tài khoản** (Epic 3)
4. **Mua tài khoản, thanh toán** (Epic 4)
5. **Chat, thông báo** (Epic 5)
6. **Đánh giá, review người bán** (Epic 6)
7. **Admin quản lý** (Epic 7)

### 🏗️ Công Nghệ Sử Dụng

```
┌─────────────────────────────────────────────────────────────┐
│                        FRONTEND                              │
│  React + TypeScript + Tailwind CSS + Apollo Client         │
│  (Phần người dùng nhìn thấy: giao diện, form, button)      │
└─────────────────────────────────────────────────────────────┘
                            ↓ ↑
                         HTTP / GraphQL
                            ↓ ↑
┌─────────────────────────────────────────────────────────────┐
│                        BACKEND                               │
│  Java 21 + Spring Boot + GraphQL + REST API                │
│  (Phần xử lý logic: đăng ký, login, lưu data, bảo mật)     │
└─────────────────────────────────────────────────────────────┘
                            ↓ ↑
                          Query / Save
                            ↓ ↑
┌─────────────────────────────────────────────────────────────┐
│                       DATABASE                               │
│         MySQL (lưu user, account, transaction)              │
│         Redis (cache để tăng tốc độ)                        │
└─────────────────────────────────────────────────────────────┘
```

**Bạn sẽ làm gì?**
- **Backend:** Viết Java code (Controller, Service, Repository)
- **Frontend:** Viết React code (Component, Page, Hook)
- **Database:** Thiết kế bảng (Entity), viết query

---

## 3. Cách Sử Dụng Trello

### 📋 Trello Là Gì?

Trello = Bảng công việc online. Giống như bảng giấy A0 dán post-it, nhưng trên máy tính.

**Tại sao dùng Trello?**
- ✅ Thấy được ai đang làm gì
- ✅ Biết việc nào đã xong, việc nào chưa
- ✅ Không bị quên task
- ✅ Lead theo dõi tiến độ

### 📊 Cấu Trúc Board

Board của team có **7 cột**:

```
┌─────────┬─────────┬─────────┬─────────┬─────────┬─────────┬─────────┐
│ Backlog │ Sprint  │  To Do  │   In    │  Code   │ Testing │  Done   │
│         │ Backlog │         │Progress │ Review  │         │         │
├─────────┼─────────┼─────────┼─────────┼─────────┼─────────┼─────────┤
│ Việc    │ Việc    │ Sẵn sàng│ Đang    │ Đã code │ Đã merge│ Hoàn    │
│ tương   │ tuần    │ làm     │ làm     │ xong,   │ đang    │ thành   │
│ lai     │ này     │         │         │ chờ     │ test    │         │
│         │         │         │         │ review  │         │         │
└─────────┴─────────┴─────────┴─────────┴─────────┴─────────┴─────────┘
```

### 🎴 Card Trello Là Gì?

**1 Card = 1 công việc (Story)**

Ví dụ Card:
```
┌───────────────────────────────────────────────────────┐
│ [1.4] User Entity & Repository                        │
│ 👤 @JuniorDev1  |  📅 Deadline: Wed 5PM  |  ⭐ 3 pts │
├───────────────────────────────────────────────────────┤
│ Mục tiêu: Tạo User entity để lưu thông tin user       │
│                                                        │
│ Việc cần làm:                                          │
│ ☐ Tạo file User.java                                  │
│ ☐ Thêm các field (id, email, password, ...)          │
│ ☐ Thêm annotation (@Entity, @Table, ...)             │
│ ☐ Tạo UserRepository.java                            │
│ ☐ Test: Chạy được, tạo table trong MySQL             │
│                                                        │
│ 🔗 Link PR: github.com/...                           │
└───────────────────────────────────────────────────────┘
```

### 🔄 Quy Trình Di Chuyển Card

**Ngày Thứ 2 (Đầu tuần):**
```
1. Lead assign card cho bạn
2. Card được chuyển từ "Sprint Backlog" → "To Do"
3. Bạn đọc card, hiểu yêu cầu
```

**Khi Bắt Đầu Làm:**
```
4. Bạn kéo card từ "To Do" → "In Progress"
5. Thêm comment: "Bắt đầu làm lúc 9:00"
```

**Trong Quá Trình Làm (Mỗi Ngày):**
```
6. Cập nhật comment: "Đã xong phần A, đang làm phần B"
7. Check ☑️ các việc đã hoàn thành trong checklist
```

**Khi Hoàn Thành:**
```
8. Tạo Pull Request trên GitHub
9. Paste link PR vào card
10. Kéo card từ "In Progress" → "Code Review"
11. Tag Lead trong comment: "@Lead Em đã tạo PR, anh review giúp em!"
```

**Sau Khi Lead Approve:**
```
12. Lead merge PR
13. Lead kéo card → "Testing"
14. Sau khi test OK, Lead kéo → "Done"
```

### ✅ Checklist Hàng Ngày Với Trello

**Mỗi Sáng (9:00):**
- [ ] Mở Trello
- [ ] Xem card của mình ở cột nào
- [ ] Thêm comment update tiến độ hôm qua
- [ ] Check xem có card mới được assign không

**Cuối Ngày (5:00):**
- [ ] Update comment: đã làm được gì hôm nay
- [ ] Check các việc đã xong trong checklist
- [ ] Nếu bị kẹt, thêm comment hỏi Lead

---

## 4. Cách Sử Dụng Git

### 🌳 Git Là Gì? (Giải Thích Đơn Giản)

**Git = Công cụ quản lý code, giống như "Ctrl+Z siêu cấp"**

**Tại sao cần Git?**
- ✅ Nhiều người code cùng lúc, không đè code của nhau
- ✅ Lưu lại lịch sử thay đổi (ai sửa cái gì, khi nào)
- ✅ Có thể quay lại phiên bản cũ nếu code mới bị lỗi
- ✅ Làm việc trên nhánh riêng, không ảnh hưởng người khác

### 📖 Thuật Ngữ Cơ Bản

| Thuật Ngữ | Giải Thích | Ví Dụ Thực Tế |
|-----------|------------|---------------|
| **Repository (Repo)** | Kho chứa code | Giống như một thư mục Google Drive chung |
| **Branch (Nhánh)** | Bản sao code để làm việc | Giống như bạn copy file Word ra làm, xong mới gộp vào bản chính |
| **Commit** | Lưu thay đổi | Giống như bấm "Ctrl+S" + ghi chú "sửa cái gì" |
| **Push** | Đẩy code lên server | Upload code lên GitHub |
| **Pull** | Kéo code mới về | Download code mới nhất về máy |
| **Pull Request (PR)** | Xin phép merge code | Giống như gửi bài cho thầy chấm |
| **Merge** | Gộp code vào nhánh chính | Sau khi thầy chấm OK, gộp vào bản chính |
| **Conflict** | Code bị xung đột | 2 người sửa cùng 1 chỗ, Git không biết giữ cái nào |

### 🌿 Cấu Trúc Nhánh (Branch)

```
main (Nhánh chính - code đã release, chạy thật)
  │
  └─── develop (Nhánh tích hợp - code đang phát triển)
         │
         ├─── feature/1.4-user-entity (Nhánh của bạn)
         │
         ├─── feature/1.5-jwt-auth (Nhánh của bạn khác)
         │
         └─── feature/2.3-graphql (Nhánh của bạn khác nữa)
```

**Quy tắc quan trọng:**
- ❌ **KHÔNG BAO GIỜ** commit thẳng vào `main` hoặc `develop`
- ✅ Luôn luôn tạo nhánh `feature/` riêng
- ✅ Tên nhánh: `feature/X.Y-ten-ngan-gon` (ví dụ: `feature/1.4-user-entity`)

### 🚀 Quy Trình Git Hàng Ngày (Copy-Paste Được Luôn!)

#### **Bước 1: Bắt Đầu Một Story Mới**

```bash
# 1. Về nhánh develop
git checkout develop

# 2. Kéo code mới nhất
git pull origin develop

# 3. Tạo nhánh mới cho story của bạn
# Đặt tên theo format: feature/<epic>.<story>-<mô-tả-ngắn>
git checkout -b feature/1.4-user-entity

# 4. Kiểm tra bạn đang ở nhánh nào
git branch
# Kết quả: * feature/1.4-user-entity (dấu * là nhánh hiện tại)

# Giờ bắt đầu code thôi!
```

#### **Bước 2: Làm Việc Và Commit**

```bash
# Bạn sửa file: backend-java/src/main/.../entity/User.java

# 1. Xem những file nào đã thay đổi
git status
# Kết quả: User.java màu đỏ = đã sửa nhưng chưa add

# 2. Add file vào staging (chuẩn bị commit)
git add backend-java/src/main/java/com/gameaccount/marketplace/entity/User.java

# Hoặc add tất cả files đã sửa:
git add .

# 3. Commit với message rõ ràng
git commit -m "[1.4] Thêm User entity với các field cơ bản"

# Tip: Message commit nên bắt đầu bằng [Story-ID] và mô tả ngắn gọn
```

**Format Message Commit Chuẩn:**
```
✅ Tốt: "[1.4] Thêm User entity với JPA annotations"
✅ Tốt: "[1.4] Tạo UserRepository với method findByEmail"
✅ Tốt: "[1.4] Fix lỗi validation email trong User entity"

❌ Xấu: "update"
❌ Xấu: "fix bug"
❌ Xấu: "asdfasdf"
```

#### **Bước 3: Push Code Lên GitHub**

```bash
# Lần đầu tiên push nhánh mới:
git push -u origin feature/1.4-user-entity

# Những lần sau chỉ cần:
git push
```

#### **Bước 4: Cập Nhật Nhánh (Mỗi Sáng Làm Cái Này)**

```bash
# Tình huống: Bạn làm story 3 ngày. 
# Trong thời gian đó, người khác đã merge code vào develop.
# Bạn cần update nhánh của mình để có code mới nhất.

# 1. Commit công việc hiện tại (nếu có)
git add .
git commit -m "[1.4] WIP: Đang làm UserRepository"

# 2. Về develop, kéo code mới
git checkout develop
git pull origin develop

# 3. Quay lại nhánh của bạn
git checkout feature/1.4-user-entity

# 4. Rebase (gộp code mới vào nhánh của bạn)
git rebase develop

# Nếu không có conflict:
# ✅ "Successfully rebased"

# Nếu có conflict:
# ❌ Xem phần "Xử Lý Conflict" bên dưới

# 5. Push (cần force vì đã rebase)
git push --force-with-lease
```

#### **Bước 5: Tạo Pull Request**

```bash
# 1. Đảm bảo code chạy được
mvn clean test  # Backend
npm test        # Frontend

# 2. Commit và push lần cuối
git add .
git commit -m "[1.4] Hoàn thành User entity và UserRepository"
git push

# 3. Vào GitHub, tạo Pull Request:
# - Từ: feature/1.4-user-entity
# - Vào: develop
# - Điền description theo template (xem phần dưới)
```

### 📝 Template Pull Request (Copy-Paste Vào GitHub)

```markdown
## [1.4] User Entity & Repository

### 📋 Trello Card
Link: [Trello Card của bạn]

### 🎯 Mục Tiêu
Tạo User JPA entity và UserRepository để lưu trữ thông tin người dùng.

### ✅ Đã Làm Gì
- Tạo User.java entity với các field: id, email, password, fullName, role, status, createdAt, updatedAt
- Thêm JPA annotations (@Entity, @Table, @Id, @GeneratedValue)
- Thêm Lombok annotations (@Getter, @Setter, @Builder)
- Tạo UserRepository.java với method: findByEmail(), existsByEmail()
- Chạy được, MySQL tự động tạo table `users`

### 🧪 Đã Test
- [x] `mvn clean compile` - OK
- [x] `mvn test` - All tests pass
- [x] Application chạy không lỗi
- [x] MySQL table `users` được tạo đúng
- [x] UserRepository.save() lưu được user

### 📸 Screenshots
![MySQL Table](link-to-screenshot)

### 🤔 Câu Hỏi Cho Reviewer
- Field `rating` nên dùng Float hay Double?
- Có cần thêm index cho field `email` không?

### 🔗 Liên Quan
- Phụ thuộc: #42 (Story 1.2 - Spring Boot Skeleton) ✅
- Chặn: #45 (Story 1.5 - JWT Implementation)
```

### ⚔️ Xử Lý Conflict (Phần Khó Nhất!)

**Conflict xảy ra khi nào?**
- Bạn sửa file `User.java` dòng 25
- Bạn khác cũng sửa `User.java` dòng 25
- Git không biết giữ code của ai

**Cách xử lý:**

```bash
# Khi rebase, Git báo conflict:
# CONFLICT (content): Merge conflict in User.java

# 1. Mở file User.java, bạn sẽ thấy:
public class User {
    private Long id;
    
<<<<<<< HEAD (code của bạn)
    private String fullName;
    private String phoneNumber;
=======
    private String name;
    private String phone;
>>>>>>> develop (code của người khác)
    
    private String password;
}

# 2. Quyết định giữ code nào:
# - Giữ code của bạn
# - Giữ code của người khác
# - Hoặc gộp cả 2

# 3. Sửa thành:
public class User {
    private Long id;
    private String fullName;  # Quyết định giữ cái này
    private String phone;     # Gộp cả 2
    private String password;
}

# 4. Xóa các dòng Git tự thêm vào:
<<<<<<< HEAD
=======
>>>>>>> develop

# 5. Test xem code còn chạy không
mvn clean test

# 6. Add và continue rebase
git add User.java
git rebase --continue

# 7. Push
git push --force-with-lease
```

**Tips Tránh Conflict:**
- ✅ Update nhánh mỗi sáng (rebase develop)
- ✅ Làm story nhanh (1-2 ngày), đừng kéo dài
- ✅ Trao đổi với team: "Em đang sửa file User.java"
- ✅ Nếu 2 người cùng sửa 1 file, pair programming

### 🆘 Các Lệnh Git Cứu Nguy

```bash
# Quên checkout nhánh mới, commit nhầm vào develop:
git reset --soft HEAD~1  # Undo commit nhưng giữ code
git checkout -b feature/1.4-user-entity  # Tạo nhánh đúng
git commit -m "[1.4] ..."  # Commit lại

# Xóa nhầm file, muốn lấy lại:
git checkout -- path/to/file.java

# Muốn quay về trạng thái lúc chưa sửa:
git reset --hard HEAD

# Push bị reject, cần force push (cẩn thận!):
git push --force-with-lease  # An toàn hơn --force

# Xem lịch sử commit:
git log --oneline

# Xem ai sửa file này lần cuối:
git log -1 User.java
```

---

## 5. Quy Trình Làm Một Story

### 📅 Timeline Một Story (Ví Dụ: Story 1.4)

**Thứ 2 (Đầu tuần):**
```
9:00 - Sprint Planning
       Lead assign Story 1.4 cho bạn
       Bạn đọc card Trello, hỏi nếu không hiểu

10:00 - Bắt đầu làm
        • git checkout -b feature/1.4-user-entity
        • Đọc kỹ Acceptance Criteria trong card
        • Đọc tài liệu liên quan (Epic 1, Architecture Doc)
        • Kéo Trello card → "In Progress"
```

**Thứ 3:**
```
9:00 - Daily Standup (post Discord)
       "Hôm qua: Đã tạo User entity với các field cơ bản
        Hôm nay: Sẽ tạo UserRepository và viết tests
        Blocker: Không có"

10:00 - Tiếp tục code
        • Tạo UserRepository.java
        • Viết unit tests
        • Commit thường xuyên (3-4 lần/ngày)
        
Cuối ngày:
        • Update Trello: Check các việc đã xong
        • Commit và push code
```

**Thứ 4 (Hoàn thành):**
```
9:00 - Sáng tiếp tục

11:00 - Hoàn thành code
        • Test kỹ: mvn test, mvn spring-boot:run
        • Tự review code của mình trước
        • Fix các lỗi nhỏ (format code, remove debug log)

2:00 - Tạo Pull Request
       • Push code lên
       • Tạo PR trên GitHub với description đầy đủ
       • Link PR vào Trello card
       • Kéo card → "Code Review"
       • Tag Lead: "@Lead em đã tạo PR, anh review giúp em!"

3:00 - Chờ review
       • Làm story khác (nếu có)
       • Hoặc review PR của người khác (học hỏi)
```

**Thứ 5:**
```
9:00 - Lead review, comment vài chỗ cần sửa

10:00 - Bạn sửa theo feedback
        • Sửa code
        • Commit: "[1.4] Fix theo review feedback"
        • Push
        • Comment vào PR: "✅ Đã fix xong"

2:00 - Lead approve và merge
       Card tự động → "Testing"
       
4:00 - Lead test OK
       Card → "Done"
       🎉 Xong story đầu tiên!
```

### ✅ Checklist Trước Khi Tạo PR

```
Trước khi tạo PR, check list này:

CODE QUALITY:
□ Code chạy được không lỗi
□ Đã test thủ công chưa
□ Đã viết unit test chưa (nếu story yêu cầu)
□ Đã xóa các dòng debug (System.out.println, console.log) chưa
□ Đã xóa code không dùng (commented code) chưa
□ Đã format code chưa (Ctrl+Alt+L trong IntelliJ)

BUILD & TEST:
□ mvn clean compile - PASS?
□ mvn test - PASS?
□ npm run lint - PASS? (nếu là frontend)
□ npm run build - PASS? (nếu là frontend)

GIT:
□ Commit message rõ ràng chưa
□ Branch name đúng format chưa
□ Đã update từ develop chưa (git rebase develop)
□ Đã push code lên chưa

TRELLO:
□ Đã link PR vào Trello card chưa
□ Đã check các Acceptance Criteria xong chưa
□ Đã update time tracking chưa

Nếu tất cả ✅ → Tạo PR!
```

---

## 6. Cách Làm Việc Nhóm

### 👥 Team Structure

```
                    Lead (Anh/Chị Team Lead)
                            │
            ┌───────────────┼───────────────┐
            │               │               │
      JuniorDev1      JuniorDev2      JuniorDev3      JuniorDev4
       (Backend)      (Frontend)      (Backend)       (Fullstack)
```

**Vai trò:**
- **Lead:** Assign task, review code, giúp debug, quyết định technical
- **JuniorDev:** Làm story được assign, hỏi khi cần, review code lẫn nhau

### 💬 Cách Giao Tiếp

#### **Daily Standup (Mỗi Sáng 9:00 - 15 phút)**

**Format:**
```markdown
**@JuniorDev1 - 09/01/2026**

✅ **Hôm qua:**
- Hoàn thành Story 1.4 (User Entity & Repository)
- Tạo PR #44, đang chờ review

⏳ **Hôm nay:**
- Nếu PR được approve: Bắt đầu Story 1.5 (JWT Authentication)
- Nếu chưa: Fix feedback từ review

🚧 **Blocker:**
- Không có blocker
- (Hoặc: "Đang bị kẹt ở việc config JWT, cần anh Lead hỗ trợ")
```

**Quy tắc:**
- ✅ Ngắn gọn, súc tích (3-5 dòng)
- ✅ Post đúng giờ (9:00)
- ✅ Thành thật (đừng nói "ok" nếu đang bị kẹt)
- ✅ Mention blocker rõ ràng

#### **Kênh Discord/Slack**

**#daily-standup:** Post báo cáo hàng ngày
```
@JuniorDev1: [standup như trên]
```

**#tech-discuss:** Hỏi đáp kỹ thuật
```
@JuniorDev1: "Anh @Lead ơi, em đang làm Story 1.4, 
gặp lỗi này: [paste error]. Em đã thử [A, B, C] 
nhưng chưa được. Anh có thể giúp em không ạ?"
```

**#general:** Tán gẫu, memes, thông báo chung
```
@JuniorDev1: "Hôm nay ăn gì? 🍜"
@Lead: "Team meeting 3PM hôm nay nhé!"
```

**#git-notifications:** Tự động (webhook từ GitHub)
```
[Bot] JuniorDev1 pushed to feature/1.4-user-entity
[Bot] JuniorDev1 created PR #44
[Bot] Lead approved PR #44
```

### 🤝 Peer Review (Review Code Lẫn Nhau)

**Tại sao phải review code của người khác?**
- ✅ Học được cách code của người khác
- ✅ Hiểu rõ hơn về codebase
- ✅ Phát hiện lỗi sớm
- ✅ Tập làm quen với vai trò senior sau này

**Cách review:**
```
1. Đọc description của PR
2. Checkout nhánh về máy:
   git fetch origin
   git checkout feature/1.5-jwt-auth
   
3. Chạy thử:
   mvn test
   mvn spring-boot:run
   
4. Đọc code trên GitHub, comment nếu:
   - Thấy bug tiềm ẩn
   - Code khó hiểu, cần comment
   - Có cách tốt hơn
   - Học được trick hay
   
5. Approve hoặc Request Changes
```

**Ví dụ comment tốt:**
```
✅ "Line 45: Tốt! Em thích cách anh dùng Optional.orElseThrow() ở đây.
    Rõ ràng và tránh được NullPointer."
    
✅ "Line 67: Em nghĩ chỗ này nên validate email format trước khi save.
    Có thể dùng @Email annotation không anh?"
    
✅ "Line 102: Anh có thể giải thích tại sao dùng BCrypt strength 12 không?
    Em thấy docs recommend 10. Em muốn học thêm."
```

**Ví dụ comment xấu:**
```
❌ "Code này sai rồi."  (Không nói tại sao, không giúp gì)
❌ "Tại sao lại làm vậy?"  (Nghe có vẻ phán xét)
❌ "LGTM"  (Không đọc kỹ,敷衍)
```

### 🚨 Khi Nào Cần Hỏi

**Hỏi NGAY (trong vòng 5 phút):**
- ❌ Setup môi trường không được (Docker, MySQL, Redis)
- ❌ Git conflict không biết xử lý
- ❌ Không hiểu acceptance criteria của story
- ❌ Bị assign story quá khó, không biết bắt đầu từ đâu

**Hỏi SAU KHI TỰ TÌM HIỂU (30 phút):**
- ⚠️ Gặp lỗi khi code
- ⚠️ Không biết dùng API/library nào
- ⚠️ Không chắc về kiến trúc (code nên để ở đâu)
- ⚠️ Không biết cách test

**Format hỏi chuẩn:**
```markdown
Anh @Lead ơi, em đang làm Story 1.4 (User Entity).

❌ **Vấn đề:** 
Em gặp lỗi này khi chạy mvn spring-boot:run:
[paste error message]

✅ **Em đã thử:**
1. Google error message → tìm được StackOverflow nhưng không giải quyết được
2. Check application.yml → datasource config nhìn đúng rồi
3. Restart Docker container → vẫn lỗi

⏱️ **Thời gian đã mắc kẹt:** 30 phút

🙏 **Nhờ anh:** Anh có thể giúp em debug không ạ?
```

**❌ ĐỪNG hỏi kiểu này:**
```
"Anh ơi, em bị lỗi."  (Lỗi gì? Đã thử gì chưa?)
"Em không biết làm."  (Không biết chỗ nào? Story nào?)
"Code em sao không chạy?"  (Paste code, paste error vào!)
```

---

## 7. Khi Nào Thì Hỏi? Hỏi Ai?

### 📞 Bảng Tra Cứu Nhanh

| Vấn Đề | Hỏi Ai | Hỏi Ở Đâu | Mức Độ Khẩn Cấp |
|--------|--------|-----------|-----------------|
| Setup môi trường không được | Lead/Senior | #tech-discuss | 🔴 Ngay lập tức |
| Git conflict | Senior | #tech-discuss | 🔴 Trong 10 phút |
| Không hiểu story | Lead | #tech-discuss hoặc DM | 🔴 Trước khi bắt đầu |
| Lỗi khi code | Search Google → #tech-discuss | #tech-discuss | 🟡 Sau 30 phút tự tìm |
| Không biết dùng library nào | Đọc docs → #tech-discuss | #tech-discuss | 🟡 Sau 30 phút tự tìm |
| Code review feedback không hiểu | Người review | Comment trong PR | 🟢 Trong ngày |
| Muốn pair programming | Lead/Senior | #tech-discuss | 🟢 Hẹn trước 1 ngày |
| Muốn học thêm topic nào đó | #resources | #resources | 🟢 Bất cứ lúc nào |

### 🎯 Quy Tắc 30 Phút

```
Bước 1: Gặp vấn đề (0 phút)
         ↓
Bước 2: Google, đọc docs, thử fix (0-30 phút)
         ↓
         ├─ Fix được? → Tuyệt! Làm tiếp
         │
         └─ Không fix được sau 30 phút?
            → Hỏi ngay, đừng ngồi debug thêm!
```

**Tại sao 30 phút?**
- ✅ Đủ thời gian để tự tìm hiểu (học được nhiều hơn)
- ✅ Không quá lâu khiến bạn stress
- ✅ Không waste thời gian của cả team

---

## 8. Tình Huống Thường Gặp & Cách Xử Lý

### 🐛 Tình Huống 1: "Backend Không Start Được"

**Triệu chứng:**
```
mvn spring-boot:run
...
ERROR: Application failed to start
```

**Checklist debug:**
```
□ Docker MySQL có đang chạy không?
  → docker ps
  → Nếu không: docker-compose up -d mysql
  
□ Port 8080 có bị chiếm không?
  → netstat -ano | findstr :8080
  → Nếu có: taskkill /PID <id> /F
  
□ application.yml config đúng chưa?
  → Check datasource URL, username, password
  
□ Dependencies có download đủ chưa?
  → mvn clean install -U
  
□ Java version đúng chưa?
  → java -version  (cần Java 21)
```

**Nếu vẫn không được:** Hỏi Lead, paste full error log.

---

### 🔀 Tình Huống 2: "Git Conflict Khi Rebase"

**Triệu chứng:**
```
git rebase develop
CONFLICT (content): Merge conflict in User.java
```

**Bước 1: Đừng hoảng!**
- Conflict là bình thường
- Có thể fix được
- Không làm mất code đâu

**Bước 2: Xem file nào bị conflict**
```bash
git status
# Unmerged paths:
#   both modified:   User.java
```

**Bước 3: Mở User.java, sửa conflict**
```java
// Bạn sẽ thấy:
<<<<<<< HEAD
private String fullName;  // Code của bạn
=======
private String name;      // Code của người khác
>>>>>>> develop

// Sửa thành (quyết định giữ cái nào):
private String fullName;  // Giữ code của bạn
```

**Bước 4: Xóa các dấu Git tự thêm**
```
<<<<<<< HEAD
=======
>>>>>>> develop
```

**Bước 5: Test xem code còn chạy không**
```bash
mvn clean compile
# Nếu compile được = OK!
```

**Bước 6: Mark là resolved và continue**
```bash
git add User.java
git rebase --continue
git push --force-with-lease
```

**Nếu quá rối:** Hỏi Senior, hoặc:
```bash
git rebase --abort  # Hủy rebase, quay lại trạng thái cũ
```

---

### ⏱️ Tình Huống 3: "Story Làm Quá Deadline"

**Ví dụ:**
- Story 1.4 estimate: 1 ngày
- Deadline: Thứ 4
- Hôm nay: Thứ 4, bạn mới làm được 50%

**Bước 1: Thông báo sớm (KHÔNG im lặng!)**
```
# Post trong #tech-discuss hoặc DM Lead
"Anh @Lead ơi, em đang làm Story 1.4.
Em estimate 1 ngày nhưng em thấy cần 2 ngày.

Lý do: Em gặp vấn đề với JPA relationship, 
mất nhiều thời gian hơn dự kiến để hiểu và fix.

Em dự kiến hoàn thành vào sáng thứ 5. 
Có ổn không anh?"
```

**Bước 2: Lead sẽ giúp**
- Hoặc extend deadline
- Hoặc pair programming giúp bạn
- Hoặc chia story thành 2 phần nhỏ hơn

**❌ ĐỪNG:**
- Im lặng, tới deadline mới nói
- Nói dối "em sắp xong" trong khi mới 30%
- Làm qua loa để kịp deadline

---

### 🤔 Tình Huống 4: "Không Hiểu Feedback Từ Code Review"

**Ví dụ:**
Lead comment trong PR:
> "Line 45: Nên move validation logic vào Service layer thay vì để ở Controller."

**Bạn nghĩ:** "Sao lại phải move? Đâu không được?"

**Cách xử lý đúng:**
```
# Comment vào PR:
"@Lead em không hiểu lắm ạ. 

Hiện tại em đang validate ở Controller như này:
[paste code]

Anh muốn em move vào Service, có phải là:
[paste code ví dụ]

Lý do là để Controller thin hơn phải không anh?
Anh có thể giải thích thêm không ạ?"
```

**Lead sẽ giải thích**, sau đó bạn sẽ hiểu và sửa.

**❌ ĐỪNG:**
- Sửa mù (không hiểu vì sao)
- Không hỏi, approval rồi mới hỏi
- Cãi lại (trừ khi bạn có lý lẽ kỹ thuật vững)

---

### 💻 Tình Huống 5: "Test Chạy Local OK, Nhưng CI Fail"

**Triệu chứng:**
```
# Local:
mvn test → All tests passed ✅

# GitHub Actions CI:
mvn test → FAILED ❌
```

**Nguyên nhân thường gặp:**
```
1. Khác Java version
   → Local: Java 17, CI: Java 21
   
2. Khác database state
   → Local: Có data test, CI: Database trống
   
3. Timezone khác
   → Local: GMT+7, CI: UTC
   
4. Dependencies không đồng bộ
   → Local: Cache cũ, CI: Download mới
```

**Cách fix:**
```bash
# 1. Check CI logs trên GitHub
# 2. Tìm dòng đầu tiên báo lỗi
# 3. Google error đó + "GitHub Actions"
# 4. Thường là config issue

# Fix phổ biến:
# - Update .github/workflows/ci.yml với Java version đúng
# - Update test để không phụ thuộc data có sẵn
# - Mock thời gian trong test
```

**Nếu vẫn không fix được:** Hỏi Lead, paste link CI build.

---

## 9. Tips Từ Anh/Chị Senior

### 💡 Tips Kỹ Thuật

#### **1. Đọc Code Người Khác = Học Nhanh Nhất**
```
Khi rảnh:
✅ Đọc PR của người khác (ngay cả khi không được assign review)
✅ Đọc code ở các file liên quan đến story bạn đang làm
✅ Hỏi: "Tại sao anh làm vậy?" → Học được best practice

Đừng chỉ đọc docs, hãy đọc code thật!
```

#### **2. Console.log / System.out.println Là Bạn Tốt Nhất**
```java
// Khi debug, đừng ngại print ra:
System.out.println(">>> User: " + user);
System.out.println(">>> Email: " + user.getEmail());

// Hiểu được data flow rồi mới xóa đi
```

#### **3. Commit Nhỏ, Commit Thường Xuyên**
```bash
# ❌ Xấu: 1 commit to đùng cho cả story
git commit -m "[1.4] Hoàn thành story"

# ✅ Tốt: Nhiều commit nhỏ
git commit -m "[1.4] Thêm User entity"
git commit -m "[1.4] Thêm JPA annotations"
git commit -m "[1.4] Tạo UserRepository"
git commit -m "[1.4] Thêm unit tests"
git commit -m "[1.4] Fix validation bugs"

# Lợi ích:
# - Dễ revert nếu lỗi
# - Dễ review
# - Dễ trace bug
```

#### **4. Google Như Một Pro**
```
❌ Xấu: "java error"
✅ Tốt: "java.lang.NullPointerException Spring Boot UserService"

❌ Xấu: "git conflict"
✅ Tốt: "git rebase conflict both modified same line"

Tip: Copy exact error message vào Google (bỏ số dòng, tên file)
```

#### **5. Đọc Error Message Từ Dưới Lên**
```
Khi có lỗi:
1. Đọc dòng CUỐI CÙNG trước (root cause)
2. Rồi đọc lên trên (stack trace)

Ví dụ:
at com.example.UserService.findUser(UserService.java:45)
at com.example.UserController.getUser(UserController.java:23)
...
Caused by: NullPointerException: email is null  ← ĐỌC DÒNG NÀY TRƯỚC!

→ Vấn đề: email null ở UserService line 45
```

---

### 🎯 Tips Làm Việc Nhóm

#### **1. Over-Communicate > Under-Communicate**
```
✅ Tốt: "Em bắt đầu làm Story 1.4 nhé anh!"
✅ Tốt: "Em đã xong 50% Story 1.4"
✅ Tốt: "Em bị kẹt ở chỗ này..."
✅ Tốt: "Em tạo PR rồi, anh review giúp em!"

Lead thích biết bạn đang làm gì hơn là phải hỏi.
```

#### **2. Hỏi Thông Minh**
```
❌ "Anh ơi, em bị lỗi."
✅ "Anh ơi, em bị lỗi [X] khi làm [Y]. Em đã thử [A, B, C]. 
   Anh có thể giúp em không?"

Format: Vấn đề + Context + Đã thử gì + Nhờ giúp
```

#### **3. Đừng Sợ "Hỏi Ngu"**
```
Câu hỏi "ngu" không tồn tại!

✅ "Em chưa hiểu JPA là gì, anh giải thích được không?"
✅ "Tại sao phải dùng @Transactional ạ?"
✅ "Git rebase khác merge như thế nào?"

Người senior rất sẵn lòng giải thích!
```

#### **4. Celebrate Small Wins**
```
✅ Commit đầu tiên? → Share trong team!
✅ PR đầu tiên được approve? → 🎉
✅ Story đầu tiên hoàn thành? → Tự thưởng mình!

Coding là marathon, không phải sprint.
Nghỉ ngơi, ăn uống đầy đủ, ngủ đủ giấc.
```

---

### ⚡ Tips Tăng Tốc

#### **1. Setup Shortcuts**
```
IntelliJ IDEA:
Ctrl + Alt + L      → Format code
Ctrl + /            → Comment/uncomment
Ctrl + D            → Duplicate line
Ctrl + Y            → Delete line
Ctrl + Shift + F    → Search in files
Alt + Enter         → Quick fix

VS Code:
Ctrl + Shift + P    → Command palette
Ctrl + `            → Terminal
Ctrl + B            → Toggle sidebar
Ctrl + P            → Quick open file
```

#### **2. Dùng AI Tools (Nhưng Hiểu Code!)**
```
✅ Dùng GitHub Copilot / ChatGPT để:
   - Giải thích lỗi
   - Suggest code snippet
   - Viết boilerplate code

❌ Đừng:
   - Copy-paste mù quáng
   - Không hiểu code AI generate
   - Submit code AI viết 100% không đọc

Nguyên tắc: AI giúp nhanh hơn, không thay thế hiểu biết!
```

#### **3. Tự Động Hóa**
```bash
# Tạo Git alias cho lệnh thường dùng:
git config --global alias.co checkout
git config --global alias.br branch
git config --global alias.cm commit
git config --global alias.st status

# Giờ chỉ cần:
git co develop
git br -a
git cm -m "message"
git st
```

---

## 📝 Phụ Lục: Checklist Tổng Hợp

### ✅ Checklist Ngày Đầu Tiên
```
□ Tạo tài khoản: GitHub, Trello, Discord
□ Cài phần mềm: Java 21, Node 18, Git, Docker, IDE
□ Clone repository
□ Chạy Docker (MySQL + Redis)
□ Chạy backend (mvn spring-boot:run)
□ Chạy frontend (npm run dev)
□ Test đăng nhập thành công
□ Đọc README, Architecture, Epic 1
□ Hiểu quy trình: Trello → Git → PR → Review
```

### ✅ Checklist Hàng Ngày
```
9:00 AM:
□ Post daily standup trong Discord
□ Check Trello: có task mới không
□ Check PR: có feedback từ review không
□ git pull origin develop (update code mới)

Trong ngày:
□ Code story được assign
□ Commit ít nhất 2-3 lần
□ Update Trello card (progress, checklist)
□ Review PR của người khác (nếu có)

5:00 PM:
□ Commit + push code (đừng để uncommitted qua đêm)
□ Update Trello: đã làm được gì hôm nay
□ Nếu bị block: báo Lead ngay
```

### ✅ Checklist Trước Khi Tạo PR
```
CODE:
□ Code chạy không lỗi
□ Test thủ công OK
□ Đã xóa debug log (console.log, System.out.println)
□ Đã xóa code không dùng (commented code)
□ Đã format code (Ctrl+Alt+L)

BUILD:
□ mvn clean compile - PASS
□ mvn test - PASS
□ npm run lint - PASS (frontend)
□ npm run build - PASS (frontend)

GIT:
□ Branch name đúng format (feature/X.Y-description)
□ Commit message rõ ràng
□ Đã rebase develop (git rebase develop)
□ Đã push (git push)

TRELLO:
□ Tất cả Acceptance Criteria đã check ✓
□ Time tracking updated

→ Tạo PR, link vào Trello, tag Lead review!
```

---

## 🎓 Kết Luận

Chào mừng bạn đã đọc đến đây! 🎉

Những gì bạn vừa đọc là **tất cả những gì một junior developer cần biết** để làm việc hiệu quả trong team.

**Nhớ rằng:**
- 🌱 Mọi người đều từng là junior
- 📚 Học bằng cách làm, không phải chỉ đọc
- 🤝 Đừng ngại hỏi, team luôn sẵn sàng giúp
- 🎯 Focus vào progress, không phải perfection
- 💪 Bạn sẽ giỏi lên mỗi ngày

**Hành trình của bạn:**
```
Tuần 1: Setup, làm quen → Hơi rối, nhiều thứ mới
Tuần 2: Story đầu tiên → Chậm nhưng học được nhiều
Tuần 3-4: Quen dần → Nhanh hơn, tự tin hơn
Tháng 2-3: Thành thạo → Làm story độc lập, review được code
Tháng 6+: Junior+ → Mentor được junior khác
```

**Tips cuối cùng:**
- Đọc tài liệu này nhiều lần (đừng cố nhớ hết)
- Bookmark lại, tra cứu khi cần
- Chia sẻ với junior khác nếu họ cần

---

## 📞 Liên Hệ & Hỗ Trợ

**Cần giúp đỡ?**
- 🔴 Khẩn cấp (setup, git disaster): Gọi Lead trực tiếp
- 🟡 Kỹ thuật: Post trong #tech-discuss
- 🟢 Thảo luận: #general

**Tài liệu liên quan:**
- [JUNIOR-DEV-ONBOARDING-GUIDE.md](JUNIOR-DEV-ONBOARDING-GUIDE.md) - Chi tiết hơn (bản tiếng Anh)
- [GIT-WORKFLOW-CHEATSHEET.md](GIT-WORKFLOW-CHEATSHEET.md) - Tham khảo Git lệnh
- [TRELLO-SETUP-GUIDE.md](TRELLO-SETUP-GUIDE.md) - Hướng dẫn Trello chi tiết

**Đừng quên:**
> "The only way to learn programming is to program." - Anonymous

Chúc bạn học tập và làm việc vui vẻ! 🚀

---

**Phiên bản:** 1.0  
**Cập nhật:** 09/01/2026  
**Tác giả:** Team Lead  
**Feedback:** Post trong #general hoặc DM Lead

