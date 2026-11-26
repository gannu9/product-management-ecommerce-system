
package com.su.Web.Service;

import com.su.Web.DTO.OrderItemRequest;
import com.su.Web.DTO.OrderRequest;
import com.su.Web.Model.Order;
import com.su.Web.Model.Product;
import com.su.Web.Repository.OrderRepository;
import com.su.Web.Repository.ProductRepository;
import com.su.Web.exception.ProductNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTests {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    private OrderRequest createRequest(
            List<OrderItemRequest> items) {

        OrderRequest request = new OrderRequest();

        request.setCustomerName("Rahul Sharma");
        request.setEmail("rahul@example.com");
        request.setPhone("9876543210");
        request.setDeliveryAddress(
                "HSR Layout, Bengaluru, Karnataka"
        );
        request.setItems(items);

        return request;
    }

    @Test
    void placeOrderSuccessfully() {

        Product iphone = new Product(
                204, "Apple iPhone 13", 32999, 5
        );

        Product macbook = new Product(
                206, "Apple MacBook Air M2", 79999, 3
        );

        when(productRepository.findByIdWithLock(204))
                .thenReturn(Optional.of(iphone));

        when(productRepository.findByIdWithLock(206))
                .thenReturn(Optional.of(macbook));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OrderRequest request = createRequest(List.of(
                new OrderItemRequest(204, 1),
                new OrderItemRequest(206, 1)
        ));

        Order result = orderService.placeOrder(request);

        assertEquals(112998L, result.getTotalAmount());
        assertEquals("PLACED", result.getOrderStatus());
        assertEquals("CASH_ON_DELIVERY", result.getPaymentMethod());
        assertEquals(2, result.getItems().size());

        assertEquals(4, iphone.getQuantity());
        assertEquals(2, macbook.getQuantity());

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void placeOrderWhenStockIsInsufficient() {

        Product iphone = new Product(
                204, "Apple iPhone 13", 32999, 1
        );

        when(productRepository.findByIdWithLock(204))
                .thenReturn(Optional.of(iphone));

        OrderRequest request = createRequest(List.of(
                new OrderItemRequest(204, 2)
        ));

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder(request)
        );

        assertEquals(1, iphone.getQuantity());

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void placeOrderWithNonexistentProduct() {

        when(productRepository.findByIdWithLock(999))
                .thenReturn(Optional.empty());

        OrderRequest request = createRequest(List.of(
                new OrderItemRequest(999, 1)
        ));

        assertThrows(
                ProductNotFoundException.class,
                () -> orderService.placeOrder(request)
        );

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void rejectDuplicateProducts() {

        OrderRequest request = createRequest(List.of(
                new OrderItemRequest(204, 1),
                new OrderItemRequest(204, 2)
        ));

        Product iphone = new Product(
                204, "Apple iPhone 13", 32999, 5
        );

        when(productRepository.findByIdWithLock(204))
                .thenReturn(Optional.of(iphone));

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder(request)
        );

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void rejectZeroQuantity() {

        OrderRequest request = createRequest(List.of(
                new OrderItemRequest(204, 0)
        ));

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder(request)
        );

        verifyNoInteractions(productRepository);
        verifyNoInteractions(orderRepository);
    }

    @Test
    void rejectNegativeQuantity() {

        OrderRequest request = createRequest(List.of(
                new OrderItemRequest(204, -2)
        ));

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder(request)
        );

        verifyNoInteractions(productRepository);
        verifyNoInteractions(orderRepository);
    }
}
