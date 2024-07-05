# Alph Bank Core
## Core-loan-service

This service is part of the banking core of Alph Bank. It is responsible for paying out loans to customers. 

This service does **not** contain the business logic for orchestrating the loan application flow, setting up e-signatures and listening for signing events, performing creditworthiness checks and so on.
This is handled by other services in order to keep the core as clean as possible, and in order to allow it to only care about internal core logic.

Core-loan-service essentially trusts that any requests to POST /loan have already been approved and should be executed without any questions. 

## Loan payouts

A call is made to the "core-internal API" in core-account-service in order to increase the balance of the account in question.

Afterwards, the loan is sent via RabbitMQ to government-transfer-service which, for compliance reasons, uploads all loan applications to the government of the account holder as a nightly batch job.