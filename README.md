
# CommerceFlow

CommerceFlow is a secure RESTful e-commerce backend built with Spring Boot and PostgreSQL.

## Features

- JWT-based authentication and authorization
- Customer and admin roles
- Product management
- Category management
- Product reviews
- Customer address management
- Shopping cart
- Order management
- Order status history
- Payment management
- Product image upload
- Stock management
- Order statistics and admin dashboard
- API validation and global exception handling
- Login rate limiting
- CORS configuration
- Database migrations with Flyway
- Swagger/OpenAPI documentation
- Production profile
- Health check endpoint

## Tech Stack

- Java 21
- Spring Boot 4.1
- Spring Security
- Spring Data JPA / Hibernate
- PostgreSQL
- Flyway
- JWT
- Maven
- Swagger / OpenAPI
- Lombok

## Architecture

CommerceFlow follows a layered backend architecture:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL Database
```

Security is handled through Spring Security and JWT authentication.

API Base URL
http://localhost:8080
Health Check
GET /api/v1/health

The health endpoint is publicly accessible.

Prerequisites

Before running CommerceFlow, install:

Java 21
Maven 3.9+
PostgreSQL 18+
Git
Database Setup

Create a PostgreSQL database:

CREATE DATABASE commerceflow_db;
Environment Variables

CommerceFlow requires the following environment variables:

DB_PASSWORD
JWT_SECRET
CORS_ALLOWED_ORIGINS

Example:

DB_PASSWORD=your_postgresql_password
JWT_SECRET=your_base64_jwt_secret
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173

Do not commit real passwords, JWT secrets, or other credentials to Git.

Running the Application

Set the required environment variables, then run:

mvn spring-boot:run

The API will start on:

http://localhost:8080
Running Tests
mvn test
Database Migrations

Database schema changes are managed using Flyway.

Migrations are located at:

src/main/resources/db/migration

Flyway automatically applies pending migrations when the application starts.

Production Profile

Run CommerceFlow with the production profile:

mvn spring-boot:run "-Dspring-boot.run.profiles=prod"

The production profile disables Swagger/OpenAPI UI and SQL logging.

API Documentation

In the default development profile, Swagger UI is available at:

http://localhost:8080/swagger-ui/index.html

OpenAPI specification:

http://localhost:8080/v3/api-docs

Swagger/OpenAPI is disabled in the production profile.

Authentication

CommerceFlow uses JWT-based authentication.

Register
POST /api/auth/register
Login
POST /api/auth/login

A successful login returns a JWT token.

For protected endpoints, send the token using:

Authorization: Bearer <JWT_TOKEN>
Authorization

The API uses role-based access control.

Available roles include:

CUSTOMER
ADMIN

Customer-only operations include:

Cart management
Creating orders
Managing personal addresses
Managing personal reviews
Creating payments

Admin-only operations include:

User management
Product management
Category management
Order administration
Payment administration
Dashboard and order statistics

Unauthorized requests are rejected with HTTP 401 Unauthorized.

Authenticated users without sufficient permissions receive HTTP 403 Forbidden.