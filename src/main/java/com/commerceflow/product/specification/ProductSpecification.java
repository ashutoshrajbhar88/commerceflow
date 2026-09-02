package com.commerceflow.product.specification;

import com.commerceflow.product.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

    public static Specification<Product> hasKeyword(String keyword) {

        return (root, query, criteriaBuilder) -> {

            if (keyword == null || keyword.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + keyword.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Product> hasCategoryId(Long categoryId) {

        return (root, query, criteriaBuilder) -> {

            if (categoryId == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("category").get("id"),
                    categoryId
            );
        };
    }

    public static Specification<Product> hasMinPrice(Double minPrice) {

        return (root, query, criteriaBuilder) -> {

            if (minPrice == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.greaterThanOrEqualTo(
                    root.get("price"),
                    minPrice
            );
        };
    }

    public static Specification<Product> hasMaxPrice(Double maxPrice) {

        return (root, query, criteriaBuilder) -> {

            if (maxPrice == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.lessThanOrEqualTo(
                    root.get("price"),
                    maxPrice
            );
        };
    }

    public static Specification<Product> hasStock(Boolean inStock) {

        return (root, query, criteriaBuilder) -> {

            if (inStock == null) {
                return criteriaBuilder.conjunction();
            }

            if (inStock) {
                return criteriaBuilder.greaterThan(
                        root.get("stockQuantity"),
                        0
                );
            } else {
                return criteriaBuilder.equal(
                        root.get("stockQuantity"),
                        0
                );
            }
        };
    }

}