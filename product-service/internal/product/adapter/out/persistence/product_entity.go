package persistence

import (
	"time"

	"github.com/google/uuid"
	"github.com/shopspring/decimal"
)

type ProductEntity struct {
	ID               uuid.UUID `gorm:"type:uuid;primaryKey;default:uuid_generate_v4()"`
	Name             string    `gorm:"index:idx_products_name"`
	Description      string
	Price            decimal.Decimal `gorm:"type:numeric(10,2)"`
	StockQuantity    int             `gorm:"default:0;check:stock_quantity >= 0"`
	ReservedQuantity int             `gorm:"default:0;check:reserved_quantity >= 0"`

	CreatedAt time.Time `gorm:"index:idx_products_created_at"`
	UpdatedAt time.Time `gorm:"index:idx_products_updated_at"`
}
