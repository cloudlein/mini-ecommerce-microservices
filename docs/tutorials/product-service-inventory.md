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

`internal/product/domain/model/inventory.go`
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

### B. Domain Exceptions (`internal/product/domain/exception/errors.go`)
Define business-level errors that are agnostic of the transport layer.

```go
package exception

import "errors"

var (
	ErrInsufficientStock = errors.New("insufficient stock available")
	ErrProductNotFound    = errors.New("product not found")
)
```

### C. Ports (`internal/product/application/port`)
Define the "What" before the "How".

- **Inbound Port**: Defines what the service does.
`internal/product/application/port/in/inventory_usecase.go`
```go
package in

type InventoryUseCase interface {
    ReserveStock(productID string, quantity int) error
}
```

- **Outbound Port**: Defines how the service interacts with the database.
`internal/product/application/port/out/inventory_repository.go`
```go
package out

type InventoryRepository interface {
    GetByProductID(id string) (*model.Inventory, error)
    Save(inv *model.Inventory) error
}
```

### D. Application Service (`internal/product/application/service`)
Strictly for business orchestration. It only depends on interfaces (Ports).

`internal/product/application/service/inventory_service.go`
```go
package service

import (
	"product-service/internal/product/application/port/out"
	"product-service/internal/product/domain/exception"
)

type inventoryService struct {
    repo out.InventoryRepository
}

func NewInventoryService(p out.InventoryRepository) *inventoryService {
    return &inventoryService{repo: p}
}

func (s *inventoryService) ReserveStock(id string, qty int) error {
    inv, err := s.repo.GetByProductID(id)
    if err != nil { return err }

    if !inv.CanReserve(qty) { return exception.ErrInsufficientStock }

    inv.Stock -= qty
    inv.Reserved += qty
    
    return s.repo.Save(inv)
}
```

### E. Driving Adapter (Gin)
Handles transport logic only, translating HTTP requests to UseCase calls.

#### 1. Contract/DTO (`internal/product/adapter/in/web/dto.go`)
Define the request and response structures here. Use framework-specific tags for validation.
```go
package web

type ReserveRequest struct {
	ID  string `json:"product_id" binding:"required"`
	Qty int    `json:"quantity" binding:"required,gt=0"`
}
```

#### 2. Handler (`internal/product/adapter/in/web/handler.go`)
The handler orchestrates the flow. It binds the DTO and maps it to the UseCase.
```go
package web

import (
	"net/http"
	"product-service/internal/product/application/port/in"
	"product-service/internal/product/domain/exception"
	"github.com/gin-gonic/gin"
)

type InventoryHandler struct {
	useCase in.InventoryUseCase
}

func NewInventoryHandler(u in.InventoryUseCase) *InventoryHandler {
	return &InventoryHandler{useCase: u}
}

func (h *InventoryHandler) Reserve(c *gin.Context) {
	var req ReserveRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid format", "details": err.Error()})
		return
	}

	// Mapping DTO to UseCase Parameters
	err := h.useCase.ReserveStock(req.ID, req.Qty)
	if err != nil {
		switch err {
		case exception.ErrInsufficientStock:
			c.JSON(http.StatusConflict, gin.H{"error": "Insufficient stock"})
		case exception.ErrProductNotFound:
			c.JSON(http.StatusNotFound, gin.H{"error": "Product not found"})
		default:
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Internal server error"})
		}
		return
	}

	c.JSON(http.StatusOK, gin.H{"status": "Stock reserved successfully"})
}
```

> [!TIP]
> **Best Practice: DTO Separation**
> Keep your DTOs in the Adapter layer. They are specifically for the Web transport (JSON tags, binding validation). The UseCase layer should remain "pure" and independent of the framework. This prevents infrastructure details from leaking into your business logic.

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
