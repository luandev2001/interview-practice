# REQUIRE.md - Yêu Cầu Dự Án Optimise

## 1. Tổng Quan
Dự án này nhằm mục đích tối ưu hóa hiệu năng database queries và xử lý dữ liệu lớn thông qua:
- Sử dụng JOOQ thay thế Spring Data JPA
- Test Virtual Threads trong Spring Boot
- Benchmark các thư viện CSV parsing khác nhau
- Xử lý dữ liệu lớn (500k-1 triệu records)

## 2. Cấu Trúc Database

### 2.1 Bảng Users
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

### 2.2 Bảng Orders
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

### 2.3 Bảng Order_Items
```sql
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id),
    product_name VARCHAR(200) NOT NULL,
    product_code VARCHAR(50),
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(15,2) NOT NULL,
    category VARCHAR(100),
    brand VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 2.4 Bảng Products
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

## 3. Yêu Cầu Chức Năng

### 3.1 JOOQ Integration
- [ ] Cấu hình JOOQ với PostgreSQL
- [ ] Generate JOOQ classes từ database schema
- [ ] Tạo Repository layer sử dụng JOOQ
- [ ] Implement các query phức tạp với JOOQ
- [ ] So sánh hiệu năng với Spring Data JPA

### 3.2 Virtual Threads
- [ ] Cấu hình Virtual Threads trong Spring Boot
- [ ] Implement async operations với Virtual Threads
- [ ] Test concurrent database operations
- [ ] Benchmark Virtual Threads vs Platform Threads
- [ ] Monitor memory usage và performance

### 3.3 CSV Processing
- [ ] Generate CSV files với 500k-1 triệu records
- [ ] Implement CSV parsing với các thư viện:
  - Univocity Parsers 2.9.1
  - OpenCSV 5.9
  - Apache Commons CSV 1.11.0
- [ ] Benchmark performance của từng thư viện
- [ ] Implement batch processing
- [ ] Memory optimization cho large files

### 3.4 Query Optimization
- [ ] Implement complex queries với JOOQ
- [ ] Pagination và sorting optimization
- [ ] Index optimization
- [ ] Query caching strategies
- [ ] Connection pooling optimization

## 4. Dữ Liệu Test

### 4.1 Users CSV Structure
```csv
id,username,email,full_name,phone,date_of_birth,gender,address,city,country,postal_code,registration_date,last_login,is_active,status
1,john_doe,john@example.com,John Doe,+1234567890,1990-01-15,MALE,123 Main St,New York,USA,10001,2023-01-01 10:00:00,2024-01-15 14:30:00,true,ACTIVE
```

### 4.2 Orders CSV Structure
```csv
id,user_id,order_number,order_date,total_amount,currency,status,payment_method,shipping_address,billing_address,notes
1,1,ORD-2024-001,2024-01-15 10:30:00,299.99,USD,COMPLETED,CREDIT_CARD,123 Main St New York,123 Main St New York,Standard delivery
```

### 4.3 Products CSV Structure
```csv
id,name,code,description,category,brand,price,cost_price,stock_quantity,weight,dimensions,is_active
1,MacBook Pro 16",MBP16,High-performance laptop,Electronics,Apple,2499.99,1800.00,50,2.1,14.2x9.8x0.6,true
```

## 5. Performance Benchmarks

### 5.1 Database Query Performance
- [ ] Simple SELECT queries (1k, 10k, 100k records)
- [ ] Complex JOIN queries
- [ ] Aggregation queries (GROUP BY, COUNT, SUM)
- [ ] Pagination performance
- [ ] Index vs No-Index performance

### 5.2 CSV Processing Performance
- [ ] Read time cho files 500k, 1M records
- [ ] Memory usage comparison
- [ ] CPU usage comparison
- [ ] Batch processing performance
- [ ] Error handling performance

### 5.3 Virtual Threads Performance
- [ ] Concurrent request handling
- [ ] Database connection efficiency
- [ ] Memory usage vs Platform Threads
- [ ] Throughput comparison
- [ ] Latency comparison

## 6. Monitoring và Metrics

### 6.1 Application Metrics
- [ ] Response time metrics
- [ ] Throughput metrics
- [ ] Error rate metrics
- [ ] Memory usage metrics
- [ ] CPU usage metrics

### 6.2 Database Metrics
- [ ] Query execution time
- [ ] Connection pool usage
- [ ] Cache hit/miss ratio
- [ ] Index usage statistics
- [ ] Lock wait time

## 7. Testing Strategy

### 7.1 Unit Tests
- [ ] Repository layer tests
- [ ] Service layer tests
- [ ] CSV processing tests
- [ ] Query optimization tests

### 7.2 Integration Tests
- [ ] Database integration tests
- [ ] CSV file processing tests
- [ ] Virtual threads tests
- [ ] Performance tests

### 7.3 Load Tests
- [ ] Concurrent user simulation
- [ ] Large dataset processing
- [ ] Memory leak detection
- [ ] Performance degradation tests

## 8. Deployment và Configuration

### 8.1 Environment Configuration
- [ ] Development environment
- [ ] Test environment
- [ ] Production environment
- [ ] Database configuration
- [ ] JOOQ configuration

### 8.2 Monitoring Setup
- [ ] Application monitoring
- [ ] Database monitoring
- [ ] Performance monitoring
- [ ] Alert configuration

## 9. Deliverables

### 9.1 Code
- [ ] JOOQ repository implementations
- [ ] Virtual threads configuration
- [ ] CSV processing services
- [ ] Performance optimization code
- [ ] Test suites

### 9.2 Documentation
- [ ] API documentation
- [ ] Performance benchmark reports
- [ ] Configuration guides
- [ ] Best practices documentation

### 9.3 Data
- [ ] Generated CSV files (500k-1M records)
- [ ] Database migration scripts
- [ ] Sample data sets
- [ ] Performance test data

## 10. Success Criteria

### 10.1 Performance Targets
- [ ] JOOQ queries 20% faster than JPA
- [ ] Virtual threads handle 2x more concurrent requests
- [ ] CSV processing under 30 seconds for 1M records
- [ ] Memory usage optimized by 30%

### 10.2 Quality Targets
- [ ] 90% code coverage
- [ ] Zero memory leaks
- [ ] All tests passing
- [ ] Documentation complete

### 10.3 Scalability Targets
- [ ] Support 10k concurrent users
- [ ] Process 1M records efficiently
- [ ] Handle database with 10M+ records
- [ ] Maintain performance under load 