package com.alphbank.payment.service.model;

import com.alphbank.payment.service.repository.model.PaymentEntity;
import com.alphbank.payment.service.repository.model.PeriodicPaymentEntity;
import com.alphbank.payment.service.repository.model.SigningBasketEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.UUID;

@Builder
@Setter
@Getter
@Accessors(fluent = true)
public class SigningBasket {

    private UUID id;
    private UUID signingSessionId;
    private List<Payment> payments;
    private List<PeriodicPayment> periodicPayments;
    private BasketSigningStatus signingStatus;

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
                .payments(payments)
                .periodicPayments(periodicPayments)
                .build();
    }
}
