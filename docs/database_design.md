# Database Design: Microservices Portfolio

A distributed database architecture using PostgreSQL and Redis, ensuring high availability, scalability, and data consistency.

## Entity Representations

### Golang Entities (GORM)

```go
// User model for User Service
type User struct {
    ID           uuid.UUID `gorm:"type:uuid;primary_key;default:gen_random_uuid()"`
    Email        string    `gorm:"unique;not null"`
    PasswordHash string    `gorm:"not null"`
    FullName     string
    CreatedAt    time.Time `gorm:"default:now()"`
}

// Product model for Product Service
type Product struct {
    ID            uuid.UUID `gorm:"type:uuid;primary_key"`
    Name          string    `gorm:"index:idx_products_name,type:gin"` // GIN index for search
    Description   string
    Price         decimal.Decimal `gorm:"type:numeric(10,2)"`
    StockQuantity int             `gorm:"default:0"`
    UpdatedAt     time.Time
}

// Order model for Order Service
type Order struct {
    ID          uuid.UUID `gorm:"type:uuid;primary_key"`
    UserID      uuid.UUID `gorm:"index:idx_orders_user_status"` // Composite index part 1
    Status      string    `gorm:"type:varchar(20);not null;index:idx_orders_user_status;index:idx_pending_orders,where:status='PENDING'"` // Composite part 2 + Partial
    TotalAmount decimal.Decimal
    OrderDate   time.Time
    OrderItems  []OrderItem
}

type OrderItem struct {
    ID        uuid.UUID `gorm:"type:uuid;primary_key"`
    OrderID   uuid.UUID `gorm:"index"`
    ProductID uuid.UUID // Logic ref to Product Service
    Quantity  int
    UnitPrice decimal.Decimal
}

// Payment model for Payment Service
type Payment struct {
    ID                    uuid.UUID `gorm:"type:uuid;primary_key"`
    OrderID               uuid.UUID `gorm:"index"` // Logic ref to Order Service
    Status                string
    ProviderTransactionID string    `gorm:"unique"`
    Amount                decimal.Decimal
    PaymentDate           time.Time
}
```

### Spring Boot Entities (JPA)

```java
// User Entity for User Service
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    private String fullName;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}

// Order Entity for Order Service
@Entity
@Table(name = "orders")
public class Order {
    @Id
    private UUID id;

    private UUID userId; // Logic ref to User Service

    @Column(nullable = false)
    private String status;

    private BigDecimal totalAmount;

    private LocalDateTime orderDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;
}
```

## Detailed Service Schemas (PostgreSQL)

### User Service
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `UUID` | `PK`, `DEFAULT gen_random_uuid()` | Unique user identifier. |
| `email` | `VARCHAR(255)` | `UNIQUE`, `NOT NULL` | User's primary email. |
| `password_hash` | `TEXT` | `NOT NULL` | Secure Argon2/BCrypt hash. |
| `created_at` | `TIMESTAMPTZ` | `DEFAULT NOW()` | Database record creation. |

### Product Service
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `UUID` | `PK` | Unique product identifier. |
| `price` | `NUMERIC(10,2)` | `CHECK (price >= 0)` | Price per unit. |
| `stock_quantity`| `INT` | `DEFAULT 0` | Available items in warehouse. |

### Order Service
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `UUID` | `PK` | Unique order identifier. |
| `user_id` | `UUID` | `INDEX` | External User ID. |
| `status` | `VARCHAR(20)` | `NOT NULL` | Current lifecycle state. |

## Indexing Strategy

### SQL Definitions (PostgreSQL)

```sql
-- 1. Unique Lookups (B-Tree)
CREATE UNIQUE INDEX idx_users_email ON users(email);
CREATE UNIQUE INDEX idx_payments_provider_tx ON payments(provider_transaction_id);

-- 2. Filtering Active Orders (Composite)
CREATE INDEX idx_orders_user_status ON orders(user_id, status);

-- 3. High-Performance Text Search (GIN)
-- Requires pg_trgm extension for partial string matches
CREATE INDEX idx_products_name_gin ON products USING gin (name gin_trgm_ops);

-- 4. Fast Queue Processing (Partial)
CREATE INDEX idx_pending_orders ON orders(status) 
WHERE status = 'PENDING';
```

## Scalability & Performance
- **Connection Pooling**: **PgBouncer** for handling high throughput.
- **CQRS**: Read replicas for heavy `GET` requests (Products, Order History).
- **Partitioning**: Monthly range partitioning on `orders(order_date)`.
- **Caching**: **Redis** for product metadata and stock reservation tokens.

## Data Consistency: Saga Pattern
- **Choreography**: Services communicate via events (RabbitMQ/Kafka).
- **Compensating Transactions**: Automatic rollback logic if payment fails or stock is missing.
