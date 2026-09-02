package com.commerceflow.order.controller;

import com.commerceflow.order.OrderStatus;
import com.commerceflow.order.dto.OrderRequest;
import com.commerceflow.order.dto.OrderResponse;
import com.commerceflow.order.dto.OrderStatsResponse;
import com.commerceflow.order.dto.OrderStatusHistoryResponse;
import com.commerceflow.order.dto.OrderStatusRequest;
import com.commerceflow.order.service.OrderService;
import com.commerceflow.product.dto.PageResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // CREATE ORDER
    @Operation(summary = "Create a new order")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or insufficient stock"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody OrderRequest request) {

        OrderResponse response = orderService.createOrder(request);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    // CHECKOUT FROM CART
    @Operation(summary = "Checkout cart")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Cart is empty or insufficient stock"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout() {

        OrderResponse response = orderService.checkout();

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    // GET ORDER STATISTICS
    @Operation(summary = "Get order statistics")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order statistics retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
    })
    @GetMapping("/stats")
    public ResponseEntity<OrderStatsResponse> getOrderStats() {

        OrderStatsResponse response = orderService.getOrderStats();

        return ResponseEntity.ok(response);
    }

    // GET ORDER DASHBOARD
    @Operation(summary = "Get order dashboard")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order dashboard retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid date range"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
    })
    @GetMapping("/dashboard")
    public ResponseEntity<OrderStatsResponse> getOrderDashboard(

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to
    ) {

        OrderStatsResponse response =
                orderService.getOrderDashboard(from, to);

        return ResponseEntity.ok(response);
    }

    // GET ORDER STATUS HISTORY
    @Operation(summary = "Get order status history")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order status history retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid order ID"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - not authorized to access this order"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}/history")
    public ResponseEntity<List<OrderStatusHistoryResponse>> getOrderStatusHistory(

            @PathVariable
            @Min(value = 1, message = "Order ID must be at least 1")
            Long id
    ) {

        List<OrderStatusHistoryResponse> response =
                orderService.getOrderStatusHistory(id);

        return ResponseEntity.ok(response);
    }

    // GET ORDER BY ID
    @Operation(summary = "Get order by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "400", description = "Invalid order ID"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - not authorized to access this order"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable
            @Min(value = 1, message = "Order ID must be at least 1")
            Long id) {

        OrderResponse response = orderService.getOrderById(id);

        return ResponseEntity.ok(response);
    }

    // GET ALL ORDERS WITH PAGINATION
    @Operation(
            summary = "Get all orders with filtering, sorting and pagination",
            description = "Retrieve orders using optional filters such as status, date range and amount range."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Orders retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters, filters, sorting or pagination values"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token is missing or invalid"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient permissions"
            )
    })
    @GetMapping
    public PageResponse<OrderResponse> getAllOrders(

            @RequestParam(required = false)
            OrderStatus status,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to,

            @RequestParam(required = false)
            @DecimalMin(value = "0.0", message = "Minimum amount cannot be negative")
            BigDecimal minAmount,

            @RequestParam(required = false)
            @DecimalMin(value = "0.0", message = "Maximum amount cannot be negative")
            BigDecimal maxAmount,

            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page number cannot be negative")
            int page,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "Size must be at least 1")
            @Max(value = 100, message = "Size cannot exceed 100")
            int size,

            @RequestParam(defaultValue = "id")
            String sortBy,

            @RequestParam(defaultValue = "desc")
            String direction
    ) {

        return orderService.getAllOrders(
                status,
                from,
                to,
                minAmount,
                maxAmount,
                page,
                size,
                sortBy,
                direction
        );
    }

    // UPDATE ORDER STATUS
    @Operation(summary = "Update order status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status or invalid status transition"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Only admin can update order status"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable
            @Min(value = 1, message = "Order ID must be at least 1")
            Long id,

            @Valid @RequestBody OrderStatusRequest request) {

        OrderResponse response =
                orderService.updateOrderStatus(id, request);

        return ResponseEntity.ok(response);
    }

    // CANCEL ORDER
    @Operation(summary = "Cancel order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
            @ApiResponse(responseCode = "400", description = "Order cannot be cancelled"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - order cancellation not allowed"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable
            @Min(value = 1, message = "Order ID must be at least 1")
            Long id) {

        OrderResponse response = orderService.cancelOrder(id);

        return ResponseEntity.ok(response);
    }

    // DELETE ORDER
    @Operation(summary = "Delete order")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Order deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Order cannot be deleted"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Only admin can delete orders"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(
            @PathVariable
            @Min(value = 1, message = "Order ID must be at least 1")
            Long id) {

        orderService.deleteOrder(id);

        return ResponseEntity.noContent().build();
    }

    // GET CURRENT USER ORDERS
    @Operation(summary = "Get current user's orders")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User orders retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/my")
    public ResponseEntity<List<OrderResponse>> getMyOrders() {

        return ResponseEntity.ok(
                orderService.getMyOrders()
        );
    }
}