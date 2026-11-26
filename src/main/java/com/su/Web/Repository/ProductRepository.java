
package com.su.Web.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

import com.su.Web.Model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    // Search products by name
    List<Product> findByProdNameContainingIgnoreCase(String keyword);

    // Lock product row during stock adjustment
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT p FROM Product p WHERE p.prodId = :prodId")
Optional<Product> findByIdWithLock(@Param("prodId") Integer prodId);

    // Get low-stock products (quantity between 1 and 5)
List<Product> findByQuantityBetween(int min, int max);

// Get out-of-stock products
List<Product> findByQuantity(int quantity);

// Get in-stock products (quantity greater than 5)
List<Product> findByQuantityGreaterThan(int quantity);

// Get customer catalogue products (quantity greater than 0)
List<Product> findByQuantityGreaterThanOrderByProdNameAsc(int quantity);
    // Get total inventory value (price * quantity)
    @Query("SELECT COALESCE(SUM(p.price * p.quantity), 0) FROM Product p")
    long getTotalInventoryValue();

    // Get average product price
    @Query("SELECT COALESCE(AVG(p.price), 0) FROM Product p")
    double getAverageProductPrice();

    // Get highest-priced product(s)
    @Query("SELECT p FROM Product p WHERE p.price = (SELECT MAX(p2.price) FROM Product p2)")
    List<Product> getHighestPricedProducts();

    // Get total stock units
    @Query("SELECT COALESCE(SUM(p.quantity), 0) FROM Product p")
    long getTotalStockUnits();
}
