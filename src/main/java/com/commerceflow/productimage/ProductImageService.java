package com.commerceflow.productimage;

import com.commerceflow.product.Product;
import com.commerceflow.product.repository.ProductRepository;
import com.commerceflow.productimage.dto.ProductImageResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.commerceflow.exception.ResourceNotFoundException;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;

    private static final String UPLOAD_DIR = "uploads/products/";

    public ProductImageService(
            ProductImageRepository productImageRepository,
            ProductRepository productRepository
    ) {
        this.productImageRepository = productImageRepository;
        this.productRepository = productRepository;
    }

    public ProductImageResponse uploadImage(
            Long productId,
            MultipartFile file
    ) {

        // Check product exists
        Product product = productRepository
                .findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + productId
                        )
                );

        // Check file is empty
        if (file == null || file.isEmpty()) {

            throw new IllegalArgumentException(
                    "File cannot be empty"
            );
        }

        // Get file information
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();

        // Check MIME type
        if (contentType == null ||
                !contentType.startsWith("image/")) {

            throw new IllegalArgumentException(
                    "Only image files are allowed"
            );
        }

        // Check file extension
        if (originalFilename == null ||
                !originalFilename.matches(
                        "(?i).*\\.(jpg|jpeg|png|gif|webp|avif)$"
                )) {

            throw new IllegalArgumentException(
                    "Only JPG, JPEG, PNG, GIF, WEBP and AVIF files are allowed"
            );
        }

        try {

            // Create uploads/products directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String fileName =
                    UUID.randomUUID()
                            + "_"
                            + originalFilename;

            // Save file
            Path filePath =
                    uploadPath.resolve(fileName);

            Files.copy(
                    file.getInputStream(),
                    filePath
            );

            // Save image information in database
            ProductImage productImage =
                    new ProductImage();

            productImage.setProduct(product);

            String imageUrl =
                    "/uploads/products/" + fileName;

            productImage.setImageUrl(imageUrl);

            ProductImage savedImage =
                    productImageRepository.save(productImage);

            return new ProductImageResponse(
                    savedImage.getId(),
                    productId,
                    savedImage.getImageUrl(),
                    savedImage.getCreatedAt()
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to upload image",
                    e
            );
        }
    }
    public List<ProductImageResponse> getImagesByProductId(
            Long productId
    ) {
        return productImageRepository
                .findByProductId(productId)
                .stream()
                .map(image -> new ProductImageResponse(
                        image.getId(),
                        productId,
                        image.getImageUrl(),
                        image.getCreatedAt()
                ))
                .toList();
    }
    public void deleteImage(
            Long productId,
            Long imageId
    ) {

        ProductImage productImage =
                productImageRepository
                        .findById(imageId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Image not found with id: " + imageId
                                )
                        );

        // Check whether the image belongs to the product
        if (!productImage.getProduct()
                .getId()
                .equals(productId)) {

            throw new ResourceNotFoundException(
                    "Image does not belong to this product"
            );
        }

        try {

            // Convert URL to file path
            String imageUrl = productImage.getImageUrl();

            String fileName =
                    imageUrl.substring(
                            imageUrl.lastIndexOf("/") + 1
                    );

            Path filePath =
                    Paths.get(UPLOAD_DIR)
                            .resolve(fileName);

            // Delete physical file
            Files.deleteIfExists(filePath);

            // Delete database record
            productImageRepository.delete(productImage);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to delete image file",
                    e
            );
        }
    }


}