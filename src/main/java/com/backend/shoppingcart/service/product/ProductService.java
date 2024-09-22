package com.backend.shoppingcart.service.product;

import com.backend.shoppingcart.dto.ImageDto;
import com.backend.shoppingcart.dto.ProductDto;
import com.backend.shoppingcart.exception.AlreadyExistsException;
import com.backend.shoppingcart.exception.ResourceNotFoundException;
import com.backend.shoppingcart.model.Category;
import com.backend.shoppingcart.model.Image;
import com.backend.shoppingcart.model.Product;
import com.backend.shoppingcart.repository.CategoryRepository;
import com.backend.shoppingcart.repository.ImageRepository;
import com.backend.shoppingcart.repository.ProductRepository;
import com.backend.shoppingcart.request.AddProductRequest;
import com.backend.shoppingcart.request.ProductUpdateRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService implements IProductService {

    // Dependency injection
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private ModelMapper modelMapper;

    // --------------------------------------- private methods
    private boolean isProductExistsWithNameAndBrand(String name, String brand) {
        return productRepository.existsByNameAndBrand(name, brand);
    }

    private Product createProduct(AddProductRequest productRequest, Category category) {
        return new Product(
                productRequest.getName(),
                productRequest.getBrand(),
                productRequest.getPrice(),
                productRequest.getInventory(),
                productRequest.getDescription(),
                category
        );
    }

    private Product updateExistingProduct(Product existingProduct, ProductUpdateRequest updateRequest) {
        existingProduct.setName(updateRequest.getName());
        existingProduct.setBrand(updateRequest.getBrand());
        existingProduct.setPrice(updateRequest.getPrice());
        existingProduct.setInventory(updateRequest.getInventory());
        existingProduct.setDescription(updateRequest.getDescription());

        // get category by name
        Category category = categoryRepository.findByName(updateRequest.getCategory().getName());
        existingProduct.setCategory(category);

        return  existingProduct;
    }

    // --------------------------------------- public methods
    @Override
    public Product addProduct(AddProductRequest productRequest) {
        if (isProductExistsWithNameAndBrand(productRequest.getName(), productRequest.getBrand())) {
            throw new AlreadyExistsException(
                    productRequest.getBrand()
                            + " "
                            + productRequest.getName()
                            + " already exists, you may update this product instead!"
            );
        }

        // if category found set it else create new with provided name
        String categoryName = productRequest.getCategory().getName();
        Category category = Optional.ofNullable(categoryRepository.findByName(categoryName))
                .orElseGet(() -> {
                    Category newCategory = new Category(categoryName);
                    return categoryRepository.save(newCategory);
                });
        productRequest.setCategory(category);

        return productRepository.save(createProduct(productRequest, category));
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository.findById(id).ifPresentOrElse(
                productRepository::delete,
                () -> {
                    throw new ResourceNotFoundException("Product not found");
                });
    }

    @Override
    public Product updateProduct(ProductUpdateRequest updateRequest, Long productId) {
        return productRepository.findById(productId)
                .map(existingProduct -> updateExistingProduct(existingProduct, updateRequest))
                .map(productRepository::save)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found!"));
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryName(category);
    }

    @Override
    public List<Product> getProductsByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    @Override
    public List<Product> getProductsByCategoryAndBrand(String category, String brand) {
        return productRepository.findByCategoryNameAndBrand(category, brand);
    }

    @Override
    public List<Product> getProductsByName(String name) {
        return productRepository.findByName(name);
    }

    @Override
    public List<Product> getProductsByBrandAndName(String brand, String name) {
        return productRepository.findByBrandAndName(brand, name);
    }

    @Override
    public Long countProductsByBrandAndName(String brand, String name) {
        return productRepository.countByBrandAndName(brand, name);
    }

    @Override
    public List<ProductDto> getConvertedProducts(List<Product> products) {
        return products.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public ProductDto convertToDto(Product product) {
        ProductDto productDto = modelMapper.map(product, ProductDto.class);

        List<Image> images = imageRepository.findByProductId(product.getId());
        List<ImageDto> imageDtos = images.stream().map(image -> modelMapper.map(image, ImageDto.class)).collect(Collectors.toList());

        productDto.setImages(imageDtos);
        return productDto;
    }
}
