CREATE TABLE periodic_payment (
    id UUID NOT NULL DEFAULT uuid_generate_v4(),
    from_account_id UUID NOT NULL,
    monetary_value NUMERIC(19,6) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    recipient_iban VARCHAR(34),
    recipient_account_id UUID,
    message_to_self TEXT,
    message_to_recipient TEXT,
    frequency TEXT,
    active boolean NOT NULL DEFAULT false,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    PRIMARY KEY(id),
    CONSTRAINT must_have_recipient
        CHECK (recipient_iban IS NOT NULL OR recipient_account_id IS NOT NULL)
);

