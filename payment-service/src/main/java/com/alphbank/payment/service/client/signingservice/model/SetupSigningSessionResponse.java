package com.alphbank.payment.service.client.signingservice.model;

import java.util.UUID;

public record SetupSigningSessionResponse (UUID signingSessionId, String signingUrl) {
}
