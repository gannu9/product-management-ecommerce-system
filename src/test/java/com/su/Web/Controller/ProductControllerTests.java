
package com.su.Web.Controller;

import tools.jackson.databind.ObjectMapper;

import com.su.Web.Model.DashboardStats;
import com.su.Web.Model.Product;
import com.su.Web.Service.ProductService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.su.Web.exception.ProductNotFoundException;
import com.su.Web.Model.DashboardStats;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;


@WebMvcTest(ProductController.class)
class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

@MockitoBean
private ProductService service;

    // Test 1: GET all products
    @Test
    void getAllProductsSuccessfully() throws Exception {

        List<Product> products = List.of(
                new Product(101, "Keyboard", 3500, 40),
                new Product(102, "Mouse", 1200, 25)
        );

        when(service.getProduct()).thenReturn(products);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].prodName").value("Keyboard"));

        verify(service).getProduct();
    }

    // Test 2: GET product by ID
    @Test
    void getProductByIdSuccessfully() throws Exception {

        Product product = new Product(101, "Keyboard", 3500, 40);

        when(service.getProductID(101)).thenReturn(product);

        mockMvc.perform(get("/products/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prodId").value(101))
                .andExpect(jsonPath("$.prodName").value("Keyboard"));
    }

    // Test 3: POST create product
    @Test
    void addProductSuccessfully() throws Exception {

        Product product = new Product(103, "Monitor", 15000, 20);

        when(service.addProduct(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.prodName").value("Monitor"));
    }

    // Test 4: PUT update product
    @Test
    void updateProductSuccessfully() throws Exception {

        Product product = new Product(101, "Gaming Keyboard", 4500, 30);

        when(service.updateProduct(any(Product.class))).thenReturn(product);

        mockMvc.perform(put("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prodName").value("Gaming Keyboard"));
    }

    // Test 5: DELETE product
    @Test
    void deleteProductSuccessfully() throws Exception {

        doNothing().when(service).deleteProduct(101);

        mockMvc.perform(delete("/products/101"))
                .andExpect(status().isNoContent());

        verify(service).deleteProduct(101);
    }

    // Test 6: PATCH add stock
    @Test
    void addStockSuccessfully() throws Exception {

        Product product = new Product(101, "Keyboard", 3500, 50);

        when(service.addStock(101, 10)).thenReturn(product);

        mockMvc.perform(patch("/products/101/add-stock")
                        .param("quantity", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(50));
    }

    // Test 7: PATCH reduce stock
    @Test
    void reduceStockSuccessfully() throws Exception {

        Product product = new Product(101, "Keyboard", 3500, 30);

        when(service.reduceStock(101, 10)).thenReturn(product);

        mockMvc.perform(patch("/products/101/reduce-stock")
                        .param("quantity", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(30));
    }

    // Test 8: Handle product not found
    @Test
    void getProductWhenNotFound() throws Exception {

        when(service.getProductID(999))
                .thenThrow(new ProductNotFoundException(
                        "Product not found with ID: 999"
                ));

        mockMvc.perform(get("/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Product not found with ID: 999"));
    }

    // Test 9: Handle invalid stock quantity
    @Test
    void addStockWithInvalidQuantity() throws Exception {

        when(service.addStock(101, -5))
                .thenThrow(new IllegalArgumentException(
                        "Stock quantity must be greater than zero"
                ));

        mockMvc.perform(patch("/products/101/add-stock")
                        .param("quantity", "-5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Stock quantity must be greater than zero"));
    }

    // Test 10: Handle invalid product validation
    @Test
    void addProductWithInvalidData() throws Exception {

        Product invalidProduct = new Product(
                0,
                "",
                -100,
                -5
        );

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Validation failed"))
                .andExpect(jsonPath("$.errors.prodName").exists());
    }

    // Test 11: Handle invalid pagination parameters
    @Test
    void getProductsWithInvalidPagination() throws Exception {

        mockMvc.perform(get("/products/page")
                        .param("page", "-1")
                        .param("size", "5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Page must be >= 0 and size must be between 1 and 100"));
    }


    // Test 12: Product search endpoint
    @Test
    void searchProductsSuccessfully() throws Exception {

        List<Product> products = List.of(
                new Product(101, "Gaming Keyboard", 3500, 40)
        );

        when(service.searchProducts("keyboard")).thenReturn(products);

        mockMvc.perform(get("/products/search")
                        .param("keyword", "keyboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].prodName")
                        .value("Gaming Keyboard"));
    }

    // Test 13: Pagination endpoint
    @Test
    void getProductsWithPaginationSuccessfully() throws Exception {

        Pageable pageable = PageRequest.of(0, 2);

        List<Product> products = List.of(
                new Product(101, "Keyboard", 3500, 40),
                new Product(102, "Mouse", 1200, 25)
        );

        Page<Product> page = new PageImpl<>(products, pageable, 2);

        when(service.getProductsWithPagination(any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/products/page")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    // Test 14: Sorting endpoint
    @Test
    void getProductsWithSortingSuccessfully() throws Exception {

        Pageable pageable = PageRequest.of(0, 2);

        List<Product> products = List.of(
                new Product(102, "Mouse", 1200, 25),
                new Product(101, "Keyboard", 3500, 40)
        );

        Page<Product> page = new PageImpl<>(products, pageable, 2);

        when(service.getProductsWithPaginationAndSorting(
                any(Pageable.class), eq("price"), eq("asc")))
                .thenReturn(page);

        mockMvc.perform(get("/products/sort")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sortBy", "price")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    // Test 15: Dashboard endpoint
    @Test
    void getDashboardSuccessfully() throws Exception {

        DashboardStats stats = new DashboardStats(
                4, 40, 140000, 50125.0,
                List.of(new Product(107, "Barbie", 77000, 0))
        );

        when(service.getDashboardStats()).thenReturn(stats);

        mockMvc.perform(get("/products/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProducts").value(4))
                .andExpect(jsonPath("$.totalStockUnits").value(40))
                .andExpect(jsonPath("$.totalInventoryValue").value(140000));
    }

    // Test 16: Low-stock endpoint
    @Test
    void getLowStockProductsSuccessfully() throws Exception {

        when(service.getLowStockProducts()).thenReturn(
                List.of(new Product(101, "Keyboard", 3500, 3))
        );

        mockMvc.perform(get("/products/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].quantity").value(3));
    }

    // Test 17: Out-of-stock endpoint
    @Test
    void getOutOfStockProductsSuccessfully() throws Exception {

        when(service.getOutOfStockProducts()).thenReturn(
                List.of(new Product(102, "Mouse", 1200, 0))
        );

        mockMvc.perform(get("/products/out-of-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].quantity").value(0));
    }

    // Test 18: In-stock endpoint
    @Test
    void getInStockProductsSuccessfully() throws Exception {

        when(service.getInStockProducts()).thenReturn(
                List.of(new Product(103, "Monitor", 15000, 20))
        );

        mockMvc.perform(get("/products/in-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].quantity").value(20));
    }

    // Test 19: Reduce stock with insufficient inventory
    @Test
    void reduceStockWithInsufficientInventory() throws Exception {

        when(service.reduceStock(101, 100))
                .thenThrow(new IllegalArgumentException(
                        "Insufficient stock. Available quantity: 40"
                ));

        mockMvc.perform(patch("/products/101/reduce-stock")
                        .param("quantity", "100"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Insufficient stock. Available quantity: 40"));
    }


}
