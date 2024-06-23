CREATE EXTENSION "uuid-ossp";

CREATE TABLE customer (
    id UUID NOT NULL DEFAULT uuid_generate_v4(),
    government_id TEXT NOT NULL,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    phone_number TEXT,
    customer_language TEXT,
    country TEXT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE(government_id, country)
);

