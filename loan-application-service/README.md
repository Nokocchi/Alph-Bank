# Loan Application Service

This service handles loan application requests from the frontend,
and does the following business logic:

* Sets up a signing session in signing-service and replies to the frontend with the signing url
* Receives a message on RabbitMQ when the signing session has changed status
* If the signing session status is COMPLETED, a credit check is done through creditworthiness-assessment-service
* If the credit worthiness result demands manual inspection, a caseworker can see the loan application in their dashboard
* If the caseworker has approved the loan application, or if the credit worthiness check was successful, the loan application is sent to core-loan-service for execution

This keeps the core-loan-service clean from "business logic", and allows it to only worry about internal core logic.