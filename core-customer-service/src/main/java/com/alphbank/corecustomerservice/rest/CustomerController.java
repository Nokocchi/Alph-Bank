package com.alphbank.corecustomerservice.rest;

import com.alphbank.corecustomerservice.rest.model.*;
import com.alphbank.corecustomerservice.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
@Tag(name = "name", description = "Description")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/search")
    @Operation(summary = "Search customers by government id", description = "Returns a list of customers")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<CustomerSearchResponse>> searchCustomers(){
        return customerService.findAllCustomers()
                .collectList()
                .map(CustomerSearchResponse::new)
                .map(this::toResponseEntity);
    }

    // TODO: Handle duplicate govId/CountryCode error and return proper 4xx error
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<Customer>> createCustomer(@RequestBody CreateCustomerRequest createCustomerRequest){
        return customerService.createCustomer(createCustomerRequest)
                .map(this::toResponseEntity);

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<UUID>> deleteCustomer(@PathVariable("id") UUID customerId){
        return customerService.deleteCustomer(customerId)
                .then(Mono.just(toResponseEntity(customerId)));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<Customer>> getCustomer(@PathVariable("id") UUID customerId){
        return customerService
                .getCustomer(customerId)
                .map(this::toResponseEntity);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<Customer>> updateCustomer(@PathVariable("id") UUID customerId, @RequestBody UpdateCustomerRequest updateCustomerRequest){
        return customerService
                .updateCustomer(customerId, updateCustomerRequest)
                .map(this::toResponseEntity);
    }

    private <T> ResponseEntity<T> toResponseEntity(T responseBody) {
        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .body(responseBody);
    }

}
