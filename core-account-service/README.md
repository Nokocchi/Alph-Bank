# Alph Bank Core
## Core-account-service

This service is part of the banking core of Alph Bank. It holds the customers' account information, including 

* Account id
* Account name
* Account IBAN
* Balance and currency

## Core-internal API

This service also provides a core-internal API that should only be used by the other core services.

It can be imagined that this API has stricter security and only allows requests from certain IPs through the firewall, etc.

This API allows the other core services to reduce and increase the balance of accounts, in relation to loan payouts or customer payments.