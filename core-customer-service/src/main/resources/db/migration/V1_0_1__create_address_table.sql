CREATE TABLE address (
    id UUID NOT NULL DEFAULT uuid_generate_v4(),
    customer_id UUID NOT NULL,
    street_address VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE
);

