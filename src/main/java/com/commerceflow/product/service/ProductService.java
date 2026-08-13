package com.commerceflow.product.service;

import com.commerceflow.category.Category;
import com.commerceflow.category.repository.CategoryRepository;
import com.commerceflow.exception.DuplicateResourceException;
import com.commerceflow.exception.ResourceNotFoundException;
import com.commerceflow.product.Product;
import com.commerceflow.product.dto.ProductRequest;
import com.commerceflow.product.dto.ProductResponse;
import com.commerceflow.product.repository.ProductRepository;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public ProductResponse createProduct(ProductRequest request) {

        if (productRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException(
                    "Product already exists with name: " + request.getName()
            );
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + request.getCategoryId()
                ));

        Product product = new Product();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        ProductResponse response = new ProductResponse();

        response.setId(savedProduct.getId());
        response.setName(savedProduct.getName());
        response.setDescription(savedProduct.getDescription());
        response.setPrice(savedProduct.getPrice());
        response.setStockQuantity(savedProduct.getStockQuantity());

        response.setCategoryId(savedProduct.getCategory().getId());
        response.setCategoryName(savedProduct.getCategory().getName());

        response.setCreatedAt(savedProduct.getCreatedAt());
        response.setUpdatedAt(savedProduct.getUpdatedAt());

        return response;
    }

    public List<ProductResponse> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(product -> {

                    ProductResponse response = new ProductResponse();

                    response.setId(product.getId());
                    response.setName(product.getName());
                    response.setDescription(product.getDescription());
                    response.setPrice(product.getPrice());
                    response.setStockQuantity(product.getStockQuantity());

                    response.setCategoryId(product.getCategory().getId());
                    response.setCategoryName(product.getCategory().getName());

                    response.setCreatedAt(product.getCreatedAt());
                    response.setUpdatedAt(product.getUpdatedAt());

                    return response;
                })
                .toList();
    }

    public ProductResponse getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id
                ));

        ProductResponse response = new ProductResponse();

        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStockQuantity(product.getStockQuantity());

        response.setCategoryId(product.getCategory().getId());
        response.setCategoryName(product.getCategory().getName());

        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());

        return response;
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id
                ));

        if (!product.getName().equals(request.getName())
                && productRepository.existsByName(request.getName())) {

            throw new DuplicateResourceException(
                    "Product already exists with name: " + request.getName()
            );
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + request.getCategoryId()
                ));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);

        ProductResponse response = new ProductResponse();

        response.setId(updatedProduct.getId());
        response.setName(updatedProduct.getName());
        response.setDescription(updatedProduct.getDescription());
        response.setPrice(updatedProduct.getPrice());
        response.setStockQuantity(updatedProduct.getStockQuantity());

        response.setCategoryId(updatedProduct.getCategory().getId());
        response.setCategoryName(updatedProduct.getCategory().getName());

        response.setCreatedAt(updatedProduct.getCreatedAt());
        response.setUpdatedAt(updatedProduct.getUpdatedAt());

        return response;
    }

    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id
                ));

        productRepository.delete(product);
    }

}