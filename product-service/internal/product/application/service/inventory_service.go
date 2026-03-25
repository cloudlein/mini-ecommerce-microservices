package service

import "product-service/internal/product/application/port/out"

type InventoryService struct {
	repo out.InventoryRepository
}

func NewInventoryService(p out.InventoryRepository) *InventoryService {
	return &InventoryService{repo: p}
}

//func (s *InventoryService) ReserveStock(id string, qty int) error {
//	inv, err := s.repo.GetByProductID(id)
//
//	if err != nil {
//		return err
//	}
//
//	if !inv.CanReserve(qty) {
//		return Err
//	}
//
//	inv.Stock -= qty
//	inv.Reserved += qty
//
//	return s.repo.Save(inv)
//}
