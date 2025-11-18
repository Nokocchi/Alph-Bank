CREATE EXTENSION "uuid-ossp";

CREATE TABLE basket (
    id UUID NOT NULL DEFAULT uuid_generate_v4(),
    signing_session_id UUID,
    signing_status TEXT NOT NULL,
    on_sign_success_redirect_uri TEXT,
    on_sign_failed_redirect_uri TEXT,
    signing_uri TEXT,
    signer_ip_address TEXT,

    PRIMARY KEY(id)
);

