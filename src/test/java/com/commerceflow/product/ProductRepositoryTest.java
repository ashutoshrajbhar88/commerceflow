package com.commerceflow.product;
import org.springframework.boot.test.context.SpringBootTest;
import com.commerceflow.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testRepository() {

        assertNotNull(productRepository);
    }
}