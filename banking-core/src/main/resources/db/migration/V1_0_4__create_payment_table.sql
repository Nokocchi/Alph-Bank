CREATE TABLE payment (
    id UUID NOT NULL DEFAULT uuid_generate_v4(),
    from_customer_id UUID NOT NULL,
    from_account_id UUID NOT NULL,
    executed boolean NOT NULL DEFAULT false,
    monetary_value NUMERIC(19,6) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    recipient_iban VARCHAR(34) NOT NULL,
    recipient_account_id UUID,
    message_to_self TEXT,
    message_to_recipient TEXT,
    scheduled_date_time TIMESTAMP WITHOUT TIME ZONE,
    execution_date_time TIMESTAMP WITHOUT TIME ZONE,

    PRIMARY KEY(id)
);

