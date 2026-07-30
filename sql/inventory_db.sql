CREATE TABLE IF NOT EXISTS product (
    id BIGSERIAL PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    price NUMERIC(19, 2) NOT NULL
);

TRUNCATE TABLE product RESTART IDENTITY;

INSERT INTO product (product_name, quantity, price) VALUES
('iPhone 15 Pro', 20, 999.99),
('MacBook Pro M3', 10, 1999.99),
('AirPods Pro 2', 50, 249.99),
('Samsung Galaxy S24 Ultra', 15, 1199.99),
('iPad Pro 12.9', 25, 1099.00),
('Sony WH-1000XM5', 30, 399.99),
('Dell XPS 15', 12, 1799.50);
