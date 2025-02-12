package com.capstone.store.dto;


public class CartItemRequest {
    public CartItemRequest(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    private Long productId;
    private int quantity;



    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }


}
