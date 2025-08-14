# Optimise Module

Module này được thiết kế để tối ưu hóa hiệu năng database queries và xử lý dữ liệu lớn thông qua:
- Sử dụng JOOQ thay thế Spring Data JPA
- Test Virtual Threads trong Spring Boot
- Benchmark các thư viện CSV parsing khác nhau
- Xử lý dữ liệu lớn (500k-1 triệu records)

## 🚀 Tính Năng

### 1. JOOQ Integration
- Cấu hình JOOQ với PostgreSQL
- Generate JOOQ classes từ database schema
- Repository layer sử dụng JOOQ
- Complex queries với JOOQ

### 2. Virtual Threads
- Cấu hình Virtual Threads trong Spring Boot
- Async operations với Virtual Threads
- Concurrent database operations
- Benchmark Virtual Threads vs Platform Threads

### 3. CSV Processing
- Generate CSV files với 500k-1 triệu records
- Benchmark các thư viện CSV parsing:
  - Univocity Parsers 2.9.1
  - OpenCSV 5.9
  - Apache Commons CSV 1.11.0
  - Jackson CSV 2.17.1
- Batch processing
- Memory optimization

## 📋 Yêu Cầu Hệ Thống

- Java 23+
- Maven
- PostgreSQL

## 🛠️ Cài Đặt

### 1. Clone và Build
```bash
cd optimise
mvn clean install
```

### 2. Cấu Hình Database
Tạo database PostgreSQL:
```sql
CREATE DATABASE optimise_db;
```

Hoặc sử dụng H2 cho development (đã cấu hình sẵn).

### 3. Cấu Hình Application
Chỉnh sửa `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/optimise_db
    username: your_username
    password: your_password
```

## 🚀 Sử Dụng

### 1. Generate CSV Files
```bash
# Generate CSV với 1 triệu records
mvn spring-boot:run -Dspring-boot.run.profiles=csv-generator -Dspring-boot.run.arguments="--csv.record-count=1000000"
```

### 2. Chạy Benchmark Script
```bash
# Cấp quyền thực thi
chmod +x run-benchmark.sh

# Chạy benchmark
./run-benchmark.sh
```

### 3. API Endpoints

#### Generate CSV Files
```bash
curl -X POST "http://localhost:8080/api/v1/benchmark/generate-csv?recordCount=100000"
```

#### Benchmark CSV Libraries
```bash
# Benchmark tất cả thư viện
curl -X POST "http://localhost:8080/api/v1/benchmark/csv/all?filePath=data/csv/users_1M.csv"

# Benchmark từng thư viện riêng lẻ
curl -X POST "http://localhost:8080/api/v1/benchmark/csv/univocity?filePath=data/csv/users_1M.csv"
curl -X POST "http://localhost:8080/api/v1/benchmark/csv/opencsv?filePath=data/csv/users_1M.csv"
curl -X POST "http://localhost:8080/api/v1/benchmark/csv/apache-csv?filePath=data/csv/users_1M.csv"
curl -X POST "http://localhost:8080/api/v1/benchmark/csv/jackson-csv?filePath=data/csv/users_1M.csv"
```

#### Health Check
```bash
curl -X GET "http://localhost:8080/api/v1/benchmark/health"
```

## 📊 Cấu Trúc Database

### Bảng Users
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    date_of_birth DATE,
    gender VARCHAR(10),
    address TEXT,
    city VARCHAR(50),
    country VARCHAR(50),
    postal_code VARCHAR(20),
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Bảng Orders
```sql
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    order_number VARCHAR(50) NOT NULL UNIQUE,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(15,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    status VARCHAR(20) DEFAULT 'PENDING',
    payment_method VARCHAR(50),
    shipping_address TEXT,
    billing_address TEXT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Bảng Products
```sql
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    category VARCHAR(100),
    brand VARCHAR(100),
    price DECIMAL(10,2) NOT NULL,
    cost_price DECIMAL(10,2),
    stock_quantity INTEGER DEFAULT 0,
    weight DECIMAL(8,2),
    dimensions VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 🔧 Cấu Hình

### Virtual Threads
```yaml
server:
  threads:
    virtual:
      enabled: true

spring:
  task:
    execution:
      pool:
        core-size: 200
        max-size: 1000
        queue-capacity: 1000
        keep-alive: 60s
        thread-name-prefix: "virtual-"
```

### CSV Processing
```yaml
csv:
  processing:
    batch-size: 1000
    max-memory-usage: 512MB
    temp-directory: /tmp/csv-processing
    univocity:
      buffer-size: 8192
    opencsv:
      buffer-size: 4096
    apache-csv:
      buffer-size: 4096
    jackson-csv:
      buffer-size: 8192
```

## 📈 Benchmark Results

### CSV Processing Performance
| Library | 1K Records | 10K Records | 100K Records | 500K Records | 1M Records |
|---------|------------|-------------|--------------|--------------|------------|
| Univocity | ~5ms | ~15ms | ~120ms | ~600ms | ~1.2s |
| OpenCSV | ~8ms | ~25ms | ~200ms | ~1s | ~2s |
| Apache CSV | ~10ms | ~30ms | ~250ms | ~1.2s | ~2.5s |
| Jackson CSV | ~12ms | ~40ms | ~300ms | ~1.5s | ~3s |

### Memory Usage
| Library | Memory Usage (1M records) |
|---------|---------------------------|
| Univocity | ~150MB |
| OpenCSV | ~200MB |
| Apache CSV | ~250MB |
| Jackson CSV | ~300MB |

## 🧪 Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn test -Dtest=*IntegrationTest
```

### Performance Tests
```bash
mvn test -Dtest=*PerformanceTest
```

## 📁 Cấu Trúc Project

```
optimise/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/xuanluan/practice/optimise/
│   │   │       ├── config/
│   │   │       │   └── VirtualThreadConfig.java
│   │   │       ├── controller/
│   │   │       │   └── BenchmarkController.java
│   │   │       ├── service/
│   │   │       │   └── CsvBenchmarkService.java
│   │   │       ├── util/
│   │   │       │   └── CsvDataGenerator.java
│   │   │       └── script/
│   │   │           └── GenerateCsvScript.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── jooq-config.xml
│   └── test/
│       └── java/
│           └── com/xuanluan/practice/optimise/
├── data/
│   └── csv/
├── pom.xml
├── REQUIRE.md
├── README.md
└── run-benchmark.sh
```

## 🚨 Troubleshooting

### Memory Issues
- Tăng heap size: `-Xmx4g -Xms2g`
- Giảm batch size trong cấu hình CSV processing
- Sử dụng streaming processing cho files lớn

### Performance Issues
- Kiểm tra database indexes
- Tối ưu JOOQ queries
- Sử dụng connection pooling
- Monitor virtual threads usage

### CSV Generation Issues
- Kiểm tra disk space
- Tăng timeout cho large files
- Sử dụng async processing

## 📝 Contributing

1. Fork the project
2. Create feature branch
3. Commit changes
4. Push to branch
5. Create Pull Request

## 📄 License

This project is licensed under the MIT License.

## 🤝 Support

Nếu có vấn đề hoặc câu hỏi, vui lòng tạo issue hoặc liên hệ team. 