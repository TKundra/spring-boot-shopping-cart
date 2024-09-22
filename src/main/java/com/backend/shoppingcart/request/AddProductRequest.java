package com.backend.shoppingcart.request;

import com.backend.shoppingcart.model.Category;
import lombok.Data;

import java.math.BigDecimal;

// It is not recommended to work directly with Entity Class, so we create Request class/DTOs
@Data
public class AddProductRequest {
    private Long id;
    private String name;
    private String brand;
    private BigDecimal price;
    private int inventory;
    private String description;
    private Category category;
}
