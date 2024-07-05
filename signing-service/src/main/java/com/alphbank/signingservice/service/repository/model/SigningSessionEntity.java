package com.alphbank.signingservice.service.repository.model;

import com.alphbank.signingservice.rest.model.SigningStatus;
import com.alphbank.signingservice.rest.model.SetupSigningSessionRequest;
import lombok.Builder;
import lombok.Data;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Builder
@Data
@Table("signingsession")
public class SigningSessionEntity {

    @Column("id")
    @Id
    private UUID signingSessionId;

    @Column("customer_id")
    private UUID customerId;

    @Column("government_id")
    private String governmentId;

    @Column("country_code")
    private String countryCode;

    @Column("locale")
    private String locale;

    @Column("document_to_sign")
    private String documentToSign;

    @Column("on_success_redirect_url")
    private String onSuccessRedirectUrl;

    @Column("on_fail_redirect_url")
    private String onFailRedirectUrl;

    @With
    @Builder.Default
    @Column("signing_status")
    private String signingStatus = SigningStatus.CREATED.toString();

    @Column("signing_status_updated_routing_key")
    private String signingStatusUpdatedRoutingKey;

    public static SigningSessionEntity from(SetupSigningSessionRequest setupSigningSessionRequest) {
        return SigningSessionEntity.builder()
                .customerId(setupSigningSessionRequest.customerId())
                .governmentId(setupSigningSessionRequest.governmentId())
                .countryCode(setupSigningSessionRequest.locale().getCountry())
                .documentToSign(setupSigningSessionRequest.documentToSign())
                .locale(setupSigningSessionRequest.locale().getLanguage())
                .onSuccessRedirectUrl(setupSigningSessionRequest.onSuccessRedirectUrl())
                .onFailRedirectUrl(setupSigningSessionRequest.onFailRedirectUrl())
                .signingStatusUpdatedRoutingKey(setupSigningSessionRequest.signingStatusUpdatedRoutingKey())
                .build();
    }
}
