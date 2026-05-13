CREATE TABLE order_items
(
    id BIGSERIAL PRIMARY KEY,

    product_name VARCHAR(255) NOT NULL,

    quantity INTEGER NOT NULL,

    price NUMERIC(19,2) NOT NULL,

    order_id BIGINT NOT NULL,

    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id)
            REFERENCES orders(id)
);