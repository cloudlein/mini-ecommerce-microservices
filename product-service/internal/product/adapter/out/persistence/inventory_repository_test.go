package persistence

import (
	"product-service/internal/product/domain/model"
	"testing"

	"github.com/glebarez/sqlite"
	"github.com/google/uuid"
	"github.com/shopspring/decimal"
	"gorm.io/gorm"
)

func SetupTestDB() (*gorm.DB, error) {
	db, err := gorm.Open(sqlite.Open("file::memory:?cache=shared"), &gorm.Config{})
	if err != nil {
		return nil, err
	}
	err = db.AutoMigrate(&ProductEntity{})
	return db, err
}

func TestInventoryRepository_Save(t *testing.T) {
	db, _ := SetupTestDB()
	repo := NewInventoryRepository(db)

	pid := uuid.New()
	db.Create(&ProductEntity{
		ID:               pid,
		Name:             "Product Name",
		Description:      "Product Description",
		Price:            decimal.NewFromFloat(1.2),
		StockQuantity:    5,
		ReservedQuantity: 5,
	})

	testInv := &model.Inventory{
		ProductID: pid.String(),
		Stock:     5,
		Reserved:  5,
	}

	err := repo.Save(testInv)

	if err != nil {
		t.Fatalf("failed to save: %v", err)
	}

	var updated ProductEntity
	db.First(&updated, "id = ?", pid)

	if updated.StockQuantity != 5 || updated.ReservedQuantity != 5 {
		t.Errorf("DB not updated correctly. Got Stock=%d Reserved=%d",
			updated.StockQuantity, updated.ReservedQuantity)
	}
}
