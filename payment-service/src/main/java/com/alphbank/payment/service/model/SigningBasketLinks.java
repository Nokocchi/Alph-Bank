package com.alphbank.payment.service.model;

import lombok.Builder;

import java.net.URI;

@Builder
public record SigningBasketLinks(URI basketStatusURI, URI basketAuthorizationStatsURI, URI basketAuthorizationURI) {
}
