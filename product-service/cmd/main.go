package main

import (
	"log"
	"os"
	"product-service/internal/product/adapter/in/web"
	"product-service/internal/product/adapter/out/persistence"
	"product-service/internal/product/application/service"
	"product-service/pkg/database"

	"github.com/gin-gonic/gin"
	"github.com/joho/godotenv"
)

func main() {
	if err := godotenv.Load("../.env"); err != nil {
		log.Println("No .env file found")
	}

	db, err := database.NewPostgresDB()

	if err != nil {
		log.Fatalf("Critical: Could not connect to DB: %v", err)
	}

	// dependency injection
	repo := persistence.NewInventoryRepository(db)
	svc := service.NewInventoryService(repo)
	handler := web.NewInventoryHandler(svc)

	// setup gin and route
	r := gin.Default()
	v1 := r.Group("/api/v1/products")
	{
		v1.POST("/reserve", handler.Reserve)
	}

	port := os.Getenv("PRODUCT_SERVICE_PORT")
	if port == "" {
		port = "8081"
	}

	log.Printf("Product Service running on :%s", port)
	if err := r.Run(":" + port); err != nil {
		log.Fatalf("failed to run server: %v", err)
	}
}
