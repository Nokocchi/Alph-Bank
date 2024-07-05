# Alph Bank Core
## Core-payment-service

This service is part of the banking core of Alph Bank. It is responsible for making payments, both within the bank and outside of the bank. 

This service does **not** contain the business logic for orchestrating the payment flow, setting up e-signatures and listening for signing events, performing AML/fraud checks and so on.
This is handled by other services in order to keep the core as clean as possible, and in order to allow it to only care about internal core logic.

Core-payment-service essentially trusts that any requests to POST /payment have already been approved and should be executed without any questions. 

## Payments

A call is made to the "core-internal API" in core-account-service in order to decrease the balance of the account the payment was made from, and increase the balance of the account whose IBAN matches the recipient IBAN that was provided.

If no account with that IBAN is found, the recipient account must be outside the bank, and we assume that the payment went well.