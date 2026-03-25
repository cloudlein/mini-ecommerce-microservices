package in

type InventoryUseCase interface {
	ReserveStock(productID string, quantity int) error
}
