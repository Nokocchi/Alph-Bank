CREATE EXTENSION "uuid-ossp";

CREATE TABLE loanapplication (
    id UUID NOT NULL DEFAULT uuid_generate_v4(),
    customer_id UUID NOT NULL,
    account_id UUID NOT NULL,
    signing_session_id UUID,
    government_id TEXT NOT NULL,
    country_code TEXT NOT NULL,
    locale TEXT NOT NULL,
    principal NUMERIC(19,6) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    fixed_rate_interest_apr NUMERIC(6,4) NOT NULL,
    loan_term_months INTEGER NOT NULL,

    PRIMARY KEY(id)
);

