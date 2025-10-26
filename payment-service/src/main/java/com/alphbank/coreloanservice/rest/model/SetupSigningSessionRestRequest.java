package com.alphbank.coreloanservice.rest.model;

import java.util.Locale;
import java.util.UUID;

public record SetupSigningSessionRestRequest(
        UUID customerId,
        String nationalId,
        Locale locale,
        String onSigningSuccessRedirectUrl,
        String onSigningFailedRedirectUrl) {
}
