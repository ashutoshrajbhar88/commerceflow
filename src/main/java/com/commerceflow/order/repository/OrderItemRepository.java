package com.commerceflow.order.repository;

import com.commerceflow.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository
        extends JpaRepository<OrderItem, Long> {

    boolean existsByProductId(Long productId);

}