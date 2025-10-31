
# Alph Bank

If you feel like you don't quite have enough money, then just create your own bank!

This is a personal project, simulating an entire banking ecosystem, which is intended to showcase my knowledge and experience in Java backend development and Fintech. 

## Features

* Core banking functionality
* Payments to and from other customers using PSD2 inspired API (Payment baskets)
* Scheduled payments
* Recurring payments
* Simulated compliance-based nightly batch uploads to government entities
* Personal Loan applications with fixed-rate interest
* Simulated signing flow for authorizing payments and applying for loans, utilizing asynchronous callbacks when customer has signed
* Simulated credit worthiness checks for loan applications
* Simulated AML and fraud checks for payments and loan applications, triggering reports to government entities
* Know Your Customer (KYC) 
* Admin interface for manually processing and approving suspicious payments and loan applications
* Supports multiple languages, countries and currencies


## How to run

- To be written..

## Documentation:

[Banking Core](/banking-core)

[Frontend](/svelte-frontend)

[Custom Spring Boot Starters](/spring-boot-starters)

[Payments](/payment-service)

[Loans](/loan-application-service/)

[Document signing](/signing-service/)

[Bruno Collection](/bruno-collection/)

## Architecture & code guidelines:

### Backend service architecture

* Java, SpringBoot, Webflux (Project Reactor)
* Custom Spring Boot Starters for common behavior
* APIs must be REST-like API (No HATEOAS :) )
* If multiple database-writes are done in a single transaction, it must be marked with @Transactional, and rollbacks must be covered by tests
* If a function needs to perform a database-write and also publish a message on a message queue in the same transaction, then the database-write must be done first, and the function must be marked as @Transactional. Rollbacks must be covered by tests.
* Each service is structured like this. Model naming is shown in bold.
* /contract
    * TODO: Contract base / domain base?
    * /domainA  (skip if only one domain)
        * /controllerA
            * create_entity_ok.groovy
            * create_entity_bad_request.groovy
* /main
    * /java
        * /domainA (skip if only one domain)
            * /rest
                * /model
                    * **MyObjectDTO**.java
                    * /request
                        * MyRequest.java
                    * /response
                        * MyResponse.java
                * /error
                    * /model
                        * MyCustomExceptionWhichResultsInAPIResponse.java
                    * ExceptionHandler.java
                * MyController.java
            * /service
                * /model
                    * **MyObject**.java
                * /error
                    * /model
                        * MyBusinessLogicExceptionWhichIsNotHandledByExceptionHandler.java
                * /repository
                    * /model
                        * **MyObjectEntity**.java
                    * MyObjectRepository.java
                * /client
                    * /externalServiceA
                        * /model
                            * /request
                                * ExternalServiceARequest.java
                            * /response
                                * ExternalServiceAResponse.java
                            * **ExternalServiceAMyObject**.java
                        * /config
                            * ExternalServiceAWebClientConfiguration.java
                        * ExternalServiceAWebClient.java
                * /amqp
                    * /config
                        * RabbitMQConfiguration.java
                    * RabbitMQService.java
                * MyService.java
    * /resources
        * /db.migration
            * V1_0_0__create_entity_table.sql
        * application.yaml
        * application-local.yaml
        * application-test.yaml
* /test
    * /domainA (skip if only one domain)
        * /integration
            * IntegrationBase.java
            * /controllerA
                * ControllerABase.java
                * EndpointAIntegrationTest.java
                * EndpointBIntegrationTest.java
        * /unit
            * ComplexFunctionUnitTest.java
        * /resources
            TODO: Wiremock .json files?
            TODO: application-test.yaml here or in main/resources?



To be determined:

* All endpoints must be documented (Generate controllers from OpenAPI spec, or write them myself with OpenAPI/Swagger annotations?)
* Where to convert between rest/service/entity/client models?

### Testing

Testing is of course important, but this is a hobby project and a human only lives so long, so I have decided to only add tests to the Banking Core and the Payment Service.
* **Integration tests:** The bulk of the testing must be done with Integration tests using @SpringBootTest and TestContainers for repositories and message queues. External APIs are mocked with Wiremock. In rare cases, if necessary, Mockito Spybeans can be used to verify calls to specific methods.
* **Unit tests:** Complex logic, usually in standalone functions, must be tested in unit tests without a full Spring application context, autowiring only the class being tested. Mockito must be used to mock dependencies and verify calls to these or lack thereof.
* **Contract tests:** Must be implemented to ensure that API changes are non-breaking and backwards compatible, and that consumers of the API are implementing the API correctly. Must be implemented in Groovy, using Mockito to mock the calls from the Controller into the Service layer. 

### Frontend

* Written in SvelteKit and Typescript
* Must use file-system (and slug)-based routing
* Must use Zod for validating requests and responses
* Must use Superforms for forms
* Must use Typescript types/interfaces for all internal typing

### Observability
* TODO: Elasticsearch as a central location for logs generated by all services?

### What is not included:
* Proper/safe authentication and authorization
* No proper deployment that can be tested. Local testing only
* Mostly intended to show backend skills and banking knowledge, so the front-end is quite barebones and does not use or provide good accessibility, best practices, nice user experiences across devices and browsers etc.
* Handling of concurrency and race conditions. These are real issues in enterprise software, especially once you implement concurrent requests and multiple replicas of the same service. There are many ways to solve this, depending on the situation. 
    * **Optimistic locking:** Only allow commits if the version number is correct, else roll back transaction. This works if multiple writes are done in a transaction.
    * **Pessimistic locking:** Get exclusive write-permission until your transaction has been committed.
    * **Write with filter**, and if affected rows is >0, continue business logic. If not, abort. This only works in special cases, like updating the enum status of a row.
    * ... And others. But I will leave these as an exercise for the reader :)

## Component diagram in the loan flow

![overall-component-diagram-loan](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/Nokocchi/Alph-Bank/master/docs/overall-component-diagram-loan.puml)

## Component diagram in the payment flow

![overall-component-diagram-payment](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/Nokocchi/Alph-Bank/master/docs/overall-component-diagram-payment.puml)

