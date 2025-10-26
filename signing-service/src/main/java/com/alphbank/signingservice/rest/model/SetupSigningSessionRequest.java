package com.alphbank.signingservice.rest.model;

import java.util.Locale;
import java.util.UUID;

public record SetupSigningSessionRequest(UUID customerId,
                                         String nationalId,
                                         Locale locale,
                                         String signingStatusUpdatedRoutingKey,
                                         String documentToSign,
                                         String onSuccessRedirectUrl,
                                         String onFailRedirectUrl) {
}
