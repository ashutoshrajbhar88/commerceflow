package com.commerceflow.order.repository;

import com.commerceflow.order.Order;
import com.commerceflow.order.OrderStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import com.commerceflow.payment.Payment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;
import com.commerceflow.order.OrderStatusHistory;

public interface OrderRepository extends
        JpaRepository<Order, Long>,
        JpaSpecificationExecutor<Order> {

    long countByStatus(OrderStatus status);

    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0)
        FROM Order o
        JOIN Payment p ON p.order = o
        WHERE o.status = 'DELIVERED'
        AND p.status = 'SUCCESS'
        """)
    BigDecimal getTotalRevenue();


    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0)
        FROM Order o
        JOIN Payment p ON p.order = o
        WHERE o.status = 'DELIVERED'
        AND p.status = 'SUCCESS'
        AND (:from IS NULL OR o.createdAt >= :from)
        AND (:to IS NULL OR o.createdAt <= :to)
        """)
    BigDecimal getTotalRevenueBetweenDates(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    long countByStatusAndCreatedAtBetween(
            OrderStatus status,
            LocalDateTime from,
            LocalDateTime to
    );

    Page<Order> findByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {
            "items",
            "items.product"
    })
    @Query("""
        SELECT o
        FROM Order o
        WHERE o.id = :id
        """)
    Optional<Order> findOrderWithItemsById(
            @Param("id") Long id
    );


    @Query("""
        SELECT h
        FROM OrderStatusHistory h
        WHERE h.order.id = :orderId
        ORDER BY h.changedAt ASC
        """)
    List<OrderStatusHistory> findStatusHistoryByOrderId(
            @Param("orderId") Long orderId
    );
    List<Order> findByUserId(Long userId);


    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsByUserIdAndItemsProductId(
            Long userId,
            Long productId
    );



}