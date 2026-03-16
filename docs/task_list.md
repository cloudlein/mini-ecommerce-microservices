# Project Checklist: Microservices Development

Track the progress of the Mini E-Commerce Microservices project across all phases.

## Phase 1: Core Infrastructure
- [ ] Initialize Docker Compose with PostgreSQL, Redis, and RabbitMQ
- [ ] Set up API Gateway (Spring Cloud Gateway / Kong)
- [ ] Implement Service Discovery (Consul/Eureka)
- [ ] Configure centralized configuration (Spring Cloud Config)

## Phase 2: Service Development
### User Service (Java)
- [ ] Auth module (JWT + Argon2)
- [ ] User CRUD operations
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
- [ ] CI/CD pipelines (GitHub Actions)
- [ ] Helm charts for Kubernetes
- [ ] Horizontal Pod Autoscaling (HPA)
- [ ] Stress testing with JMeter / k6
