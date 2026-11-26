package com.su.Web.DTO;

import com.su.Web.Model.OrderItem;

public class OrderItemResponse {

    private Integer productId;
    private String productName;
    private int quantity;
    private long unitPrice;
    private long subtotal;

    public OrderItemResponse() {
    }

    public OrderItemResponse(OrderItem item) {
        this.productId = item.getProductId();
        this.productName = item.getProductName();
        this.quantity = item.getQuantity();
        this.unitPrice = item.getUnitPrice();
        this.subtotal = item.getSubtotal();
    }

    public Integer getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public long getUnitPrice() {
        return unitPrice;
    }

    public long getSubtotal() {
        return subtotal;
    }
}