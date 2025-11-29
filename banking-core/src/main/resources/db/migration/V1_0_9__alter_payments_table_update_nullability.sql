ALTER TABLE payment
    ALTER COLUMN recipient_iban SET NOT NULL,
    ALTER COLUMN recipient_account_id SET NOT NULL;

ALTER TABLE periodic_payment
    ALTER COLUMN end_date DROP NOT NULL,
    ALTER COLUMN recipient_iban SET NOT NULL,
    ALTER COLUMN recipient_account_id SET NOT NULL,
    ALTER COLUMN frequency SET NOT NULL;