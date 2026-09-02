CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,

    status VARCHAR(50),

    total_amount DECIMAL(19, 2),

    user_id BIGINT NOT NULL,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_orders_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);