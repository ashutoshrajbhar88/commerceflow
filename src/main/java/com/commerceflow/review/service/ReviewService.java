package com.commerceflow.review.service;

import com.commerceflow.exception.ResourceNotFoundException;
import com.commerceflow.exception.UnauthorizedAccessException;
import com.commerceflow.order.repository.OrderRepository;
import com.commerceflow.product.Product;
import com.commerceflow.product.repository.ProductRepository;
import com.commerceflow.review.Review;
import com.commerceflow.review.dto.ReviewRequest;
import com.commerceflow.review.dto.ReviewResponse;
import com.commerceflow.review.repository.ReviewRepository;
import com.commerceflow.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public ReviewService(
            ReviewRepository reviewRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository
    ) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public ReviewResponse createReview(
            Long productId,
            ReviewRequest request,
            User user
    ) {

        Product product = productRepository
                .findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + productId
                        )
                );

        // Check duplicate review
        if (reviewRepository.existsByUserIdAndProductId(
                user.getId(),
                productId
        )) {
            throw new IllegalStateException(
                    "You have already reviewed this product"
            );
        }

        // Check whether user purchased the product
        boolean purchased =
                orderRepository.existsByUserIdAndItemsProductId(
                        user.getId(),
                        productId
                );

        if (!purchased) {
            throw new UnauthorizedAccessException(
                    "You can review only products you have purchased"
            );
        }

        Review review = new Review();

        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setUser(user);
        review.setProduct(product);

        Review savedReview = reviewRepository.save(review);

        return mapToResponse(savedReview);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getProductReviews(Long productId) {

        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException(
                    "Product not found with id: " + productId
            );
        }

        return reviewRepository
                .findByProductId(productId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ReviewResponse mapToResponse(Review review) {

        ReviewResponse response = new ReviewResponse();

        response.setId(review.getId());
        response.setRating(review.getRating());
        response.setComment(review.getComment());

        response.setUserId(
                review.getUser().getId()
        );

        // Change getName() if your User entity uses another field
        response.setUserName(
                review.getUser().getName()
        );

        response.setProductId(
                review.getProduct().getId()
        );

        response.setCreatedAt(
                review.getCreatedAt()
        );

        response.setUpdatedAt(
                review.getUpdatedAt()
        );

        return response;
    }

    @Transactional
    public ReviewResponse updateReview(
            Long reviewId,
            ReviewRequest request,
            User user
    ) {

        Review review = reviewRepository
                .findById(reviewId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Review not found with id: " + reviewId
                        )
                );

        // Only the review owner can update it
        if (!review.getUser().getId()
                .equals(user.getId())) {

            throw new UnauthorizedAccessException(
                    "You are not authorized to update this review"
            );
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review updatedReview = reviewRepository.save(review);

        return mapToResponse(updatedReview);
    }

    @Transactional
    public void deleteReview(
            Long reviewId,
            User user
    ) {

        Review review = reviewRepository
                .findById(reviewId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Review not found with id: " + reviewId
                        )
                );

        // Only the review owner can delete it
        if (!review.getUser().getId()
                .equals(user.getId())) {

            throw new UnauthorizedAccessException(
                    "You are not authorized to delete this review"
            );
        }

        reviewRepository.delete(review);
    }


}