package com.commerceflow.productimage.dto;

import java.time.LocalDateTime;

public class ProductImageResponse {

    private Long id;
    private Long productId;
    private String imageUrl;
    private LocalDateTime createdAt;

    public ProductImageResponse() {
    }

    public ProductImageResponse(
            Long id,
            Long productId,
            String imageUrl,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.productId = productId;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}