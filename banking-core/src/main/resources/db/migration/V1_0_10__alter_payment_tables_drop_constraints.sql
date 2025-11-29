ALTER TABLE payment
    DROP CONSTRAINT must_have_recipient;

ALTER TABLE periodic_payment
    DROP CONSTRAINT must_have_recipient
