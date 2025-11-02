# SecureAuth - Identity and Access Management Platform

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen?style=for-the-badge&logo=spring-boot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue?style=for-the-badge&logo=postgresql)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)

**A centralized, enterprise-grade Identity and Access Management (IAM) platform built with Spring Boot**

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Key Features](#-key-features)
- [Technology Stack](#-technology-stack)
- [Architecture](#-architecture)
- [Getting Started](#-getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Configuration](#configuration)
- [Usage](#-usage)
- [API Documentation](#-api-documentation)
- [Security Features](#-security-features)
- [Project Structure](#-project-structure)
- [Contributing](#-contributing)
- [License](#-license)

---

## 🎯 Overview

**SecureAuth** is a comprehensive Identity and Access Management (IAM) solution designed to provide centralized authentication, authorization, and user management for modern applications. Built on Spring Boot and following industry best practices, SecureAuth offers a robust, scalable, and secure platform for managing user identities and access controls.

### Why SecureAuth?

- **Centralized Management**: Single platform for all identity and access management needs
- **Enterprise Security**: Industry-standard security implementations with JWT authentication
- **Audit Trail**: Comprehensive logging and monitoring of all security-related events
- **Role-Based Access Control**: Fine-grained permission management with flexible role assignment
- **Account Protection**: Built-in protection against brute force attacks and unauthorized access

---

## ✨ Key Features

### Authentication & Authorization
- 🔐 **JWT-Based Authentication**: Secure token-based authentication mechanism
- 🔄 **Token Refresh**: Seamless token refresh functionality
- 🚪 **Login/Logout Management**: Complete session management
- 👤 **User Registration**: Secure user onboarding with email validation

### Security & Protection
- 🛡️ **Brute Force Protection**: Automatic account locking after failed login attempts
- 🔒 **Password Validation**: Strong password enforcement with custom validators
- 📧 **Email Verification**: Account activation via email confirmation
- 🚫 **Account Blocking**: Administrative control to block/unblock users

### Access Control
- 👥 **Role-Based Access Control (RBAC)**: Multiple role types (USER, ADMIN, MANAGER, SECURITY)
- 🎫 **Permission Management**: Granular permission assignment and control
- 🔗 **Role-Permission Mapping**: Flexible association between roles and permissions

### Audit & Monitoring
- 📊 **Comprehensive Audit Logging**: Track all security-relevant actions
- 🔍 **Severity Levels**: Categorize events by severity (INFO, WARNING, ERROR, CRITICAL)
- 📝 **Detailed Event Tracking**: Record user actions, IP addresses, timestamps, and outcomes
- ✅ **Audit Status**: Monitor success and failure of operations

### User Management
- 👤 **User CRUD Operations**: Complete user lifecycle management
- 📋 **User Profile Management**: Maintain user information and settings
- 🔄 **Account Status Control**: Enable/disable user accounts
- 📅 **Timestamp Tracking**: Creation and update timestamps for all entities

---

## 🛠 Technology Stack

### Backend Framework
- **Spring Boot 3.5.7**: Modern Java framework for building enterprise applications
- **Spring Security**: Comprehensive security framework
- **Spring Data JPA**: Data persistence with JPA/Hibernate
- **Spring Validation**: Bean validation with Hibernate Validator

### Database
- **PostgreSQL**: Robust, enterprise-grade relational database

### Security
- **JWT (JSON Web Tokens)**: Stateless authentication mechanism
- **BCrypt**: Password hashing algorithm

### Build & Development Tools
- **Maven**: Dependency management and build automation
- **Lombok**: Reduce boilerplate code with annotations
- **Java 17**: Latest LTS version of Java

---

## 🏗 Architecture

SecureAuth follows a layered architecture pattern:

```
┌─────────────────────────────────────────┐
│         Controllers Layer               │
│   (REST API Endpoints)                  │
├─────────────────────────────────────────┤
│         Service Layer                   │
│   (Business Logic)                      │
├─────────────────────────────────────────┤
│         Repository Layer                │
│   (Data Access)                         │
├─────────────────────────────────────────┤
│         Security Layer                  │
│   (JWT, Filters, Config)                │
├─────────────────────────────────────────┤
│         Database                        │
│   (PostgreSQL)                          │
└─────────────────────────────────────────┘
```

### Core Components

1. **Entities**: User, Role, Permission, AuditLog
2. **DTOs**: LoginRequest, RegisterRequest, AuthResponse, RoleRequest
3. **Services**: AuthService, UserService, RoleService, PermissionService, AuditService
4. **Controllers**: AuthController, UserController, RoleController, AuditController
5. **Security**: JwtService, JwtFilter, SecurityConfig, AuthenticationEntryPoint

---

## 🚀 Getting Started

### Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK) 17** or higher
- **Maven 3.6+** for building the project
- **PostgreSQL 12+** database server
- **Git** for version control

### Installation

1. **Clone the repository**

```bash
git clone https://github.com/Azzammoo10/SecureAuth-.git
cd SecureAuth-
```

2. **Set up PostgreSQL database**

```sql
CREATE DATABASE secureauth_db;
CREATE USER secureauth_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE secureauth_db TO secureauth_user;
```

3. **Build the project**

```bash
./mvnw clean install
```

### Configuration

Configure your application by creating or modifying `src/main/resources/application.properties`:

```properties
# Application Configuration
spring.application.name=SecurAuth

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/secureauth_db
spring.datasource.username=secureauth_user
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true

# JWT Configuration
jwt.secret=your-secret-key-change-this-in-production
jwt.expiration=86400000
jwt.refresh.expiration=604800000

# Email Configuration (if using email verification)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Server Configuration
server.port=8080

# Logging Configuration
logging.level.com.project.SecurAuth=DEBUG
logging.level.org.springframework.security=DEBUG
```

**Important Security Notes:**
- Change the `jwt.secret` to a strong, random value in production
- Use environment variables for sensitive configurations
- Never commit credentials to version control

### Running the Application

1. **Using Maven Wrapper**

```bash
./mvnw spring-boot:run
```

2. **Using the JAR file**

```bash
./mvnw clean package
java -jar target/SecurAuth-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

---

## 📖 Usage

### Basic Workflow

1. **Register a new user**: Create an account with email verification
2. **Login**: Authenticate and receive JWT token
3. **Access Protected Resources**: Use the JWT token in Authorization header
4. **Manage Roles and Permissions**: Admin users can assign roles and permissions
5. **Monitor Activity**: View audit logs for security monitoring

### Example Requests

#### Register a New User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john.doe@example.com",
    "password": "SecureP@ssw0rd!",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

#### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "SecureP@ssw0rd!"
  }'
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "expiresIn": 86400000
}
```

#### Access Protected Resource

```bash
curl -X GET http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## 📚 API Documentation

### Authentication Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/auth/register` | Register a new user | No |
| POST | `/api/auth/login` | Authenticate user | No |
| POST | `/api/auth/logout` | Logout user | Yes |
| POST | `/api/auth/refresh` | Refresh JWT token | Yes |

### User Management Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/users` | Get all users | Yes (Admin) |
| GET | `/api/users/{id}` | Get user by ID | Yes |
| PUT | `/api/users/{id}` | Update user | Yes |
| DELETE | `/api/users/{id}` | Delete user | Yes (Admin) |
| POST | `/api/users/{id}/lock` | Lock user account | Yes (Admin) |
| POST | `/api/users/{id}/unlock` | Unlock user account | Yes (Admin) |

### Role Management Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/roles` | Get all roles | Yes (Admin) |
| POST | `/api/roles` | Create new role | Yes (Admin) |
| PUT | `/api/roles/{id}` | Update role | Yes (Admin) |
| DELETE | `/api/roles/{id}` | Delete role | Yes (Admin) |
| POST | `/api/roles/{id}/permissions` | Assign permissions to role | Yes (Admin) |

### Audit Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/audit` | Get audit logs | Yes (Admin/Security) |
| GET | `/api/audit/{id}` | Get specific audit log | Yes (Admin/Security) |
| GET | `/api/audit/user/{username}` | Get user audit logs | Yes (Admin/Security) |

---

## 🔒 Security Features

### Authentication Security
- **JWT Token-Based**: Stateless authentication without session management
- **Token Expiration**: Configurable token lifetime
- **Refresh Tokens**: Secure token renewal without re-authentication
- **Password Hashing**: BCrypt algorithm for secure password storage

### Access Control
- **Role-Based Authorization**: Multiple predefined roles (USER, ADMIN, MANAGER, SECURITY)
- **Permission System**: Fine-grained access control with custom permissions
- **Method Security**: Annotation-based security on service methods

### Account Protection
- **Failed Login Tracking**: Monitor and track failed authentication attempts
- **Account Locking**: Automatic locking after multiple failed attempts
- **Manual Blocking**: Administrative control to block suspicious accounts
- **Email Verification**: Ensure valid email addresses during registration

### Password Security
- **Strong Password Validation**: Custom validator for password strength
- **Minimum Requirements**: Enforce complexity requirements
- **Password History**: Prevent password reuse (can be implemented)

### Audit & Compliance
- **Comprehensive Logging**: All security events are logged
- **Severity Classification**: Events categorized by severity level
- **IP Address Tracking**: Record source IP for all actions
- **Timestamp Recording**: Precise timing of all events
- **Status Tracking**: Success/failure status for all operations

### Security Headers & CORS
- **CORS Configuration**: Control cross-origin requests
- **Security Headers**: Standard security headers implementation
- **CSRF Protection**: Protection against cross-site request forgery

---

## 📁 Project Structure

```
SecureAuth/
├── src/
│   ├── main/
│   │   ├── java/com/project/SecurAuth/
│   │   │   ├── controller/          # REST Controllers
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── UserController.java
│   │   │   │   ├── RoleController.java
│   │   │   │   └── AuditController.java
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── RegisterRequest.java
│   │   │   │   ├── AuthResponse.java
│   │   │   │   └── RoleRequest.java
│   │   │   ├── entity/              # JPA Entities
│   │   │   │   ├── User.java
│   │   │   │   ├── Role.java
│   │   │   │   ├── Permission.java
│   │   │   │   ├── AuditLog.java
│   │   │   │   └── Enum/
│   │   │   │       ├── RoleType.java
│   │   │   │       ├── PermissionType.java
│   │   │   │       ├── AuditStatus.java
│   │   │   │       └── SeverityLevel.java
│   │   │   ├── Exception/           # Custom Exceptions
│   │   │   │   ├── EmailAlreadyExistsException.java
│   │   │   │   ├── UserBlockedException.java
│   │   │   │   └── WeakPasswordException.java
│   │   │   ├── repository/          # Data Repositories
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── RoleRepository.java
│   │   │   │   └── AuditLogRepository.java
│   │   │   ├── security/            # Security Configuration
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── JwtService.java
│   │   │   │   ├── JwtFilter.java
│   │   │   │   └── AuthenticationEntryPoint.java
│   │   │   ├── service/             # Business Logic
│   │   │   │   ├── interfaces/
│   │   │   │   │   ├── AuthService.java
│   │   │   │   │   ├── UserService.java
│   │   │   │   │   ├── RoleService.java
│   │   │   │   │   ├── PermissionService.java
│   │   │   │   │   └── AuditService.java
│   │   │   │   └── impl/
│   │   │   │       ├── AuthServiceImpl.java
│   │   │   │       ├── UserServiceImpl.java
│   │   │   │       ├── RoleServiceImpl.java
│   │   │   │       ├── PermissionServiceImpl.java
│   │   │   │       └── AuditServiceImpl.java
│   │   │   ├── validation/          # Custom Validators
│   │   │   │   ├── StrongPassword.java
│   │   │   │   └── StrongPasswordValidator.java
│   │   │   └── SecurAuthApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/project/SecurAuth/
│           └── SecurAuthApplicationTests.java
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

---

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

### How to Contribute

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/AmazingFeature
   ```
3. **Commit your changes**
   ```bash
   git commit -m 'Add some AmazingFeature'
   ```
4. **Push to the branch**
   ```bash
   git push origin feature/AmazingFeature
   ```
5. **Open a Pull Request**

### Development Guidelines

- Follow Java coding conventions and Spring Boot best practices
- Write unit tests for new features
- Update documentation for any API changes
- Ensure all tests pass before submitting PR
- Keep commits atomic and write meaningful commit messages

### Code Style

- Use Lombok annotations to reduce boilerplate
- Follow RESTful API design principles
- Implement proper error handling and validation
- Add JavaDoc comments for public methods

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 📞 Contact & Support

- **Project Repository**: [https://github.com/Azzammoo10/SecureAuth-](https://github.com/Azzammoo10/SecureAuth-)
- **Issue Tracker**: [GitHub Issues](https://github.com/Azzammoo10/SecureAuth-/issues)

---

## 🙏 Acknowledgments

- Spring Boot Team for the excellent framework
- Spring Security Team for comprehensive security features
- PostgreSQL Team for the robust database system
- Open Source Community for continuous inspiration and support

---

<div align="center">

**Built with ❤️ using Spring Boot**

⭐ Star this repository if you find it helpful!

</div>
