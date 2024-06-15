package com.alphbank.coreaccountservice.rest.model;

import java.util.List;

// Should be deleted and endpoint should return Flux<Account>
public record AccountSearchResponse(List<Account> accounts) {
}
