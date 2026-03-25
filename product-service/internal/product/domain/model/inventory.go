package model

type Inventory struct {
	ProductID string
	Stock     int
	Reserved  int
}

func (i *Inventory) canReserve(quantity int) bool {
	return i.Stock >= quantity
}
