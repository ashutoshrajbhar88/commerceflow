CREATE TABLE product_images (
    id BIGSERIAL PRIMARY KEY,

    image_url VARCHAR(500) NOT NULL,

    product_id BIGINT NOT NULL,

    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_product_images_product
        FOREIGN KEY (product_id)
        REFERENCES products(id)
        ON DELETE CASCADE
);