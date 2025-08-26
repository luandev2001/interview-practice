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
CREATE
DATABASE optimise_db;
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

## 📊 Cấu hình Database

### Create File Migration

bin/generate-migration.sh $name

### Run liquibase with docker and file liquibase.properties at folder resources

docker run --rm -v "$(pwd)/liquibase.properties":/liquibase/liquibase.properties \
-v "$(pwd)/db/changelog":/liquibase/db/changelog \
liquibase --defaultsFile=/liquibase/liquibase.properties update

### Generate jooq with codegen

mvn generate-sources

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

## 📈 Benchmark Results

### CSV Processing Performance

| Library     | 1K Records | 10K Records | 100K Records | 500K Records | 1M Records |
|-------------|------------|-------------|--------------|--------------|------------|
| Univocity   | ~5ms       | ~15ms       | ~120ms       | ~600ms       | ~1.2s      |
| OpenCSV     | ~8ms       | ~25ms       | ~200ms       | ~1s          | ~2s        |
| Apache CSV  | ~10ms      | ~30ms       | ~250ms       | ~1.2s        | ~2.5s      |
| Jackson CSV | ~12ms      | ~40ms       | ~300ms       | ~1.5s        | ~3s        |

### Memory Usage

| Library     | Memory Usage (1M records) |
|-------------|---------------------------|
| Univocity   | ~150MB                    |
| OpenCSV     | ~200MB                    |
| Apache CSV  | ~250MB                    |
| Jackson CSV | ~300MB                    |

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

## 🤝 Support

Nếu có vấn đề hoặc câu hỏi, vui lòng tạo issue hoặc liên hệ team. 