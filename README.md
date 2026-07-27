# Hệ Thống Quản Lý Nguồn Vốn, Hợp Đồng Vay và Tài Sản Bảo Đảm

## Tổng Quan Dự Án

Hệ thống quản lý nguồn vốn, hợp đồng vay và tài sản bảo đảm là một nền tảng toàn diện để quản lý các hoạt động tài chính của tổ chức. Hệ thống hỗ trợ quản lý đối tác, hạn mức tín dụng, hợp đồng vay, khế ước nhận nợ (KUNN), dư nợ, tài sản bảo đảm và tính lãi cuối ngày.

## Công Nghệ Sử Dụng

### Backend
- **Ngôn ngữ**: Java 21
- **Framework**: Spring Boot 3.5.16
- **Database**: PostgreSQL 14+
- **Cache**: Redis
- **Storage**: MinIO (Cloud Storage)
- **Authentication**: JWT (JSON Web Token)
- **API Documentation**: OpenAPI/Swagger

### Các Dependency Chính
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.6</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    <dependency>
        <groupId>io.minio</groupId>
        <artifactId>minio</artifactId>
        <version>8.5.11</version>
    </dependency>
</dependencies>
```

## Cấu Trúc Dự Án

```
src/main/java/vix/local/api/
├── ApiApplication.java
├── modules/
│   ├── audit/              # Audit trail management
│   ├── capital_source/     # Capital source management
│   ├── document/           # Document management
│   ├── hr/                 # Human resources
│   ├── identity/           # Identity and access management
│   ├── permission/         # Permission management
│   └── worker/             # Worker/employee management
└── shared/                 # Shared components
```

## Các Module Chính

### 1. Quản Lý Đối Tác (Partner Management)
- Quản lý thông tin chung của đối tác
- Uỷ quyền và người đại diện pháp luật
- Thông tin ngân hàng
- Quản lý hợp đồng
- Quản lý hạn mức
- Danh mục tài sản bảo đảm

### 2. Quản Lý Hạn Mức (Limit Management)
- Thiết lập nhiều hạn mức cho đối tác
- Kiểm tra hạn mức khả dụng
- Điều chỉnh tăng giảm hạn mức
- Theo dõi tổng hạn mức đã sử dụng và còn lại

### 3. Quản Lý Hợp Đồng Vay (Loan Contract Management)
- Tạo hợp đồng vay gắn với đối tác và hạn mức
- Quản lý các khế ước nhận nợ (KUNN)
- Kiểm tra hạn mức khi tạo KUNN
- Theo dõi trạng thái hợp đồng (Pending, Open, Closed, Overdue)

### 4. Tài Sản Bảo Đảm (Asset Management)
- Ghi nhận tài sản mua và cầm cố/thế chấp
- Quản lý tỷ lệ khấu trừ (Haircut)
- Trạng thái tài sản (Available, Pledged, Released, Sold)
- Kiểm tra danh mục tài sản khi cầm cố

### 5. Batch Processing (EOD - End of Day)
- Tính lãi cuối ngày
- Cập nhật trạng thái Open/Closed/Overdue
- Phát cảnh báo tự động
- Quản lý ngày nghỉ và quy tắc dồn lãi

## Các Trạng Thái Chính

| Đối tượng | Trạng thái | Ý nghĩa |
|-----------|------------|---------|
| Đối tác | Active/Inactive | Được phép hoặc không được phép sử dụng |
| Hạn mức | Pending/Active/Expired/Closed | Vòng đời hạn mức |
| Hợp đồng vay | Pending/Open/Closed/Overdue | Quản lý hợp đồng và tình trạng dư nợ |
| KUNN | Pending/Open/Partially Paid/Closed/Overdue/Cancelled | Trạng thái khế ước nhận nợ |
| Tài sản bảo đảm | Available/Pledged/Partially Released/Released/Sold | Trạng thái tài sản |

## Cấu Hình Hệ Thống

### Danh mục cấu hình chính:
- **Loại giao dịch**: BR, TD, CD, GOVI, Bond, CP, CASA, FX, INT, Transaction, IVT, AVG, LOCK, BUY, SELL, REPO, DER, OTHER
- **Đơn vị tiền tệ**: VND, USD, CP
- **Mục đích vay**: Invest, Gapping, GovBond, Margin, Stock, Repo, CASA-TU, CASA-CK, loan_purpose
- **Loại hình KH**: Cá nhân trong nước, cá nhân nước ngoài, tổ chức trong nước, tổ chức NN
- **Loại hình kinh tế**: Cá nhân, ngân hàng, CT bảo hiểm, quỹ đầu tư, CT tài chính, tổ chức khác

## Cấu Hình Ứng Dụng

### application.properties
```properties
spring.application.name=api
server.port=8888

# DataSource
spring.datasource.url=jdbc:postgresql://localhost:5432/vix
spring.datasource.username=postgres
spring.datasource.password=thanh48

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# JWT
jwt.secret=VixLocalSecretKeyMustBe512BitsLongForHS512AlgorithmSecurity2025!
jwt.access-token-expiration=86400000
jwt.refresh-token-expiration=604800000

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379

# MinIO
minio.endpoint=http://localhost:9000
minio.access-key=minioadmin
minio.secret-key=minioadmin
minio.bucket=vix-documents

# Mail
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=
spring.mail.password=

# Scheduler
spring.task.scheduling.pool.size=5

# OpenAPI
springdoc.swagger-ui.enabled=true
springdoc.api-docs.path=/v3/api-docs

# Frontend URL
app.frontend.url=http://localhost:3000
app.api.url=http://localhost:8888
```

## Tính Năng Chính

1. **Quản lý đối tác và thông tin pháp lý**
2. **Thiết lập hạn mức tín dụng cho đối tác**
3. **Quản lý hợp đồng vay và khế ước nhận nợ (KUNN)**
4. **Quản lý tài sản bảo đảm và cầm cố**
5. **Tính lãi cuối ngày (EOD batch processing)**
6. **Tra cứu báo cáo quản trị**
7. **Quản lý người dùng và phân quyền**
8. **Hệ thống audit trail**
9. **Quản lý tài liệu và lưu trữ**

## Hướng Dẫn Chạy Dự Án

### Yêu cầu hệ thống:
- Java 21
- PostgreSQL 14+
- Redis 6+
- MinIO (để lưu trữ tài liệu)

### Cài đặt và chạy:
```bash
# Clone repo
git clone <repository-url>

# Build project
./mvnw clean package

# Run application
./mvnw spring-boot:run
```

## API Documentation

Hệ thống cung cấp Swagger UI để xem và test API:
- URL: `http://localhost:8888/swagger-ui.html`
- API Docs: `http://localhost:8888/v3/api-docs`

## Bảo Mật

- Authentication sử dụng JWT
- Authorization với các role và permission
- Secure configuration cho database và external services
- Audit trail cho tất cả các thao tác quan trọng

## Mở Rộng

Hệ thống được thiết kế theo mô-đun để dễ dàng mở rộng:
- Có thể thêm module mới cho các chức năng đặc thù
- Hỗ trợ modular architecture với Spring Modulith
- Hệ thống có thể tích hợp với các hệ thống khác thông qua REST API