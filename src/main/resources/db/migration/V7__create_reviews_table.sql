CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,

    rating INTEGER NOT NULL,

    comment VARCHAR(1000),

    user_id BIGINT NOT NULL,

    product_id BIGINT NOT NULL,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_reviews_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_reviews_product
        FOREIGN KEY (product_id)
        REFERENCES products(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_reviews_user_product
        UNIQUE (user_id, product_id)
);