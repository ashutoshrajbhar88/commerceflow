package com.commerceflow.review.repository;

import com.commerceflow.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository
        extends JpaRepository<Review, Long> {

    boolean existsByUserIdAndProductId(
            Long userId,
            Long productId
    );

    List<Review> findByProductId(Long productId);
}
