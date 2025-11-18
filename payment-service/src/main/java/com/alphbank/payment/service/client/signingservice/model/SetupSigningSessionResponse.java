package com.alphbank.payment.service.client.signingservice.model;

import java.net.URI;
import java.util.UUID;

public record SetupSigningSessionResponse (UUID signingSessionId, URI signingUrl) {
}
