CREATE EXTENSION "uuid-ossp";

CREATE TABLE basket (
    id UUID NOT NULL DEFAULT uuid_generate_v4(),
    customer_id UUID NOT NULL,
    signing_session_id UUID,
    signing_status TEXT NOT NULL,

    PRIMARY KEY(id)
);

