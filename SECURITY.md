# Security Guide - Warehouse Management System

Panduan lengkap security implementation, best practices, dan security testing untuk project ini.

## Security Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        HTTP Request                         │
└────────────────────┬────────────────────────────────────────┘
                     │
        ┌────────────▼─────────────┐
        │   CORS Filter            │ (Cross-Origin Resource Sharing)
        └────────────┬─────────────┘
                     │
        ┌────────────▼─────────────┐
        │   Security Filter Chain  │ (Spring Security)
        └────────────┬─────────────┘
                     │
        ┌────────────▼──────────────────────┐
        │   Authentication (JWT/Session)   │
        └────────────┬──────────────────────┘
                     │
        ┌────────────▼──────────────────────┐
        │   Authorization (Role-Based)     │
        └────────────┬──────────────────────┘
                     │
        ┌────────────▼──────────────────────┐
        │   CSRF Protection                │
        └────────────┬──────────────────────┘
                     │
        ┌────────────▼──────────────────────┐
        │   Rate Limiting                  │
        └────────────┬──────────────────────┘
                     │
        ┌────────────▼──────────────────────┐
        │   Controller/Business Logic      │
        └────────────┬──────────────────────┘
                     │
        ┌────────────▼──────────────────────┐
        │   Data Access Layer              │
        └─────────────────────────────────┘
```

## Security Components

### 1. JWT (JSON Web Token) Authentication

**Purpose**: Stateless authentication mechanism

**Components**:
- Header: Algorithm & Token Type
- Payload: User claims (id, roles, expiration)
- Signature: HMAC SHA-256

**Configuration**:
```properties
jwt.secret=your-256-bit-secret-key-minimum-length-required
jwt.expiration=86400000  # 24 hours in milliseconds
jwt.refresh-expiration=604800000  # 7 days
```

**Token Structure**:
```
Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.
eyJzdWIiOiIxIiwiZXhwIjoxNjcwMDAwMDAwLCJpYXQiOjE2Njk5MDA MDAwfQ.
SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

### 2. Spring Security Configuration

**Key Features**:
- Servlet Filter Chain
- Method-level security (@PreAuthorize, @PostAuthorize)
- CORS configuration
- CSRF protection
- Password encoding (BCrypt)

**Security Filters**:
1. `CorsFilter` - Handle CORS
2. `SecurityContextHolderFilter` - Manage security context
3. `JwtAuthenticationFilter` - Extract & validate JWT
4. `AuthorizationFilter` - Check permissions
5. `ExceptionTranslationFilter` - Handle security exceptions

### 3. Role-Based Access Control (RBAC)

**Roles**:
- `ROLE_USER` - Regular users (item viewing, selling)
- `ROLE_MERCHANT` - Merchants (inventory management)
- `ROLE_ADMIN` - Administrators (system configuration)

**Access Control**:
```java
@PreAuthorize("hasRole('USER')")
@PreAuthorize("hasRole('MERCHANT')")
@PreAuthorize("hasAnyRole('MERCHANT', 'ADMIN')")
@PreAuthorize("@customSecurity.isOwner(#id, authentication)")
```

### 4. Password Security

**Implementation**: BCrypt hashing with salt

**Configuration**:
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12); // Strength 12
}
```

**Password Requirements**:
- Minimum 8 characters
- At least 1 uppercase letter
- At least 1 lowercase letter
- At least 1 digit
- At least 1 special character

### 5. CORS (Cross-Origin Resource Sharing)

**Allowed Origins**: Configure based on environment
- Development: http://localhost:3000
- Production: https://yourdomain.com

**Allowed Methods**: GET, POST, PUT, DELETE, OPTIONS

**Allowed Headers**: Authorization, Content-Type, Accept

**Configuration**:
```java
@Bean
public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
        }
    };
}
```

## Authentication Flow

### 1. User Registration
```
POST /api/auth/register
├─ Validate input
├─ Check duplicate username
├─ Hash password (BCrypt)
├─ Save user to database
└─ Return user info (without password)
```

### 2. User Login
```
POST /api/auth/login
├─ Validate credentials
├─ Match password with hash
├─ Check if account locked/disabled
├─ Generate JWT token
├─ Return token + expiration
└─ Token stored in client (localStorage/sessionStorage)
```

### 3. Request with Token
```
GET /api/items
Header: Authorization: Bearer <jwt-token>
├─ Extract token from header
├─ Validate signature
├─ Check expiration
├─ Load user from claims
├─ Set security context
└─ Process request
```

### 4. Token Refresh
```
POST /api/auth/refresh
Header: Authorization: Bearer <refresh-token>
├─ Validate refresh token
├─ Generate new access token
└─ Return new token
```

## Authorization Rules

### Endpoint Access Matrix

| Endpoint | Anonymous | User | Merchant | Admin |
|----------|-----------|------|----------|-------|
| POST /auth/register | ✅ | ❌ | ❌ | ❌ |
| POST /auth/login | ✅ | ❌ | ❌ | ❌ |
| GET /items | ❌ | ✅ | ✅ | ✅ |
| POST /items | ❌ | ❌ | ✅ | ✅ |
| PUT /items/{id} | ❌ | ❌ | ✅* | ✅ |
| DELETE /items/{id} | ❌ | ❌ | ❌ | ✅ |
| GET /stock | ❌ | ✅ | ✅ | ✅ |
| POST /stock/sell | ❌ | ✅ | ✅ | ✅ |
| POST /stock/adjust | ❌ | ❌ | ✅ | ✅ |
| GET /admin/* | ❌ | ❌ | ❌ | ✅ |

*Only if merchant owns the item

## Securing Sensitive Data

### 1. Password Storage
```java
@Entity
public class User {
    @Column(nullable = false)
    private String password; // Stored as BCrypt hash
    
    public void setPassword(String rawPassword) {
        this.password = passwordEncoder.encode(rawPassword);
    }
    
    public boolean checkPassword(String rawPassword) {
        return passwordEncoder.matches(rawPassword, this.password);
    }
}
```

### 2. Sensitive Fields in DTO
```java
@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    // NEVER include password field
    
    // Exclude sensitive fields from serialization
    @JsonIgnore
    private String password;
}
```

### 3. Environment Variables
```properties
# Never hardcode secrets
jwt.secret=${JWT_SECRET}
database.password=${DB_PASSWORD}
api.key=${API_KEY}
```

### 4. HTTP Security Headers
```java
.headers().frameOptions().deny() // Prevent clickjacking
        .and().xssProtection() // XSS protection
        .and().contentSecurityPolicy(...); // CSP
```

## Validation & Input Sanitization

### 1. Input Validation
```java
@Entity
public class Item {
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100)
    private String name;
    
    @DecimalMin("0.01", message = "Price must be positive")
    private Double basePrice;
    
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "Invalid SKU format")
    private String sku;
}
```

### 2. SQL Injection Prevention
```java
// ❌ VULNERABLE
String query = "SELECT * FROM items WHERE sku = '" + sku + "'";

// ✅ SAFE - Using JPA Query
@Query("SELECT i FROM Item i WHERE i.sku = :sku")
Optional<Item> findBySku(@Param("sku") String sku);

// ✅ SAFE - Using JPA Methods
Optional<Item> findBySku(String sku);
```

### 3. Cross-Site Scripting (XSS) Prevention
```java
// ❌ VULNERABLE - Direct HTML output
response.getWriter().println("<h1>" + userInput + "</h1>");

// ✅ SAFE - Escaping output
String escaped = HtmlUtils.htmlEscape(userInput);
response.getWriter().println("<h1>" + escaped + "</h1>");

// ✅ SAFE - Using templates with auto-escaping
// Thymeleaf auto-escapes by default
<h1 th:text="${userInput}"></h1>
```

### 4. Command Injection Prevention
```java
// ❌ VULNERABLE
Runtime.getRuntime().exec("bash -c " + userCommand);

// ✅ SAFE - Use ProcessBuilder with array
ProcessBuilder pb = new ProcessBuilder("bash", "-c", userCommand);
```

## API Security Best Practices

### 1. Rate Limiting
```java
@Bean
public RateLimitingFilter rateLimitingFilter() {
    return new RateLimitingFilter(
        requestsPerMinute = 100,
        burstSize = 20
    );
}
```

**Configuration**:
```properties
rate.limit.enabled=true
rate.limit.requests-per-minute=100
rate.limit.burst-size=20
```

### 2. API Key Management
```java
@Bean
public WebSecurityFilterChain securityFilterChain(HttpSecurity http) {
    http.authorizeRequests()
        .antMatchers("/api/public/**").permitAll()
        .antMatchers("/api/v2/**").authenticated()
        .and()
        .addFilterBefore(new ApiKeyFilter(), UsernamePasswordAuthenticationFilter.class);
    return http.build();
}
```

### 3. HTTPS Enforcement
```properties
server.ssl.enabled=true
server.ssl.key-store=${SSL_KEYSTORE_PATH}
server.ssl.key-store-password=${SSL_KEYSTORE_PASSWORD}
server.ssl.key-store-type=PKCS12
```

### 4. Security Headers
```java
@Bean
public SecurityHeadersFilter securityHeadersFilter() {
    return (request, response, chain) -> {
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("Strict-Transport-Security", 
            "max-age=31536000; includeSubDomains");
        chain.doFilter(request, response);
    };
}
```

## Audit Logging

### 1. Log All Security Events
```java
@Aspect
@Component
public class SecurityAuditAspect {
    
    @Before("execution(* com.warehouse.controller.AuthController.*(..))")
    public void logAuthEvent(JoinPoint joinPoint) {
        logger.info("AUTH_EVENT: {} - User: {}", 
            joinPoint.getSignature(), getCurrentUser());
    }
}
```

### 2. Log Sensitive Operations
```java
public void deleteUser(Long userId) {
    logger.warn("SECURITY: User deletion requested for ID: {} by: {}",
        userId, getCurrentUser());
    userRepository.deleteById(userId);
    logger.warn("SECURITY: User {} deleted successfully", userId);
}
```

### 3. Log Failed Login Attempts
```java
public void loginFailed(String username) {
    logger.warn("SECURITY: Failed login attempt for user: {}", username);
    incrementLoginAttempts(username);
    
    if (getLoginAttempts(username) > 5) {
        lockAccount(username);
        logger.error("SECURITY: Account locked due to multiple failed attempts: {}", username);
    }
}
```

## Security Testing

See [Testing.md](#authentication-security-tests) for comprehensive security testing guide.

**Key Test Coverage**:
- ✅ Authentication (registration, login, logout)
- ✅ Authorization (role-based access)
- ✅ JWT validation (signature, expiration)
- ✅ CSRF protection
- ✅ Input validation
- ✅ SQL injection prevention
- ✅ XSS prevention
- ✅ Account lockout
- ✅ Password reset

## Dependency Management

### Vulnerable Dependencies Check
```bash
# Check for known vulnerabilities
mvn org.owasp:dependency-check-maven:check
```

### Keep Dependencies Updated
```bash
# Check for updates
mvn versions:display-dependency-updates

# Update to latest
mvn versions:use-latest-versions
```

## Environment Configuration

### Development
```properties
# Less strict security for development
security.enabled=false
jwt.expiration=86400000
cors.allowed-origins=http://localhost:3000
```

### Production
```properties
# Strict security
security.enabled=true
jwt.expiration=3600000  # 1 hour
cors.allowed-origins=https://yourdomain.com
server.ssl.enabled=true
security.require-https=true
```

## Incident Response

### 1. Account Breach
```
1. Immediately lock/deactivate account
2. Send notification to user
3. Force password reset on next login
4. Log all access attempts
5. Notify admins
```

### 2. Token Compromise
```
1. Add token to blacklist
2. Invalidate all user sessions
3. Force re-authentication
4. Rotate JWT secret (if necessary)
5. Audit affected transactions
```

### 3. Database Breach
```
1. Revoke access credentials
2. Enable audit logging
3. Monitor for unauthorized access
4. Notify affected users
5. Force password reset
6. Check for data exfiltration
```

## Security Checklist

- [ ] All passwords hashed with BCrypt
- [ ] JWT tokens include expiration
- [ ] CORS configured for allowed origins only
- [ ] CSRF protection enabled
- [ ] Rate limiting implemented
- [ ] Input validation on all endpoints
- [ ] SQL injection protection (parameterized queries)
- [ ] XSS protection (output escaping)
- [ ] Security headers configured
- [ ] HTTPS enforced in production
- [ ] Sensitive data not logged
- [ ] Authentication required for all protected endpoints
- [ ] Authorization validated for all operations
- [ ] Security tests passing (80%+ coverage)
- [ ] Dependency vulnerabilities checked
- [ ] Secrets in environment variables (not hardcoded)
- [ ] Audit logging enabled
- [ ] Account lockout implemented
- [ ] Password reset mechanism
- [ ] Security documentation updated

## Common Vulnerabilities & Fixes

### 1. Broken Authentication
**Problem**: Weak password policy, session fixation

**Fix**:
```java
// Enforce strong passwords
@NotBlank @Size(min=8) 
@Pattern(regexp="^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
private String password;

// Use HTTP-only cookies
response.setHttpOnly(true);
```

### 2. Sensitive Data Exposure
**Problem**: Passwords in logs, secrets in config

**Fix**:
```java
// Don't log passwords
logger.info("User login attempt for: {}", username); // NOT password

// Use environment variables
String secret = System.getenv("JWT_SECRET");
```

### 3. XML External Entities (XXE)
**Problem**: Parsing untrusted XML

**Fix**:
```java
// Disable XXE
DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
```

### 4. Broken Access Control
**Problem**: Missing authorization checks

**Fix**:
```java
@PreAuthorize("@itemSecurityService.isOwner(#itemId, authentication)")
@DeleteMapping("/{itemId}")
public void deleteItem(@PathVariable Long itemId) {
    itemService.deleteItem(itemId);
}
```

### 5. Cross-Site Request Forgery (CSRF)
**Problem**: Token not validated on state-changing requests

**Fix**:
```java
// CSRF enabled by default in Spring Security 6.0+
// Ensure tokens are included in forms/AJAX
.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
```

## Resources

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Security Reference](https://spring.io/projects/spring-security)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8949)
- [NIST Password Guidelines](https://pages.nist.gov/800-63-3/)

---

**Last Updated**: October 2026
