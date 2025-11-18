package com.alphbank.payment.service.model;

import com.alphbank.payment.service.repository.model.PaymentEntity;
import com.alphbank.payment.service.repository.model.PeriodicPaymentEntity;
import com.alphbank.payment.service.repository.model.SigningBasketEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.With;
import lombok.experimental.Accessors;
import org.berlingroup.rest.model.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Builder
@Setter
@Getter
@Accessors(fluent = true)
public class SigningBasket {

    private UUID id;
    private UUID signingSessionId;
    @With
    private List<Payment> payments;
    @With
    private List<PeriodicPayment> periodicPayments;
    private BasketSigningStatus signingStatus;
    private String signerIPAddress;
    private URI onSigningSuccessRedirectUrl;
    private URI onSigningFailedRedirectUrl;
    private URI signingURI;

    public static SigningBasket from(SigningBasketEntity basketEntity, List<PaymentEntity> paymentEntities, List<PeriodicPaymentEntity> periodicPaymentEntities) {
        List<PeriodicPayment> periodicPayments = periodicPaymentEntities.stream()
                .map(PeriodicPaymentEntity::toModel)
                .toList();

        List<Payment> payments = paymentEntities.stream()
                .map(PaymentEntity::toModel)
                .toList();

        return SigningBasket.builder()
                .id(basketEntity.getId())
                .signingSessionId(basketEntity.getSigningSessionId())
                .signingStatus(basketEntity.getSigningStatus())
                .signerIPAddress(basketEntity.getSignerIpAddress())
                .onSigningSuccessRedirectUrl(basketEntity.getOnSignSuccessRedirectUri())
                .onSigningFailedRedirectUrl(basketEntity.getOnSignFailedRedirectUri())
                .signingURI(basketEntity.getSigningURI())
                .payments(payments)
                .periodicPayments(periodicPayments)
                .build();
    }

    public static SigningBasket from(String psuIPAddress, URI tppRedirectURI, URI tppNokRedirectURI) {
        return SigningBasket.builder()
                .signerIPAddress(psuIPAddress)
                .onSigningSuccessRedirectUrl(tppRedirectURI)
                .onSigningFailedRedirectUrl(tppNokRedirectURI)
                .build();
    }

    public SigningBasketResponse201DTO toPSD2InitiationDTO(SigningBasketLinks links) {
        return SigningBasketResponse201DTO.builder()
                .basketId(id.toString())
                .links(LinksSigningBasketDTO.builder()
                        .scaRedirect(HrefTypeDTO.builder()
                                .href(links.basketAuthorizationURI().toString())
                                .build())
                        .status(HrefTypeDTO.builder()
                                .href(links.basketStatusURI().toString())
                                .build())
                        .scaStatus(HrefTypeDTO.builder()
                                .href(links.basketAuthorizationStatsURI().toString())
                                .build())
                        .build())
                .build();
    }

    public SigningBasketResponse200DTO toPSD2InfoDTO() {
        List<String> paymentIds = payments.stream().map(payment -> payment.id().toString()).toList();
        return SigningBasketResponse200DTO.builder()
                .payments(paymentIds)
                .transactionStatus(signingStatus.toPSD2TransactionStatus())
                .build();
    }

    public StartScaprocessResponseDTO toPSD2StartAuthorizationDTO(SigningBasketLinks links) {
        return StartScaprocessResponseDTO.builder()
                .scaStatus(signingStatus.toPSD2ScaStatus())
                .authorisationId(id.toString())
                .links(LinksStartScaProcessDTO.builder()
                        .scaStatus(HrefTypeDTO.builder()
                                .href(links.basketAuthorizationStatsURI().toString())
                                .build())
                        .build())
                .build();
    }
}
