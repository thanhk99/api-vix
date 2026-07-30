# VIX Local — Internal Company Platform API

> **Nền tảng nội bộ dùng chung toàn công ty.** Mỗi phòng ban là một module độc lập với schema riêng trong cùng một PostgreSQL database.

---

## 📑 Mục lục

- [Tổng quan hệ thống](#tổng-quan-hệ-thống)
- [Kiến trúc tổng thể](#kiến-trúc-tổng-thể)
- [Tech Stack](#tech-stack)
- [Cấu trúc thư mục](#cấu-trúc-thư-mục)
- [Core: Multi-Tenant theo Schema](#core-multi-tenant-theo-schema)
- [Core: Authentication & Authorization](#core-authentication--authorization)
- [Core: Background Worker System](#core-background-worker-system)
- [Core: Audit Logging (AOP)](#core-audit-logging-aop)
- [Core: Redis Cache](#core-redis-cache)
- [Core: File Storage (MinIO)](#core-file-storage-minio)
- [Modules](#modules)
- [Database Layout](#database-layout)
- [API Conventions](#api-conventions)
- [Cách chạy dự án](#cách-chạy-dự-án)
- [Cấu hình môi trường](#cấu-hình-môi-trường)

---

## Tổng quan hệ thống

VIX Local là hệ thống backend nội bộ phục vụ **toàn thể công ty**, được xây dựng theo mô hình **Modular Monolith** với kiến trúc **DDD Layered Architecture**. Mỗi phòng ban tương ứng với một **module Spring Boot** và sử dụng một **PostgreSQL schema riêng biệt**, đảm bảo tách biệt dữ liệu hoàn toàn mà không cần nhiều database.

```
Công ty
├── Phòng HR            → module: hr              → schema: shared (dùng chung)
├── Phòng Kế toán       → module: capital_source  → schema: shared (dùng chung)
├── Phòng Tài liệu      → module: document        → schema: shared
├── Quản lý quyền       → module: permission      → schema: shared
└── Hệ thống nội lõi    → module: identity, audit, worker
```

---

## Kiến trúc tổng thể

```
┌─────────────────────────────────────────────────────────────────────┐
│                         CLIENT LAYER                                 │
│  Company Local + Login Portal  ←→  Department UI                    │
└────────────────────────┬────────────────────────────────────────────┘
                         │ HTTP / REST
┌────────────────────────▼────────────────────────────────────────────┐
│                       BACKEND LAYER                                  │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │                      API GATEWAY                             │    │
│  │   Route → JWT Filter → TenantContext → Cache (Redis)        │    │
│  └──────────────────────────┬──────────────────────────────────┘    │
│                             │                                        │
│  ┌──────────────────────────▼──────────────────────────────────┐    │
│  │       Authentication Service (Role + Department)             │    │
│  │  Login → selectDepartment → JWT(schemaTarget) → Routing     │    │
│  └──────────────────────────┬──────────────────────────────────┘    │
│                             │                                        │
│  ┌──────────────────────────▼──────────────────────────────────┐    │
│  │                    Service / API Layer                        │    │
│  │         Publish Events → Domain Business Logic               │    │
│  └──────────────────────────┬──────────────────────────────────┘    │
│                             │                                        │
│  ┌──────────────────────────▼──────────────────────────────────┐    │
│  │             Infrastructure (Document Module)                 │    │
│  │              MinIO File Storage + JPA Repositories           │    │
│  └──────────────────────────┬──────────────────────────────────┘    │
└────────────────────────────-┼───────────────────────────────────────┘
                              │
┌─────────────────────────────▼───────────────────────────────────────┐
│                       DATABASE LAYER                                  │
│   Database DB (module schemas) | Log DB (audit) | Shared DB         │
└──────────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────────┐
│                    BACKGROUND WORKER (Redis + Scheduled)              │
│   Export Worker | Import Worker | Cleanup Worker | Mail Worker        │
│                         → File Storage (MinIO)                        │
└──────────────────────────────────────────────────────────────────────┘
```

---

## Tech Stack

| Thành phần       | Công nghệ                              | Phiên bản  |
|-----------------|----------------------------------------|------------|
| Runtime          | Java                                   | 21         |
| Framework        | Spring Boot                            | 3.5.16     |
| Modular System   | Spring Modulith                        | 1.4.12     |
| Database         | PostgreSQL                             | latest     |
| ORM              | Spring Data JPA / Hibernate            | (Boot BOM) |
| Security         | Spring Security + JWT (JJWT)           | 0.12.6     |
| Cache            | Spring Data Redis                      | (Boot BOM) |
| File Storage     | MinIO (S3-compatible)                  | 8.5.11     |
| Mail             | Spring Boot Mail (SMTP)                | (Boot BOM) |
| API Docs         | SpringDoc OpenAPI (Swagger UI)         | 2.8.16     |
| UUID             | uuid-creator (UUIDv7)                  | 6.0.0      |
| Boilerplate      | Lombok                                 | (Boot BOM) |
| Build Tool       | Maven                                  | (wrapper)  |
| AI (future)      | Spring AI BOM                          | 1.1.8      |

---

## Cấu trúc thư mục

```
src/main/java/vix/local/api/
├── ApiApplication.java                        # Entry point
│
├── shared/                                    # Shared kernel — dùng chung toàn hệ thống
│   ├── security/
│   │   ├── SecurityConfig.java                # Spring Security filter chain, CORS
│   │   ├── JwtUtil.java                       # Tạo/xác thực JWT, nhúng schemaTarget
│   │   └── JwtAuthenticationFilter.java       # Interceptor: parse JWT → set TenantContext
│   ├── tenant/
│   │   ├── TenantContext.java                 # ThreadLocal chứa schema hiện tại
│   │   ├── TenantIdentifierResolver.java      # Hibernate: resolve schema từ TenantContext
│   │   ├── SchemaMultiTenantConnectionProvider.java  # Hibernate: switch schema trên Connection
│   │   ├── TenantRoutingDataSource.java       # DataSource routing
│   │   └── TenantSchemaInterceptor.java       # Interceptor bổ sung
│   ├── cache/
│   │   └── RedisConfig.java                   # Cấu hình Redis Cache + Template
│   ├── audit/
│   │   └── AuditLoggingAspect.java            # AOP: tự động ghi audit log mọi REST call
│   ├── config/
│   │   ├── AsyncConfig.java                   # Thread pool async
│   │   └── WebConfig.java                     # CORS, WebMvc config
│   ├── dto/                                   # Shared DTO (ApiResponse, PageResponse…)
│   └── exception/                             # Global exception handler
│
└── modules/                                   # Business modules (1 module = 1 phòng ban)
    ├── identity/                              # Module xác thực người dùng
    ├── hr/                                    # Module Nhân sự
    ├── permission/                            # Module phân quyền RBAC
    ├── document/                              # Module tài liệu nội bộ
    ├── audit/                                 # Module audit log
    ├── capital_source/                        # Module nguồn vốn / kế toán
    └── worker/                                # Background Worker system
```

Mỗi module có cấu trúc DDD 4 tầng:

```
modules/<tên_module>/
├── api/                  # REST Controllers, Request/Response DTOs
│   └── v1/
│       ├── XxxController.java
│       └── dto/
├── application/          # Use Cases, Application Services
│   ├── service/
│   ├── mapper/
│   └── port/             # Interface định nghĩa contract với lớp ngoài
├── domain/               # Domain Model, Business Rules (không phụ thuộc framework)
│   ├── model/            # Aggregate Root, Entity, Value Object
│   ├── repository/       # Repository interface (domain contract)
│   └── exception/        # Domain-specific exceptions
└── infrastructure/       # JPA Entity, Repository Impl, External Services
    └── persistence/
        ├── XxxEntity.java
        ├── XxxJpaRepository.java
        └── XxxRepositoryImpl.java
```

---

## Core: Multi-Tenant theo Schema

> **Đây là cơ chế cốt lõi nhất của hệ thống.** Mỗi phòng ban dùng một PostgreSQL schema riêng, tất cả trên cùng một database `vix`.

### Luồng hoạt động

```
1. Client gửi request với JWT (chứa claim: schemaTarget)
          ↓
2. JwtAuthenticationFilter.doFilterInternal()
   → Giải mã JWT
   → TenantContext.setSchema(schemaTarget)    ← Set vào ThreadLocal
          ↓
3. Spring Security xác thực user
          ↓
4. Service/Repository gọi Hibernate
          ↓
5. TenantIdentifierResolver.resolveCurrentTenantIdentifier()
   → Đọc TenantContext.getSchema()            ← Lấy từ ThreadLocal
   → Trả về tên schema (e.g., "shared", "hr", "finance")
          ↓
6. SchemaMultiTenantConnectionProvider.getConnection(tenantId)
   → connection.setSchema(tenantIdentifier)   ← Switch schema trên JDBC Connection
          ↓
7. Hibernate thực thi SQL đúng schema
          ↓
8. Sau request: TenantContext.clear()          ← Xóa ThreadLocal
```

### Các file liên quan

| File | Vai trò |
|------|---------|
| `TenantContext.java` | ThreadLocal lưu schema name cho request hiện tại |
| `TenantIdentifierResolver.java` | Hibernate SPI — resolve schema từ TenantContext, default = `"shared"` |
| `SchemaMultiTenantConnectionProvider.java` | Hibernate SPI — thực sự switch schema trên JDBC Connection |
| `JwtAuthenticationFilter.java` | Set TenantContext từ JWT claim `schemaTarget` |

### Cấu hình Hibernate

```properties
# application.properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.hbm2ddl.create_namespaces=true   # Tự tạo schema nếu chưa có
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

---

## Core: Authentication & Authorization

### Luồng đăng nhập đa phòng ban

```
POST /v1/identity/auth/login
       ↓
1. Xác thực email + password (BCrypt)
2. Kiểm tra UserStatus = ACTIVE
3. Load danh sách UserDepartment (user có thể thuộc nhiều phòng ban)
4. Kịch bản:
   ├── 1 phòng ban  → Tự động cấp JWT, route = dept.code.toLowerCase()
   ├── > 1 phòng ban → Trả danh sách departments, yêu cầu chọn
   └── 0 phòng ban + SUPER_ADMIN → JWT với schema = "shared"

POST /v1/identity/auth/select-department   (khi user có nhiều phòng ban)
       ↓
Cấp JWT mới với schemaTarget = schema phòng ban đã chọn
```

### Cấu trúc JWT

JWT Access Token chứa các claims:

| Claim | Kiểu | Mô tả |
|-------|------|-------|
| `sub` | String | Email người dùng |
| `userId` | UUID | ID người dùng |
| `roles` | List\<String\> | Danh sách role (ADMIN, MEMBER, SUPER_ADMIN…) |
| `deptId` | UUID | ID phòng ban đang active |
| `schemaTarget` | String | Schema DB cần kết nối (`"shared"`, `"hr"`…) |

**Token expiry:**
- Access Token: 86,400,000 ms = **24 giờ**
- Refresh Token: 604,800,000 ms = **7 ngày**

### Phân quyền (RBAC)

Module `permission` cung cấp hệ thống RBAC theo nhóm quyền:

```
RoleGroup  (Nhóm quyền: "Manager", "Accountant"…)
    ↓
RoleGroupPermission  (ResourceCode + ActionCode)
    ↓
UserRoleGroup  (User → thuộc nhóm quyền nào)
```

**ResourceCode** (tài nguyên được phân quyền):

| Resource | Hành động được phép |
|----------|-------------------|
| `DASHBOARD` | VIEW |
| `DOCUMENT` | VIEW, CREATE, UPDATE, DELETE |
| `AUDIT_LOG` | VIEW, EXPORT |
| `HR_USER` | VIEW, CREATE, UPDATE, DELETE |
| `HR_DEPARTMENT` | VIEW, CREATE, UPDATE, DELETE |
| `REPORT` | VIEW, EXPORT, APPROVE |
| `PAYROLL` | VIEW, CREATE, UPDATE, APPROVE |
| `MEETING` | VIEW, CREATE, UPDATE, DELETE, APPROVE |
| `MANAGE_ROLE_GROUP` | VIEW, CREATE, UPDATE, DELETE |

**Spring Security** sử dụng `@EnableMethodSecurity` — dùng `@PreAuthorize("hasRole('ADMIN')")` trên từng endpoint.

---

## Core: Background Worker System

Hệ thống worker nội bộ chạy **không cần message broker bên ngoài** (Kafka/RabbitMQ). Thay vào đó dùng **PostgreSQL** làm job queue và **Spring Scheduler** để polling.

### Mô hình

```
Business Service
    ↓ Enqueue job (INSERT into worker_jobs table)
WorkerJobRepository.save(job)

    ↓ Scheduled polling (every N seconds)
Worker (MailWorker / CleanupWorker / …)
    ↓
1. findPendingJobs(type, now, limit)
2. job.markProcessing()
3. Xử lý business logic
4. job.markCompleted()  hoặc  job.markFailed(error, maxRetries)
5. save(job)
```

### Worker hiện có

| Worker | Schedule | Chức năng |
|--------|----------|-----------|
| `MailWorker` | Mỗi 10 giây | Gửi email hàng loạt từ job queue |
| `CleanupWorker` | Cron định kỳ | Dọn dẹp file/data tạm thời |
| *(Export Worker)* | Theo kế hoạch | Export báo cáo → MinIO |
| *(Import Worker)* | Theo kế hoạch | Import dữ liệu hàng loạt |

### Retry Strategy (Backoff)

```
retryCount++;
if (retryCount >= maxRetries) {
    status = "FAILED"           // Hết lần retry
} else {
    status = "PENDING"
    nextRunTime = now + (5 * retryCount) minutes  // Exponential-like backoff
}
```

**Trạng thái job:** `PENDING` → `PROCESSING` → `COMPLETED` | `FAILED`

### Domain Model: WorkerJob

| Field | Kiểu | Mô tả |
|-------|------|-------|
| `id` | UUID | ID job |
| `jobType` | String | `MAIL`, `CLEANUP`, `EXPORT`, `IMPORT` |
| `payload` | String | JSON data cho job |
| `status` | String | PENDING / PROCESSING / COMPLETED / FAILED |
| `retryCount` | int | Số lần đã retry |
| `errorLog` | String | Log lỗi cuối |
| `nextRunTime` | LocalDateTime | Thời điểm retry tiếp theo |

---

## Core: Audit Logging (AOP)

Mọi REST API call thành công đều được **tự động ghi audit log** thông qua Spring AOP — không cần developer thêm code thủ công.

### Cơ chế

```java
// AuditLoggingAspect.java
@Pointcut("within(@RestController *) && execution(* vix.local.api.modules..api..*(..))")
// Intercept tất cả controller trong /modules/**/api/

@AfterReturning   // Chỉ log khi KHÔNG có exception
public void logAfter(JoinPoint joinPoint) {
    // Thu thập: HTTP Method + URI, email user, departmentId, IP address
    // Gọi: auditService.logAsync(logData)   ← Async, không block request
}
```

### Thông tin được ghi lại

| Trường | Nguồn |
|--------|-------|
| `action` | `HTTP_METHOD + URI` |
| `module` | Tên class Controller |
| `description` | Tên method được gọi |
| `performedBy` | Email từ SecurityContext |
| `departmentId` | Header `X-Department-Id` |
| `ipAddress` | `request.getRemoteAddr()` |

---

## Core: Redis Cache

Redis phục vụ 2 mục đích chính:

### 1. Cache Layer (Spring Cache)

| Cache Name | TTL | Nội dung |
|------------|-----|---------|
| `departmentPermissions` | 30 phút | Danh sách quyền của phòng ban |
| `tokenBlacklist` | 7 ngày | Token đã bị revoke (logout) |
| *(default)* | 1 giờ | Cache chung cho các query |

**Serialization:** JSON (GenericJackson2JsonRedisSerializer) — tương thích đa ngôn ngữ.

### 2. Worker Coordination

Worker polling qua PostgreSQL `worker_jobs` table. Redis dùng để cache metadata và tránh xử lý trùng lặp job giữa các thread.

---

## Core: File Storage (MinIO)

MinIO được dùng như **S3-compatible object storage** để lưu tài liệu nội bộ và file export/import.

| Cấu hình | Giá trị mặc định |
|----------|-----------------|
| Endpoint | `http://localhost:9000` |
| Bucket | `vix-documents` |
| Access Key | `minioadmin` |

**Sử dụng trong modules:**
- `document` module: upload/download tài liệu nội bộ
- Worker → Export Worker: ghi file báo cáo vào MinIO

---

## Modules

### `identity` — Xác thực hệ thống

Quản lý user, đăng nhập, cấp JWT. Endpoint `/v1/identity/auth/**` là public (không cần JWT).

**Domain Models:**
- `User` — email, fullName, passwordHash, status (ACTIVE/INACTIVE)
- `UserDepartment` — Mapping user ↔ phòng ban + role (1 user có thể ở nhiều phòng ban)
- `UserRole` — Enum: ADMIN, MEMBER, SUPER_ADMIN…
- `UserStatus` — Enum: ACTIVE, INACTIVE, PENDING…

---

### `hr` — Nhân sự

Quản lý phòng ban và nhân viên toàn công ty.

**Domain Models:**
- `HrDepartment` — name, code, managerId (trưởng phòng), status
- `HrUser` — Hồ sơ nhân sự (thông tin cá nhân, vị trí, phòng ban)
- `HrPosition` — Chức danh/vị trí
- `Gender` — Enum giới tính

---

### `permission` — Phân quyền RBAC

Hệ thống phân quyền dựa trên nhóm quyền (Role Group).

**Domain Models:**
- `RoleGroup` — Nhóm quyền (Manager, Staff, Viewer…)
- `RoleGroupPermission` — ResourceCode + ActionCode cho nhóm
- `UserRoleGroup` — Gắn user vào nhóm quyền
- `ResourceCode` — Enum tài nguyên được phân quyền
- `ActionCode` — Enum: VIEW, CREATE, UPDATE, DELETE, APPROVE, EXPORT

---

### `document` — Tài liệu nội bộ

Quản lý tài liệu, lưu trữ file lên MinIO.

**Domain Models:**
- `Document` — Metadata tài liệu (tên, đường dẫn MinIO, loại file)

---

### `audit` — Audit Log

Lưu trữ lịch sử thao tác của người dùng. Được ghi **bất đồng bộ** qua `AuditLoggingAspect`.

**Domain Models:**
- `AuditLog` — action, module, performedBy, departmentId, ipAddress

---

### `capital_source` — Nguồn vốn

Module nghiệp vụ cho phòng kế toán/tài chính.

**Domain Models:**
- `Category` — Danh mục nguồn vốn
- `CategoryGroup` — Nhóm danh mục
- `CategoryStatus` — Enum trạng thái (ACTIVE / INACTIVE)

---

### `worker` — Background Jobs

Hệ thống xử lý tác vụ nền (mail, cleanup, export, import).

**Domain Models:**
- `WorkerJob` — Job entity với retry logic tích hợp sẵn (markProcessing, markCompleted, markFailed)

---

## Database Layout

```
PostgreSQL Database: vix
│
├── Schema: shared          (default — dùng chung cho tất cả module hiện tại)
│   ├── users
│   ├── user_departments
│   ├── hr_departments
│   ├── hr_users
│   ├── hr_positions
│   ├── role_groups
│   ├── role_group_permissions
│   ├── user_role_groups
│   ├── documents
│   ├── audit_logs
│   ├── worker_jobs
│   └── capital_source_categories
│
└── Schema: <dept_code>     (schema riêng cho từng phòng ban trong tương lai)
    └── ... (các bảng nghiệp vụ riêng của phòng ban)
```

> **Lưu ý:** Hiện tại `schemaTarget` trong JWT đang được set là `"shared"` cho tất cả phòng ban. Hệ thống đã sẵn sàng để chuyển sang schema riêng (`"hr"`, `"finance"`…) chỉ bằng cách thay đổi giá trị này — không cần sửa code.

---

## API Conventions

### Base URL

```
http://localhost:8888
```

### Versioning

Tất cả endpoint theo pattern:

```
/v1/{module}/{resource}
```

Ví dụ:
```
POST   /v1/identity/auth/login
POST   /v1/identity/auth/select-department
GET    /v1/permissions/metadata
GET    /v1/hr/departments
```

### Public Endpoints (không cần JWT)

```
/v1/identity/auth/**          # Đăng nhập / refresh token
/v1/permissions/metadata      # Lấy danh sách resource/action codes
/v3/api-docs/**               # OpenAPI spec
/swagger-ui/**                # Swagger UI
/actuator/**                  # Health check, metrics
```

---

## Cách chạy dự án

### Yêu cầu

- Java 21+
- Maven 3.8+
- PostgreSQL 14+
- Redis 7+
- MinIO (optional — chỉ cần cho document upload)

### Bước 1: Chuẩn bị Database

```sql
CREATE DATABASE vix;
CREATE SCHEMA IF NOT EXISTS shared;
```

### Bước 2: Cấu hình môi trường

Chỉnh sửa `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/vix
spring.datasource.username=postgres
spring.datasource.password=<your_password>

spring.data.redis.host=localhost
spring.data.redis.port=6379

minio.endpoint=http://localhost:9000
minio.access-key=minioadmin
minio.secret-key=minioadmin
```

### Bước 3: Chạy ứng dụng

```bash
./mvnw spring-boot:run
```

Hoặc build JAR:

```bash
./mvnw clean package -DskipTests
java -jar target/api-0.0.1-SNAPSHOT.jar
```

### Bước 4: Kiểm tra

| URL | Mô tả |
|-----|-------|
| http://localhost:8888/swagger-ui.html | Swagger UI |
| http://localhost:8888/actuator/health | Health check |
| http://localhost:8888/v3/api-docs | OpenAPI JSON |

---

## Cấu hình môi trường

| Property | Mô tả | Default |
|----------|-------|---------|
| `server.port` | Port ứng dụng | `8888` |
| `spring.datasource.url` | JDBC URL PostgreSQL | `localhost:5432/vix` |
| `jwt.secret` | Secret key HS512 (tối thiểu 64 bytes) | *(xem file)* |
| `jwt.access-token-expiration` | Access token TTL (ms) | `86400000` (24h) |
| `jwt.refresh-token-expiration` | Refresh token TTL (ms) | `604800000` (7d) |
| `spring.data.redis.host` | Redis host | `localhost` |
| `spring.data.redis.port` | Redis port | `6379` |
| `minio.endpoint` | MinIO endpoint | `http://localhost:9000` |
| `minio.bucket` | MinIO bucket name | `vix-documents` |
| `spring.mail.host` | SMTP host | `smtp.gmail.com` |
| `spring.task.scheduling.pool.size` | Thread pool cho Scheduler | `5` |
| `app.frontend.url` | URL Frontend (CORS) | `http://localhost:3000` |

---

## Hướng dẫn thêm Module mới (phòng ban mới)

Khi công ty có phòng ban mới, thực hiện theo các bước sau:

1. Tạo thư mục `src/main/java/vix/local/api/modules/<tên_module>/`
2. Tạo đủ 4 tầng: `api/`, `application/`, `domain/`, `infrastructure/`
3. Định nghĩa Domain Model trong `domain/model/`
4. Tạo Repository interface trong `domain/repository/`
5. Implement JPA Entity + Repository trong `infrastructure/persistence/`
6. Viết Application Service trong `application/service/`
7. Tạo REST Controller trong `api/v1/`
8. Thêm `ResourceCode` mới vào `permission` module nếu cần phân quyền riêng
9. *(Tùy chọn)* Tạo PostgreSQL schema riêng + cập nhật `schemaTarget` trong logic login

> Tham khảo skill `ddd-module-structure` trong `.agent/skills/` để biết chi tiết quy ước đặt tên và nguyên tắc phụ thuộc giữa các layer.

---

*Tài liệu được tạo từ codebase. Cập nhật lần cuối: 2026-07-27.*
