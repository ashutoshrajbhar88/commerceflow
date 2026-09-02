package com.commerceflow.order.service;

import com.commerceflow.order.Order;
import com.commerceflow.order.OrderItem;
import com.commerceflow.order.OrderStatus;
import com.commerceflow.order.OrderStatusHistory;
import com.commerceflow.order.dto.OrderItemRequest;
import com.commerceflow.order.dto.OrderItemResponse;
import com.commerceflow.order.dto.OrderRequest;
import com.commerceflow.order.dto.OrderResponse;
import com.commerceflow.order.dto.OrderStatusRequest;
import com.commerceflow.order.repository.OrderRepository;
import com.commerceflow.product.Product;
import com.commerceflow.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import java.time.LocalDateTime;
import com.commerceflow.order.specification.OrderSpecification;
import org.springframework.data.jpa.domain.Specification;
import com.commerceflow.order.dto.OrderStatsResponse;
import com.commerceflow.order.dto.OrderStatusHistoryResponse;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import com.commerceflow.user.User;
import com.commerceflow.user.Role;
import com.commerceflow.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.commerceflow.exception.UnauthorizedAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.commerceflow.payment.Payment;
import com.commerceflow.payment.PaymentStatus;
import com.commerceflow.payment.PaymentRepository;


import com.commerceflow.exception.InsufficientStockException;

import com.commerceflow.exception.ResourceNotFoundException;
import com.commerceflow.product.dto.PageResponse;


import com.commerceflow.cart.Cart;
import com.commerceflow.cart.CartItem;
import com.commerceflow.cart.repository.CartRepository;
import com.commerceflow.cart.repository.CartItemRepository;

import java.util.List;



@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final PaymentRepository paymentRepository;


    private final UserRepository userRepository;




    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            PaymentRepository paymentRepository
    ) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        Set<Long> productIds = new HashSet<>();

        for (OrderItemRequest itemRequest : request.getItems()) {

            if (!productIds.add(itemRequest.getProductId())) {
                throw new IllegalArgumentException(
                        "Duplicate product ID in order: "
                                + itemRequest.getProductId()
                );
            }
        }

        User user = getCurrentUser();

        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);
        order.setUser(user);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {

            Product product = productRepository
                    .findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with id: " + itemRequest.getProductId()
                    ));

            totalAmount = totalAmount.add(
                    product.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(itemRequest.getQuantity())
                            )
            );




            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock for product: " + product.getName()
                );
            }



            product.setStockQuantity(
                    product.getStockQuantity() - itemRequest.getQuantity()
            );



            OrderItem orderItem = new OrderItem();

            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());

// SAVE PRODUCT PRICE AT THE TIME OF ORDER
            orderItem.setPrice(product.getPrice());

            orderItem.setOrder(order);

            order.getItems().add(orderItem);
        }

        order.setTotalAmount((totalAmount));

        Order savedOrder = orderRepository.save(order);

        return mapToResponse(savedOrder);
    }

    @Transactional
    public OrderResponse checkout() {

        User user = getCurrentUser();

        // Find user's cart
        Cart cart = cartRepository
                .findByUser(user)
                .orElseThrow(() ->
                        new IllegalStateException("Cart not found")
                );

        // Get cart items
        List<CartItem> cartItems =
                cartItemRepository.findByCart(cart);

        // Check if cart is empty
        if (cartItems.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot checkout an empty cart"
            );
        }

        // Create order
        Order order = new Order();

        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;

        // Convert CartItems into OrderItems
        for (CartItem cartItem : cartItems) {

            Product product = cartItem.getProduct();

            Integer quantity = cartItem.getQuantity();

            // Check stock
            if (product.getStockQuantity() < quantity) {
                throw new InsufficientStockException(
                        "Insufficient stock for product: "
                                + product.getName()
                );
            }

            // Reduce stock
            product.setStockQuantity(
                    product.getStockQuantity() - quantity
            );

            // Create order item
            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);

            // Save product price at checkout time
            orderItem.setPrice(product.getPrice());

            order.getItems().add(orderItem);

            // Calculate total
            totalAmount = totalAmount.add(
                    product.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(quantity)
                            )
            );
        }

        order.setTotalAmount(totalAmount);

        // Save order
        Order savedOrder = orderRepository.save(order);

        // Clear cart
        cartItemRepository.deleteByCart(cart);

        return mapToResponse(savedOrder);
    }
    public OrderResponse getOrderById(Long id) {

        Order order = orderRepository.findOrderWithItemsById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + id
                ));

        User currentUser = getCurrentUser();



        checkOrderAccess(order, currentUser);

        return mapToResponse(order);
    }

//    public OrderResponse getOrderById(Long id) {
//
//        Order order = orderRepository.findOrderWithItemsById(id)
//
//
//
//
//                .orElseThrow(() -> new ResourceNotFoundException(
//                        "Order not found with id: " + id
//                ));
//
//        User currentUser = getCurrentUser();
//
//        checkOrderAccess(order, currentUser);
//
//        return mapToResponse(order);
//    }

    public PageResponse<OrderResponse> getAllOrders(
            OrderStatus status,
            LocalDateTime from,
            LocalDateTime to,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            int page,
            int size,
            String sortBy,
            String direction
    ) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException(
                    "From date cannot be after to date"
            );
        }

        if (minAmount != null
                && minAmount.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Minimum amount cannot be negative"
            );
        }

        if (maxAmount != null
                && maxAmount.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Maximum amount cannot be negative"
            );
        }

        if (minAmount != null
                && maxAmount != null
                && minAmount.compareTo(maxAmount) > 0) {

            throw new IllegalArgumentException(
                    "Minimum amount cannot be greater than maximum amount"
            );
        }


        // Validate sortBy
        List<String> allowedSortFields = List.of(
                "id",
                "status",
                "totalAmount",
                "createdAt",
                "updatedAt"
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

        Specification<Order> specification = Specification
                .where(OrderSpecification.hasStatus(status))
                .and(OrderSpecification.createdAfter(from))
                .and(OrderSpecification.createdBefore(to))
                .and(OrderSpecification.totalAmountGreaterThanOrEqual(minAmount))
                .and(OrderSpecification.totalAmountLessThanOrEqual(maxAmount));


        User currentUser = getCurrentUser();

// Normal USER can only see their own orders
        if (!isAdmin(currentUser)) {

            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    root.get("user").get("id"),
                                    currentUser.getId()
                            )
            );
        }

        Page<Order> orders = orderRepository.findAll(
                specification,
                pageable
        );





        if (page >= orders.getTotalPages() && orders.getTotalPages() > 0) {
            throw new IllegalArgumentException(
                    "Requested page does not exist"
            );
        }

        List<OrderResponse> content = orders.getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return new PageResponse<>(
                content,
                orders.getNumber(),
                orders.getSize(),
                orders.getTotalElements(),
                orders.getTotalPages(),
                orders.isLast()
        );
    }
    public OrderStatsResponse getOrderStats() {

        User currentUser = getCurrentUser();

        if (!isAdmin(currentUser)) {
            throw new UnauthorizedAccessException(
                    "Only admin can access order statistics"
            );
        }

        OrderStatsResponse response = new OrderStatsResponse();

        response.setTotalOrders(
                orderRepository.count()
        );

        response.setPendingOrders(
                orderRepository.countByStatus(OrderStatus.PENDING)
        );

        response.setConfirmedOrders(
                orderRepository.countByStatus(OrderStatus.CONFIRMED)
        );

        response.setShippedOrders(
                orderRepository.countByStatus(OrderStatus.SHIPPED)
        );

        response.setDeliveredOrders(
                orderRepository.countByStatus(OrderStatus.DELIVERED)
        );

        response.setCancelledOrders(
                orderRepository.countByStatus(OrderStatus.CANCELLED)
        );

        response.setTotalRevenue(
                orderRepository.getTotalRevenue()
        );

        return response;
    }


    @Transactional
    public OrderResponse updateOrderStatus(
            Long id,
            OrderStatusRequest request) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + id
                ));


        User currentUser = getCurrentUser();

        if (!isAdmin(currentUser)) {
            throw new UnauthorizedAccessException(
                    "Only admin can update order status"
            );
        }


        OrderStatus currentStatus = order.getStatus();
        OrderStatus newStatus = request.getStatus();

        // Order can be confirmed only after successful payment
        if (currentStatus == OrderStatus.PENDING
                && newStatus == OrderStatus.CONFIRMED) {

            Payment payment = paymentRepository
                    .findByOrder(order)
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "Payment not found for this order"
                            )
                    );

            if (payment.getStatus() != PaymentStatus.SUCCESS) {
                throw new IllegalStateException(
                        "Order cannot be confirmed until payment is successful"
                );
            }
        }

        if (!currentStatus.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    "Invalid order status transition from "
                            + currentStatus
                            + " to "
                            + newStatus
            );
        }

        // Restore stock when order is cancelled
        if (newStatus == OrderStatus.CANCELLED) {

            for (OrderItem item : order.getItems()) {

                Product product = item.getProduct();

                product.setStockQuantity(
                        product.getStockQuantity() + item.getQuantity()
                );
            }
        }
        OrderStatusHistory history = new OrderStatusHistory();

        history.setOrder(order);
        history.setOldStatus(currentStatus);
        history.setNewStatus(newStatus);

        order.getStatusHistory().add(history);

        order.setStatus(newStatus);

        Order updatedOrder = orderRepository.save(order);

        return mapToResponse(updatedOrder);
    }

    @Transactional
    public OrderResponse cancelOrder(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + id
                ));

        User currentUser = getCurrentUser();

        // Only CUSTOMER can cancel their own order
        if (isAdmin(currentUser)) {
            throw new UnauthorizedAccessException(
                    "Admin cannot cancel orders through this endpoint"
            );
        }

        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException(
                    "You are not authorized to cancel this order"
            );
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending orders can be cancelled"
            );
        }
        Payment payment = paymentRepository
                .findByOrder(order)
                .orElse(null);

        if (payment != null
                && payment.getStatus() == PaymentStatus.SUCCESS) {

            throw new IllegalStateException(
                    "Order cannot be cancelled because payment is already successful"
            );
        }

        // Restore stock
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();

            product.setStockQuantity(
                    product.getStockQuantity() + item.getQuantity()
            );
        }

        // Save status history
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setOldStatus(OrderStatus.PENDING);
        history.setNewStatus(OrderStatus.CANCELLED);

        order.getStatusHistory().add(history);

        // Update status
        order.setStatus(OrderStatus.CANCELLED);

        Order updatedOrder = orderRepository.save(order);

        return mapToResponse(updatedOrder);
    }


    @Transactional
    public void deleteOrder(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + id
                ));
        User currentUser = getCurrentUser();

        if (!isAdmin(currentUser)) {
            throw new UnauthorizedAccessException(
                    "Only admin can delete an order"
            );
        }



        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Cancelled order cannot be deleted"
            );
        }

        if (order.getStatus() == OrderStatus.SHIPPED) {
            throw new IllegalStateException(
                    "Shipped order cannot be deleted"
            );
        }

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException(
                    "Delivered order cannot be deleted"
            );
        }

        // Restore product stock
        for (OrderItem item : order.getItems()) {

            Product product = item.getProduct();

            product.setStockQuantity(
                    product.getStockQuantity() + item.getQuantity()
            );
        }
        paymentRepository.findByOrder(order)
                .ifPresent(paymentRepository::delete);

        orderRepository.delete(order);
    }


    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        return (User) authentication.getPrincipal();
    }


    private boolean isAdmin(User user) {

        return user.getRole() == Role.ADMIN;
    }


    private void checkOrderAccess(
            Order order,
            User currentUser
    ) {

        // ADMIN can access every order
        if (isAdmin(currentUser)) {
            return;
        }

        // Normal USER can access only their own order
        if (!order.getUser().getId()
                .equals(currentUser.getId())) {

            throw new UnauthorizedAccessException(
                    "You are not authorized to access this order"
            );
        }
    }

    private OrderResponse mapToResponse(Order order) {

        OrderResponse response = new OrderResponse();

        response.setId(order.getId());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());

        List<OrderItemResponse> itemResponses = order.getItems()
                .stream()
                .map(item -> {

                    OrderItemResponse itemResponse =
                            new OrderItemResponse();

                    itemResponse.setProductId(
                            item.getProduct().getId()
                    );

                    itemResponse.setProductName(
                            item.getProduct().getName()
                    );

                    itemResponse.setQuantity(
                            item.getQuantity()
                    );

                    itemResponse.setPrice(
                            item.getPrice()
                    );

                    itemResponse.setSubtotal(
                            item.getPrice()
                                    .multiply(
                                            BigDecimal.valueOf(
                                                    item.getQuantity()
                                            )
                                    )
                    );

                    return itemResponse;
                })
                .toList();

        response.setItems(itemResponses);

        return response;
    }


    public OrderStatsResponse getOrderDashboard(
            LocalDateTime from,
            LocalDateTime to
    ) {
        User currentUser = getCurrentUser();

        if (!isAdmin(currentUser)) {
            throw new UnauthorizedAccessException(
                    "Only admin can access order dashboard"
            );
        }

        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException(
                    "From date cannot be after to date"
            );
        }

        Specification<Order> specification = Specification
                .where(OrderSpecification.createdAfter(from))
                .and(OrderSpecification.createdBefore(to));

        List<Order> orders = orderRepository.findAll(specification);

        OrderStatsResponse response = new OrderStatsResponse();

        response.setTotalOrders(orders.size());

        response.setPendingOrders(
                orders.stream()
                        .filter(order ->
                                order.getStatus() == OrderStatus.PENDING)
                        .count()
        );

        response.setConfirmedOrders(
                orders.stream()
                        .filter(order ->
                                order.getStatus() == OrderStatus.CONFIRMED)
                        .count()
        );

        response.setShippedOrders(
                orders.stream()
                        .filter(order ->
                                order.getStatus() == OrderStatus.SHIPPED)
                        .count()
        );

        response.setDeliveredOrders(
                orders.stream()
                        .filter(order ->
                                order.getStatus() == OrderStatus.DELIVERED)
                        .count()
        );

        response.setCancelledOrders(
                orders.stream()
                        .filter(order ->
                                order.getStatus() == OrderStatus.CANCELLED)
                        .count()
        );

        BigDecimal totalRevenue = orders.stream()
                .filter(order ->
                        order.getStatus() == OrderStatus.DELIVERED)
                .filter(order ->
                        paymentRepository.findByOrder(order)
                                .map(payment ->
                                        payment.getStatus() == PaymentStatus.SUCCESS)
                                .orElse(false)
                )
                .map(Order::getTotalAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        response.setTotalRevenue(totalRevenue);

        return response;
    }

    public List<OrderStatusHistoryResponse> getOrderStatusHistory(
            Long orderId
    ) {

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with id: " + orderId
                        )
                );

        User currentUser = getCurrentUser();

        // USER can only see their own order history
        // ADMIN can see any order history
        checkOrderAccess(order, currentUser);

        List<OrderStatusHistory> historyList =
                orderRepository.findStatusHistoryByOrderId(orderId);

        return historyList.stream()
                .map(history -> {

                    OrderStatusHistoryResponse response =
                            new OrderStatusHistoryResponse();

                    response.setOldStatus(
                            history.getOldStatus()
                    );

                    response.setNewStatus(
                            history.getNewStatus()
                    );

                    response.setChangedAt(
                            history.getChangedAt()
                    );

                    return response;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(User user) {

        List<Order> orders =
                orderRepository.findByUserIdOrderByCreatedAtDesc(
                        user.getId()
                );

        return orders.stream()
                .map(this::mapToResponse)
                .toList();
    }
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders() {

        User user = getCurrentUser();

        List<Order> orders =
                orderRepository.findByUserIdOrderByCreatedAtDesc(
                        user.getId()
                );

        return orders.stream()
                .map(this::mapToResponse)
                .toList();
    }

}