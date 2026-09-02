CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,

    order_id BIGINT NOT NULL UNIQUE,

    amount DECIMAL(19, 2) NOT NULL,

    status VARCHAR(50) NOT NULL,

    payment_method VARCHAR(50) NOT NULL,

    created_at TIMESTAMP,

    CONSTRAINT fk_payment_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
);