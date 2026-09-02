package com.commerceflow.cart.service;

import com.commerceflow.cart.dto.AddCartItemRequest;
import com.commerceflow.cart.dto.CartResponse;
import com.commerceflow.cart.dto.UpdateCartItemRequest;
import com.commerceflow.order.dto.OrderResponse;

public interface CartService {

    CartResponse addItemToCart(
            AddCartItemRequest request
    );

    CartResponse getCart();

    CartResponse updateCartItem(
            Long productId,
            UpdateCartItemRequest request
    );

    void removeCartItem(Long productId);

    void clearCart();

    OrderResponse checkout();
}