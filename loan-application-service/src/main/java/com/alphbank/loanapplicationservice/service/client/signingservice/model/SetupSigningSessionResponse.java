package com.alphbank.loanapplicationservice.service.client.signingservice.model;

import java.util.UUID;

public record SetupSigningSessionResponse (UUID signingSessionId, String signingUrl) {
}
