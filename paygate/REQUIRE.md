# Bài toán thực tế: Hệ thống thanh toán tích hợp VNPay

## 🎯 Mục tiêu

Xây dựng hệ thống xử lý thanh toán từ người dùng và tích hợp cổng thanh toán bên thứ 3 như VNPay, ZaloPay,..., xử lý đảm
bảo dữ liệu lớn, an toàn, đồng bộ trạng thái giao dịch và tích hợp retry khi thanh toán thất bại.

---

## ✅ Yêu cầu hệ thống

- Sử dụng **Spring Boot Web (blocking)** để đảm bảo xử lý chính xác, từng bước theo thứ tự.
- Tích hợp với VNPay:
    - Sinh URL thanh toán
    - Xử lý callback xác nhận kết quả thanh toán
- Lưu trạng thái giao dịch vào PostgreSQL
- Đảm bảo không tạo giao dịch trùng lặp, dùng lock pessimistic
- Retry thanh toán khi gặp lỗi kết nối VNPay

---

## 🧩 Thành phần chính

- `TransactionController`: tạo giao dịch, redirect user đến VNPay
- `PaymentCallbackController`: nhận callback từ VNPay
- `TransactionService`: thực hiện nghiệp vụ (create, update, validate)
- **PostgreSQL**:
    - Bảng `transactions` lưu giao dịch
    - Trường trạng thái: `PENDING`, `SUCCESS`, `FAILED`

---

## 🔐 Kỹ thuật xử lý an toàn

- Dùng `@Transactional` đảm bảo rollback khi có lỗi
- Dùng `SELECT ... FOR UPDATE` hoặc `@Lock(PESSIMISTIC_WRITE)` để khoá dòng giao dịch khi xử lý callback → tránh double
  payment
- Dùng `UniqueConstraint` trên `merchantTxnId` để tránh insert trùng
- Dùng **Kafka** để đẩy trạng thái giao dịch ra hệ thống kế toán, báo cáo, hoặc đếm lượt thành công

---

## ✅ Test Case gợi ý

- ✅ Tạo giao dịch hợp lệ → trả về URL VNPay
- ✅ VNPay callback thành công → cập nhật trạng thái `SUCCESS`
- ✅ VNPay callback lặp lại → hệ thống vẫn **idempotent** (trả về OK, không thay đổi dữ liệu)
- ❌ Giao dịch sai chữ ký → trả về 400, không cập nhật
- ⚠️ Tình huống mất kết nối VNPay → ghi lại lỗi, retry bằng cron job

---

## 🚀 Bonus nâng cao

- Kết hợp **Outbox Pattern** để gửi trạng thái giao dịch ra Kafka an toàn
- Dùng **Redis** để chống spam callback hoặc xử lý timeout
- Ghi **audit log toàn bộ luồng** bằng interceptor + Kafka
- Xây dựng **dashboard realtime** theo dõi trạng thái giao dịch bằng dữ liệu Kafka hoặc Redis

---

## 📌 Gợi ý thêm

- Ghi log đầy đủ các bước ra Sentry, ELK hoặc Datadog
- Có cơ chế alert nếu một giao dịch nằm ở `PENDING` quá lâu (>10 phút)
- Đảm bảo bảo mật callback bằng `secure hash`, IP whitelist hoặc token

