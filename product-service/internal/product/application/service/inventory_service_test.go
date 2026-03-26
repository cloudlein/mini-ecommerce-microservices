package service

import (
	"errors"
	"product-service/internal/product/domain/exception"
	"product-service/internal/product/domain/model"
	"testing"
)

// MockInventoryRepository is a manual mock for out.InventoryRepository
type MockInventoryRepository struct {
	GetByProductIDFunc func(id string) (*model.Inventory, error)
	SaveFunc           func(inv *model.Inventory) error
}

func (m *MockInventoryRepository) GetByProductID(id string) (*model.Inventory, error) {
	return m.GetByProductIDFunc(id)
}

func (m *MockInventoryRepository) Save(inv *model.Inventory) error {
	return m.SaveFunc(inv)
}

func TestInventoryService_ReserveStock(t *testing.T) {
	tests := []struct {
		name          string
		productID     string
		quantity      int
		mockRepo      *MockInventoryRepository
		wantErr       bool
		checkErr      func(t *testing.T, err error)
	}{
		{
			name:      "Success Case",
			productID: "prod-1",
			quantity:  5,
			mockRepo: &MockInventoryRepository{
				GetByProductIDFunc: func(id string) (*model.Inventory, error) {
					return &model.Inventory{ProductID: id, Stock: 10, Reserved: 0}, nil
				},
				SaveFunc: func(inv *model.Inventory) error {
					if inv.Stock != 5 || inv.Reserved != 5 {
						t.Errorf("expected stock 5 and reserved 5, got stock %d and reserved %d", inv.Stock, inv.Reserved)
						return errors.New("incorrect calculation")
					}
					return nil
				},
			},
			wantErr: false,
		},
		{
			name:      "Insufficient Stock Case",
			productID: "prod-1",
			quantity:  15,
			mockRepo: &MockInventoryRepository{
				GetByProductIDFunc: func(id string) (*model.Inventory, error) {
					return &model.Inventory{ProductID: id, Stock: 10, Reserved: 0}, nil
				},
			},
			wantErr: true,
			checkErr: func(t *testing.T, err error) {
				var stockErr exception.ErrInsufficientStock
				if !errors.As(err, &stockErr) {
					t.Errorf("expected ErrInsufficientStock, got %v", err)
					return
				}
				if stockErr.Available != 10 || stockErr.Requested != 15 {
					t.Errorf("expected Available=10 Requested=15, got Available=%d Requested=%d", stockErr.Available, stockErr.Requested)
				}
			},
		},
		{
			name:      "Repository Get Error",
			productID: "prod-1",
			quantity:  1,
			mockRepo: &MockInventoryRepository{
				GetByProductIDFunc: func(id string) (*model.Inventory, error) {
					return nil, errors.New("db error")
				},
			},
			wantErr: true,
		},
		{
			name:      "Repository Save Error",
			productID: "prod-1",
			quantity:  1,
			mockRepo: &MockInventoryRepository{
				GetByProductIDFunc: func(id string) (*model.Inventory, error) {
					return &model.Inventory{ProductID: id, Stock: 10, Reserved: 0}, nil
				},
				SaveFunc: func(inv *model.Inventory) error {
					return errors.New("save error")
				},
			},
			wantErr: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			s := NewInventoryService(tt.mockRepo)
			err := s.ReserveStock(tt.productID, tt.quantity)

			if (err != nil) != tt.wantErr {
				t.Errorf("ReserveStock() error = %v, wantErr %v", err, tt.wantErr)
				return
			}

			if tt.checkErr != nil {
				tt.checkErr(t, err)
			}
		})
	}
}
