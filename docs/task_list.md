# Project Checklist: Microservices Development

Track the progress of the Mini E-Commerce Microservices project across all phases.

## Phase 1: Core Infrastructure
- [x] Initialize Docker Compose with PostgreSQL, Redis, and RabbitMQ
- [x] Set up API Gateway (Spring Cloud Gateway)
- [x] Implement Service Discovery (Eureka)
- [x] Configure centralized configuration (Spring Cloud Config)

## Phase 2: Service Development
### User Service (Java)
- [x] Define project metadata and structure
- [x] Auth module (JWT + Argon2)
- [x] User CRUD operations + cursor pagination
- [ ] Database migrations (Liquibase)

### Product Service (Go)
- [x] Inventory management logic & Domain models
- [x] GORM & PostgreSQL persistence layer
- [x] RESTful API with Gin (Reserve Stock)
- [ ] Redis caching integration
- [ ] gRPC server for stock checks

### Order Service
- [ ] Order lifecycle state machine (Saga Orchestration)
- [ ] Saga pattern choreography logic
- [ ] Order-related event producers/consumers

### Payment Service
- [ ] Payment gateway simulation (Stripe/PayPal Mock)
- [ ] Payment transaction records & status tracking
- [ ] Retry & dead-letter queue (DLQ) for payment events

## Phase 3: Observability & Security
- [ ] Prometheus + Grafana dashboards
- [ ] ELK Stack / Loki for centralized logging
- [ ] OpenTelemetry for distributed tracing
- [ ] Keycloak integration for OIDC

## Phase 4: DevOps & Deployment
- [x] CI/CD pipelines (GitHub Actions)
- [ ] Helm charts for Kubernetes
- [ ] Horizontal Pod Autoscaling (HPA)
- [ ] Stress testing with JMeter / k6
