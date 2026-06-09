# 🚀 EduVerse - Hệ Sinh Thái Chợ Số & Mạng Xã Hội Học Tập Tích Hợp AI

**EduVerse** là một hệ sinh thái tri thức toàn diện được thiết kế dành riêng cho cộng đồng sinh viên và giới trẻ (Gen Z/Gen Y). Vượt qua mô hình của các kho lưu trữ tài liệu tĩnh truyền thống, EduVerse chuyển đổi tài liệu học tập số thành một **Sàn thương mại điện tử hoạt động theo mô hình Kinh tế chia sẻ (Sharing Economy)**, kết hợp với **Mạng xã hội tương tác thời gian thực** và **Hệ thống tự động ôn thi thông minh tích hợp AI**.

Hệ thống Backend được thiết kế và tối ưu hóa dựa trên nền tảng Java hiện đại, tập trung giải quyết các bài toán về xử lý đồng thời cao (High Concurrency), giao tiếp thời gian thực mượt mà và bảo đảm tính toàn vẹn tuyệt đối cho dữ liệu tài chính.

---

## 🎯 Các Phân Hệ & Tính Năng Cốt Lõi

### 1. EduMarket (Chợ Tài Liệu Số & Trò Chơi Hóa)
* **Kho tài liệu miễn phí (Free Hub):** Người dùng có thể xem và tải tài liệu miễn phí. Để kích thích đóng góp từ cộng đồng, hệ thống áp dụng cơ chế tự động trích xu thưởng (`EduCoin`) từ quỹ nền tảng để cộng cho tác giả dựa trên mỗi lượt tải hợp lệ.
* **Khu chợ cao cấp (Premium Market):** Nơi các học sinh, sinh viên xuất sắc có thể kinh doanh và kiếm tiền từ các bộ tài liệu chất lượng cao (đề cương tự soạn, slide bài giảng, bài tập lớn mẫu) bằng VND hoặc EduCoin.
* **Xem trước thông minh (Smart Preview):** Tự động băm file PDF khi upload để tạo bản xem trước 3 trang đầu tiên, ngăn chặn việc rò rỉ dữ liệu trước khi người dùng thực hiện giao dịch mua.

### 2. EduChat & Mạng Xã Hội Kết Nối Đồng Trang Lứa
* **Mạng lưới tương tác:** Cho phép sinh viên theo dõi (Follow) các tác giả uy tín hoặc các "thủ khoa" để nhận thông báo tức thời ngay khi họ có tài liệu mới.
* **Hệ thống Chat Real-time:** Hỗ trợ nhắn tin 1-1 trực tiếp giữa người mua và người bán, đồng thời tự động khởi tạo các **Nhóm học tập (Study Groups)** theo từng Trường và Môn học để sinh viên cùng thảo luận, ghim tài liệu hay trước mỗi mùa thi.

### 3. Ví Điện Tử & Hệ Thống Sổ Cái Chuẩn Fintech
* Quản lý toàn bộ dòng tiền nạp/rút tiền mặt (VND) và sự biến động của đồng xu nội bộ (`EduCoin`).
* Tích hợp cổng thanh toán trực tuyến qua mã **VietQR động** để tự động xử lý gạch nợ theo thời gian thực thông qua hệ thống Webhook an toàn.

### 4. AI-Quiz Generator (Hệ Thống Ôn Thi Thông Minh)
* Ứng dụng công nghệ AI để đọc hiểu và phân tích nội dung từ file tài liệu PDF do user cung cấp, từ đó tự động sinh ra các bộ đề thi trắc nghiệm ngẫu nhiên, hỗ trợ làm bài trực tuyến có đếm ngược thời gian, chấm điểm và giải thích chi tiết từng câu.

---

## 🛠️ Công Nghệ Sử Dụng (Tech Stack)

* **Backend Core:** Java 17, Spring Boot 3.x, Spring Data JPA.
* **Bảo mật (Security):** Spring Security, JWT (JSON Web Token), Google OAuth2.
* **Cơ sở dữ liệu (Database Engine):** PostgreSQL 16 (Cơ sở dữ liệu quan hệ chính đảm bảo tính ACID), MongoDB (Lưu trữ lịch sử chat hiệu năng cao).
* **Bộ nhớ đệm & Bộ môi giới message (Caching & Broker):** Redis 7 (Cache metadata, Rate Limiting, Pub/Sub để đồng bộ WebSocket), Kafka/RabbitMQ (Xử lý hàng đợi bất đồng bộ).
* **Lưu trữ phân tán:** MinIO / AWS S3 (Object Storage xử lý file qua cơ chế Presigned URL).
* **Công cụ DevOps:** Docker, Docker Compose, Flyway DB (Quản lý Version Migration Database).

---

## 💎 Điểm Nhấn Kỹ Thuật (Key Technical Highlights)

### 🏪 Giải Quyết Bài Toán Đồng Thời Cao & Chống Lỗi Race Condition (Optimistic Locking)
Để bảo vệ số dư tài khoản của người dùng khi có hàng ngàn sinh viên cùng truy cập mua tài liệu vào các mùa cao điểm (ví dụ: mùa thi cử), kiến trúc ví được thiết kế tách biệt hoàn toàn bảng `wallets` ra khỏi bảng `users`. Hệ thống áp dụng cơ chế **Optimistic Locking** (thông qua trường `@Version` của JPA) giúp xử lý các giao dịch tài chính một cách an toàn, nhất quán mà không làm block database hoặc gây nghẽn luồng xử lý của hệ thống.

### 🛡️ Thiết Kế Sổ Cái Chống Trùng Lặp Giao Dịch (Idempotent Ledger)
Mọi giao dịch tài chính liên quan đến nạp/rút tiền hoặc mua bán đều áp dụng cơ chế khóa **Idempotency Key** chặt chẽ. Đảm bảo các request hoặc webhook thanh toán bị gửi trùng lặp từ bên thứ ba (do lỗi mạng, kết nối chậm dẫn đến việc hệ thống tự gửi lại hoặc người dùng bấm double-click) chỉ được xác thực và xử lý chính xác **duy nhất một lần**, loại bỏ hoàn toàn rủi ro mất mát tiền bạc hoặc cộng trùng xu thưởng.

### ⚡ Kiến Trúc Bất Đồng Bộ Dựa Trên Sự Kiện Khi Tích Hợp AI (Event-Driven AI Integration)
Việc gọi API sang các mô hình ngôn ngữ lớn (LLM) để đọc hiểu file và sinh đề thi thường mất rất nhiều thời gian (từ 5 đến 15 giây). Để tránh tình trạng user bị block màn hình và server Java bị cạn kiệt luồng xử lý, EduVerse áp dụng **Kiến trúc hướng sự kiện với Message Queue (Kafka/RabbitMQ)**. Luồng xử lý chính của Spring Boot sẽ đẩy tác vụ nặng vào hàng đợi chỉ trong vài mili-giây và giải phóng Thread ngay lập tức (đạt tỷ lệ nghẽn Thread Starvation bằng 0%). Khi AI xử lý xong, kết quả sẽ được Worker tự động đẩy ngược lại màn hình Client thông qua **WebSockets**.

### 📉 Tối Ưu Hóa Băng Thông Server & Giảm Tải Database
* **Presigned URLs:** Ứng dụng Client sẽ truyền tải trực tiếp các file PDF dung lượng lớn lên thẳng Object Storage (S3/MinIO) thông qua một đường link tạm thời do Backend cấp, giúp bypass qua API Gateway và tiết kiệm đến 90% băng thông cho server.
* **Hierarchical Redis Caching:** Hệ thống danh mục trường học/môn học đa cấp và metadata của các tài liệu đang "hot trend" được lưu vào bộ nhớ đệm Redis, giảm thiểu tối đa các câu lệnh `JOIN` phức tạp xuống cơ sở dữ liệu PostgreSQL cốt lõi.

---

## 📅 Lộ Trình Phát Triển Dự Án (Project Roadmap)

- [x] **Giai đoạn 1:** Thiết lập hạ tầng Docker, cấu hình Postgres, cấu hình `application.yml` và khởi tạo hệ thống bảng tự động bằng Flyway.
- [ ] **Giai đoạn 2:** Phát triển Module Auth (Đăng ký/Đăng nhập với JWT, Google OAuth2) và Module ví tiền, xử lý giao dịch sử dụng Optimistic Locking.
- [ ] **Giai đoạn 3:** Xây dựng tính năng Quản lý, phân loại tài liệu, tích hợp upload file qua cơ chế Presigned URL và kết nối cổng thanh toán tự động qua Webhook.
- [ ] **Giai đoạn 4:** Triển khai luồng Chat nhóm và Chat 1-1 thời gian thực sử dụng Spring WebSocket kết hợp Redis Pub/Sub và lưu lịch sử chat vào MongoDB.
- [ ] **Giai đoạn 5:** Tích hợp Spring AI / OpenAI API để xây dựng worker xử lý đọc file sinh đề thi bất đồng bộ thông qua Message Queue.

---
*Dự án được xây dựng và phát triển theo tiêu chuẩn dự án Product thực tế.*
