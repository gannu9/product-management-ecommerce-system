
package com.su.Web.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.su.Web.Model.Product;
import com.su.Web.Repository.ProductRepository;
import com.su.Web.exception.ProductNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.su.Web.Model.DashboardStats;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    // Get all products
    public List<Product> getProduct() {
        return repo.findAll();
    }

    // Get product by ID
    public Product getProductID(int prodId) {
        return repo.findById(prodId)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product not found with ID: " + prodId
                ));
    }

 // Search products by name
public List<Product> searchProducts(String keyword) {

    if (keyword == null || keyword.isBlank()) {
        throw new IllegalArgumentException(
                "Search keyword cannot be empty"
        );
    }

    return repo.findByProdNameContainingIgnoreCase(keyword.trim());
}
    // Add product
    public Product addProduct(Product prod) {
        return repo.save(prod);
    }

    // Update existing product
    public Product updateProduct(Product prod) {

        if (!repo.existsById(prod.getProdId())) {
            throw new ProductNotFoundException(
                    "Cannot update. Product not found with ID: " + prod.getProdId()
            );
        }

        return repo.save(prod);
    }

    // Delete existing product
    public void deleteProduct(int prodId) {

        if (!repo.existsById(prodId)) {
            throw new ProductNotFoundException(
                    "Cannot delete. Product not found with ID: " + prodId
            );
        }

        repo.deleteById(prodId);
    }
    // Get products with pagination
public Page<Product> getProductsWithPagination(Pageable pageable) {
    return repo.findAll(pageable);
}

// Get products with pagination and sorting
public Page<Product> getProductsWithPaginationAndSorting(
    Pageable pageable,
    String sortBy,
    String direction) {

// Allow only valid Product fields
if (!sortBy.equals("prodId")
        && !sortBy.equals("prodName")
        && !sortBy.equals("price")) {
    throw new IllegalArgumentException("Invalid sorting field");
}

if (!direction.equalsIgnoreCase("asc")
    && !direction.equalsIgnoreCase("desc")) {
throw new IllegalArgumentException("Direction must be asc or desc");
}

Sort sort = direction.equalsIgnoreCase("desc")
    ? Sort.by(sortBy).descending()
    : Sort.by(sortBy).ascending();

Pageable sortedPageable = PageRequest.of(
        pageable.getPageNumber(),
        pageable.getPageSize(),
        sort
);

return repo.findAll(sortedPageable);
}

// Get dashboard statistics

public long getTotalInventoryValue() {
    return repo.getTotalInventoryValue();
}

public double getAverageProductPrice() {
    return repo.getAverageProductPrice();
}

public List<Product> getHighestPricedProducts() {
    return repo.getHighestPricedProducts();
}
// Get complete dashboard statistics
public DashboardStats getDashboardStats() {

    long totalProducts = repo.count();

    long totalStockUnits = repo.getTotalStockUnits();

    long totalInventoryValue = repo.getTotalInventoryValue();

    double averageProductPrice = repo.getAverageProductPrice();

    List<Product> highestPricedProducts =
            repo.getHighestPricedProducts();

    return new DashboardStats(
            totalProducts,
            totalStockUnits,
            totalInventoryValue,
            averageProductPrice,
            highestPricedProducts
    );
}
// Get total stock units
public long getTotalStockUnits() {
    return repo.getTotalStockUnits();
}

// Get low-stock products
public List<Product> getLowStockProducts() {
    return repo.findByQuantityBetween(1, 5);
}

// Get out-of-stock products
public List<Product> getOutOfStockProducts() {
    return repo.findByQuantity(0);
}

// Get in-stock products
public List<Product> getInStockProducts() {
    return repo.findByQuantityGreaterThan(5);
}

// Get products available for the customer catalogue
public List<Product> getCustomerCatalogue() {
    return repo.findByQuantityGreaterThanOrderByProdNameAsc(0);
}
   // Add stock to an existing product
@Transactional
public Product addStock(int prodId, int quantity) {

    if (quantity <= 0) {
        throw new IllegalArgumentException(
                "Stock quantity must be greater than zero"
        );
    }

    Product product = repo.findByIdWithLock(prodId)
            .orElseThrow(() -> new ProductNotFoundException(
                    "Product not found with ID: " + prodId
            ));

    if (quantity > Integer.MAX_VALUE - product.getQuantity()) {
        throw new IllegalArgumentException(
                "Stock quantity exceeds the maximum allowed value"
        );
    }

    product.setQuantity(product.getQuantity() + quantity);

    return repo.save(product);
}

// Reduce stock from an existing product
@Transactional
public Product reduceStock(int prodId, int quantity) {

    if (quantity <= 0) {
        throw new IllegalArgumentException(
                "Stock quantity must be greater than zero"
        );
    }

    Product product = repo.findByIdWithLock(prodId)
            .orElseThrow(() -> new ProductNotFoundException(
                    "Product not found with ID: " + prodId
            ));

    if (product.getQuantity() < quantity) {
        throw new IllegalArgumentException(
                "Insufficient stock. Available quantity: "
                + product.getQuantity()
        );
    }

    product.setQuantity(product.getQuantity() - quantity);

    return repo.save(product);
}
}
