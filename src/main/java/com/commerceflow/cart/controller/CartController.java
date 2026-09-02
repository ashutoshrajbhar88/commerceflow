package com.commerceflow.cart.controller;

import com.commerceflow.cart.dto.AddCartItemRequest;
import com.commerceflow.cart.dto.CartResponse;
import com.commerceflow.cart.dto.UpdateCartItemRequest;
import com.commerceflow.cart.service.CartService;
import com.commerceflow.order.dto.OrderResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // ADD PRODUCT TO CART
    @Operation(summary = "Add product to cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product added to cart successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or insufficient stock"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PostMapping("/items")
    @ResponseStatus(HttpStatus.OK)
    public CartResponse addItemToCart(
            @Valid @RequestBody AddCartItemRequest request
    ) {
        return cartService.addItemToCart(request);
    }

    // GET CURRENT USER CART
    @Operation(summary = "Get current user's cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping
    public CartResponse getCart() {
        return cartService.getCart();
    }

    // UPDATE PRODUCT QUANTITY
    @Operation(summary = "Update cart item quantity")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart item updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid quantity or insufficient stock"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Product or cart item not found")
    })
    @PutMapping("/items/{productId}")
    public CartResponse updateCartItem(
            @PathVariable
            @Min(value = 1, message = "Product ID must be at least 1")
            Long productId,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        return cartService.updateCartItem(productId, request);
    }

    // REMOVE ONE PRODUCT FROM CART
    @Operation(summary = "Remove product from cart")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cart item removed successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Cart item not found")
    })
    @DeleteMapping("/items/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCartItem(
            @PathVariable
            @Min(value = 1, message = "Product ID must be at least 1")
            Long productId
    ) {
        cartService.removeCartItem(productId);
    }

    // CLEAR ENTIRE CART
    @Operation(summary = "Clear current user's cart")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cart cleared successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart() {
        cartService.clearCart();
    }

    // CHECKOUT CART
    @Operation(summary = "Checkout current user's cart")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Cart is empty or insufficient stock"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout() {

        OrderResponse response =
                cartService.checkout();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
}