package web

import (
	"net/http"
	"product-service/internal/product/application/port/in"
	"product-service/internal/product/domain/exception"

	"github.com/gin-gonic/gin"
)

type InventoryHandler struct {
	useCase in.InventoryUseCase
}

func NewInventoryHandler(u in.InventoryUseCase) *InventoryHandler {
	return &InventoryHandler{u}
}

func (h *InventoryHandler) Reserve(c *gin.Context) {
	var req ReserveRequest

	if err := c.ShouldBindBodyWithJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid format", "details": err.Error()})
		return
	}

	// mapping
	err := h.useCase.ReserveStock(req.ID, req.Qty)
	if err != nil {
		switch err {
			case exception.ErrInsufficientStock{
				ProductID: "",
				Available: 0,
				Requested: 0,
			}
		}
	}
}
