CREATE TABLE payment (
    id UUID NOT NULL DEFAULT uuid_generate_v4(),
    basket_id UUID NOT NULL,
    account_id UUID NOT NULL,
    recipient_iban TEXT NOT NULL,
    message_to_self TEXT,
    message_to_recipient TEXT,
    payment_amount NUMERIC(19,6) NOT NULL,
    payment_currency VARCHAR(3) NOT NULL,
    scheduled_date_time TIMESTAMP WITHOUT TIME ZONE,

    PRIMARY KEY(id)
);

