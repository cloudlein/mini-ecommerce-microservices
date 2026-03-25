package out

import "product-service/internal/product/domain/model"

type InventoryRepository interface {
	GetByProductID(id string) (*model.Inventory, error)
	Save(inv *model.Inventory) error
}
