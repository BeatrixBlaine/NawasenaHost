# NawasenaHost

A multi-hotel management platform built with **Java 25, Spring Boot 4 and a microservices architecture**.
One installation serves many hotels: an **ADMIN** manages the whole system, a **MANAGER** manages the hotel assigned to the account, and **STAFF** work the front desk.

## Features

- **Dashboard and reporting** – revenue, total bookings, occupancy rate, arrivals and departures, charts per day, status and source.
- **Hotel management** – several hotels in one system, ADMIN only.
- **Room management** – create, update and remove rooms per hotel.
- **Availability and inventory** – per room per night availability, room inventory counters, block and unblock rooms for maintenance.
- **Booking management** – create, confirm, cancel (also a confirmed and paid booking, with a reason), complete (check-out), filters and detail pages.
- **Payment management** – list, revenue report, confirmation, automatic refund when a paid booking is cancelled.
- **Customer management** – list, search, detail, edit.
- **Users, roles and permissions** – ADMIN, MANAGER, STAFF, tenant isolation per hotel.
- **Audit logs** – who did what and when, written by every service, filterable in the panel.
- **Concurrency control** – optimistic locking, pessimistic row locks and database unique constraints, see [docs/CONCURRENCY.md](docs/CONCURRENCY.md).
- **Load balancer** – nginx in front of replicated gateways and panels, Eureka client side balancing between services.
- **OTA ready** – bookings carry a `source` and an `externalReference`, see [docs/OTA-INTEGRATION.md](docs/OTA-INTEGRATION.md).

## Services

| Service | Port | Responsibility |
|---|---|---|
| nginx | 80 | Load balancer, the only published port |
| api-gateway | 8888 | Routing and JWT authorization |
| auth-service | 8087 | Login, users, OAuth2 authorization server, **audit logs** |
| hotel-service | 8081 | Hotels |
| room-service | 8082 | Rooms |
| availability-service | 8083 | Availability, room inventory, blocks, occupancy |
| booking-service | 8084 | Bookings, cancel, complete, booking report |
| customer-service | 8085 | Customers, create booking with customer |
| payment-service | 8086 | Payments, refunds, revenue report |
| eureka-server | 8761 | Service discovery |
| admin-panel-service | 8089 | Web interface (Thymeleaf, Tailwind CSS, Chart.js) |

## Quick start (Docker)

```bash
./scripts/generate-env.sh admin you@example.com   # creates .env with fresh secrets
docker compose up -d --build
```

Open `http://localhost` and log in with the ADMIN account printed by the script.
Then create the first hotel, add rooms and initialize the inventory. Full steps: [docs/DEPLOYMENT.md](docs/DEPLOYMENT.md).

## Documentation

| Document | Content |
|---|---|
| [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) | Services, data ownership, request flows |
| [docs/API.md](docs/API.md) | Endpoint reference |
| [docs/SECURITY.md](docs/SECURITY.md) | Tokens, roles, tenant isolation, audit logs |
| [docs/CONCURRENCY.md](docs/CONCURRENCY.md) | Locking strategy and load balancing |
| [docs/DEPLOYMENT.md](docs/DEPLOYMENT.md) | Environment, Docker, scaling, upgrade, backup |
| [docs/OTA-INTEGRATION.md](docs/OTA-INTEGRATION.md) | How an OTA / channel manager plugs in later |
| [docs/USER-GUIDE.md](docs/USER-GUIDE.md) | Using the admin panel |

## Technologies

Java 25, Spring Boot 4, Spring Security (OAuth2 Resource Server and Authorization Server), Spring Data JPA, Spring Cloud Gateway, OpenFeign, Eureka, MySQL, Thymeleaf, Tailwind CSS, Chart.js, Docker Compose, nginx, Maven.

## Local development

Every service runs from its own folder with `./mvnw spring-boot:run` (start `eureka-server` and `auth-service` first). The defaults in each `application.properties` point to `localhost` and the old per service MySQL ports. The admin panel CSS is already built (`static/css/output.css`); to change styles run `npm install` and `npm run build:css` in `admin-panel-service`.
