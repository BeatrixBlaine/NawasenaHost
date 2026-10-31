# NawasenaHost

A hotel management platform built with **Java, Spring Boot, and Microservices Architecture**, designed to handle hotel operations through independent, interconnected services.

## Overview

NawasenaHost provides a centralized platform for managing hotel operations, including hotel information, room management, availability, bookings, payments, and customer data.

The system follows a microservices architecture, with centralized authentication, API routing, and service discovery. It is designed with future multi-hotel support and OTA integrations in mind.

## Core Services

- **API Gateway** – Centralized API routing and JWT-based request authorization.
- **Auth Service** – Authentication, JWT generation, and role-based access control.
- **Customer Service** – Customer profile and booking customer information management.
- **Hotel Service** – Hotel information management.
- **Room Service** – Room information and capacity management.
- **Availability Service** – Room availability tracking.
- **Booking Service** – Booking creation and booking status management.
- **Payment Service** – Payment processing and payment status management.
- **Admin Panel** – Hotel management interface built with Thymeleaf and Tailwind CSS.

## Technologies Used

**Backend**
- Java 25
- Spring Boot 4
- Spring Security
- Spring Data JPA
- Spring Cloud Gateway
- OpenFeign
- OAuth2 Resource Server (JWT)

**Database**
- MySQL

**Infrastructure & Tools**
- Docker & Docker Compose
- Netflix Eureka
- Maven
- Git & GitHub
- Postman

**Frontend**
- Thymeleaf
- Tailwind CSS

## Architecture

- Microservices-based architecture
- Service discovery and inter-service communication
- Centralized API Gateway
- JWT-based authentication and authorization
- Containerized service deployment

## Project Status

Currently under active development. Core backend services and infrastructure have been implemented, with the admin panel under development.

**Planned Enhancements**
- Multi-hotel management support
- OTA integration through a Channel Manager
- Further improvements to inter-service communication and security
