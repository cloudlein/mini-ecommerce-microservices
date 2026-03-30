# Mini Ecommerce Microservices

This repository contains the documentation and microservices for the Mini Ecommerce project.

## Documentation

All project documentation can be found in the [docs/](docs/) directory:

- [Project Overview](docs/project_overview.md)
- [API Specification](docs/api_specification.md)
- [Database Design](docs/database_design.md)
- [Task List](docs/task_list.md)
- [Installation Guide](docs/installation.md)

## Tech Stack

The project leverages a modern microservices architecture with the following technologies:

- **Microservices**:
  - **Go 1.22+** (Product Service - Gin, GORM)
  - **Java JDK 17+** (User Service, Gateway, Config Server, Discovery Client - Spring Boot 3)
- **Infrastructure**:
  - **PostgreSQL**: Primary relational database.
  - **Redis**: Caching and distributed locking.
  - **RabbitMQ**: Message broker for event-driven patterns (Saga).
- **Service Discovery**: Netflix Eureka.
- **Config Management**: Spring Cloud Config Server.
- **API Gateway**: Spring Cloud Gateway.

## Getting Started

To set up the project on your local machine, please follow the [Installation Guide](docs/installation.md).

