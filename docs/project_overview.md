# Project Overview: Mini E-Commerce Microservices

A polyglot microservices system built with Golang and Java (Spring Boot) to simulate a scalable e-commerce backend.

- [API Specification](file:///d:/Work/microservices-docs/api_specification.md)
- [Database Design](file:///d:/Work/microservices-docs/database_design.md)

## Design Flow

## System Interaction Flow

### 1. Authentication Phase
- **Client** → **API Gateway**: `POST /auth/login` (Submit credentials)
- **API Gateway** → **User Service**: Validate credentials
- **User Service** → **Client**: Return JWT Token

### 2. Discovery Phase
- **Client** → **API Gateway**: `GET /products`
- **API Gateway** → **Product Service**: Fetch product list
- **Product Service** → **Client**: Products (optimized with Redis Cache)

### 3. Order Placement Phase (Saga Flow)
1. **Initiation**: **Client** sends `POST /orders` (with JWT) to **API Gateway**.
2. **Order Creation**: **API Gateway** forwards to **Order Service**.
3. **Stock Validation**: **Order Service** calls **Product Service** via **gRPC** to check and reserve stock.
4. **Persistence**: **Order Service** saves order with status `PENDING`.
5. **Event Emission**: **Order Service** publishes `OrderCreated` event to **Message Broker (RabbitMQ/Kafka)**.
6. **Payment Processing**: **Payment Service** consumes `OrderCreated`, processes payment, and publishes `PaymentCompleted`.
7. **Completion**: **Order Service** consumes `PaymentCompleted`, updates order status to `COMPLETED`.
8. **Feedback**: **Order Service** sends success notification to **Client**.


## Project Structure

The project is organized as a monorepo containing multiple microservices and documentation.

```text
mini-ecommerce-microservices/ # Mini E-Commerce Microservices
├── order-service/          # Java (Spring Boot) - Order processing & Saga orchestration
├── product-service/        # Golang - Product catalog & Inventory management
├── user-service/           # Java (Spring Boot) - Authentication & User management
├── payment-service/        # Golang - Payment processing logic
├── api-gateway/            # API Gateway & Request routing
├── message-broker/         # RabbitMQ/Kafka configuration & setup
├── docs/                   # System documentation
│   ├── api-specification.md
│   ├── database-design.md
│   └── project-overview.md
```

## Features per Service

### User Service (Java - Spring Boot)
- **Authentication**: JWT-based secure login.
- **Registration**: New user onboarding.
- **Profile Management**: CRUD for user data.
- **Security**: Password hashing and role-based access.

### Product Service (Golang)
- **Catalog**: Product CRUD operations.
- **Inventory**: Atomic stock updates and reservations.
- **Search**: High-performance product search using GIN indexes.
- **Performance**: Redis caching for popular products.

### Order Service (Java - Spring Boot)
- **Order Lifecycle**: PENDING -> COMPLETED / FAILED / CANCELLED.
- **Validation**: Stock check via gRPC before order creation.
- **Distributed Transactions**: Orchestrates the Saga pattern for consistency.

### Payment Service (Golang)
- **Payment Processing**: Simulates third-party gateway integration.
- **Event Handling**: Asynchronous processing via message queues.
- **Reliability**: Internal retry logic for transient failures.

## Communication Patterns
1. **Synchronous**: REST (Client to Gateway) and gRPC (Internal service-to-service calls for immediate validation).
2. **Asynchronous**: Event-driven architecture using RabbitMQ/Kafka for long-running workflows like payments.
