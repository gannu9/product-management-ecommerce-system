
package com.su.Web.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "products")

public class Product {

    @Id
    @NotNull(message = "Product ID is required")
    @Min(value = 1, message = "Product ID must be greater than zero")
    private Integer prodId;

    @NotBlank(message = "Product name cannot be empty")
    @Size(min = 2, max = 100,
          message = "Product name must contain 2 to 100 characters")
    private String prodName;

    @Min(value = 0, message = "Price cannot be negative")
    private int price;

    @Min(value = 0, message = "Quantity cannot be negative")
private int quantity;

    public Product() {
    }

    public Product(Integer prodId, String prodName, int price,int quantity) {
        this.prodId = prodId;
        this.prodName = prodName;
        this.price = price;
        this.quantity = quantity;
    }

    public Integer getProdId() {
        return prodId;
    }

    public void setProdId(Integer prodId) {
        this.prodId = prodId;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
