CREATE DATABASE inventory_db;
\c inventory_db;

CREATE TABLE IF NOT EXISTS product (
    id BIGSERIAL PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    price NUMERIC(19, 2) NOT NULL
);

INSERT INTO product (product_name, quantity, price) VALUES
('iPhone 15 Pro', 20, 999.99),
('MacBook Pro M3', 10, 1999.99),
('AirPods Pro 2', 50, 249.99),
('Samsung Galaxy S24 Ultra', 15, 1199.99),
('iPad Pro 12.9', 25, 1099.00),
('Sony WH-1000XM5', 30, 399.99),
('Dell XPS 15', 12, 1799.50);

CREATE DATABASE order_db;
\c order_db;

CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    status VARCHAR(255) NOT NULL,
    total_price NUMERIC(19, 2) NOT NULL
);

INSERT INTO orders (product_id, quantity, status, total_price) VALUES
(1, 2, 'COMPLETED', 1999.98),
(2, 1, 'COMPLETED', 1999.99),
(3, 3, 'COMPLETED', 749.97),
(1, 1, 'PENDING', 999.99),
(4, 5, 'CANCELLED', 5999.95);
