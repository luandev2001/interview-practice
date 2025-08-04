# Bài toán thực tế: Hệ thống thanh toán tích hợp VNPay

## 🎯 Mục tiêu

Xây dựng hệ thống xử lý thanh toán từ người dùng và tích hợp cổng thanh toán bên thứ 3 như VNPay, ZaloPay,..., xử lý đảm
bảo dữ liệu lớn, an toàn, đồng bộ trạng thái giao dịch và tích hợp retry khi thanh toán thất bại.

**Lưu ý**: Đây là dự án test/demo nên một số tính năng được đơn giản hóa so với thực tế:
- `returnUrl` được xử lý trực tiếp thay vì redirect sang giao diện merchant
- IPN URL được xử lý trực tiếp trong hệ thống thay vì gọi từ VNPay
- Một số validation và security được đơn giản hóa

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

## 🔐 Kỹ thuật xử lý an toàn

- Dùng `@Transactional` đảm bảo rollback khi có lỗi
- Dùng `SELECT ... FOR UPDATE` hoặc `@Lock(PESSIMISTIC_WRITE)` để khoá dòng giao dịch khi xử lý callback → tránh double payment
- Dùng `UniqueConstraint` trên `merchantTxnId` để tránh insert trùng
- Dùng **Kafka** để đẩy trạng thái giao dịch ra hệ thống kế toán, báo cáo, hoặc đếm lượt thành công

---

## ✅ Test Case đã implement

- ✅ Tạo giao dịch hợp lệ → trả về URL VNPay
- ✅ VNPay callback thành công → cập nhật trạng thái `NETWORK_CONFIRMED`
- ✅ VNPay callback lặp lại → hệ thống vẫn **idempotent** (trả về OK, không thay đổi dữ liệu)
- ✅ **Integration Tests**: Test toàn bộ flow từ tạo payment đến callback