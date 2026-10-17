# 🚀 QUICK START GUIDE

## Build & Run

```bash
# Build project
mvn clean install

# Run application
java -jar target/warehouse-management-1.0.0.jar
```

## Access Points

| URL | Purpose |
|-----|---------|
| http://localhost:8080 | Health check |
| http://localhost:8080/swagger-ui/index.html | **API Documentation** |
| http://localhost:8080/h2-console | Database console |

---

## Test Users

### Admin User
```
Username: admin
Password: admin123
```

### Regular User
```
Username: testuser
Password: test123
```

---

## API Examples

### 1. Register New User
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "email": "new@example.com",
    "password": "SecurePass123!"
  }'
```

### 2. Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

### 3. Get Current User
```bash
curl -X GET http://localhost:8080/auth/me \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="
```

### 4. Get All Items
```bash
curl -X GET http://localhost:8080/items \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="
```

### 5. Create Item (ADMIN only)
```bash
curl -X POST http://localhost:8080/items \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Product A",
    "sku": "PROD-001",
    "basePrice": 99.99,
    "reorderLevel": 10,
    "category": "Electronics"
  }'
```

### 6. Update Item (ADMIN only)
```bash
curl -X PUT http://localhost:8080/items/1 \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Product A Updated",
    "sku": "PROD-001",
    "basePrice": 89.99,
    "reorderLevel": 15,
    "category": "Electronics"
  }'
```

### 7. Delete Item (ADMIN only)
```bash
curl -X DELETE http://localhost:8080/items/1 \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="
```

---

## HTTP Status Codes

| Status | Meaning |
|--------|---------|
| 200 | OK - Request successful |
| 201 | Created - Resource created |
| 204 | No Content - Successful delete |
| 400 | Bad Request - Validation error |
| 401 | Unauthorized - No credentials |
| 403 | Forbidden - Insufficient permissions |
| 404 | Not Found - Resource not found |
| 500 | Server Error - System error |

---

## Validation Rules

### Item Creation
- **Name:** Required, 3-100 characters
- **SKU:** Required, uppercase letters/numbers/hyphens, 3-50 characters
- **Price:** Required, must be > 0
- **Reorder Level:** Required, minimum 1

### User Registration
- **Username:** 3-50 characters
- **Email:** Valid email format
- **Password:** Min 8 chars, must include: uppercase, lowercase, digit, special char

---

## Troubleshooting

### Port 8080 already in use
```bash
# Kill existing process
lsof -ti:8080 | xargs kill -9
# or
netstat -ano | findstr :8080  # Windows
```

### Database locked
- Try restarting the application
- H2 in-memory database resets on restart

### Authentication fails
- Verify credentials: admin / admin123
- Check Authorization header format: `Basic base64(username:password)`

---

## Key Features

✅ **RESTful API** - Standard HTTP methods  
✅ **Authentication** - HTTP Basic Auth with BCrypt  
✅ **Authorization** - Role-based access control  
✅ **Validation** - Input validation on all endpoints  
✅ **Error Handling** - Descriptive error messages  
✅ **Documentation** - Swagger UI for testing  
✅ **Database** - H2 in-memory with schema migration  

---

**Ready to test? Open http://localhost:8080/swagger-ui/index.html in your browser!**
