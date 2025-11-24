package com.alphbank.core.customer.rest;

import com.alphbank.commons.impl.JsonLog;
import com.alphbank.core.customer.service.CustomerService;
import com.alphbank.core.customer.service.model.Customer;
import com.alphbank.core.rest.model.CreateCustomerRequestDTO;
import com.alphbank.core.rest.model.CustomerDTO;
import com.alphbank.core.rest.model.UpdateCustomerRequestDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.api.CustomerApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
public class CustomerController implements CustomerApi {

    private final CustomerService customerService;
    private final JsonLog jsonLog;

    @Override
    public Mono<ResponseEntity<CustomerDTO>> createCustomer(Mono<CreateCustomerRequestDTO> createCustomerRequest, ServerWebExchange exchange) {
        return createCustomerRequest
                .doOnNext(request -> log.info("Creating customer customers from request {}", jsonLog.format(request)))
                .map(Customer::from)
                .flatMap(customerService::createCustomer)
                .map(Customer::toDTO)
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto))
                .doOnNext(response -> log.info("Returning created customer: {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error creating customer", e));
    }

    @Override
    public Mono<ResponseEntity<CustomerDTO>> getCustomer(UUID customerId, ServerWebExchange exchange) {
        log.info("Getting customer with id: {}", customerId);
        return customerService.getCustomer(customerId)
                .map(Customer::toDTO)
                .doOnNext(response -> log.info("Returning customer: {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error getting customer with id: {}", customerId, e))
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteCustomer(UUID customerId, ServerWebExchange exchange) {
        log.info("Deleting customer with id: {}", customerId);
        return customerService.deleteCustomer(customerId)
                .doOnSuccess(ignored -> log.info("Deleted customer with id: {}", customerId))
                .doOnError(e -> log.error("Error deleting customer with id: {}", customerId, e))
                .thenReturn(ResponseEntity.noContent().build());
    }

    @Override
    public Mono<ResponseEntity<CustomerDTO>> updateCustomer(UUID customerId, Mono<UpdateCustomerRequestDTO> updateCustomerRequestDTO, ServerWebExchange exchange) {
        return updateCustomerRequestDTO
                .doOnNext(request -> log.info("Updating customer with id: {} and new info: {}", customerId, request))
                .flatMap(request -> customerService.updateCustomer(customerId, request))
                .map(Customer::toDTO)
                .doOnNext(response -> log.info("Returning updated customer: {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error updating customer with id " + customerId, e))
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Flux<CustomerDTO>>> searchCustomers(ServerWebExchange exchange) {
        log.info("Searching for all customers");
        Flux<CustomerDTO> customers = customerService.findAllCustomers()
                .map(Customer::toDTO);

        return Mono.just(ResponseEntity.ok(customers));
    }

}
