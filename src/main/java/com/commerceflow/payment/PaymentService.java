package com.commerceflow.payment;

import com.commerceflow.exception.ResourceNotFoundException;
import com.commerceflow.order.Order;
import com.commerceflow.order.OrderStatus;
import com.commerceflow.order.repository.OrderRepository;
import com.commerceflow.payment.dto.PaymentResponse;
import org.springframework.stereotype.Service;
import com.commerceflow.user.User;
import com.commerceflow.user.Role;
import com.commerceflow.exception.UnauthorizedAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.commerceflow.payment.specification.PaymentSpecification;
import com.commerceflow.product.dto.PageResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;


import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository
    ) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    public PaymentResponse createPayment(PaymentRequest request) {

        Order order = orderRepository
                .findById(request.getOrderId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with id: " + request.getOrderId()
                        )
                );

        User currentUser = getCurrentUser();

        // User can only create payment for their own order
        if (!order.getUser().getId()
                .equals(currentUser.getId())) {

            throw new UnauthorizedAccessException(
                    "You are not authorized to pay for this order"
            );
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Cannot create payment for a cancelled order"
            );
        }

        // Prevent duplicate payment
        if (paymentRepository.findByOrder(order).isPresent()) {
            throw new IllegalStateException(
                    "Payment already exists for this order"
            );
        }

        Payment payment = new Payment();

        payment.setOrder(order);

        // Always take amount from database
        payment.setAmount(order.getTotalAmount());

        payment.setPaymentMethod(
                request.getPaymentMethod()
        );

        payment.setStatus(
                PaymentStatus.PENDING
        );

        Payment savedPayment = paymentRepository.save(payment);

        return mapToResponse(savedPayment);
    }


    @Transactional
    public PaymentResponse markPaymentSuccess(Long paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found with id: " + paymentId
                        )
                );
        User currentUser = getCurrentUser();

        if (currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException(
                    "Only admin can update payment status"
            );
        }

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending payments can be marked as successful"
            );
        }

        payment.setStatus(PaymentStatus.SUCCESS);

        Payment updatedPayment =
                paymentRepository.save(payment);

        return mapToResponse(updatedPayment);
    }


    @Transactional
    public PaymentResponse markPaymentFailed(Long paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found with id: " + paymentId
                        )
                );
        User currentUser = getCurrentUser();

        if (currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException(
                    "Only admin can update payment status"
            );
        }

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending payments can be marked as failed"
            );
        }

        payment.setStatus(PaymentStatus.FAILED);

        Payment updatedPayment =
                paymentRepository.save(payment);

        return mapToResponse(updatedPayment);
    }

    @Transactional
    public PaymentResponse refundPayment(Long paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found with id: " + paymentId
                        )
                );

        User currentUser = getCurrentUser();

        if (currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException(
                    "Only admin can refund payments"
            );
        }

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new IllegalStateException(
                    "Only successful payments can be refunded"
            );
        }

        payment.setStatus(PaymentStatus.REFUNDED);

        Payment updatedPayment =
                paymentRepository.save(payment);

        return mapToResponse(updatedPayment);
    }

    public PaymentResponse getPaymentByOrderId(Long orderId) {

        Payment payment = paymentRepository
                .findByOrder_Id(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found for order id: " + orderId
                        )
                );

        User currentUser = getCurrentUser();

        // ADMIN can see any payment
        if (currentUser.getRole() != Role.ADMIN
                && !payment.getOrder().getUser().getId()
                .equals(currentUser.getId())) {

            throw new UnauthorizedAccessException(
                    "You are not authorized to view this payment"
            );
        }

        return mapToResponse(payment);
    }


    private PaymentResponse mapToResponse(Payment payment) {

        PaymentResponse response =
                new PaymentResponse();

        response.setId(payment.getId());

        response.setOrderId(
                payment.getOrder().getId()
        );

        response.setAmount(
                payment.getAmount()
        );

        response.setStatus(
                payment.getStatus()
        );

        response.setPaymentMethod(
                payment.getPaymentMethod()
        );

        response.setCreatedAt(
                payment.getCreatedAt()
        );

        return response;
    }
    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        return (User) authentication.getPrincipal();
    }

    public PaymentResponse getPaymentById(Long paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found with id: " + paymentId
                        )
                );

        User currentUser = getCurrentUser();

        // ADMIN can access any payment
        if (currentUser.getRole() == Role.ADMIN) {
            return mapToResponse(payment);
        }

        // CUSTOMER can access only their own payment
        if (!payment.getOrder().getUser().getId()
                .equals(currentUser.getId())) {

            throw new UnauthorizedAccessException(
                    "You are not authorized to access this payment"
            );
        }

        return mapToResponse(payment);
    }

    public List<PaymentResponse> getMyPayments() {

        User currentUser = getCurrentUser();

        List<Payment> payments =
                paymentRepository
                        .findByOrder_User_IdOrderByCreatedAtDesc(
                                currentUser.getId()
                        );

        return payments.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public PageResponse<PaymentResponse> getAllPayments(
            PaymentStatus status,
            PaymentMethod paymentMethod,
            LocalDateTime from,
            LocalDateTime to,
            int page,
            int size,
            String sortBy,
            String direction
    ) {

        User currentUser = getCurrentUser();

        // Only ADMIN can access all payments
        if (currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException(
                    "Only admin can access all payments"
            );
        }

        // Validate date range
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException(
                    "From date cannot be after to date"
            );
        }

        // Allowed sorting fields
        List<String> allowedSortFields = List.of(
                "id",
                "amount",
                "status",
                "paymentMethod",
                "createdAt"
        );

        if (!allowedSortFields.contains(sortBy)) {
            throw new IllegalArgumentException(
                    "Invalid sort field: " + sortBy
            );
        }

        // Validate direction
        if (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc")) {

            throw new IllegalArgumentException(
                    "Direction must be either asc or desc"
            );
        }

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(
                page,
                size,
                sort
        );

        Specification<Payment> specification =
                Specification
                        .where(PaymentSpecification.hasStatus(status))
                        .and(PaymentSpecification.hasPaymentMethod(paymentMethod))
                        .and(PaymentSpecification.createdAfter(from))
                        .and(PaymentSpecification.createdBefore(to));

        Page<Payment> payments =
                paymentRepository.findAll(
                        specification,
                        pageable
                );

        // Invalid page check
        if (page >= payments.getTotalPages()
                && payments.getTotalPages() > 0) {

            throw new IllegalArgumentException(
                    "Requested page does not exist"
            );
        }

        List<PaymentResponse> content =
                payments.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        return new PageResponse<>(
                content,
                payments.getNumber(),
                payments.getSize(),
                payments.getTotalElements(),
                payments.getTotalPages(),
                payments.isLast()
        );
    }


}