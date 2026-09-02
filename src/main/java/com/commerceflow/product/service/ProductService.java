package com.commerceflow.product.service;

import com.commerceflow.category.Category;
import com.commerceflow.category.repository.CategoryRepository;
import com.commerceflow.exception.DuplicateResourceException;
import com.commerceflow.exception.ResourceNotFoundException;
import com.commerceflow.product.Product;
import com.commerceflow.product.dto.PageResponse;
import com.commerceflow.product.dto.ProductRequest;
import com.commerceflow.product.dto.ProductResponse;
import com.commerceflow.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import com.commerceflow.product.dto.ProductStatsResponse;
import com.commerceflow.order.repository.OrderItemRepository;

import com.commerceflow.productimage.ProductImageRepository;
import com.commerceflow.productimage.dto.ProductImageResponse;

import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.commerceflow.product.specification.ProductSpecification;
import org.springframework.data.jpa.domain.Specification;


import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    private final OrderItemRepository orderItemRepository;
    private final ProductImageRepository productImageRepository;


    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            OrderItemRepository orderItemRepository,
            ProductImageRepository productImageRepository
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.orderItemRepository = orderItemRepository;
        this.productImageRepository = productImageRepository;
    }

    private void validateProductRequest(ProductRequest request) {

        if (request.getPrice() == null
                || request.getPrice().compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Product price cannot be negative"
            );
        }

        if (request.getStockQuantity() == null
                || request.getStockQuantity() < 0) {

            throw new IllegalArgumentException(
                    "Stock quantity cannot be negative"
            );
        }
    }

    public ProductResponse createProduct(ProductRequest request) {

        validateProductRequest(request);

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

        return mapToResponse(savedProduct);
    }

    public List<ProductResponse> getAllProducts(
            int page,
            int size,
            String sortBy,
            String direction
    ) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(
                page,
                size,
                sort
        );

        Page<Product> products =
                productRepository.findAll(pageable);

        List<Product> productList =
                products.getContent();

        if (productList.isEmpty()) {
            return List.of();
        }

        List<Long> productIds =
                productList.stream()
                        .map(Product::getId)
                        .toList();

        List<ProductImageResponse> images =
                productImageRepository
                        .findByProductIdIn(productIds)
                        .stream()
                        .map(image -> new ProductImageResponse(
                                image.getId(),
                                image.getProduct().getId(),
                                image.getImageUrl(),
                                image.getCreatedAt()
                        ))
                        .toList();

        return productList.stream()
                .map(product -> {

                    ProductResponse response =
                            mapToResponseWithoutImages(product);

                    List<ProductImageResponse> productImages =
                            images.stream()
                                    .filter(image ->
                                            image.getProductId()
                                                    .equals(product.getId())
                                    )
                                    .toList();

                    response.setImages(productImages);

                    return response;
                })
                .toList();
    }

    public ProductResponse getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id
                ));

        return mapToResponse(product);
    }
    public List<ProductResponse> searchProducts(String keyword) {

        List<Product> products =
                productRepository
                        .findByNameContainingIgnoreCase(keyword);

        if (products.isEmpty()) {
            return List.of();
        }

        List<Long> productIds =
                products.stream()
                        .map(Product::getId)
                        .toList();

        List<ProductImageResponse> images =
                productImageRepository
                        .findByProductIdIn(productIds)
                        .stream()
                        .map(image -> new ProductImageResponse(
                                image.getId(),
                                image.getProduct().getId(),
                                image.getImageUrl(),
                                image.getCreatedAt()
                        ))
                        .toList();

        return products.stream()
                .map(product -> {

                    ProductResponse response =
                            mapToResponseWithoutImages(product);

                    List<ProductImageResponse> productImages =
                            images.stream()
                                    .filter(image ->
                                            image.getProductId()
                                                    .equals(product.getId())
                                    )
                                    .toList();

                    response.setImages(productImages);

                    return response;
                })
                .toList();
    }

    public List<ProductResponse> getProductsByCategory(Long categoryId) {

        List<Product> products =
                productRepository.findByCategoryId(categoryId);

        if (products.isEmpty()) {
            return List.of();
        }

        List<Long> productIds =
                products.stream()
                        .map(Product::getId)
                        .toList();

        List<ProductImageResponse> images =
                productImageRepository
                        .findByProductIdIn(productIds)
                        .stream()
                        .map(image -> new ProductImageResponse(
                                image.getId(),
                                image.getProduct().getId(),
                                image.getImageUrl(),
                                image.getCreatedAt()
                        ))
                        .toList();

        return products.stream()
                .map(product -> {

                    ProductResponse response =
                            mapToResponseWithoutImages(product);

                    List<ProductImageResponse> productImages =
                            images.stream()
                                    .filter(image ->
                                            image.getProductId()
                                                    .equals(product.getId())
                                    )
                                    .toList();

                    response.setImages(productImages);

                    return response;
                })
                .toList();
    }



    public ProductResponse updateProduct(Long id, ProductRequest request) {


        validateProductRequest(request);

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

        return mapToResponse(updatedProduct);
    }

    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id
                ));

        productRepository.delete(product);
    }

    public PageResponse<ProductResponse> filterProducts(
            String keyword,
            Long categoryId,
            Double minPrice,
            Double maxPrice,
            Boolean inStock,
            int page,
            int size,
            String sortBy,
            String direction
    ) {

        List<String> allowedSortFields = List.of(
                "id",
                "name",
                "price",
                "stockQuantity",
                "createdAt",
                "updatedAt"
        );

        if (!allowedSortFields.contains(sortBy)) {
            throw new IllegalArgumentException(
                    "Invalid sort field: " + sortBy
            );
        }

        if (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc")) {

            throw new IllegalArgumentException(
                    "Direction must be either asc or desc"
            );
        }

        if (minPrice != null && minPrice < 0) {
            throw new IllegalArgumentException(
                    "Minimum price cannot be negative"
            );
        }

        if (maxPrice != null && maxPrice < 0) {
            throw new IllegalArgumentException(
                    "Maximum price cannot be negative"
            );
        }

        if (minPrice != null && maxPrice != null
                && minPrice > maxPrice) {

            throw new IllegalArgumentException(
                    "Minimum price cannot be greater than maximum price"
            );
        }

        Specification<Product> specification =
                Specification.where(
                        ProductSpecification.hasKeyword(keyword)
                ).and(
                        ProductSpecification.hasCategoryId(categoryId)
                ).and(
                        ProductSpecification.hasMinPrice(minPrice)
                ).and(
                        ProductSpecification.hasMaxPrice(maxPrice)
                ).and(
                        ProductSpecification.hasStock(inStock)
                );

        Sort.Direction sortDirection =
                direction.equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        Sort sort = Sort.by(sortDirection, sortBy);

        Pageable pageable = PageRequest.of(
                page,
                size,
                sort
        );

        Page<Product> products =
                productRepository.findAll(specification, pageable);

        List<Product> productList =
                products.getContent();

        List<Long> productIds =
                productList.stream()
                        .map(Product::getId)
                        .toList();

        List<ProductImageResponse> images =
                productIds.isEmpty()
                        ? List.of()
                        : productImageRepository
                        .findByProductIdIn(productIds)
                        .stream()
                        .map(image -> new ProductImageResponse(
                                image.getId(),
                                image.getProduct().getId(),
                                image.getImageUrl(),
                                image.getCreatedAt()
                        ))
                        .toList();

        List<ProductResponse> productResponses =
                productList.stream()
                        .map(product -> {

                            ProductResponse response =
                                    mapToResponseWithoutImages(product);

                            List<ProductImageResponse> productImages =
                                    images.stream()
                                            .filter(image ->
                                                    image.getProductId()
                                                            .equals(product.getId())
                                            )
                                            .toList();

                            response.setImages(productImages);

                            return response;
                        })
                        .toList();

        PageResponse<ProductResponse> response =
                new PageResponse<>();

        response.setContent(productResponses);
        response.setPage(products.getNumber());
        response.setSize(products.getSize());
        response.setTotalElements(products.getTotalElements());
        response.setTotalPages(products.getTotalPages());
        response.setLast(products.isLast());

        return response;
    }
    private ProductResponse mapToResponseWithoutImages(
            Product product
    ) {

        ProductResponse response =
                new ProductResponse();

        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStockQuantity(product.getStockQuantity());

        response.setCategoryId(
                product.getCategory().getId()
        );

        response.setCategoryName(
                product.getCategory().getName()
        );

        response.setCreatedAt(
                product.getCreatedAt()
        );

        response.setUpdatedAt(
                product.getUpdatedAt()
        );

        return response;
    }

    private ProductResponse mapToResponse(Product product) {

        ProductResponse response = new ProductResponse();

        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStockQuantity(product.getStockQuantity());

        response.setCategoryId(product.getCategory().getId());
        response.setCategoryName(product.getCategory().getName());

        List<ProductImageResponse> images =
                productImageRepository
                        .findByProductId(product.getId())
                        .stream()
                        .map(image -> new ProductImageResponse(
                                image.getId(),
                                product.getId(),
                                image.getImageUrl(),
                                image.getCreatedAt()
                        ))
                        .toList();

        response.setImages(images);

        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());

        return response;
    }


    public ProductStatsResponse getProductStats() {

        ProductStatsResponse response = new ProductStatsResponse();

        // TOTAL PRODUCTS
        response.setTotalProducts(
                productRepository.count()
        );

        // TOTAL STOCK
        response.setTotalStock(
                productRepository.getTotalStock()
        );

        // LOW STOCK PRODUCTS
        response.setLowStockProducts(
                productRepository.countByStockQuantityLessThanEqual(5)
        );

        // OUT OF STOCK PRODUCTS
        response.setOutOfStockProducts(
                productRepository.countByStockQuantity(0)
        );

        // TOTAL INVENTORY VALUE
        response.setTotalInventoryValue(
                productRepository.getTotalInventoryValue()
        );

        return response;



    }
}