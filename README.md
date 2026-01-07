# Capstone Service Management System – Backend

This repository contains the backend implementation of the Service Management System, developed as part of a capstone project. The system follows a microservices architecture using Spring Boot, Spring Cloud, and MongoDB.

##  Features

- User authentication and authorization
- Service catalog management
- Booking creation, assignment, cancellation, and rescheduling
- Invoice generation and payment status tracking
- Notification handling
- Service discovery and API gateway routing
- Centralized configuration management

##  Microservices Overview

The backend consists of the following services:

- Auth Service – User authentication and authorization  
- Service Catalog Service – Manages services and categories  
- Booking Service – Handles booking lifecycle (create, assign, reschedule, cancel)  
- Billing Service – Generates invoices and manages payments  
- Notification Service – Sends notifications to users and technicians  
- API Gateway – Routes requests to appropriate services  
- Service Registry (Eureka) – Service discovery  
- Config Server – Centralized configuration management  

##  Tech Stack

- Java 17
- Spring Boot
- Spring Cloud (Eureka, Config Server, Gateway)
- MongoDB
- RabbitMQ (event-driven communication)
- Maven
- REST APIs

##  Project Structure

Capstone-ServiceManagement-Backend/
├── api-gateway/
├── auth-service/
├── booking-service/
├── billing-service/
├── notification-service/
├── service-catalog-service/
├── service-registry/
├── config-server/
└── README.md

##  Prerequisites

- Java 17 or higher
- Maven
- MongoDB
- RabbitMQ
- Git

##  How to Run the Project

### 1. Clone the repository

git clone https://github.com/PolakuJoshikaSree/Capstone-ServiceManagement-Backend.git  
cd Capstone-ServiceManagement-Backend

### 2. Start supporting services

- Start MongoDB  
- Start RabbitMQ  

### 3. Run services in this order

1. Config Server  
2. Service Registry (Eureka)  
3. Auth Service  
4. Remaining microservices  
5. API Gateway  

Each service can be started using:

mvn spring-boot:run

##  Event Communication

RabbitMQ is used for asynchronous communication. Events such as Booking Completed, Booking Cancelled, and Invoice Generated are published and consumed across services.

##  Security

- Authentication handled via Auth Service
- Token-based access control
- Role-based operations (Customer, Technician, Admin)

##  Design Highlights

- Microservices architecture
- Loose coupling using event-driven communication
- Centralized configuration management
- Scalable and maintainable design
- Clear separation of responsibilities

##  Future Enhancements

- Payment gateway integration
- Invoice PDF generation
- Advanced analytics and reports
- Caching using Redis
- Role-based dashboards

##  Author

Joshika Sree Polaku  
Capstone Project – Service Management System

##  License

This project is developed for academic purposes as part of a capstone submission.
