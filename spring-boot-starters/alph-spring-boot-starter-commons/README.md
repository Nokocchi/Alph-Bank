# Features

## ObjectMapper configuration

This project makes use of [JSR 354 Java Money API](https://javamoney.github.io/apidocs/javax/money/MonetaryAmount.html)

For the MonetaryAmount implementation, the Alph Bank project is using [Moneta](https://github.com/JavaMoney/jsr354-ri/blob/master/moneta-core/src/main/asciidoc/userguide.adoc)

Unfortunately, the default JSON representation of MonetaryAmount contains a lot of fields that we doesn't need. 

For that reason, I am using the [jackson-datatype-money Jackson Module](https://github.com/zalando/jackson-datatype-money/tree/main) in order to customize the standard ObjectMapper to serialize and deserialize MonetaryAmount as simple (Amount, Currency) tuples.


Unfortunately, Spring Webflux WebClient uses its own ObjectMapper instead of the one we configured, which means that the ObjectMapper we configured is not used in any WebClient calls. 
This starter provides a WebClient.Builder which has been configured to use the proper ObjectMapper.

## Json pretty-printing

In order to pretty-print objects as formatted JSON, use the jsonLog bean.

The standard ObjectMapper is used, which this starter has already configured to serialize and deserialize MonetaryAmount as (Amount, Currency) tuples.

```
  private final JsonLog jsonLog;
  log.info("Response: {}", jsonLog.format(response));
```

## Netty reactive wiretapping 

NOTE: This can greatly affect the performance and throughput of the service using this feature. For that reason, only use this for debugging, and never in production.

This starter provides wiretapping capabilities for both server and client.

More specifically, this means that it is possible to print 

* Requests received by the Controllers
* Requests sent from the WebClient
* Responses received in the WebClient

The following properties need to be set:

```
alph-commons.wiretapEnabled: true
```

```
logging:
    level:
        reactor:
            netty:
                http:
                    client:
                        HttpClient: DEBUG
                    server:
                        HttpServer: DEBUG
```

## Swagger

A Swagger page is available for all services at

### Core account service
http://localhost:8080/swagger-ui.html

### Core payment service
http://localhost:8081/swagger-ui.html

### Core loan service
http://localhost:8082/swagger-ui.html

### Core customer service
http://localhost:8083/swagger-ui.html

### Configuration
This starter overwrites the standard JSON representation of MonetaryAmount within Swagger. This means that any example values of MonetaryAmount presented to consumers in Swagger will be a simple (Amount, Currency) tuple instead of a big complex JSON object.

