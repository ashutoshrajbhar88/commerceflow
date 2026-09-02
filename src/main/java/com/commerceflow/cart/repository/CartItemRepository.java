package com.commerceflow.cart.repository;

import com.commerceflow.cart.Cart;
import com.commerceflow.cart.CartItem;
import com.commerceflow.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartAndProduct(
            Cart cart,
            Product product
    );

    List<CartItem> findByCart(Cart cart);

    void deleteByCart(Cart cart);

}