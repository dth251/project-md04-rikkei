CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    status VARCHAR(255) NOT NULL,
    total_price NUMERIC(19, 2) NOT NULL
);

TRUNCATE TABLE orders RESTART IDENTITY;

INSERT INTO orders (product_id, quantity, status, total_price) VALUES
(1, 2, 'COMPLETED', 1999.98),
(2, 1, 'COMPLETED', 1999.99),
(3, 3, 'COMPLETED', 749.97),
(1, 1, 'PENDING', 999.99),
(4, 5, 'CANCELLED', 5999.95);
