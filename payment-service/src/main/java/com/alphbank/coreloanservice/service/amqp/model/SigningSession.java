package com.alphbank.coreloanservice.service.amqp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Locale;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
public class SigningSession {

    private UUID signingSessionId;
    private UUID customerId;
    private String nationalId;
    private Locale locale;
    private SigningStatus signingStatus;
    private String documentToSign;
    private String signingStatusUpdatedRoutingKey;
    private String onSuccessRedirectUrl;
    private String onFailRedirectUrl;
    private String signingUrl;
}
