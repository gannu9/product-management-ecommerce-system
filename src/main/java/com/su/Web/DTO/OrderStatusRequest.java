
package com.su.Web.DTO;

import jakarta.validation.constraints.NotBlank;

public class OrderStatusRequest {

    @NotBlank(message = "Order status is required.")
    private String status;

    public OrderStatusRequest() {
    }

    public OrderStatusRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
