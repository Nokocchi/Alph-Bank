CREATE TABLE loan (
    id UUID NOT NULL DEFAULT uuid_generate_v4(),
    customer_id UUID NOT NULL,
    account_id UUID NOT NULL,
    principal NUMERIC(19,6) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    fixed_rate_interest_apr NUMERIC(6,4) NOT NULL,
    loan_term_months INTEGER NOT NULL,
    payout_date_time TIMESTAMP WITHOUT TIME ZONE,

    PRIMARY KEY(id)
);

