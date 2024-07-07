# Payment Service

This service handles payment requests from the frontend.

This API is inspired by PSD2, the European payment directive.

Payments are not executed directly or individually, but instead they are added to baskets
which can then be authorized through e-signature authorization by the customer.

Each customer can have one active basket at a time. A basket is created automatically if there are no current active baskets for the customer.

The baskets are signed through signing-service, and once payment-service gets notified by signing-service that the signing went well, 
the payments are sent to core-payment-service for execution.