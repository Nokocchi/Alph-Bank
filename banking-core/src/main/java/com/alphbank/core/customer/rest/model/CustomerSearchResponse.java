package com.alphbank.core.customer.rest.model;

import java.util.List;

// Should be deleted and endpoint should return Flux<Customer>
public record CustomerSearchResponse(List<Customer> customers) {
}
