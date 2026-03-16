# API Specification: Microservices Portfolio

Detailed endpoint definitions for each service, following the native implementation styles (Spring Boot for Java and Gin for Golang).

# API Specification: Microservices Portfolio

Concise list of endpoints for each service.

## User Service (Java - Spring Boot)
Base Path: `/api/v1`

- `POST /auth/login` - User authentication
- `POST /auth/register` - New user registration
- `GET /users/profile` - Get current user profile

## Product Service (Golang - Gin)
Base Path: `/api/v1`

- `GET /products` - List all products
- `GET /products/:id` - Get product details by ID

## Order Service (Java - Spring Boot)
Base Path: `/api/v1`

- `POST /orders` - Create a new order
- `GET /orders/:id` - Get order details
- `GET /orders/history` - Get user order history

## Payment Service (Golang - Gin)
Base Path: `/api/v1`

- `POST /payments/process` - Internal payment processing
- `GET /payments/status/:tx_id` - Check payment transaction status

