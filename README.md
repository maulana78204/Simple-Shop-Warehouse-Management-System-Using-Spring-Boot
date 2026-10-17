# Simple Shop Warehouse Management System

Sistem manajemen gudang toko yang komprehensif dan aman, dibangun dengan Spring Boot 3.2, JWT Authentication, dan OpenAPI Documentation. Sistem ini dirancang untuk mengelola inventaris produk, stok barang, dan variannya dengan role-based access control.

## 📋 Daftar Isi

- [Gambaran Umum](#gambaran-umum)
- [Fitur Utama](#fitur-utama)
- [Arsitektur Sistem](#arsitektur-sistem)
- [Prasyarat](#prasyarat)
- [Instalasi & Setup](#instalasi--setup)
- [Konfigurasi Database](#konfigurasi-database)
- [API Documentation](#api-documentation)
- [Struktur Project](#struktur-project)
- [Penggunaan](#penggunaan)
- [Security](#security)
- [Role & Permissions](#role--permissions)
- [Docker Deployment](#docker-deployment)
- [Testing](#testing)
- [Troubleshooting](#troubleshooting)

## 🎯 Gambaran Umum

Simple Shop Warehouse Management System adalah REST API yang menyediakan solusi lengkap untuk mengelola inventaris warehouse. Sistem ini menggunakan arsitektur berlapis (layered architecture) dengan pemisahan jelas antara controller, service, dan repository layer.

**Teknologi Utama:**
- **Framework**: Spring Boot 3.2.0
- **Java Version**: 17
- **Authentication**: JWT (JSON Web Token)
- **Database**: PostgreSQL / H2 (development)
- **API Documentation**: OpenAPI 3.0 (Swagger UI)
- **ORM**: Spring Data JPA
- **Migration**: Flyway
- **Mapping**: MapStruct
- **Containerization**: Docker & Docker Compose

## ✨ Fitur Utama

### 1. **Authentication & Authorization**
- Registrasi user baru dengan validasi password yang ketat
- Login dengan JWT token generation
- Role-based access control (RBAC)
- Token refresh mechanism
- Secure password encoding dengan Spring Security

### 2. **Manajemen Item/Produk**
- Create, Read, Update, Delete (CRUD) produk
- Support untuk item dengan multiple variants
- Track product details (nama, SKU, deskripsi, harga)
- Active/Inactive status untuk produk

### 3. **Manajemen Stock/Inventaris**
- Real-time stock tracking per item variant
- Stock adjustment (penambahan/pengurangan)
- Stock movement history
- Low stock alerts
- Multi-warehouse support ready

### 4. **Item Variants**
- Support untuk varian produk (ukuran, warna, dll)
- Tracking stok per variant
- Variant-specific pricing

### 5. **API Documentation**
- Auto-generated OpenAPI/Swagger documentation
- Interactive API testing via Swagger UI
- Comprehensive endpoint documentation

## 🏗️ Arsitektur Sistem

### Layered Architecture

```
┌─────────────────────────────────────┐
│         REST API Endpoints           │
│       (Controllers)                  │
└──────────────────┬──────────────────┘
                   │
┌──────────────────▼──────────────────┐
│         Business Logic              │
│         (Services)                  │
└──────────────────┬──────────────────┘
                   │
┌──────────────────▼──────────────────┐
│      Data Access Layer              │
│      (Repositories/JPA)             │
└──────────────────┬──────────────────┘
                   │
┌──────────────────▼──────────────────┐
│      Database Layer                 │
│      (PostgreSQL/H2)                │
└─────────────────────────────────────┘
```

### Core Components

#### **Controllers** (`src/main/java/com/warehouse/controller/`)
- `AuthController`: Authentication & user management
- `ItemController`: Product management
- `StockController`: Stock operations
- `GlobalExceptionHandler`: Centralized error handling

#### **Services** (`src/main/java/com/warehouse/service/`)
- `UserService`: User business logic
- `ItemService`: Item/product business logic
- `CustomUserDetailsService`: User details provider for Spring Security

#### **Repositories** (`src/main/java/com/warehouse/repository/`)
- `UserRepository`: User data access
- `ItemRepository`: Item/product data access
- `StockRepository`: Stock data access
- `ItemVariantRepository`: Item variant data access

#### **Entities** (`src/main/java/com/warehouse/entity/`)
- `User`: User account information
- `Item`: Product master data
- `ItemVariant`: Product variants (sizes, colors, etc.)
- `Stock`: Inventory/stock information

#### **DTOs** (`src/main/java/com/warehouse/dto/`)
- `AuthLoginRequest`: Login credentials
- `AuthRegisterRequest`: Registration data
- `AuthResponse`: JWT token response
- `ItemDTO`: Item data transfer
- `StockAdjustmentDTO`: Stock adjustment request

#### **Configuration** (`src/main/java/com/warehouse/config/`)
- `SecurityConfig`: Spring Security configuration
- `OpenApiConfig`: Swagger/OpenAPI documentation setup
- `DataSeeder`: Initial data population for development

## 📦 Prasyarat

- Java 17 atau lebih tinggi
- Maven 3.8.0 atau lebih tinggi
- PostgreSQL 12+ (untuk production)
- Docker & Docker Compose (untuk containerized deployment)
- Git

### Verifikasi Installation:
```bash
java -version
mvn -version
docker --version
docker-compose --version
```

## 🚀 Instalasi & Setup

### 1. Clone Repository
```bash
git clone https://github.com/yourusername/warehouse-management.git
cd warehouse-management
```

### 2. Build Project
```bash
# Windows
.\rebuild.bat

# Linux/MacOS
./rebuild.sh

# Atau menggunakan Maven langsung
mvn clean package -DskipTests
```

### 3. Run Application
```bash
# Development mode (H2 In-memory database)
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Production mode (PostgreSQL)
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```

Aplikasi akan berjalan di: `http://localhost:8080`

## 🗄️ Konfigurasi Database

### Development (H2 In-Memory Database)
File: `src/main/resources/application-dev.properties`

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.h2.console.enabled=true
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

**Akses H2 Console**: `http://localhost:8080/h2-console`

### Production (PostgreSQL)
File: `src/main/resources/application-prod.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/warehouse_db
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

### Database Initialization

Schema otomatis di-create melalui Flyway migrations:
- `src/main/resources/db/migration/V001__Warehouse_Management_Stock_Schema.sql`

Initial data di-seed melalui `DataSeeder` configuration.

## 📚 API Documentation

### Swagger UI
Access API documentation interaktif:
```
http://localhost:8080/swagger-ui.html
```

### OpenAPI JSON
```
http://localhost:8080/v3/api-docs
```

### Main Endpoints

#### **Authentication**
```
POST   /api/auth/register      - Register user baru
POST   /api/auth/login         - Login & dapatkan JWT token
POST   /api/auth/refresh       - Refresh token
```

#### **Items/Products**
```
GET    /api/items              - List semua produk
GET    /api/items/{id}         - Get detail produk
POST   /api/items              - Create produk baru
PUT    /api/items/{id}         - Update produk
DELETE /api/items/{id}         - Delete produk
```

#### **Stock**
```
GET    /api/stock              - List semua stok
GET    /api/stock/{id}         - Get detail stok
POST   /api/stock/adjust       - Adjust stok (tambah/kurang)
GET    /api/stock/item/{itemId} - Get stok per item
```

## 📂 Struktur Project

```
warehouse-management/
├── src/
│   ├── main/
│   │   ├── java/com/warehouse/
│   │   │   ├── config/              # Configuration classes
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── OpenApiConfig.java
│   │   │   │   └── DataSeeder.java
│   │   │   ├── controller/          # REST Controllers
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── ItemController.java
│   │   │   │   ├── StockController.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── service/             # Business Logic
│   │   │   │   ├── UserService.java
│   │   │   │   ├── ItemService.java
│   │   │   │   └── CustomUserDetailsService.java
│   │   │   ├── repository/          # Data Access
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── ItemRepository.java
│   │   │   │   ├── StockRepository.java
│   │   │   │   └── ItemVariantRepository.java
│   │   │   ├── entity/              # JPA Entities
│   │   │   │   ├── User.java
│   │   │   │   ├── Item.java
│   │   │   │   ├── ItemVariant.java
│   │   │   │   └── Stock.java
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   │   ├── AuthLoginRequest.java
│   │   │   │   ├── AuthRegisterRequest.java
│   │   │   │   ├── AuthResponse.java
│   │   │   │   ├── ItemDTO.java
│   │   │   │   └── StockAdjustmentDTO.java
│   │   │   ├── mapper/              # Entity-DTO Mappers
│   │   │   │   └── ItemMapper.java
│   │   │   ├── exception/           # Custom Exceptions
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   └── WarehouseManagementApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── application-prod.properties
│   │       └── db/migration/
│   │           └── V001__Warehouse_Management_Stock_Schema.sql
│   └── test/                        # Unit & Integration Tests
│       └── java/com/warehouse/
├── pom.xml                          # Maven configuration
├── Dockerfile                       # Docker image definition
├── docker-compose.yml               # Docker compose setup
├── checkstyle.xml                   # Code style rules
├── DATABASE_SCHEMA.md               # Database schema documentation
└── README.md                        # This file
```

## 💻 Penggunaan

### 1. Registrasi User Baru
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "warehouse_admin",
    "email": "admin@warehouse.com",
    "password": "SecurePassword123!@#",
    "role": "ADMIN"
  }'
```

### 2. Login & Dapatkan Token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "warehouse_admin",
    "password": "SecurePassword123!@#"
  }'
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "warehouse_admin",
  "email": "admin@warehouse.com",
  "roles": ["ADMIN"]
}
```

### 3. Gunakan Token untuk Akses Protected Endpoints
```bash
curl -X GET http://localhost:8080/api/items \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

### 4. Create Product Baru
```bash
curl -X POST http://localhost:8080/api/items \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop ASUS",
    "sku": "SKU-LAPTOP-001",
    "description": "Laptop Gaming ASUS ROG",
    "price": 15000000.00
  }'
```

### 5. Adjust Stock
```bash
curl -X POST http://localhost:8080/api/stock/adjust \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "itemVariantId": 1,
    "quantity": 50,
    "type": "ADJUSTMENT",
    "notes": "Initial stock entry"
  }'
```

## 🔒 Security

### Password Requirements
- Minimum 8 karakter
- Harus mengandung: huruf besar, huruf kecil, angka, dan special character
- Contoh password valid: `SecurePass123!@#`

### JWT Token
- Algorithm: HS512
- Expiration: 24 jam
- Refresh token tersedia untuk extend session

### Spring Security Features
- CSRF protection
- CORS configuration
- Method-level security dengan `@PreAuthorize`
- Password encoding dengan BCrypt

### HTTPS/TLS
- Production environment harus menggunakan HTTPS
- Certificate configuration di `application-prod.properties`

## 👥 Role & Permissions

### Available Roles
- **ADMIN**: Full access ke semua endpoint
- **MANAGER**: Access ke item management dan stock reports
- **STAFF**: Read-only access ke stok dan produk

### Permission Mapping
```
ADMIN   → Create, Read, Update, Delete all resources
MANAGER → Create, Read, Update items & stock; Generate reports
STAFF   → Read-only untuk items & stock
```

Lihat `ROLE_PERMISSIONS_FINAL.md` untuk detail lengkap.

## 🐳 Docker Deployment

### Build Docker Image
```bash
docker build -t warehouse-management:1.0.0 .
```

### Run dengan Docker Compose
```bash
docker-compose up -d
```

Struktur Docker Compose:
- **App Service**: Spring Boot application (port 8080)
- **Database Service**: PostgreSQL (port 5432)
- **Network**: Internal network untuk komunikasi antar service

### Environment Variables
Konfigurasi di `docker-compose.yml`:
```yaml
environment:
  SPRING_PROFILES_ACTIVE: prod
  SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/warehouse_db
  SPRING_DATASOURCE_USERNAME: postgres
  SPRING_DATASOURCE_PASSWORD: postgres
```

### Access Application
```
API: http://localhost:8080
Swagger UI: http://localhost:8080/swagger-ui.html
```

## 🧪 Testing

### Run Tests
```bash
# Run semua tests
mvn test

# Run specific test class
mvn test -Dtest=ItemServiceTest

# Run dengan coverage
mvn test jacoco:report
```

### Test Coverage Report
Generated di: `target/site/jacoco/index.html`

### Available Tests
- `ItemServiceTest`: 13 passing tests untuk service logic
- Integration tests untuk controller endpoints

## 🛠️ Troubleshooting

### Issue: Database Connection Error
**Solusi:**
1. Pastikan PostgreSQL running: `docker-compose up -d postgres`
2. Verify credentials di `application-prod.properties`
3. Check database exists: `CREATE DATABASE warehouse_db;`

### Issue: JWT Token Expired
**Solusi:**
1. Get new token dengan login endpoint
2. Gunakan refresh token endpoint untuk extend session

### Issue: Port 8080 Already in Use
**Solusi:**
```bash
# Change port di application.properties
server.port=8081

# Atau kill process
lsof -ti:8080 | xargs kill -9  # Linux/MacOS
netstat -ano | findstr :8080 | findstr LISTENING  # Windows
```

### Issue: Checkstyle Failures
**Solusi:**
```bash
# Fix formatting issues automatically
mvn checkstyle:check -X

# Run dengan strict check disabled (dev only)
mvn clean package -DskipTests -Dcheckstyle.skip
```

## 📖 Dokumentasi Tambahan

- `DATABASE_SCHEMA.md` - Database schema details
- `API_ACCESS_GUIDE.md` - Detailed API usage guide
- `SECURITY.md` - Security policies & implementation
- `CUSTOMIZATION_GUIDE.md` - How to extend the system

## 🤝 Kontribusi

Untuk kontribusi ke project ini:

1. Fork repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

