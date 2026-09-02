package com.commerceflow.payment.specification;

import com.commerceflow.payment.Payment;
import com.commerceflow.payment.PaymentMethod;
import com.commerceflow.payment.PaymentStatus;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class PaymentSpecification {

    public static Specification<Payment> hasStatus(
            PaymentStatus status
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


    public static Specification<Payment> hasPaymentMethod(
            PaymentMethod paymentMethod
    ) {
        return (root, query, criteriaBuilder) -> {

            if (paymentMethod == null) {
                return null;
            }

            return criteriaBuilder.equal(
                    root.get("paymentMethod"),
                    paymentMethod
            );
        };
    }


    public static Specification<Payment> createdAfter(
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


    public static Specification<Payment> createdBefore(
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
}