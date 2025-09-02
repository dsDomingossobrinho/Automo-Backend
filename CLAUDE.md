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
3. **JWT Security**: Stateless authentication with role-based access control
4. **Multi-Role System**: Many-to-many relationship between users and roles
5. **Base Entity Pattern**: `AbstractModel` provides id, createdAt, updatedAt

### Authentication & Authorization
- **JWT Utils**: Use `JwtUtils` bean to access current user information
- **Multi-Role Support**: Users can have multiple roles (ADMIN, USER, AGENT, MANAGER)
- **Account Types**: Back Office (INDIVIDUAL) vs Corporate (CORPORATE)
- **Login Endpoints**: 
  - `/auth/login` - General authentication
  - `/auth/login/backoffice` - Back Office only
  - `/auth/login/user` - Corporate users only

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

### Database Configuration
- **Connection**: `jdbc:postgresql://localhost:5432/automo_db`
- **DDL Mode**: `update` (auto-updates schema)
- **Auditing**: Enabled via `@EnableJpaAuditing`
- **Data Seeding**: `DataSeeder` class initializes base data

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