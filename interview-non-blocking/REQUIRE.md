# Các công nghệ được dùng
## Công nghệ
- Spring Boot (Reactive)
- PostgreSQL
- Redis
- Kafka
- Elasticsearch


# Bài toán thực tế: Hệ thống phân tích hành vi người dùng real-time cho một nền tảng thương mại điện tử
## Mô tả yêu cầu
- Một nền tảng thương mại điện tử muốn xây dựng hệ thống phân tích hành vi người dùng theo thời gian thực, phục vụ các mục tiêu:

- Ghi nhận mọi tương tác của người dùng trên web/app (xem sản phẩm, tìm kiếm, thêm vào giỏ, thanh toán...)

- Hiển thị sản phẩm liên quan / cá nhân hóa đề xuất

- Cung cấp dashboard real-time cho admin để xem xu hướng sản phẩm, từ khóa tìm kiếm phổ biến, top category,...

- Hệ thống phải có khả năng scale lớn, xử lý hàng triệu sự kiện/ngày

## Chi tiết xử lý
1. Ghi nhận hành vi người dùng
- Mỗi khi user click/sự kiện xảy ra → gọi API POST /user-events (non-blocking).

- Dữ liệu gửi lên bao gồm: user_id, timestamp, event_type, product_id, search_term,...

- Spring WebFlux xử lý sự kiện này → push vào Kafka topic user-events.

2. Xử lý bất đồng bộ với Kafka
- Consumer (Spring Boot app) lắng nghe topic user-events:

- Lưu event vào PostgreSQL để audit/log.

- Gửi dữ liệu vào Elasticsearch cho việc truy vấn (top sản phẩm, từ khóa...).

- Tăng counter trong Redis (VD: view_count:product:123).

* Gợi ý:

- Dùng batch consumer (@KafkaListener(batch = true))

- Sử dụng thread pool cho việc xử lý song song

- Dùng Spring Retry cho lỗi tạm thời khi ghi xuống Elasticsearch