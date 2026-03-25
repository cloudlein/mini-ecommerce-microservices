package exception

import "fmt"

type ErrInsufficientStock struct {
	ProductID string
	Available int
	Requested int
}

type ErrProductNotFound struct {
	ProductID string
}

type ErrInternalServer struct {
	Op  string
	Err error
}

func (e ErrInsufficientStock) Error() string {
	return fmt.Sprintf(
		"insufficient stock for product %s (available=%d, requested=%d)",
		e.ProductID,
		e.Available,
		e.Requested,
	)
}

func (e ErrProductNotFound) Error() string {
	return fmt.Sprintf("product with id %s not found", e.ProductID)
}

func (e ErrInternalServer) Error() string {
	return fmt.Sprintf("%s: %v", e.Op, e.Err)
}

func (e *ErrInsufficientStock) Code() string {
	return "INSUFFICIENT_STOCK"
}
func (e *ErrProductNotFound) Code() string { return "PRODUCT_NOT_FOUND" }
func (e *ErrInternalServer) Code() string  { return "INTERNAL_SERVER_ERROR" }
