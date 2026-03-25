package web

import (
	"errors"
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
		var stockErr *exception.ErrInsufficientStock
		var notFoundErr *exception.ErrProductNotFound
		switch {
		case errors.As(err, &stockErr):
			c.JSON(http.StatusConflict, gin.H{
				"error":     "Insufficient stock",
				"available": stockErr.Available,
				"requested": stockErr.Requested,
			})
		case errors.As(err, &notFoundErr):
			c.JSON(http.StatusNotFound, gin.H{
				"error": "Product not found",
			})
		default:
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Internal server error", "details": err.Error()})
		}
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "Stock reserved successfully"})
}
