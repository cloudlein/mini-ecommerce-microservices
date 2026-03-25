package exception

import "fmt"

type ErrInsufficientStock struct {
	ProductID string
	Available int
	Requested int
}

func (e ErrInsufficientStock) Error() string {
	return fmt.Sprintf(
		"insufficient stock for product %s (available=%d, requested=%d)",
		e.ProductID,
		e.Available,
		e.Requested,
	)
}

func (e *ErrInsufficientStock) Code() string {
	return "INSUFFICIENT_STOCK"
}
