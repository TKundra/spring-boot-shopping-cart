package com.backend.shoppingcart.service.product;

import com.backend.shoppingcart.dto.ProductDto;
import com.backend.shoppingcart.model.Product;
import com.backend.shoppingcart.request.AddProductRequest;
import com.backend.shoppingcart.request.ProductUpdateRequest;

import java.util.List;

public interface IProductService {
    Product addProduct(AddProductRequest productRequest);
    Product getProductById(Long id);

    void deleteProductById(Long id);
    Product updateProduct(ProductUpdateRequest updateRequest, Long productId);

    List<Product> getAllProducts();
    List<Product> getProductsByCategory(String category);
    List<Product> getProductsByBrand(String brand);
    List<Product> getProductsByCategoryAndBrand(String category, String brand);
    List<Product> getProductsByName(String name);
    List<Product> getProductsByBrandAndName(String category, String name);
    List<ProductDto> getConvertedProducts(List<Product> products);

    Long countProductsByBrandAndName(String brand, String name);

    ProductDto convertToDto(Product product);
}
