
package com.su.Web.Controller;

import com.su.Web.DTO.OrderRequest;
import com.su.Web.DTO.OrderResponse;
import com.su.Web.DTO.OrderStatusRequest;
import com.su.Web.Model.Order;
import com.su.Web.Service.OrderService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = {
    "http://localhost:5173",
    "http://localhost:5174",
    "http://localhost:5175",
    "http://localhost:5176"
})
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Customer checkout
    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> placeOrder(
            @Valid @RequestBody OrderRequest request) {

        Order savedOrder = orderService.placeOrder(request);

        OrderResponse response = new OrderResponse(savedOrder);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Admin: Get all orders, newest first
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {

        List<OrderResponse> orders = orderService
                .getAllOrders()
                .stream()
                .map(OrderResponse::new)
                .toList();

        return ResponseEntity.ok(orders);
    }

    // Admin: Get one order by ID
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long orderId) {

        Order order = orderService.getOrderById(orderId);

        return ResponseEntity.ok(
                new OrderResponse(order)
        );
    }

    // Admin: Update order status
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderStatusRequest request) {

        Order updatedOrder = orderService.updateOrderStatus(
                orderId,
                request.getStatus()
        );

        return ResponseEntity.ok(
                new OrderResponse(updatedOrder)
        );
    }
}


