package com.commerceflow.cart.service;

import com.commerceflow.cart.Cart;
import com.commerceflow.cart.dto.CartItemResponse;

import java.math.BigDecimal;
import java.util.List;
import com.commerceflow.exception.InsufficientStockException;
import com.commerceflow.exception.ResourceNotFoundException;

import com.commerceflow.order.dto.OrderResponse;
import com.commerceflow.order.dto.OrderRequest;
import com.commerceflow.order.dto.OrderItemRequest;
import com.commerceflow.order.service.OrderService;

import org.springframework.transaction.annotation.Transactional;

import com.commerceflow.cart.CartItem;
import com.commerceflow.cart.dto.AddCartItemRequest;
import com.commerceflow.cart.dto.CartResponse;
import com.commerceflow.cart.dto.UpdateCartItemRequest;
import com.commerceflow.cart.repository.CartItemRepository;
import com.commerceflow.cart.repository.CartRepository;
import com.commerceflow.product.Product;
import com.commerceflow.product.repository.ProductRepository;
import com.commerceflow.user.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    private final OrderService orderService;


    public CartServiceImpl(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            OrderService orderService
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.orderService = orderService;
    }

    private User getCurrentUser() {

        return (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    @Override
    @Transactional
    public CartResponse addItemToCart(
            AddCartItemRequest request
    ) {

        User user = getCurrentUser();

        // Find product
        Product product = productRepository
                .findById(request.getProductId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found")
                );

        // Find existing cart or create new cart
        Cart cart = cartRepository
                .findByUser(user)
                .orElseGet(() -> {

                    Cart newCart = new Cart();

                    newCart.setUser(user);

                    java.time.LocalDateTime now =
                            java.time.LocalDateTime.now();

                    newCart.setCreatedAt(now);
                    newCart.setUpdatedAt(now);

                    return cartRepository.save(newCart);
                });

        // Check whether product already exists in cart
        CartItem cartItem = cartItemRepository
                .findByCartAndProduct(cart, product)
                .orElse(null);

        if (cartItem != null) {

            int newQuantity =
                    cartItem.getQuantity()
                            + request.getQuantity();

            // Check available stock
            if (newQuantity > product.getStockQuantity()) {
                throw new InsufficientStockException(
                        "Requested quantity exceeds available stock"
                );
            }

            // Increase existing quantity
            cartItem.setQuantity(newQuantity);

        } else {

            // Check available stock
            if (request.getQuantity() > product.getStockQuantity()) {
                throw new InsufficientStockException(
                        "Requested quantity exceeds available stock"
                );
            }

            // Create new cart item
            cartItem = new CartItem();

            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
        }

        cartItemRepository.save(cartItem);

        cart.setUpdatedAt(
                java.time.LocalDateTime.now()
        );

        cartRepository.save(cart);

        return getCart();
    }

    @Override
    public CartResponse getCart() {

        User user = getCurrentUser();

        Cart cart = cartRepository
                .findByUser(user)
                .orElse(null);

        CartResponse response = new CartResponse();

        // If user does not have a cart yet
        if (cart == null) {

            response.setItems(List.of());
            response.setTotalAmount(BigDecimal.ZERO);

            return response;
        }

        List<CartItem> cartItems =
                cartItemRepository.findByCart(cart);

        // Convert CartItem -> CartItemResponse
        List<CartItemResponse> items = cartItems
                .stream()
                .map(cartItem -> {

                    Product product = cartItem.getProduct();

                    BigDecimal subtotal = product.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            cartItem.getQuantity()
                                    )
                            );

                    CartItemResponse itemResponse =
                            new CartItemResponse();

                    itemResponse.setProductId(product.getId());
                    itemResponse.setProductName(product.getName());
                    itemResponse.setPrice(product.getPrice());
                    itemResponse.setQuantity(cartItem.getQuantity());
                    itemResponse.setSubtotal(subtotal);

                    return itemResponse;
                })
                .toList();

        // Calculate total amount
        BigDecimal totalAmount = items
                .stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        response.setCartId(cart.getId());
        response.setItems(items);
        response.setTotalAmount(totalAmount);

        return response;
    }

    @Override
    @Transactional
    public CartResponse updateCartItem(
            Long productId,
            UpdateCartItemRequest request
    ) {

        User user = getCurrentUser();

        // Find user's cart
        Cart cart = cartRepository
                .findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart not found")
                );

        // Find product
        Product product = productRepository
                .findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found")
                );

        // Find cart item
        CartItem cartItem = cartItemRepository
                .findByCartAndProduct(cart, product)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product is not in the cart"
                        )
                );

        // Check available stock
        if (request.getQuantity() > product.getStockQuantity()) {
            throw new InsufficientStockException(
                    "Requested quantity exceeds available stock"
            );
        }

// Update quantity
        cartItem.setQuantity(
                request.getQuantity()
        );

        cartItemRepository.save(cartItem);

        // Update cart timestamp
        cart.setUpdatedAt(
                java.time.LocalDateTime.now()
        );

        cartRepository.save(cart);

        return getCart();
    }

    @Override
    @Transactional
    public void removeCartItem(Long productId) {

        User user = getCurrentUser();

        // Find user's cart
        Cart cart = cartRepository
                .findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cart not found"
                        )
                );

        // Find product
        Product product = productRepository
                .findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found"
                        )
                );

        // Find cart item
        CartItem cartItem = cartItemRepository
                .findByCartAndProduct(cart, product)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product is not in the cart"
                        )
                );

        // Delete cart item
        cartItemRepository.delete(cartItem);

        // Update cart timestamp
        cart.setUpdatedAt(
                java.time.LocalDateTime.now()
        );

        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void clearCart() {

        User user = getCurrentUser();

        // Find user's cart
        Cart cart = cartRepository
                .findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cart not found"
                        )
                );

        // Get all items from the cart
        List<CartItem> cartItems =
                cartItemRepository.findByCart(cart);

        // Delete all cart items
        cartItemRepository.deleteAll(cartItems);

        // Update cart timestamp
        cart.setUpdatedAt(
                java.time.LocalDateTime.now()
        );

        cartRepository.save(cart);
    }



    @Override
    @Transactional
    public OrderResponse checkout() {

        User user = getCurrentUser();

        Cart cart = cartRepository
                .findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart not found")                );

        List<CartItem> cartItems =
                cartItemRepository.findByCart(cart);

        if (cartItems.isEmpty()) {
            throw new IllegalStateException(
                    "Cart is empty"
            );
        }

        List<OrderItemRequest> orderItems =
                cartItems.stream()
                        .map(cartItem -> {

                            OrderItemRequest item =
                                    new OrderItemRequest();

                            item.setProductId(
                                    cartItem.getProduct().getId()
                            );

                            item.setQuantity(
                                    cartItem.getQuantity()
                            );

                            return item;
                        })
                        .toList();

        OrderRequest orderRequest =
                new OrderRequest();

        orderRequest.setItems(orderItems);

        // Create order using existing OrderService
        OrderResponse orderResponse =
                orderService.createOrder(orderRequest);

        // Clear cart after successful checkout
        cartItemRepository.deleteByCart(cart);

// Update cart timestamp
        cart.setUpdatedAt(
                java.time.LocalDateTime.now()
        );

        cartRepository.save(cart);

        return orderResponse;
    }

}