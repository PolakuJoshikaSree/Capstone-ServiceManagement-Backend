# Service Management System – Backend

## Overview

The **Service Management System Backend** is a Spring Boot–based RESTful application designed to manage end-to-end service requests for a service-based organization.  
It handles **authentication, booking lifecycle management, technician assignment, and billing integration**, following clean architecture and enterprise best practices.

This backend serves as the core business layer for the full-stack application, exposing secure APIs consumed by the Angular frontend.

---

## Technology Stack

### Backend
- Java 17+
- Spring Boot 3.x
- Spring Web (REST APIs)
- Spring Data JPA
- Hibernate ORM
- MySQL (Primary database)
- Spring Security with JWT
- Redis (Token storage)
- Maven (Build tool)

---

## Architecture

The backend follows a **Layered Architecture**:

- **Controller Layer**
  - Exposes REST APIs
  - Handles request validation and authentication context
- **Service Layer**
  - Contains business logic
  - Manages booking lifecycle and assignments
- **Repository Layer**
  - Uses Spring Data JPA / MongoRepository
  - Handles database operations
- **DTO Layer**
  - Prevents entity exposure
  - Enables clean request/response contracts
- **Security Layer**
  - JWT authentication
  - Role-based access control (RBAC)

---

## User Roles Supported

1. **Customer**
   - Register and login
   - Create service requests
   - View booking history
   - Track service status

2. **Service Manager**
   - View all requested bookings
   - Assign technicians to bookings
   - Monitor booking status

3. **Technician**
   - View assigned bookings
   - Update service status (In Progress, Completed)

4. **Admin (Basic)**
   - User and role management (implicit via Auth service)

---

## Core Modules

### 1. Authentication & Security
- User registration and login
- JWT-based authentication
- Role-based authorization
- Token validation using Redis
- Secure password handling (BCrypt)

### 2. Booking Management
- Create service bookings
- Service lifecycle tracking:
  - `REQUESTED → ASSIGNED → IN_PROGRESS → COMPLETED`
- Cancel or update bookings
- Pagination and filtering support

### 3. Technician Assignment
- Assign technicians to bookings
- Track technician availability
- Prevent invalid assignments
- Free technician after service completion

### 4. Billing Integration
- Automatic invoice generation on booking completion
- Integration with Billing microservice
- Line-item–based invoice creation

---

## Key API Endpoints

### Authentication
- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/logout`
- `POST /api/auth/refresh-token`

### Bookings
- `POST /api/bookings`
- `GET /api/bookings`
- `GET /api/bookings/my`
- `GET /api/bookings/technician/my`
- `PUT /api/bookings/{bookingId}/status`
- `PUT /api/bookings/{bookingId}/assign/{technicianId}`

---

## Booking Lifecycle

1. Customer creates a booking → `REQUESTED`
2. Manager assigns technician → `ASSIGNED`
3. Technician starts work → `IN_PROGRESS`
4. Technician completes work → `COMPLETED`
5. Invoice auto-generated on completion

---

## Security Highlights

- Stateless authentication using JWT
- Redis-backed token validation
- Role-based endpoint protection
- Secure inter-service communication
- CORS configuration for frontend access

---

## Error Handling

- Centralized exception handling
- Meaningful HTTP status codes
- Validation and business rule enforcement
- Clean error messages for API consumers

---

## Testing & Validation

- APIs tested using Postman
- JWT and role validation tested across services
- Pagination and filtering verified
- End-to-end booking lifecycle validated

---

## Future Enhancements

- Advanced reporting and analytics
- Notification service (Email/SMS)
- Payment gateway integration
- Full admin management module
- Swagger/OpenAPI documentation

---

## Conclusion

This backend implementation demonstrates:
- Real-world service management workflows
- Secure, scalable REST API design
- Clean separation of concerns
- Enterprise-grade authentication and authorization

It serves as a robust foundation for a production-ready Service Management System.
