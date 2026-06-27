# DeliveryFlow — Cloud-Native Logistics Platform

A production-style microservices backend simulating a real-world logistics and delivery platform. Built to demonstrate distributed systems engineering concepts including event-driven architecture, API gateway routing, JWT authentication, and Kubernetes orchestration.

## Architecture Overview

```
                        ┌─────────────────┐
                        │   API Gateway   │  :8080
                        │  (JWT Auth +    │
                        │   Routing)      │
                        └────────┬────────┘
                                 │
              ┌──────────────────┼──────────────────┐
              │                  │                  │
     ┌────────▼───────┐ ┌───────▼────────┐ ┌──────▼──────────┐
     │  Order Service │ │ Driver Service │ │Tracking Service │
     │    :8081       │ │    :8082       │ │    :8083        │
     │  PostgreSQL    │ │  PostgreSQL    │ │  PostgreSQL     │
     └────────┬───────┘ └───────┬────────┘ └──────┬──────────┘
              │                  │                  │
              └──────────────────┼──────────────────┘
                                 │ RabbitMQ Events
                        ┌────────▼────────┐
                        │  Notification   │
                        │    Service      │  :8084
                        └─────────────────┘
```

## Services

| Service | Port | Responsibility |
|---|---|---|
| api-gateway | 8080 | JWT validation, request routing |
| order-service | 8081 | Orders, auth (register/login) |
| driver-service | 8082 | Driver profiles, availability |
| tracking-service | 8083 | Order tracking history |
| notification-service | 8084 | Event-driven SMS/email alerts |

## Tech Stack

- **Java 17** + **Spring Boot 3.3**
- **Spring Cloud Gateway** — API Gateway with JWT filter
- **RabbitMQ** — Async event-driven communication between services
- **PostgreSQL** — Each service owns its own database (database-per-service pattern)
- **Docker** + **Docker Compose** — Local development
- **Kubernetes** — Production orchestration

## Key Concepts Demonstrated

- **Microservices architecture** — 5 independently deployable services
- **Event-driven communication** — Services communicate via RabbitMQ, not direct HTTP calls
- **Database-per-service pattern** — Each service has its own isolated PostgreSQL instance
- **API Gateway pattern** — Single entry point, JWT auth handled centrally
- **Fault isolation** — A failure in notification-service does not affect order creation
- **Containerisation** — Every service runs in its own Docker container

## Running Locally

### Prerequisites
- Docker Desktop installed and running
- Java 17+, Maven 3.9+

### Start all services with Docker Compose

```bash
docker-compose up --build
```

Services will be available at:
- API Gateway: http://localhost:8080
- RabbitMQ Management UI: http://localhost:15672 (guest/guest)

### Run with Kubernetes (Docker Desktop)

Enable Kubernetes in Docker Desktop settings, then:

```bash
kubectl apply -f k8s/
```

## API Usage

### 1. Register a user
```
POST http://localhost:8080/api/auth/register
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "role": "CUSTOMER"
}
```
Response includes a JWT token.

### 2. Create an order (use token from step 1)
```
POST http://localhost:8080/api/orders
Authorization: Bearer <token>
{
  "pickupAddress": "Drottninggatan 1, Stockholm",
  "deliveryAddress": "Kungsgatan 5, Stockholm",
  "recipientName": "Jane Smith",
  "recipientPhone": "+46701234567",
  "packageDescription": "Electronics"
}
```

### 3. Check available drivers
```
GET http://localhost:8080/api/drivers/available
Authorization: Bearer <token>
```

### 4. Track an order
```
GET http://localhost:8080/api/tracking/{orderId}
Authorization: Bearer <token>
```

## Event Flow

When an order is created:
1. `order-service` saves the order to its PostgreSQL DB
2. `order-service` publishes an `order.created` event to RabbitMQ
3. `tracking-service` consumes the event → creates a tracking record
4. `notification-service` consumes the event → logs a simulated SMS to the recipient

When a driver is assigned:
1. `order-service` updates order status to `ASSIGNED`
2. Publishes an `order.assigned` event
3. `tracking-service` records the assignment event
4. `notification-service` sends a simulated delivery notification

## Project Structure

```
deliveryflow/
├── api-gateway/              # Spring Cloud Gateway + JWT filter
├── order-service/            # Orders + Auth (register/login/JWT generation)
├── driver-service/           # Driver profiles + availability
├── tracking-service/         # Tracking history + RabbitMQ consumer
├── notification-service/     # Notification consumer (event-driven)
├── k8s/                      # Kubernetes manifests for all services
├── docker-compose.yml        # Local development orchestration
└── README.md
```

## Author

Joseph Onyenemerem — github.com/stacknerdjoe
