package com.alphbank.payment.rest.model.response;

import com.alphbank.payment.service.model.BasketSigningStatus;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record BasketDTO(UUID basketId,
                        BasketSigningStatus signingStatus,
                        List<InternalPaymentDTO> payments,
                        List<InternalPeriodicPaymentDTO> periodicPayments) {

}
