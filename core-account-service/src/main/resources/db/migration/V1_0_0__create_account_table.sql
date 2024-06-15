CREATE EXTENSION "uuid-ossp";

CREATE TABLE account (
    id UUID NOT NULL DEFAULT uuid_generate_v4(),
    customer_id UUID NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    balance NUMERIC(19,6) NOT NULL DEFAULT 0,
    iban VARCHAR(34) NOT NULL,
    PRIMARY KEY (id)
);

