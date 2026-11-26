
package com.su.Web.DTO;

import java.time.LocalDateTime;
import java.util.List;

import com.su.Web.Model.Order;
import com.su.Web.Model.OrderItem;

public class OrderResponse {

    private Long orderId;
    private String customerName;
    private String email;
    private String phone;
    private String deliveryAddress;
    private long totalAmount;
    private String paymentMethod;
    private String orderStatus;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;

    public OrderResponse() {
    }

    public OrderResponse(Order order) {
        this.orderId = order.getOrderId();
        this.customerName = order.getCustomerName();
        this.email = order.getEmail();
        this.phone = order.getPhone();
        this.deliveryAddress = order.getDeliveryAddress();
        this.totalAmount = order.getTotalAmount();
        this.paymentMethod = order.getPaymentMethod();
        this.orderStatus = order.getOrderStatus();
        this.createdAt = order.getCreatedAt();

        this.items = order.getItems()
                .stream()
                .map(OrderItemResponse::new)
                .toList();
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }
}
