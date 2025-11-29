CREATE TABLE transaction_ (
    id UUID NOT NULL DEFAULT uuid_generate_v4(),
    account_id UUID NOT NULL,
    currency VARCHAR(3) NOT NULL,
    amount NUMERIC(19,6) NOT NULL,
    new_balance NUMERIC(19,6) NOT NULL,
    message TEXT,
    execution_date_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,,
    created_from_type TEXT NOT NULL,
    created_from_id UUID NOT NULL,

    PRIMARY KEY(id)
);

