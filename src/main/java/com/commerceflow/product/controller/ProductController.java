package com.commerceflow.product.controller;

import com.commerceflow.product.dto.PageResponse;
import com.commerceflow.product.dto.ProductRequest;
import com.commerceflow.product.dto.ProductResponse;
import com.commerceflow.product.dto.ProductStatsResponse;
import com.commerceflow.product.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // CREATE PRODUCT
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(
            @Valid @RequestBody ProductRequest request
    ) {
        return productService.createProduct(request);
    }

    // GET ALL PRODUCTS WITH FILTERING, PAGINATION AND SORTING
    @GetMapping
    public PageResponse<ProductResponse> getAllProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean inStock,

            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page number cannot be negative")
            int page,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "Page size must be at least 1")
            @Max(value = 100, message = "Page size cannot exceed 100")
            int size,

            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {

        return productService.filterProducts(
                keyword,
                categoryId,
                minPrice,
                maxPrice,
                inStock,
                page,
                size,
                sortBy,
                direction
        );
    }

    // SEARCH PRODUCTS
    @GetMapping("/search")
    public List<ProductResponse> searchProducts(
            @RequestParam String keyword
    ) {
        return productService.searchProducts(keyword);
    }

    // GET PRODUCTS BY CATEGORY
    @GetMapping("/category/{categoryId}")
    public List<ProductResponse> getProductsByCategory(
            @PathVariable Long categoryId
    ) {
        return productService.getProductsByCategory(categoryId);
    }

    // GET PRODUCT BY ID
    @Operation(summary = "Get product by ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product found successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid product ID"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    @GetMapping("/{id}")
    public ProductResponse getProductById(
            @PathVariable
            @Min(value = 1, message = "Product ID must be at least 1")
            Long id
    ) {
        return productService.getProductById(id);
    }

    // UPDATE PRODUCT
    @PutMapping("/{id}")
    public ProductResponse updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request
    ) {
        return productService.updateProduct(id, request);
    }

    // DELETE PRODUCT
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(
            @PathVariable Long id
    ) {
        productService.deleteProduct(id);
    }

    // GET PRODUCT STATS
    @GetMapping("/stats")
    public ResponseEntity<ProductStatsResponse> getProductStats() {

        ProductStatsResponse response =
                productService.getProductStats();

        return ResponseEntity.ok(response);
    }
}