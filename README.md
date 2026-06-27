# DeliveryFlow — Cloud-Native Logistics Platform

> I built DeliveryFlow from the ground up as a production-style distributed system, designing the service boundaries, implementing event-driven communication between independently deployable microservices, solving real distributed systems challenges like competing consumers and JWT propagation across service boundaries, and orchestrating the entire platform with Docker and Kubernetes.

DeliveryFlow is a microservices-based logistics and delivery platform that simulates how modern distributed systems handle delivery operations at scale. Customers can create delivery orders, drivers can be assigned, and every status change is tracked in real time through asynchronous event-driven communication between services.

## Architecture

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

Each service owns its own PostgreSQL database and communicates with the rest of the system exclusively through RabbitMQ events, never through direct database access or synchronous HTTP calls to other services.

## Services

| Service | Port | Responsibility |
|---|---|---|
| api-gateway | 8080 | JWT validation and request routing |
| order-service | 8081 | Order management and user authentication |
| driver-service | 8082 | Driver profiles and availability |
| tracking-service | 8083 | Real-time order tracking history |
| notification-service | 8084 | Event-driven delivery alerts |

## Tech Stack

Java 17, Spring Boot 3.3, Spring Cloud Gateway, RabbitMQ, PostgreSQL, Docker, Docker Compose, Kubernetes

## What I Built and Why

I deliberately chose a microservices architecture because each service has a different rate of change and different scaling needs. If order creation spikes, I can scale just the order-service without touching anything else.

I implemented the fan-out messaging pattern after identifying and fixing a competing consumers bug where both the tracking-service and notification-service were sharing the same RabbitMQ queue. RabbitMQ was distributing messages round-robin between them, so each service only received roughly half the events. The fix was to give each consumer its own dedicated queue, both bound to the same exchange with the same routing key, so every service receives a complete independent copy of every event.

I handled authentication at the gateway level using JWT so none of the individual services need to implement auth logic. The gateway validates the token, extracts the user ID and role, and injects them as HTTP headers (X-User-Id, X-User-Role) for downstream services to use.

I published all service images to Docker Hub and wrote Kubernetes manifests for every component so the entire platform can be deployed to any cluster with a single command.

## Key Concepts Demonstrated

**Microservices architecture** — 5 independently deployable services, each with its own codebase, database, and deployment lifecycle.

**Event-driven communication** — Services communicate asynchronously via RabbitMQ. The order-service never calls tracking or notification directly. It publishes an event and moves on, which means a failure in either consumer has zero impact on order creation.

**Fan-out messaging pattern** — Each consumer service has its own dedicated queue bound to the same exchange, ensuring every service receives a full independent copy of every event.

**Database-per-service pattern** — Each service owns its own isolated PostgreSQL instance. No service queries another service's database directly, which gives true fault isolation and allows each service to evolve its schema independently.

**API Gateway pattern** — Single entry point handles JWT validation centrally and injects user context as request headers for downstream services.

**JWT authentication** — Stateless auth using HS256 signed tokens. The gateway validates on every request and public endpoints like register and login are whitelisted.

**Fault isolation** — A failure in the notification-service does not affect order creation or tracking. Undelivered events stay in the RabbitMQ queue and are processed when the service recovers.

**Containerisation** — Every service and dependency runs in its own Docker container. The full stack starts with a single command.

**Kubernetes orchestration** — Production-ready manifests for all services. Images are published to Docker Hub and pulled automatically by the cluster.

**Competing consumers problem** — Identified and fixed a distributed systems bug where shared queues caused round-robin message distribution between consumers instead of fan-out delivery.

## Running Locally

**Prerequisites:** Docker Desktop with Kubernetes enabled, Java 17, Maven 3.9

### Docker Compose

```bash
docker-compose up --build
```

The API Gateway will be available at http://localhost:8080 and the RabbitMQ management UI at http://localhost:15672 (guest/guest).

### Kubernetes

```bash
kubectl apply -f k8s/
```

All service images are published to Docker Hub and pulled automatically:

- onyenemerem/deliveryflow-api-gateway
- onyenemerem/deliveryflow-order-service
- onyenemerem/deliveryflow-driver-service
- onyenemerem/deliveryflow-tracking-service
- onyenemerem/deliveryflow-notification-service

To access the API through the cluster:

```bash
kubectl port-forward service/api-gateway 8090:8080
```

Then send requests to http://localhost:8090

## API Usage

**Register a user**

```
POST /api/auth/register
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "role": "CUSTOMER"
}
```

The response includes a JWT token. Use it as a Bearer token on all subsequent requests.

**Create an order**

```
POST /api/orders
Authorization: Bearer <token>
{
  "pickupAddress": "Drottninggatan 1, Stockholm",
  "deliveryAddress": "Kungsgatan 5, Stockholm",
  "recipientName": "Jane Smith",
  "recipientPhone": "+46701234567",
  "packageDescription": "Electronics"
}
```

**Check available drivers**

```
GET /api/drivers/available
Authorization: Bearer <token>
```

**Track an order**

```
GET /api/tracking/{orderId}
Authorization: Bearer <token>
```

## Event Flow

When an order is created, the order-service saves it to PostgreSQL and publishes an order.created event to RabbitMQ. The tracking-service consumes it and creates a tracking record. The notification-service consumes it independently and sends a simulated SMS to the recipient. Both services receive the event simultaneously through separate queues on the same exchange.

When a driver is assigned, the order-service publishes an order.assigned event. Both services consume it again independently, updating the tracking history and notifying the recipient that a driver is on the way.

## Project Structure

```
deliveryflow/
├── api-gateway/              Spring Cloud Gateway with JWT filter
├── order-service/            Orders, auth, JWT generation, RabbitMQ publisher
├── driver-service/           Driver profiles and availability
├── tracking-service/         Tracking history and RabbitMQ consumer
├── notification-service/     Notification consumer
├── k8s/                      Kubernetes manifests for all services and databases
├── docker-compose.yml        Local development orchestration
└── README.md
```

## Author

Joseph Onyenemerem
github.com/stacknerdjoe | josefportfolio.netlify.app | linkedin.com/in/joseph-onyenemerem-946201b6
