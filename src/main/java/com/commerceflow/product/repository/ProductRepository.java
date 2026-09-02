package com.commerceflow.product.repository;

import com.commerceflow.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends
        JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product> {

    boolean existsByName(String name);

    List<Product> findByNameContainingIgnoreCase(String keyword);

    List<Product> findByCategoryId(Long categoryId);


    // PRODUCTS WITH LOW STOCK
    long countByStockQuantityLessThanEqual(int stockQuantity);


    // OUT OF STOCK PRODUCTS
    long countByStockQuantity(int stockQuantity);

    @Query("""
        SELECT COALESCE(SUM(p.stockQuantity), 0)
        FROM Product p
        """)
    Long getTotalStock();


    @Query("""
        SELECT COALESCE(SUM(p.price * p.stockQuantity), 0)
        FROM Product p
        """)
    BigDecimal getTotalInventoryValue();

}

