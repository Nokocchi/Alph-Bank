CREATE EXTENSION "uuid-ossp";

CREATE TABLE customer (
    id UUID NOT NULL DEFAULT uuid_generate_v4(),
    government_id VARCHAR(20) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    customer_language VARCHAR(50),
    country VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE(government_id, country)
);

