package com.alphbank.core.customer.rest;

import com.alphbank.commons.impl.JsonLog;
import com.alphbank.core.customer.rest.model.CreateCustomerRequest;
import com.alphbank.core.customer.rest.model.Customer;
import com.alphbank.core.customer.rest.model.CustomerSearchResponse;
import com.alphbank.core.customer.rest.model.UpdateCustomerRequest;
import com.alphbank.core.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
@Tag(name = "Customer", description = "Description")
public class CustomerController {

    private final CustomerService customerService;
    private final JsonLog jsonLog;

    @GetMapping("/search")
    @Operation(summary = "Search all customers", description = "Returns a list of customers")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<CustomerSearchResponse>> searchCustomers() {
        log.info("Searching for all customers");
        return customerService.findAllCustomers()
                .collectList()
                .map(CustomerSearchResponse::new)
                .doOnNext(response -> log.info("Returning all customers: {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error searching all customers", e))
                .map(this::toResponseEntity);
    }

    // TODO: Handle duplicate govId/CountryCode error and return proper 4xx error
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<Customer>> createCustomer(@RequestBody CreateCustomerRequest createCustomerRequest) {
        log.info("Creating customer customers from request {}", jsonLog.format(createCustomerRequest));
        return customerService.createCustomer(createCustomerRequest)
                .doOnNext(response -> log.info("Returning created customer: {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error creating customer", e))
                .map(this::toResponseEntity);

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<UUID>> deleteCustomer(@PathVariable("id") UUID customerId) {
        log.info("Deleting customer customers with id {}", customerId);
        return customerService.deleteCustomer(customerId)
                .doOnSuccess(response -> log.info("Deleted customer with id {}", customerId))
                .doOnError(e -> log.error("Error deleting customer with id " + customerId, e))
                .then(Mono.just(toResponseEntity(customerId)));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<Customer>> getCustomer(@PathVariable("id") UUID customerId) {
        log.info("Getting customer with id {}", customerId);
        return customerService
                .getCustomer(customerId)
                .doOnNext(response -> log.info("Returning customer: {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error getting customer with id " + customerId, e))
                .map(this::toResponseEntity);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<Customer>> updateCustomer(@PathVariable("id") UUID customerId, @RequestBody UpdateCustomerRequest updateCustomerRequest) {
        log.info("Updating customer with id {} and new info {}", customerId, updateCustomerRequest);
        return customerService
                .updateCustomer(customerId, updateCustomerRequest)
                .doOnNext(response -> log.info("Returning updated customer: {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error updating customer with id " + customerId, e))
                .map(this::toResponseEntity);
    }

    private <T> ResponseEntity<T> toResponseEntity(T responseBody) {
        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .body(responseBody);
    }

}
