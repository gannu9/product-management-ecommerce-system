
package com.su.Web.Service;

import com.su.Web.Model.DashboardStats;
import com.su.Web.Model.Product;
import com.su.Web.Repository.ProductRepository;
import com.su.Web.exception.ProductNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class ProductServiceTests {

    @Mock
    private ProductRepository repo;

    @InjectMocks
    private ProductService service;

    // Test 1: Successfully add stock
    @Test
    void addStockSuccessfully() {

        Product product = new Product(101, "Keyboard", 3500, 40);

        when(repo.findByIdWithLock(101))
                .thenReturn(Optional.of(product));

        when(repo.save(product)).thenReturn(product);

        Product result = service.addStock(101, 10);

        assertEquals(50, result.getQuantity());

        verify(repo).findByIdWithLock(101);
        verify(repo).save(product);
    }

    // Test 2: Successfully reduce stock
    @Test
    void reduceStockSuccessfully() {

        Product product = new Product(101, "Keyboard", 3500, 40);

        when(repo.findByIdWithLock(101))
                .thenReturn(Optional.of(product));

        when(repo.save(product)).thenReturn(product);

        Product result = service.reduceStock(101, 10);

        assertEquals(30, result.getQuantity());

        verify(repo).save(product);
    }

    // Test 3: Reject zero or negative stock addition
    @Test
    void addStockWithInvalidQuantity() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.addStock(101, 0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.addStock(101, -5)
        );

        verifyNoInteractions(repo);
    }

    // Test 4: Reject zero or negative stock reduction
    @Test
    void reduceStockWithInvalidQuantity() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.reduceStock(101, 0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.reduceStock(101, -5)
        );

        verifyNoInteractions(repo);
    }

    // Test 5: Reject reduction beyond available stock
    @Test
    void reduceStockWhenInsufficientStock() {

        Product product = new Product(101, "Keyboard", 3500, 10);

        when(repo.findByIdWithLock(101))
                .thenReturn(Optional.of(product));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.reduceStock(101, 15)
        );

        assertTrue(exception.getMessage().contains("Insufficient stock"));

        verify(repo, never()).save(any(Product.class));
    }

    // Test 6: Reject stock addition beyond Integer.MAX_VALUE
    @Test
    void addStockBeyondMaximumIntegerValue() {

        Product product = new Product(
                101,
                "Keyboard",
                3500,
                Integer.MAX_VALUE - 2
        );

        when(repo.findByIdWithLock(101))
                .thenReturn(Optional.of(product));

        assertThrows(
                IllegalArgumentException.class,
                () -> service.addStock(101, 3)
        );

        verify(repo, never()).save(any(Product.class));
    }

    // Test 7: Reject stock addition for nonexistent product
    @Test
    void addStockForNonexistentProduct() {

        when(repo.findByIdWithLock(999))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> service.addStock(999, 10)
        );

        verify(repo, never()).save(any(Product.class));
    }

    // Test 8: Reject stock reduction for nonexistent product
    @Test
    void reduceStockForNonexistentProduct() {

        when(repo.findByIdWithLock(999))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> service.reduceStock(999, 10)
        );

        verify(repo, never()).save(any(Product.class));
    }

    
    // Test 9: Retrieve all products
    @Test
    void getAllProductsSuccessfully() {

        List<Product> products = List.of(
                new Product(101, "Keyboard", 3500, 40),
                new Product(102, "Mouse", 1200, 25)
        );

        when(repo.findAll()).thenReturn(products);

        List<Product> result = service.getProduct();

        assertEquals(2, result.size());
        assertEquals("Keyboard", result.get(0).getProdName());

        verify(repo).findAll();
    }

    // Test 10: Retrieve product by ID
    @Test
    void getProductByIdSuccessfully() {

        Product product = new Product(101, "Keyboard", 3500, 40);

        when(repo.findById(101))
                .thenReturn(Optional.of(product));

        Product result = service.getProductID(101);

        assertEquals(101, result.getProdId());
        assertEquals("Keyboard", result.getProdName());
    }

    // Test 11: Handle nonexistent product during retrieval
    @Test
    void getProductByIdWhenProductDoesNotExist() {

        when(repo.findById(999))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> service.getProductID(999)
        );
    }

    // Test 12: Add a product
    @Test
    void addProductSuccessfully() {

        Product product = new Product(103, "Monitor", 15000, 20);

        when(repo.save(product)).thenReturn(product);

        Product result = service.addProduct(product);

        assertEquals("Monitor", result.getProdName());
        assertEquals(15000, result.getPrice());

        verify(repo).save(product);
    }

    // Test 13: Update an existing product
    @Test
    void updateProductSuccessfully() {

        Product product = new Product(101, "Gaming Keyboard", 4500, 30);

        when(repo.existsById(101)).thenReturn(true);
        when(repo.save(product)).thenReturn(product);

        Product result = service.updateProduct(product);

        assertEquals("Gaming Keyboard", result.getProdName());
        assertEquals(4500, result.getPrice());

        verify(repo).save(product);
    }

    // Test 14: Reject update when product does not exist
    @Test
    void updateProductWhenProductDoesNotExist() {

        Product product = new Product(999, "Unknown", 1000, 10);

        when(repo.existsById(999)).thenReturn(false);

        assertThrows(
                ProductNotFoundException.class,
                () -> service.updateProduct(product)
        );

        verify(repo, never()).save(any(Product.class));
    }

    // Test 15: Delete an existing product
    @Test
    void deleteProductSuccessfully() {

        when(repo.existsById(101)).thenReturn(true);

        service.deleteProduct(101);

        verify(repo).deleteById(101);
    }

    // Test 16: Reject deletion when product does not exist
    @Test
    void deleteProductWhenProductDoesNotExist() {

        when(repo.existsById(999)).thenReturn(false);

        assertThrows(
                ProductNotFoundException.class,
                () -> service.deleteProduct(999)
        );

        verify(repo, never()).deleteById(999);
    }

    // Test 17: Search products successfully
    @Test
    void searchProductsSuccessfully() {

        List<Product> products = List.of(
                new Product(101, "Gaming Keyboard", 3500, 40)
        );

        when(repo.findByProdNameContainingIgnoreCase("keyboard"))
                .thenReturn(products);

        List<Product> result = service.searchProducts("keyboard");

        assertEquals(1, result.size());
        assertEquals("Gaming Keyboard", result.get(0).getProdName());

        verify(repo).findByProdNameContainingIgnoreCase("keyboard");
    }

    // Test 18: Reject empty search keyword
    @Test
    void searchProductsWithEmptyKeyword() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.searchProducts(" ")
        );

        verifyNoInteractions(repo);
    }

    // Test 19: Search with whitespace around keyword
    @Test
    void searchProductsWithWhitespace() {

        List<Product> products = List.of(
                new Product(101, "Gaming Keyboard", 3500, 40)
        );

        when(repo.findByProdNameContainingIgnoreCase("keyboard"))
                .thenReturn(products);

        List<Product> result = service.searchProducts(" keyboard ");

        assertEquals(1, result.size());

        verify(repo).findByProdNameContainingIgnoreCase("keyboard");
    }

    // Test 20: Retrieve products with pagination
    @Test
    void getProductsWithPaginationSuccessfully() {

        Pageable pageable = PageRequest.of(0, 2);

        List<Product> products = List.of(
                new Product(101, "Keyboard", 3500, 40),
                new Product(102, "Mouse", 1200, 25)
        );

        Page<Product> expectedPage =
                new PageImpl<>(products, pageable, 2);

        when(repo.findAll(pageable)).thenReturn(expectedPage);

        Page<Product> result =
                service.getProductsWithPagination(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(0, result.getNumber());
        assertEquals(2, result.getTotalElements());
    }

    // Test 21: Sort products by price ascending
    @Test
    void sortProductsByPriceAscending() {

        Pageable pageable = PageRequest.of(0, 2);

        List<Product> products = List.of(
                new Product(102, "Mouse", 1200, 25),
                new Product(101, "Keyboard", 3500, 40)
        );

        Page<Product> expectedPage =
                new PageImpl<>(products);

        when(repo.findAll(any(Pageable.class)))
                .thenReturn(expectedPage);

        Page<Product> result =
                service.getProductsWithPaginationAndSorting(
                        pageable, "price", "asc"
                );

        assertEquals(2, result.getContent().size());
        assertTrue(
                result.getContent().get(0).getPrice()
                < result.getContent().get(1).getPrice()
        );

        verify(repo).findAll(any(Pageable.class));
    }

    // Test 22: Sort products by price descending
    @Test
    void sortProductsByPriceDescending() {

        Pageable pageable = PageRequest.of(0, 2);

        List<Product> products = List.of(
                new Product(101, "Keyboard", 3500, 40),
                new Product(102, "Mouse", 1200, 25)
        );

        Page<Product> expectedPage =
                new PageImpl<>(products);

        when(repo.findAll(any(Pageable.class)))
                .thenReturn(expectedPage);

        Page<Product> result =
                service.getProductsWithPaginationAndSorting(
                        pageable, "price", "desc"
                );

        assertEquals(2, result.getContent().size());
        assertTrue(
                result.getContent().get(0).getPrice()
                > result.getContent().get(1).getPrice()
        );

        verify(repo).findAll(any(Pageable.class));
    }

    // Test 23: Reject invalid sorting field
    @Test
    void sortProductsWithInvalidField() {

        Pageable pageable = PageRequest.of(0, 2);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.getProductsWithPaginationAndSorting(
                        pageable, "invalidField", "asc"
                )
        );

        verifyNoInteractions(repo);
    }

    // Test 24: Reject invalid sorting direction
    @Test
    void sortProductsWithInvalidDirection() {

        Pageable pageable = PageRequest.of(0, 2);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.getProductsWithPaginationAndSorting(
                        pageable, "price", "sideways"
                )
        );

        verifyNoInteractions(repo);
    }

    // Test 25: Calculate total inventory value
    @Test
    void getTotalInventoryValueSuccessfully() {

        when(repo.getTotalInventoryValue()).thenReturn(140000L);

        long result = service.getTotalInventoryValue();

        assertEquals(140000L, result);

        verify(repo).getTotalInventoryValue();
    }

    // Test 26: Calculate average product price
    @Test
    void getAverageProductPriceSuccessfully() {

        when(repo.getAverageProductPrice()).thenReturn(50125.0);

        double result = service.getAverageProductPrice();

        assertEquals(50125.0, result, 0.001);

        verify(repo).getAverageProductPrice();
    }

    // Test 27: Retrieve highest-priced products
    @Test
    void getHighestPricedProductsSuccessfully() {

        List<Product> products = List.of(
                new Product(107, "Barbie", 77000, 0)
        );

        when(repo.getHighestPricedProducts()).thenReturn(products);

        List<Product> result = service.getHighestPricedProducts();

        assertEquals(1, result.size());
        assertEquals(77000, result.get(0).getPrice());
    }

    // Test 28: Calculate total stock units
    @Test
    void getTotalStockUnitsSuccessfully() {

        when(repo.getTotalStockUnits()).thenReturn(40L);

        long result = service.getTotalStockUnits();

        assertEquals(40L, result);

        verify(repo).getTotalStockUnits();
    }

    // Test 29: Retrieve complete dashboard statistics
    @Test
    void getDashboardStatsSuccessfully() {

        List<Product> highestPricedProducts = List.of(
                new Product(107, "Barbie", 77000, 0)
        );

        when(repo.count()).thenReturn(4L);
        when(repo.getTotalStockUnits()).thenReturn(40L);
        when(repo.getTotalInventoryValue()).thenReturn(140000L);
        when(repo.getAverageProductPrice()).thenReturn(50125.0);
        when(repo.getHighestPricedProducts())
                .thenReturn(highestPricedProducts);

        DashboardStats result = service.getDashboardStats();

        assertEquals(4L, result.getTotalProducts());
        assertEquals(40L, result.getTotalStockUnits());
        assertEquals(140000L, result.getTotalInventoryValue());
        assertEquals(50125.0, result.getAverageProductPrice(), 0.001);
        assertEquals(1, result.getHighestPricedProducts().size());

        verify(repo).count();
    }

    // Test 30: Retrieve low-stock products
    @Test
    void getLowStockProductsSuccessfully() {

        List<Product> products = List.of(
                new Product(101, "Keyboard", 3500, 3)
        );

        when(repo.findByQuantityBetween(1, 5)).thenReturn(products);

        List<Product> result = service.getLowStockProducts();

        assertEquals(1, result.size());
        assertEquals(3, result.get(0).getQuantity());
    }

    // Test 31: Retrieve out-of-stock products
    @Test
    void getOutOfStockProductsSuccessfully() {

        List<Product> products = List.of(
                new Product(102, "Mouse", 1200, 0)
        );

        when(repo.findByQuantity(0)).thenReturn(products);

        List<Product> result = service.getOutOfStockProducts();

        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getQuantity());
    }

    // Test 32: Retrieve in-stock products
    @Test
    void getInStockProductsSuccessfully() {

        List<Product> products = List.of(
                new Product(103, "Monitor", 15000, 20)
        );

        when(repo.findByQuantityGreaterThan(5)).thenReturn(products);

        List<Product> result = service.getInStockProducts();

        assertEquals(1, result.size());
        assertEquals(20, result.get(0).getQuantity());
    }



}
