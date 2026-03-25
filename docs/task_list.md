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
- [ ] Inventory management logic
- [ ] Redis caching integration
- [ ] GIN and B-Tree indexing setup
- [ ] gRPC server for stock checks

### Order & Payment Services
- [ ] Order lifecycle state machine
- [ ] Saga pattern choreography logic
- [ ] Payment gateway simulation
- [ ] Retry & dead-letter queue (DLQ) for events

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
