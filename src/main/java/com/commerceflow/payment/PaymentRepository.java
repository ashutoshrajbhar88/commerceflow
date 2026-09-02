package com.commerceflow.payment;

import com.commerceflow.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends
        JpaRepository<Payment, Long>,
        JpaSpecificationExecutor<Payment> {

    Optional<Payment> findByOrder(Order order);


    Optional<Payment> findByOrder_Id(Long orderId);
    List<Payment> findByOrder_User_IdOrderByCreatedAtDesc(Long userId);
}