# Alph Bank Styleguide

## Backend

### Technologies used
* **Production:**
  * Java 25
  * SpringBoot 3.5.7
  * Webflux (Project Reactor)
  * Postgres
  * Flyway
  * RabbitMQ
  * Redis
* **Testing:**
  * Wiremock
  * Junit
  * AssertJ
  * Mockito
  * Testcontainers
* Custom Spring Boot Starters for common behavior
* Use Maven local to publish contract stubs and OpenAPI specs.

### APIs and code generation
* APIs must be JSON-based and REST-like (Without HATEOAS :) )
* APIs must be built spec-first, with a version-controlled OpenAPI spec in the repository
* You must publish this OpenAPI spec and make sure it is versioned.
* Your own API must be generated from the versioned published OpenAPI spec, to ensure that all changes to your API exist as a new version for consumers as well.
* As part of the build step, you should auto generate the controller interfaces and controller models for your own API, 
as well as models and client APIs for your dependencies, using their published OpenAPI spec.
The generated code should be in a folder separate from the source code, and not checked into version control.
Only the interface for your API should be generated, not the controller implementation itself.

### Naming
* All naming is restricted to ASCII, using the English language.
* Enum values and final (static) string constants must use UPPERCASE_SNAKE_CASE.
* Models
  * Service-layer models have no suffix or prefix. 
    * Example: Payment.java
  * Rest-layer models are suffixed with -DTO. 
    * Example: PaymentDTO.java
  * Repository-layer models are suffixed with -Entity. 
    * Example: PaymentEntity.java
  * Models for dependent services are prefixed with the name of the service, and suffixed with -DTO. (TODO: Can we do this with OpenApi Generator?)
    * Example: CoreBankingPaymentDTO.java
* APIs
  * **Resources:** Use kebab-case, plural noun
  * **Path variables:** Use kebab-case, singular noun.
  * **Query parameters:** Use snake_case, noun
  * Example: http://localhost:8080/payments/{payment-id}/card-transactions?transaction_type=OUTGOING

### Model conversion
We should follow three core principles:
1. The business logic in the service layer should not know about or depend on implementation details of the controller or repository layers.
This means that we should theoretically be able to swap out the API implementation or database implementation without touching the service layer (or at least the business logic).
2. There should not be any model conversion methods in the service layer. Instead, put the model conversion methods
   in the models themselves.
3. The service layer methods take service layer models as input, and return service layer models as output

Unfortunately, things are not that simple in a Webflux project using R2DBC and generated models:
* There are no officially supported reactive ORMs for Spring Webflux r2dbc.
  This means that we must build service-layer models ourselves, and these often consist of data from multiple database tables
* The generated REST-layer DTOs cannot be changed, so model conversion methods cannot be put there.
* The client models are generated as well

For these reasons, the conclusion is that the model conversion **methods** must be placed here:

* DTO <-> Service layer
  * Must happen in the service layer model
* Entity -> Service layer
  * Must happen in the entity model if possible
  * If multiple entities are needed, then do the conversion in the service layer model
* Service layer -> Entity
  * In the entity model
* Service layer <-> Client
  * A dedicated mapper class for this client.

And they must be called from:

* DTO <-> Service layer
  * In the controller.
* Service layer <-> Entity
  * In the service layer.

### Error handling and transactions
* Malformed request bodies, missing headers and missing query parameters must result in a 400 Bad Request with a clear error message. 
* If any business logic requires a 4xx response, you must throw an exception, catch it in a Rest Error Handler, and return a standardized error object.
* If multiple database-writes are done in a single transaction, it must be marked with @Transactional, and rollbacks must be covered by tests
* If a function needs to perform a database-write and also publish a message on a message queue in the same transaction, then the database-write must be done first, and the function must be marked as @Transactional. Rollbacks must be covered by tests.

### Testing

Testing is of course important, but this is a hobby project and a human only lives so long, so I have decided to only add tests to the Banking Core and the Payment Service.

As we know from the testing pyramid, unit tests are the fastest, simplest and least brittle tests. As we move towards
integration tests, and beyond, the tests get bigger, slower and more brittle. The reason is that, in the case of
integration tests that send a request to your service, you immediately run into some issues: 

#### Alph Bank's stance on integration tests

1. Integration tests require setting up an entire Spring Application Context, web server, loading all the beans and dependencies, etc.,
and this is very slow and resource intensive compared to a simple unit test. Add too many integration tests, and your test
suite will quickly take several minutes. 
2. Loading the entire application context means that you have a **lot** of dependencies.
3. You cannot prepare data in your database
by calling repository.save() directly from your test. The test and the request run in separate threads, and the changes in
the repository made from the test code are not committed before the request is handled. This can maybe be avoided with @Sql annotation,
or some kind of @BeforeEach setup, but this is a lot to maintain long term and is not very elegant. This means that
you often need to create the data in the database via requests to your service, which can be argued is the goal of integration tests
after all. 
4. Now your test is really big and sends multiple requests, and relies on your entire service running, including all its dependencies.
Your single test probably touches hundreds or thousands of lines of code in many different classes, and relies on the current
specific behavior of all this code. 
5. Now you have two more issues:
   1. Your test covers so much code, but only **one** of the many possible **specific** outcomes. Do you create an entire
   new and very similar integration test for the case where you return 200 OK instead of 201 Created if the entity already exists?
   Or where you return 409 if the request contains some data that's already in the database? Where do you draw the line?
   2. If anybody wants to make a change somewhere in the code, and there are many big integration tests, there is a high
   likelihood that these tests will break. It will be difficult for them to understand and update your integration tests.
   And, getting back to the speed of the test suite: It will take many, many minutes for them to fix and re-run these many slow tests.

But it is also true that integration tests are the best guarantee you have that the individual parts of your service
work together as intended. You can test that somewhere deep in your service layer you will throw CustomerNotFoundException 
if the customer could not be found, and then you can test that your controller returns 404 Not Found if your service layer 
is mocked to throw CustomerNotFoundException, but what if a coworker changes the code in between such that your request
never hits the code that throws CustomerNotFoundException? Then all your tests would continue to pass, but in a real transaction,
you would get an unexpected result. So it is important to have some integration tests, but only just enough for basic sanity tests.


#### Testing guidelines

* **Unit tests:** 
  * The bulk of the testing must be done with unit tests. 
  * Spring Application Context / webserver should not be run, so don't use @SpringBootTest. 
  * Unit tests usually only cover a small piece of code, preferably a single function, and have very few non-mocked dependencies, preferably none.
  * Mockito must be used to mock dependencies and verify calls to these or lack thereof. These tests should run with just @ExtendWith(MockitoExtension.class) and @Mock/@InjectMocks annotations.
  * If necessary, use test slices (@DataR2dbcTest, @DataMongoTest, DataJpaTest etc.), which only load the necessary minimal Spring beans. You can use @MockitoBean for dependencies, and @Import() the bean(s) that you need the real implementation of. 
  * There should be unit tests for client-implementations using the published contract stubs. This verifies that the requests and responses have the correct format, including headers, authorization, etc.
* **Contract tests:** 
  * Must be implemented to ensure that API changes are non-breaking and backwards compatible. 
  * Must be implemented in Groovy, and the test base class should not use @SpringBootTest unless absolutely necessary. Using @ExtendWith(MockitoExtension.class) and @Mock annotation on the service class should be enough in most cases,
    unless you need some ObjectMapper or security configuration.
  * If you have any exceptions that are caught in your Rest Exception Handler, then just mock that the service class throws this exception.
* **Integration tests:** 
  * Some few integration tests should be implemented, just to sanity check entire flows. 
  * You should use @SpringBootTest, and TestContainers for repositories and message queues. 
  * External APIs should be manually mocked with Wiremock - not using stubs. In rare cases, if necessary, Mockito Spybeans can be used to verify calls to specific methods.
* **Wiremock:** Mocking must be done in Java, in the test method the mock is needed. Don't use JSON-file mappings. These can be quite difficult to understand in large test suites.

### Versioning

#### Custom Spring Boot Starters

The version number should consist of 5 digits separated by periods. The first three should be equal to the version of Spring Boot used.
The fourth digit is the major version number of the starter, which is updated when there are breaking changes to the starter.
Finally, the fifth digit is the path version of the starter, which is updated when the changes are backwards compatible.

This allows us to version the code within the starter correctly, while also targeting multiple Spring Boot versions.

#### Contract stubs & API Versions

Alph Bank uses spec-first API development, which means that all changes must be made in the openapi.yaml file within the repository.
The API of a service should not be generated directly from the openapi.yaml file in the repository, but instead from the published one.
This ensures that the API of a service does not get out of sync with the generated client implementation in the consumers. 
In other words: If your published openapi.yaml is good enough for consumers, it should be good enough for your own service to use as well.

Additionally, this theoretically allows you to support multiple past API versions in your service.

Contract stubs and API specs are always published with the same version, namely the version specified in `build.gradle`.
This ensures that if your API has been updated, there will be corresponding contract stubs for the consumers of your API. Realistically,
they will probably not specify a specific version of the contract stubs in their tests, but instead just fetch the newest, 
which means that even if they forget or fail to update the version of your openapi.yaml spec, 
their stub tests will still fail if your API has any breaking changes.

#### Flyway migrations

Let's be honest - does anybody ever use the major and minor version of Flyway migrations? For now, just increment the patch version indefinitely.
In microservices with dedicated small databases, it is unlikely that you will have that many versions anyway.

I suppose you could create a new baseline by exporting the current database structure as a new migration with an incremented major version,
and delete the Flyway migration history in the database. This way, we have a fresh start and can delete the old migrations. 
But that doesn't seem like the intended use case..


### How to

#### Publishing new version of the Alph Commons Spring Boot Starter

Make the desired change in the Spring Boot starter, increment the version number in `build.gradle` and use the Gradle task `publishToMavenLocal`.

#### Updating API in service

TODO: Should maybe use the published openapi.yaml?

Make a change in the openapi.yaml file. Then, to update the API in your own project, run the Gradle task `openApiGenerate`. 

Also, run the Gradle Task `publishToMavenLocal` in order to publish your new version for consumers to use.

In a real life scenario, this would be automated in the CI/CD pipeline.

#### Publishing new version of contract stubs

TODO


## Frontend

* Written in SvelteKit and Typescript
* Must use file-system-(and slug)-based routing
* Must use Zod for validating requests and responses
* Must use Superforms for forms
* Must use Typescript types/interfaces for all internal typing