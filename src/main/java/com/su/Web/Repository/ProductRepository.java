package com.su.Web.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.su.Web.Model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    // Search products by name
    List<Product> findByProdNameContainingIgnoreCase(String keyword);

    // Get total inventory value
    @Query("SELECT COALESCE(SUM(p.price), 0) FROM Product p")
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