ALTER TABLE payment
    DROP COLUMN from_customer_id,
    ALTER COLUMN recipient_iban DROP NOT NULL,
    DROP COLUMN executed,
    ADD COLUMN periodic_payment_id UUID,
    ADD CONSTRAINT must_have_recipient
    CHECK (recipient_iban IS NOT NULL OR recipient_account_id IS NOT NULL);