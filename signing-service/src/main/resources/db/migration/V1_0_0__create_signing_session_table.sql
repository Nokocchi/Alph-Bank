CREATE EXTENSION "uuid-ossp";

CREATE TABLE signingsession (
    id UUID NOT NULL DEFAULT uuid_generate_v4(),
    customer_id UUID NOT NULL,
    government_id TEXT NOT NULL,
    country_code TEXT NOT NULL,
    locale TEXT NOT NULL,
    signing_status TEXT NOT NULL,
    signing_status_updated_routing_key TEXT NOT NULL,
    document_to_sign TEXT NOT NULL,
    on_success_redirect_url TEXT NOT NULL,
    on_fail_redirect_url TEXT NOT NULL,

    PRIMARY KEY(id)
);

