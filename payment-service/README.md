# Payment Service

## About PSD2

In short, the European PSD2 directive states that bank customers (PSU / Payment Service User) have the right to use any third party
providers (TPP) for their online banking services.
This includes payments (PIS / Payment Initiation Service), account information (AIS / Account Information Service), 
and various other domains.
Additionally, there is a requirement for strong customer authentication (SCA). 

The guideline for the scope of required implementation of the PSD2 API is that if a customer can do an action described by PSD2
directly in their banks's online portals, then the customer must be able to do the same action from a TPP
without extra obstacles or disadvantages.

This means that if an AlphBank customer can fetch their accounts in the AlphBank frontend, there should be an API that allows other banks and financial institutions to provide the same account information through their online services, if the AlphBank customer decides to use their services. AlphBank is not allowed to require extra steps or limit the provided data in these cases.

## This service

### Is this a good way to Implement PSD2? 

For the sake of a simple demonstration, this service naïvely assumes we will only ever have one Payment API, and that it's safe to implement the PSD2 API with the same backend.

In a large bank, you probably have many different payment flows for different products - some that are in scope of PSD2, and some that are not.
Additionally, PSD2 covers more than just payments, including account information and card accounts, which all have the same issue:
If you build the PSD2 API together with one of these internal APIs, relying on the same backend, then you risk needing to implement multiple PSD2 APIs for the same domain in order to cover all compliance needs.
For that reason, you should probably have a separate, dedicated PSD2 service (or services) in a real life scenario, which supports the complete PSD2-required feature set of all your other APIs.

### APIs

This services provides two APIs:

* PSD2 API
  * This API is intended to be called from another service, simulating a TPP. They must keep track of payment IDs and basketIDs.
  * This API uses models generated from official Berlin Group OpenAPI YAML specs.
  * .. but the controllers are manually created to keep Swagger clean.
  * I have decided that consents are not in scope - You can assume it is handled by a PSD2/Open Banking aggregation vendor which is the sole consumer of this API.
* Internal payment API
  * This API is intended to be called directly from the AlphBank frontend, acting as "our internal payment API".
  * Not bound by PSD2 specs, so we can simplify things a bit, like server-side filtering.
  * Inspired by the PSD2 specification (PSD2-first approach) in order to ensure compliance of the internal API. This also allows me to re-use service layer business logic.
  * Supports scheduled payments, which are not in scope for PSD2. 

Payments are not executed directly, but instead they are added to baskets
which can then be authorized through e-signature authorization by the customer. Payment-service gets a callback from signing-service when the signing basket authorization has completed,
and the payments will be sent to the BankingCore for execution.

### Payments / Periodic Payments

Despite these two types of "payments" sharing a common interface and API in the PSD2 spec, these have been implemented as two separate paths in the code, with two separate controllers and two separate database tables, etc., and there is a reason for that.

- One reason is purely technical: Inheritance, anyOf() and instanceOf() do not behave nicely in the generated REST models and controllers.
I also think it makes for a confusing, less clearly defined API, especially when viewed in Swagger. 
- Secondly, these two concepts are simply not comparable. A "payment" is a single payment that is by itself executable. A "periodic payment" is merely a template for how to generate individual Payments to be executed at specific times. Payments have a scheduledExecutionTime field, whereas PeriodicPayments have other fields like frequency and endDate. Treating them as the same thing, even under a common interface, is in my opinion incorrect and can lead to unnecessary complexity in the code.
