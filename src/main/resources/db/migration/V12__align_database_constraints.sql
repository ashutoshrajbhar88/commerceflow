-- Align categories with Category entity

ALTER TABLE categories
    ALTER COLUMN name SET NOT NULL;

ALTER TABLE categories
    ALTER COLUMN name TYPE VARCHAR(100);

ALTER TABLE categories
    ALTER COLUMN created_at SET NOT NULL;

ALTER TABLE categories
    ALTER COLUMN updated_at SET NOT NULL;


-- Align products with Product entity

ALTER TABLE products
    ALTER COLUMN category_id SET NOT NULL;

ALTER TABLE products
    ALTER COLUMN price TYPE NUMERIC(12,2);

ALTER TABLE products
    ALTER COLUMN created_at SET NOT NULL;

ALTER TABLE products
    ALTER COLUMN updated_at SET NOT NULL;


-- Align orders with Order entity

ALTER TABLE orders
    ALTER COLUMN status SET NOT NULL;

ALTER TABLE orders
    ALTER COLUMN total_amount SET NOT NULL;


-- Align payments with Payment entity

ALTER TABLE payments
    ALTER COLUMN created_at SET NOT NULL;