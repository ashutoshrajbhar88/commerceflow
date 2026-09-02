package com.commerceflow.order.specification;

import com.commerceflow.order.Order;
import com.commerceflow.order.OrderStatus;
import java.math.BigDecimal;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import com.commerceflow.user.User;

public class OrderSpecification {

    public static Specification<Order> hasStatus(
            OrderStatus status
    ) {
        return (root, query, criteriaBuilder) -> {

            if (status == null) {
                return null;
            }

            return criteriaBuilder.equal(
                    root.get("status"),
                    status
            );
        };
    }

    public static Specification<Order> createdAfter(
            LocalDateTime from
    ) {
        return (root, query, criteriaBuilder) -> {

            if (from == null) {
                return null;
            }

            return criteriaBuilder.greaterThanOrEqualTo(
                    root.get("createdAt"),
                    from
            );
        };
    }

    public static Specification<Order> createdBefore(
            LocalDateTime to
    ) {
        return (root, query, criteriaBuilder) -> {

            if (to == null) {
                return null;
            }

            return criteriaBuilder.lessThanOrEqualTo(
                    root.get("createdAt"),
                    to
            );
        };
    }

    public static Specification<Order> totalAmountGreaterThanOrEqual(
            BigDecimal minAmount
    ) {
        return (root, query, criteriaBuilder) -> {

            if (minAmount == null) {
                return null;
            }

            return criteriaBuilder.greaterThanOrEqualTo(
                    root.get("totalAmount"),
                    minAmount
            );
        };
    }

    public static Specification<Order> totalAmountLessThanOrEqual(
            BigDecimal maxAmount
    ) {
        return (root, query, criteriaBuilder) -> {

            if (maxAmount == null) {
                return null;
            }

            return criteriaBuilder.lessThanOrEqualTo(
                    root.get("totalAmount"),
                    maxAmount
            );
        };
    }

    public static Specification<Order> hasUser(User user) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("user"),
                        user
                );
    }

}