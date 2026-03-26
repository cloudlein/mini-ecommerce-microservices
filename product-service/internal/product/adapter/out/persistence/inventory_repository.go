package persistence

import (
	"product-service/internal/product/domain/model"

	"github.com/google/uuid"
	"gorm.io/gorm"
)

type InventoryRepository struct {
	db *gorm.DB
}

func NewInventoryRepository(db *gorm.DB) *InventoryRepository {
	return &InventoryRepository{db: db}
}

func (r *InventoryRepository) GetByProductID(id string) (*model.Inventory, error) {
	var entity ProductEntity

	uid, _ := uuid.Parse(id)

	if err := r.db.First(&entity, "id = ?", uid).Error; err != nil {
		return nil, err
	}

	// map entity -> domain
	return &model.Inventory{
		ProductID: entity.ID.String(),
		Stock:     entity.StockQuantity,
		Reserved:  entity.ReservedQuantity,
	}, nil
}

func (r *InventoryRepository) Save(inv *model.Inventory) error {
	uid, _ := uuid.Parse(inv.ProductID)

	return r.db.Model(&ProductEntity{}).
		Where("id = ?", uid).
		Updates(map[string]interface{}{
			"stock_quantity":    inv.Stock,
			"reserved_quantity": inv.Reserved,
		}).Error
}
