ALTER TABLE payment
    ADD COLUMN recipient_name TEXT NOT NULL;

ALTER TABLE periodic_payment
    ADD COLUMN recipient_name TEXT NOT NULL;
