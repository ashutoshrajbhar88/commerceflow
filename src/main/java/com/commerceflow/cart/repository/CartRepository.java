package com.commerceflow.cart.repository;

import com.commerceflow.cart.Cart;
import com.commerceflow.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser(User user);

}