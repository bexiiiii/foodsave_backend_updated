-- Add new fields to users table
ALTER TABLE users
    ADD COLUMN date_of_birth TIMESTAMP,
    ADD COLUMN status VARCHAR(20) DEFAULT 'ACTIVE';

-- Add new fields to products table
ALTER TABLE products
    ADD COLUMN stock_quantity INTEGER DEFAULT 0,
    ADD COLUMN sold_quantity INTEGER DEFAULT 0,
    ADD COLUMN created_at TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP;

-- Update existing records
UPDATE products
SET created_at = CURRENT_TIMESTAMP,
    updated_at = CURRENT_TIMESTAMP
WHERE created_at IS NULL; 