package web

type ReserveRequest struct {
	ID  string `json:"product_id" binding:"required"`
	Qty int    `json:"quantity" binding:"required, gt=0"`
}
