# Installation Guide

This guide provides step-by-step instructions for setting up and running the Mini E-Commerce Microservices project on your local machine.

## Prerequisites

Before you begin, ensure you have the following installed:

- **Docker & Docker Compose**: For infrastructure orchestration.
- **Go 1.22+**: For the `product-service`.
- **Java JDK 17+**: For Spring Boot based services (`user-service`, `gateway`, `config-server`, `discovery-client`).
- **PostgreSQL Client (Optional)**: For manual database inspection.
- **Git**: For cloning the repository.

---

## Initial Configuration

1. **Clone the Repository**:
   ```bash
   git clone <repository-url>
   cd mini-ecommerce-microservices
   ```

2. **Environment Variables**:
   Copy the example environment file and update it with your local settings:
   ```bash
   cp env.example .env
   ```
   > [!IMPORTANT]
   > Ensure the `JWT_SECRET` is a secure, base64-encoded string for production use. For local development, the default in `env.example` is sufficient.

---

## Infrastructure Setup

Start the core infrastructure (Database, Cache, and Message Broker) using Docker Compose:

```bash
docker-compose up -d
```

This will start:
- **PostgreSQL**: Accessible on port `5432` (default).
- **Redis**: Accessible on port `6379`.
- **RabbitMQ**: Accessible on port `5672` (AMQP) and `15672` (Management UI).

### Database Initialization
The database `ecommerce_db` is automatically created and initialized using the script located at `docker/postgres/init-db.sql`.

---

## Running the Microservices

The services must be started in a specific order to ensure correct configuration and service discovery.

### 1. Config Server (First)
Centralized configuration for all Java services.
```bash
cd config-server
./gradlew bootRun
```
*Port: 8888*

### 2. Discovery Client (Second)
Eureka server for service registration and discovery.
```bash
cd ../discovery-client
./gradlew bootRun
```
*Port: 8761*

### 3. User Service
User management and authentication.
```bash
cd ../user-service
./gradlew bootRun
```
*Port: 8080*

### 4. Product Service (Go)
Inventory and product management.
```bash
cd ../product-service
go run cmd/main.go
```
*Port: 8081*

### 5. API Gateway (Last)
The entry point for all client requests.
```bash
cd ../gateway
./gradlew bootRun
```
*Port: 8000*

---

## Verification

Once all services are running, you can verify the setup:

- **Eureka Dashboard**: [http://localhost:8761](http://localhost:8761) (Check if all services are registered).
- **RabbitMQ Management**: [http://localhost:15672](http://localhost:15672) (Default Login: `guest`/`guest`).
- **API Gateway Test**:
  ```bash
  curl http://localhost:8000/api/v1/products/reserve -X POST
  ```

---

## Troubleshooting

- **Port Conflicts**: If the ports (8080, 8081, 8888, 8761, 8000) are already in use, update the `.env` file or the service `application.yaml` files.
- **Config Server Search Path**: Ensure the `search-locations` in `config-server/src/main/resources/application.yaml` points to the correct absolute path of your `config-repo`.
- **Database Connection**: If services fail to connect to Postgres, verify that the container is healthy: `docker ps`.
