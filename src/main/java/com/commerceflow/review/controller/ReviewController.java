package com.commerceflow.review.controller;

import com.commerceflow.review.dto.ReviewRequest;
import com.commerceflow.review.dto.ReviewResponse;
import com.commerceflow.review.service.ReviewService;
import com.commerceflow.user.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // Create review
    @PostMapping("/products/{productId}/reviews")
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Long productId,
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal User user
    ) {

        ReviewResponse response =
                reviewService.createReview(
                        productId,
                        request,
                        user
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Get all reviews of a product
    @GetMapping("/products/{productId}/reviews")
    public ResponseEntity<List<ReviewResponse>> getProductReviews(
            @PathVariable Long productId
    ) {

        return ResponseEntity.ok(
                reviewService.getProductReviews(productId)
        );
    }

    // Update own review
    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal User user
    ) {

        return ResponseEntity.ok(
                reviewService.updateReview(
                        reviewId,
                        request,
                        user
                )
        );
    }

    // Delete own review
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal User user
    ) {

        reviewService.deleteReview(
                reviewId,
                user
        );

        return ResponseEntity.noContent().build();
    }
}