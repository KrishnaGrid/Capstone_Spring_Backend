package com.capstone.store.dto;




public class CartItemModifyRequest {
    public CartItemModifyRequest(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    private Long productId;
    private int quantity;

    public Long getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }
}
