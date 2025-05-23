CREATE TABLE discounts (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    percentage DOUBLE PRECISION NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    start_date TIMESTAMP,
    end_date TIMESTAMP
); 