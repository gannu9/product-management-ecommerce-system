
package com.su.Web.Service;

import com.su.Web.DTO.OrderRequest;
import com.su.Web.DTO.OrderItemRequest;
import com.su.Web.Model.Order;
import com.su.Web.Model.OrderItem;
import com.su.Web.Model.Product;
import com.su.Web.Repository.OrderRepository;
import com.su.Web.Repository.ProductRepository;
import com.su.Web.exception.ProductNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository) {

        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    // Place a customer COD order
    @Transactional
    public Order placeOrder(OrderRequest request) {

        if (request.getItems() == null ||
                request.getItems().isEmpty()) {
            throw new IllegalArgumentException(
                    "Order must contain at least one item."
            );
        }

        Order order = new Order();

        order.setCustomerName(request.getCustomerName());
        order.setEmail(request.getEmail());
        order.setPhone(request.getPhone());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setPaymentMethod("CASH_ON_DELIVERY");
        order.setOrderStatus("PLACED");
        order.setCreatedAt(LocalDateTime.now());

        long totalAmount = 0;
        Set<Integer> productIds = new HashSet<>();

        for (OrderItemRequest itemRequest : request.getItems()) {

            Integer productId = itemRequest.getProductId();
            Integer requestedQuantity = itemRequest.getQuantity();

            if (productId == null || productId <= 0) {
                throw new IllegalArgumentException(
                        "Invalid product ID."
                );
            }

            if (requestedQuantity == null ||
                    requestedQuantity <= 0) {
                throw new IllegalArgumentException(
                        "Quantity must be greater than zero."
                );
            }

            if (!productIds.add(productId)) {
                throw new IllegalArgumentException(
                        "Duplicate product in order: " + productId
                );
            }

            Product product = productRepository
                    .findByIdWithLock(productId)
                    .orElseThrow(() ->
                            new ProductNotFoundException(
                                    "Product not found: " + productId
                            )
                    );

            if (product.getPrice() < 0) {
                throw new IllegalArgumentException(
                        "Invalid product price."
                );
            }

            if (product.getQuantity() < requestedQuantity) {
                throw new IllegalArgumentException(
                        "Insufficient stock for product: "
                                + product.getProdName()
                );
            }

            long subtotal =
                    (long) product.getPrice() * requestedQuantity;

            if (Long.MAX_VALUE - totalAmount < subtotal) {
                throw new IllegalArgumentException(
                        "Order total is too large."
                );
            }

            totalAmount += subtotal;

            product.setQuantity(
                    product.getQuantity() - requestedQuantity
            );

            OrderItem orderItem = new OrderItem();

            orderItem.setProductId(product.getProdId());
            orderItem.setProductName(product.getProdName());
            orderItem.setQuantity(requestedQuantity);
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setSubtotal(subtotal);

            order.addItem(orderItem);
        }

        order.setTotalAmount(totalAmount);

        return orderRepository.save(order);
    }

    // Get all orders, newest first
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    // Get one order by ID
    @Transactional(readOnly = true)
    public Order getOrderById(Long orderId) {

        return orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order not found: " + orderId
                        )
                );
    }

    // Update order status
    @Transactional
    public Order updateOrderStatus(
            Long orderId,
            String newStatus) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order not found: " + orderId
                        )
                );

        if (newStatus == null) {
            throw new IllegalArgumentException(
                    "Order status is required."
            );
        }

        String status = newStatus
                .trim()
                .toUpperCase();

        Set<String> allowedStatuses = Set.of(
                "PLACED",
                "CONFIRMED",
                "SHIPPED",
                "DELIVERED",
                "CANCELLED"
        );

        if (!allowedStatuses.contains(status)) {
            throw new IllegalArgumentException(
                    "Invalid order status: " + status
            );
        }

        if ("CANCELLED".equals(order.getOrderStatus())) {
            throw new IllegalArgumentException(
                    "Cancelled orders cannot be updated."
            );
        }

        if ("DELIVERED".equals(order.getOrderStatus())) {
            throw new IllegalArgumentException(
                    "Delivered orders cannot be updated."
            );
        }

        if ("CANCELLED".equals(status)) {

            if (!"PLACED".equals(order.getOrderStatus()) &&
                    !"CONFIRMED".equals(order.getOrderStatus())) {
                throw new IllegalArgumentException(
                        "Only placed or confirmed orders can be cancelled."
                );
            }

            // Restore stock for each item in the cancelled order
            for (OrderItem item : order.getItems()) {

                Product product = productRepository
                        .findByIdWithLock(item.getProductId())
                        .orElseThrow(() ->
                                new ProductNotFoundException(
                                        "Product not found: "
                                                + item.getProductId()
                                )
                        );

                product.setQuantity(
                        product.getQuantity() + item.getQuantity()
                );
            }
        }

        order.setOrderStatus(status);

        return orderRepository.save(order);
    }
}

