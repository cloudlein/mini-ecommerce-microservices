# Tutorial: Advanced Inventory Management with Clean Architecture (Go)

This guide outlines how to build the `product-service` using **Clean/Hexagonal Architecture**, ensuring maintainability, testability, and clear separation of concerns.

## 1. Clean Architecture Hierarchy

We will structure the service around the **Internal Domain**, with distinct layers for Adapters (In/Out) and Application Ports.

```text
product-service/
├── cmd/
│   └── main.go                 # Entry point: Dependency Injection & Bootstrapping
├── internal/
│   ├── product/                # Boundary for Product/Inventory domain
│   │   ├── adapter/
│   │   │   ├── in/             # Driving Adapters (Input)
│   │   │   │   ├── web/        # Gin controllers (REST)
│   │   │   │   └── grpc/       # gRPC server implementation
│   │   │   └── out/            # Driven Adapters (Output)
│   │   │       └── persistence/# GORM/Postgres implementation
│   │   ├── application/
│   │   │   ├── port/
│   │   │   │   ├── in/         # Use Case interfaces (Driving Ports)
│   │   │   │   └── out/        # Repository interfaces (Driven Ports)
│   │   │   └── service/        # Business Logic (The implementation)
│   │   └── domain/
│   │       ├── model/          # Entities (Pure Go, no external tags)
│   │       └── exception/      # Domain-specific errors (e.g., ErrOutOfStock)
├── pkg/                        # Common cross-cutting concerns (logger, etc.)
├── api/                        # API contracts (OpenAPI, Proto)
├── go.mod
└── go.sum
```

---

## 2. Core Principles & Implementation

### A. Domain Entities (`internal/product/domain/model`)
The domain layer must stay "pure" (no DB tags or external library references).

```go
package model

type Inventory struct {
    ProductID string
    Stock     int
    Reserved  int
}

func (i *Inventory) CanReserve(quantity int) bool {
    return i.Stock >= quantity
}
```

### B. Ports (`internal/product/application/port`)
Define the "What" before the "How".

- **Inbound Port**: Defines what the service does.
```go
package in

type InventoryUseCase interface {
    ReserveStock(productID string, quantity int) error
}
```

- **Outbound Port**: Defines how the service interacts with the database.
```go
package out

type InventoryRepository interface {
    GetByProductID(id string) (*model.Inventory, error)
    Save(inv *model.Inventory) error
}
```

### C. Application Service (`internal/product/application/service`)
Strictly for business orchestration. It only depends on interfaces (Ports).

```go
package service

import "product-service/internal/product/application/port/out"

type inventoryService struct {
    repo out.InventoryRepository
}

func NewInventoryService(p out.InventoryRepository) *inventoryService {
    return &inventoryService{repo: p}
}

func (s *inventoryService) ReserveStock(id string, qty int) error {
    inv, err := s.repo.GetByProductID(id)
    if err != nil { return err }

    if !inv.CanReserve(qty) { return ErrInsufficientStock }

    inv.Stock -= qty
    inv.Reserved += qty
    
    return s.repo.Save(inv)
}
```

### D. Driving Adapter (Gin)
Handles transport logic only.

```go
func (h *Handler) Reserve(c *gin.Context) {
    var req ReserveRequest
    if err := c.ShouldBindJSON(&req); err != nil {
        c.JSON(400, gin.H{"error": "Invalid format"})
        return
    }

    err := h.useCase.ReserveStock(req.ID, req.Qty)
    // Handle specific domain errors (409 Conflict vs 404 Not Found)
}
```

---

## 3. Go Best Practices

1.  **Dependency Injection**: Perform DI in `cmd/main.go`. Do NOT use global variables for DB/Repository instances.
2.  **Explicit Error Handling**: Define unique sentinel errors in the `domain/exception` package to map cleanly to HTTP status codes.
3.  **Concurrency Safety**: If multiple orders hit the same product, uses **pessimistic locking** (SQL `FOR UPDATE`) or **optimistic locking** (versioning) in your Repository.
4.  **Logging**: Use a structured logger like `zap` or `logrus` in adapters, but keep the business logic layer quiet.
5.  **Environment Variables**: Use `viper` or `envconfig` for configuration. Avoid hardcoding database URLs.

## 4. Next Steps for Implementation
1.  **Initialize**: `go mod init product-service`
2.  **Define Proto**: Create `api/inventory.proto` and generate Go code.
3.  **Implement Repo**: Create the GORM/Postgres implementation in `adapter/out/persistence`.
4.  **Setup Gin**: Build the router and register handlers in `adapter/in/web`.
5.  **Wire Up**: Inject dependencies in `main.go`.
