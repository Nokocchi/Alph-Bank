package com.alphbank.signingservice.rest.model;

import java.util.UUID;

public record SetupSigningSessionResponse(UUID signingSessionId, String signingUrl) {
}
