ALTER TABLE order_items
    ADD COLUMN sku VARCHAR(255);

UPDATE order_items
SET sku = product_name
WHERE sku IS NULL;

ALTER TABLE order_items
    ALTER COLUMN sku SET NOT NULL;