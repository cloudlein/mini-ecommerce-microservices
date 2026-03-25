# Build stage
FROM golang:1.26.1-bookworm AS builder

# Set the working directory
WORKDIR /app

# Copy the dependency files
COPY go.mod go.sum ./

# Download dependencies
RUN go mod download

# Copy the source code
COPY . .

# Build the binary as a static application
# CGO_ENABLED=0 ensures the binary doesn't depend on system libraries
RUN CGO_ENABLED=0 GOOS=linux go build -o main ./cmd/main.go

# Production stage (Secure & Minimal)
# Using Distroless for a minimal, non-root, and secure runtime
FROM gcr.io/distroless/static-debian12:latest

# Set the working directory
WORKDIR /

# Copy the binary from the build stage
COPY --from=builder /app/main /main

# Expose the port (adjust based on your config, default 8081 for product-service)
EXPOSE 8081

# Run the application
ENTRYPOINT ["/main"]
