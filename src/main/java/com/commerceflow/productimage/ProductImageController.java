package com.commerceflow.productimage;

import com.commerceflow.productimage.dto.ProductImageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products/{productId}/images")
public class ProductImageController {

    private final ProductImageService productImageService;

    public ProductImageController(
            ProductImageService productImageService
    ) {
        this.productImageService = productImageService;
    }

    @PostMapping(consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductImageResponse uploadImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file
    ) {
        return productImageService.uploadImage(
                productId,
                file
        );
    }
    @GetMapping
    public List<ProductImageResponse> getProductImages(
            @PathVariable Long productId
    ) {
        return productImageService
                .getImagesByProductId(productId);
    }

    @DeleteMapping("/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage(
            @PathVariable Long productId,
            @PathVariable Long imageId
    ) {
        productImageService.deleteImage(
                productId,
                imageId
        );
    }

}