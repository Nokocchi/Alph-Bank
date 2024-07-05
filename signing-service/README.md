# Signing Service

This service provides e-signature capabilities. 
This is needed when customers apply for loans or make payments, and the bank needs authorization and a digital signature from the customer.

## Usage

* POST /signing 

This sets up a signing session for the customer and returns a link to the page where they can sign, as well as the id of the signing session.

All updates about the signing session are sent through RabbitMQ.

In the request body, the client provides the document to sign as plain text, 
as well as a routing-key that should be used when signing-service sends status updates about this particular signing session.

* PATCH /signing/{signingSessionId}

This endpoint updates the status of a signing session.

In real-life scenarios, we would probably be using a third party vendor to facilitate these e-signature flows.
You can imagine that they would send us signing status updates through a webhook / callback, but since we are mocking this flow in Alph Bank,
the front-end can just call this PATCH endpoint directly.

* GET /signing/{signingSessionId}

Returns a signing session, including the document to sign. This is used by the front-end in the signing flow when the customer is about to sign.