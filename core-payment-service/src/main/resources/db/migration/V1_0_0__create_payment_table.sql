CREATE EXTENSION "uuid-ossp";

CREATE TABLE payment (
    id UUID NOT NULL DEFAULT uuid_generate_v4(),
    customer_id UUID NOT NULL,
    account_id UUID NOT NULL,
    executed boolean NOT NULL DEFAULT false,
    remittance_amount NUMERIC(19,6) NOT NULL,
    remittance_currency VARCHAR(3) NOT NULL,
    recipient_iban VARCHAR(34) NOT NULL,
    message_to_self VARCHAR(255),
    execution_date_time TIMESTAMP WITHOUT TIME ZONE,

    PRIMARY KEY(id)
);

