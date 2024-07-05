package com.alphbank.signingservice.rest.model;

import java.util.Locale;
import java.util.UUID;

public record SetupSigningSessionRequest(UUID customerId,
                                         String governmentId,
                                         Locale locale,
                                         String signingStatusUpdatedRoutingKey,
                                         String documentToSign,
                                         String onSuccessRedirectUrl,
                                         String onFailRedirectUrl) {
}
