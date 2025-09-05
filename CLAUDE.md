# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

### Development
```bash
./mvnw spring-boot:run          # Run the application (port 8080)
./mvnw clean compile            # Compile the project
./mvnw clean package            # Build JAR file
./mvnw clean package -DskipTests # Build without running tests
```

### Testing
```bash
./mvnw test                     # Run all tests
./mvnw test -Dtest=ClassName    # Run specific test class
```

### Docker
```bash
# Start all services (PostgreSQL + PgAdmin + App)
docker-compose up -d

# Start only database
docker-compose up -d postgres

# Stop all services
docker-compose down

# View logs
docker-compose logs -f app

# Rebuild and restart
docker-compose up --build
```

## Architecture Overview

This is a **Spring Boot 3.5.5** application using **Java 21** with a feature-based package structure.

### Technology Stack
- **Framework**: Spring Boot with Spring Security
- **Database**: PostgreSQL with Spring Data JPA/Hibernate
- **Authentication**: JWT with refresh tokens
- **Documentation**: Swagger/OpenAPI 3
- **Build Tool**: Maven

### Package Structure
Base package: `com.automo`

**Core Configuration**:
- `config/` - Application configurations including security/JWT
- `model/` - Base entities and abstractions

**Feature Modules** (each with controller/service/repository/entity/dto layers):
- `auth/` - JWT authentication system
- `user/` - User management  
- `agent/` - Agent management
- `product/` - Product catalog
- `lead/` - Lead management
- `deal/` - Deal/transaction management
- `payment/` - Payment processing
- `subscription/` - Subscription plans
- `notification/` - Notification system
- Plus 20+ other business domain modules

### Key Architectural Patterns
1. **Layered Architecture**: Controller → Service → Repository → Entity
2. **Feature-Based Packages**: Each business domain is self-contained
3. **Service-to-Service Communication**: Services only communicate with other services, never directly with repositories from other domains
4. **JWT Security**: Stateless authentication with role-based access control
5. **Multi-Role System**: Many-to-many relationship between users and roles
6. **Base Entity Pattern**: `AbstractModel` provides id, createdAt, updatedAt
7. **Soft Delete Pattern**: Records are marked as "ELIMINATED" instead of physical deletion

### Authentication & Authorization
- **JWT Utils**: Use `JwtUtils` bean to access current user information
- **Multi-Role Support**: Users can have multiple roles (ADMIN, USER, AGENT, MANAGER)
- **Account Types**: Back Office (INDIVIDUAL) vs Corporate (CORPORATE)
- **OTP System**: One-Time Password authentication with email/SMS support
- **Password Recovery**: OTP-based password reset functionality

### Authentication Endpoints
#### **Direct Login**
- `/auth/login` - Direct authentication with credentials

#### **OTP-Based Login**
- `/auth/login/request-otp` - Request OTP for general authentication
- `/auth/login/verify-otp` - Verify OTP and authenticate
- `/auth/login/resend-otp` - Resend OTP for general authentication

#### **Back Office OTP Login**
- `/auth/login/backoffice/request-otp` - Request OTP for Back Office
- `/auth/login/backoffice/verify-otp` - Verify OTP and authenticate Back Office
- `/auth/login/backoffice/resend-otp` - Resend OTP for Back Office

#### **Corporate User OTP Login**
- `/auth/login/user/request-otp` - Request OTP for corporate users
- `/auth/login/user/verify-otp` - Verify OTP and authenticate corporate user
- `/auth/login/user/resend-otp` - Resend OTP for corporate users

#### **Password Recovery**
- `/auth/forgot-password` - Request OTP for password recovery
- `/auth/reset-password` - Reset password with OTP verification

#### **Other**
- `/auth/register` - User registration

### Role-Based Access Control
```java
// Inject JwtUtils to access current user
@Autowired
private JwtUtils jwtUtils;

// Check user roles
if (jwtUtils.isCurrentUserAdmin()) { /* Admin logic */ }
if (jwtUtils.isCurrentUserAgent()) { /* Agent logic */ }
if (jwtUtils.hasCurrentUserRole(2L)) { /* Specific role check */ }

// Get user information
Long userId = jwtUtils.getCurrentUserId();
String email = jwtUtils.getCurrentUserEmail();
List<Long> allRoles = jwtUtils.getCurrentUserRoleIds();
```

### Service Architecture Rules
**CRITICAL: Service-to-Service Communication Pattern**

All services MUST follow these rules:
1. **Repository Access**: Services can ONLY access their own repository directly
2. **Cross-Domain Access**: To access other entities, services MUST use other services, never repositories
3. **Required Methods**: Every service MUST implement:
   - `findById(Long id)` - For inter-service communication
   - `findByIdAndStateId(Long id, Long stateId)` - For state-aware lookups

```java
// ❌ WRONG: AdminService accessing StateRepository directly
@Service
public class AdminServiceImpl {
    private final StateRepository stateRepository; // VIOLATION!
    
    public void someMethod() {
        State state = stateRepository.findById(id); // WRONG!
    }
}

// ✅ CORRECT: AdminService using StateService
@Service
public class AdminServiceImpl {
    private final StateService stateService; // CORRECT!
    
    public void someMethod() {
        State state = stateService.findById(id); // CORRECT!
    }
}
```

### Soft Delete Implementation
**All entities with state relationships use soft delete:**

- **Delete Operations**: Set entity state to "ELIMINATED" instead of physical deletion
- **State Management**: Uses `StateService.getEliminatedState()` to get ELIMINATED state
- **Data Preservation**: Records remain in database for audit trails and recovery
- **Filter Queries**: List methods should filter out ELIMINATED records

```java
// Soft delete pattern
@Override
public void deleteEntity(Long id) {
    Entity entity = this.findById(id);
    State eliminatedState = stateService.getEliminatedState();
    entity.setState(eliminatedState);
    entityRepository.save(entity);
}
```

**Entities with physical delete (no state relationship):**
- AccountType, PaymentType, OrganizationType
- IdentifierType, NotificationType, LeadType, Role

### Database Configuration
- **Connection**: `jdbc:postgresql://localhost:5432/automo_db`
- **DDL Mode**: `update` (auto-updates schema)
- **Auditing**: Enabled via `@EnableJpaAuditing`
- **Data Seeding**: `DataSeeder` class initializes base data
- **States Available**: ACTIVE, INACTIVE, PENDING, ELIMINATED, UNREAD, READ, PENDING PAYMENT, PAYMENT IN ANALYSIS, APPROVED PAYMENT

### API Documentation
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **API Docs**: `http://localhost:8080/v3/api-docs`
- **Health Check**: `http://localhost:8080/actuator/health`

### Environment Configuration
- `application.properties` - Main configuration with environment variables
- `application-dev.properties` - Development settings
- `application-prod.properties` - Production settings (Swagger disabled)
- `application-local.properties` - Local development
- `.env.example` - Environment variables template
- `docker-compose.yml` - Complete Docker setup with PostgreSQL, PgAdmin, and App

### Recent Improvements (Production Ready)
- **Security Hardened**: Environment variables for all secrets, custom exceptions, CORS properly configured
- **Performance Optimized**: Database indexes, connection pooling, JPA batch processing, caching layer
- **Error Handling**: Global exception handler with structured error responses
- **Logging**: Structured logging with correlation IDs and security audit trails
- **Testing**: Unit tests for core security components
- **Docker**: Single docker-compose.yml with PostgreSQL, PgAdmin, and application

### Development Notes
- Always use `@SecurityRequirement(name = "bearerAuth")` for protected endpoints
- Follow the established package structure when adding new features
- Use `@RequiredArgsConstructor` for constructor injection
- All entities should extend `AbstractModel` for auditing
- JWT tokens expire after 4 hours, refresh tokens after 7 days
- Use environment variables for all sensitive configuration

**CRITICAL Architecture Rules:**
- **Service Communication**: Services ONLY communicate with other services, NEVER with repositories from other domains
- **Required Service Methods**: Every service MUST implement `findById(Long id)` and `findByIdAndStateId(Long id, Long stateId)`
- **Soft Delete**: Use `stateService.getEliminatedState()` for delete operations on entities with state relationships
- **State Filtering**: List methods should filter out ELIMINATED records to hide soft-deleted data
- **Physical Delete**: Only use for entities without state relationships (AccountType, PaymentType, etc.)