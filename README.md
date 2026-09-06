# CommerceFlow

CommerceFlow is a secure full-stack e-commerce application built with Spring Boot, React, and PostgreSQL.

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

### Backend

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

### Frontend

- React
- Vite
- React Router
- Axios
- JavaScript
- CSS

## Architecture

CommerceFlow follows a layered backend architecture:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Entity
    ↓
PostgreSQL Database


Security is handled through Spring Security and JWT authentication.

The frontend communicates with the backend through REST APIs:

React Frontend
      ↓
   Axios
      ↓
Spring Boot REST API
      ↓
Spring Security + JWT
      ↓
Service Layer
      ↓
Repository Layer
      ↓
PostgreSQL



Frontend

CommerceFlow includes a React-based frontend that communicates with the Spring Boot REST API using Axios.

Frontend Features
Customer login and authentication
Product browsing
Product details
Shopping cart
Checkout
Order listing and order details
Payment workflow
Customer address management
Product reviews
Admin dashboard
Admin product management
Admin order management
Admin payment management
Role-based route protection
Responsive user interface
Project Structure

CommerceFlow is organized into separate backend and frontend applications.

CommerceFlow/
│
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/commerceflow/
│   │   │   │       ├── auth/
│   │   │   │       ├── cart/
│   │   │   │       ├── category/
│   │   │   │       ├── order/
│   │   │   │       ├── payment/
│   │   │   │       ├── product/
│   │   │   │       ├── review/
│   │   │   │       ├── user/
│   │   │   │       └── security/
│   │   │   │
│   │   │   └── resources/
│   │   │       ├── db/
│   │   │       │   └── migration/
│   │   │       └── application.properties
│   │   │
│   │   └── test/
│   │
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   ├── context/
│   │   ├── pages/
│   │   ├── services/
│   │   ├── App.jsx
│   │   └── main.jsx
│   │
│   ├── public/
│   ├── package.json
│   └── vite.config.js
│
└── README.md

API Overview

The CommerceFlow backend exposes RESTful APIs for authentication, products, categories, reviews, addresses, cart, orders, and payments.

Authentication
Method	Endpoint	Access
POST	/api/auth/register	Public
POST	/api/auth/login	Public


Users
Method	Endpoint	Access
GET	/api/users/me	Authenticated
GET	/api/users	ADMIN
GET	/api/users/{id}	ADMIN


Products
Method	Endpoint	Access
GET	/api/v1/products	Public
GET	/api/v1/products/{id}	Public
POST	/api/v1/products	ADMIN
PUT	/api/v1/products/{id}	ADMIN
DELETE	/api/v1/products/{id}	ADMIN


Categories
Method	Endpoint	Access
GET	/api/v1/categories	Public
POST	/api/v1/categories	ADMIN
PUT	/api/v1/categories/{id}	ADMIN
DELETE	/api/v1/categories/{id}	ADMIN


Cart
Method	Endpoint	Access
GET	/api/cart	CUSTOMER
POST	/api/cart/items	CUSTOMER
PUT	/api/cart/items/{productId}	CUSTOMER
DELETE	/api/cart/items/{productId}	CUSTOMER
DELETE	/api/cart	CUSTOMER
POST	/api/cart/checkout	CUSTOMER


Orders
Method	Endpoint	Access
POST	/api/orders	CUSTOMER
GET	/api/orders/my	CUSTOMER
GET	/api/orders/{id}	Owner / ADMIN
GET	/api/orders/{id}/history	Owner / ADMIN
POST	/api/orders/{id}/cancel	Owner / ADMIN
GET	/api/orders	ADMIN
PATCH	/api/orders/{id}/status	ADMIN
DELETE	/api/orders/{id}	ADMIN
GET	/api/orders/stats	ADMIN
GET	/api/orders/dashboard	ADMIN


Payments
Method	Endpoint	Access
POST	/api/payments	CUSTOMER
GET	/api/payments/my	CUSTOMER
GET	/api/payments/order/{orderId}	Owner / ADMIN
GET	/api/payments/{paymentId}	Owner / ADMIN
GET	/api/payments	ADMIN
PUT	/api/payments/{paymentId}/success	ADMIN
PUT	/api/payments/{paymentId}/failed	ADMIN
PUT	/api/payments/{paymentId}/refund	ADMIN


Reviews
Method	Endpoint	Access
POST	/api/v1/products/{productId}/reviews	CUSTOMER
GET	/api/v1/products/{productId}/reviews	Public
PUT	/api/v1/reviews/{reviewId}	Review Owner
DELETE	/api/v1/reviews/{reviewId}	Review Owner


Health
Method	Endpoint	Access
GET	/api/v1/health	Public


Authentication & Security

CommerceFlow uses JWT-based authentication with Spring Security.

Authentication Flow

User
  ↓
Register / Login
  ↓
Spring Boot Authentication API
  ↓
BCrypt Password Verification
  ↓
JWT Token Generated
  ↓
Frontend Stores JWT
  ↓
JWT Sent with Protected API Requests
  ↓
JwtAuthenticationFilter
  ↓
Token Validation
  ↓
Role-Based Authorization
  ↓
Controller / Service


JWT Authentication

After successful login, the backend returns a JWT token.

Protected API requests must include:

Authorization: Bearer <JWT_TOKEN>

The JWT contains the authenticated user's identity and role.


Role-Based Authorization
CommerceFlow supports two roles:

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


Security Measures
JWT-based stateless authentication
BCrypt password hashing
Role-based access control
Login rate limiting
Backend request validation
Resource ownership checks
Stock validation
CORS configuration
CSRF disabled for stateless API authentication
Security response headers
Protected administrative endpoints
Global exception handling
Environment-based configuration for secrets


HTTP Security Responses
401 Unauthorized
→ Authentication is missing or invalid.

403 Forbidden
→ User is authenticated but does not have permission
  to access the requested resource.


Database
CommerceFlow uses PostgreSQL as its relational database.

PostgreSQL
    ↓
commerceflow_db


The application uses Spring Data JPA and Hibernate for database access.

Main Entities
User
Product
Category
ProductImage
Review
Address
Cart
CartItem
Order
OrderItem
OrderStatusHistory
Payment


Entity Relationships :
User
 ├── Addresses
 ├── Cart
 ├── Orders
 │    ├── OrderItems
 │    └── OrderStatusHistory
 ├── Payments
 └── Reviews

Category
 └── Products
      └── ProductImages

Product
 ├── OrderItems
 ├── Reviews
 └── CartItems

Database Migrations:

Database schema changes are managed using Flyway.

Migration files are located at:

src/main/resources/db/migration/

Migrations follow the Flyway naming convention:

V1__description.sql
V2__description.sql
V3__description.sql

Flyway automatically applies pending migrations when the application starts.


Data Integrity:

The application uses database and JPA constraints for data integrity, including:

Non-null constraints
Unique constraints
Foreign-key relationships
One-to-one relationships
One-to-many relationships
Optimistic locking where required
Transactional service operations

Order & Payment Workflow :

CommerceFlow implements an order lifecycle with controlled status transitions and a separate payment lifecycle.

Order Workflow :

A customer can create an order from the shopping cart or through the order API.
Cart
  ↓
Checkout
  ↓
Order Created
  ↓
PENDING
  ↓
CONFIRMED
  ↓
SHIPPED
  ↓
DELIVERED

An eligible pending order can also be cancelled:
PENDING
   ↓
CANCELLED


Order Status :

Supported order statuses include:

PENDING
CONFIRMED
SHIPPED
DELIVERED
CANCELLED


Payment Workflow :

Payments have an independent lifecycle:
PENDING
   ├──→ SUCCESS
   │      ↓
   │   REFUNDED
   │
   └──→ FAILED

Only administrators can mark payments as successful, failed, or refunded.

Payment Methods :

CommerceFlow supports:
UPI
Card
Cash on Delivery


Payment Integrity :

Payment amounts are derived from the order total on the backend rather than being trusted from the client request.

The system also prevents:

Duplicate payments for the same order
Payments for cancelled orders
Refunds for unsuccessful payments
Invalid payment state transitions


Inventory Management :

Product stock is validated during cart and order operations.

The backend prevents:

Zero or negative quantities
Quantities exceeding available stock
Orders containing unavailable products
Invalid product references

Stock is updated as part of transactional order processing.

Testing :

CommerceFlow includes automated backend tests and manual API security testing.

Automated Tests :

Run the complete test suite with:
mvn test

The test suite covers areas including:

Spring Boot application context
Product repository operations
Login rate limiting
Security integration
Authentication and authorization behavior


Security Testing :

Manual security testing was performed for important authorization and business-rule scenarios, including:

CUSTOMER cannot access admin-only APIs
CUSTOMER cannot create or modify products
CUSTOMER cannot manage categories
CUSTOMER cannot change order status
ADMIN cannot create customer orders
Payment operations are role-restricted
Invalid payment state transitions are rejected
Order cancellation rules are enforced
Stock limits are enforced
Invalid quantities are rejected
Duplicate product IDs in orders are rejected
Duplicate payments are rejected
Cancelled orders cannot receive payments
Review ownership is validated


Expected HTTP Status Codes :

200 OK
→ Successful request

201 Created
→ Resource successfully created

400 Bad Request
→ Invalid request or business-rule violation

401 Unauthorized
→ Authentication is missing or invalid

403 Forbidden
→ Authenticated user lacks permission

404 Not Found
→ Requested resource does not exist


Environment Configuration :

CommerceFlow uses environment variables for sensitive and environment-specific configuration.

Required Environment Variables :
DB_PASSWORD
JWT_SECRET

Example:
DB_PASSWORD=your_postgresql_password
JWT_SECRET=your_base64_jwt_secret

Sensitive values must not be hardcoded in source code or committed to Git.

Never commit:

Database passwords
JWT secrets
API keys
Other credentials

For local development, configure the required environment variables in the operating system or development environment.

Prerequisites :

Before running CommerceFlow, install:

Java 21
Maven 3.9+
PostgreSQL 18+
Node.js and npm
Git


Database Setup :

Create a PostgreSQL database:

CREATE DATABASE commerceflow_db;

Running the Application :

Backend:
Navigate to the backend directory:

cd backend

Set the required environment variables, then run:

mvn spring-boot:run

The backend will start on:

http://localhost:8080

Frontend:

Navigate to the frontend directory:

cd frontend

Install dependencies:

npm install

Start the development server:

npm run dev

The frontend will normally be available at:

http://localhost:5173


Health Check :

GET /api/v1/health

The health endpoint is publicly accessible.

Production Profile :

Run CommerceFlow with the production profile:

mvn spring-boot:run "-Dspring-boot.run.profiles=prod"

The production profile disables Swagger/OpenAPI UI and SQL logging.

API Documentation :

In the default development profile, Swagger UI is available at:

http://localhost:8080/swagger-ui/index.html

OpenAPI specification:

http://localhost:8080/v3/api-docs

Swagger/OpenAPI is disabled in the production profile.

Screenshots :

The application provides separate interfaces for customers and administrators.

Customer Interface :

Login
Home page
Product listing
Product details
Shopping cart
Checkout and payment
Orders
Order details


Admin Interface :

Admin dashboard
Product management
Create and edit products
Order management
Payment management

Screenshots can be added here to demonstrate the application's UI and major workflows.

Future Improvements :

Potential improvements for future versions include:

Real payment gateway integration
Email notifications for orders and payments
Product search and advanced filtering
Product pagination and sorting
Wishlist functionality
Coupon and discount management
Inventory management enhancements
Customer profile management
Improved admin analytics
Automated API and frontend test coverage
Docker containerization
CI/CD pipeline
Cloud deployment
Production database configuration
Application monitoring and logging