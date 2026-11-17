package com.su.Web.Model;

import java.util.List;

public class DashboardStats {

    private long totalProducts;
    private long totalStockUnits;
    private long totalInventoryValue;
    private double averageProductPrice;
    private List<Product> highestPricedProducts;

    public DashboardStats(
        long totalProducts,
        long totalStockUnits,
        long totalInventoryValue,
        double averageProductPrice,
        List<Product> highestPricedProducts) {

    this.totalProducts = totalProducts;
    this.totalStockUnits = totalStockUnits;
    this.totalInventoryValue = totalInventoryValue;
    this.averageProductPrice = averageProductPrice;
    this.highestPricedProducts = highestPricedProducts;
}

    public long getTotalProducts() {
        return totalProducts;
    }
    public long getTotalStockUnits() {
        return totalStockUnits;
    }

    public long getTotalInventoryValue() {
        return totalInventoryValue;
    }

    public double getAverageProductPrice() {
        return averageProductPrice;
    }

    public List<Product> getHighestPricedProducts() {
        return highestPricedProducts;
    }
}
