CREATE EXTENSION "uuid-ossp";

CREATE TABLE loan (
    id UUID NOT NULL DEFAULT uuid_generate_v4(),
    customer_id UUID NOT NULL,
    account_id UUID NOT NULL,
    paid_out boolean NOT NULL DEFAULT false,
    amount NUMERIC(19,6) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    fixed_rate_interest NUMERIC(9,6) NOT NULL,
    loan_period_months INTEGER NOT NULL,
    payout_date_time TIMESTAMP WITHOUT TIME ZONE,

    PRIMARY KEY(id)
);

