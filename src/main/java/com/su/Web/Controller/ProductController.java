
package com.su.Web.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

import com.su.Web.Model.DashboardStats;
import com.su.Web.Model.Product;
import com.su.Web.Service.ProductService;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    private final ProductService service;

    // Constructor injection
    public ProductController(ProductService service) {
        this.service = service;
    }

    // Get all products
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProducts() {

        List<Product> products = service.getProduct();

        return ResponseEntity.ok(products);
    }

    // Get product by ID
    @GetMapping("/products/{prodId}")
    public ResponseEntity<Product> getProductID(
            @PathVariable int prodId) {

        Product product = service.getProductID(prodId);

        return ResponseEntity.ok(product);
    }
     
    // Get products with pagination
@GetMapping("/products/page")
public ResponseEntity<Page<Product>> getProductsWithPagination(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size) {

            if (page < 0 || size < 1 || size > 100) {
                throw new IllegalArgumentException(
                        "Page must be >= 0 and size must be between 1 and 100"
                );
            }
    Pageable pageable = PageRequest.of(page, size);

    Page<Product> products = service.getProductsWithPagination(pageable);

    return ResponseEntity.ok(products);
}

     // Search products by name
@GetMapping("/products/search")
public ResponseEntity<List<Product>> searchProducts(
        @RequestParam String keyword) {

    List<Product> products = service.searchProducts(keyword);

    return ResponseEntity.ok(products);
}

// Get products with pagination and sorting
@GetMapping("/products/sort")
public ResponseEntity<Page<Product>> getProductsWithPaginationAndSorting(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(defaultValue = "prodId") String sortBy,
        @RequestParam(defaultValue = "asc") String direction) {

            if (page < 0 || size < 1 || size > 100) {
                throw new IllegalArgumentException(
                        "Page must be >= 0 and size must be between 1 and 100"
                );
            }
    Pageable pageable = PageRequest.of(page, size);

    Page<Product> products = service.getProductsWithPaginationAndSorting(
            pageable,
            sortBy,
            direction
    );

    return ResponseEntity.ok(products);
}


   
    // Add a new product
    @PostMapping("/products")
    public ResponseEntity<Product> addProduct(
            @Valid @RequestBody Product prod) {

        Product savedProduct = service.addProduct(prod);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedProduct);
    }

    // Update an existing product
    @PutMapping("/products")
    public ResponseEntity<Product> updateProduct(
            @Valid @RequestBody Product prod) {

        Product updatedProduct = service.updateProduct(prod);

        return ResponseEntity.ok(updatedProduct);
    }

    // Delete a product
    @DeleteMapping("/products/{prodId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable int prodId) {

        service.deleteProduct(prodId);

        return ResponseEntity.noContent().build();
    }
   
    // Get dashboard statistics
@GetMapping("/products/dashboard")
public ResponseEntity<DashboardStats> getDashboardStats() {

    DashboardStats stats = service.getDashboardStats();

    return ResponseEntity.ok(stats);
}
// Get low-stock products
@GetMapping("/products/low-stock")
public ResponseEntity<List<Product>> getLowStockProducts() {

    List<Product> products = service.getLowStockProducts();

    return ResponseEntity.ok(products);
}

// Get out-of-stock products
@GetMapping("/products/out-of-stock")
public ResponseEntity<List<Product>> getOutOfStockProducts() {

    List<Product> products = service.getOutOfStockProducts();

    return ResponseEntity.ok(products);
}

// Get in-stock products
@GetMapping("/products/in-stock")
public ResponseEntity<List<Product>> getInStockProducts() {

    List<Product> products = service.getInStockProducts();

    return ResponseEntity.ok(products);
}

}
