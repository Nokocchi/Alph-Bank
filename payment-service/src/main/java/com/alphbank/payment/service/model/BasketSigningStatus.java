package com.alphbank.payment.service.model;

import org.berlingroup.rest.model.ScaStatusDTO;
import org.berlingroup.rest.model.ScaStatusResponseDTO;
import org.berlingroup.rest.model.TransactionStatusSBSDTO;

import java.util.Set;

public enum BasketSigningStatus {
    NOT_YET_STARTED,
    SIGNING_SESSION_CREATED,
    SIGNING_IN_PROGRESS,
    FAILED,
    COMPLETED;

    private static final Set<BasketSigningStatus> editableBasketStatuses = Set.of(BasketSigningStatus.NOT_YET_STARTED, BasketSigningStatus.FAILED);

    public boolean editable() {
        return editableBasketStatuses.contains(this);
    }

    public TransactionStatusSBSDTO toPSD2TransactionStatus() {
        return switch (this) {
            case NOT_YET_STARTED, SIGNING_SESSION_CREATED, SIGNING_IN_PROGRESS, FAILED -> TransactionStatusSBSDTO.RCVD;
            case COMPLETED -> TransactionStatusSBSDTO.ACTC;
        };
    }

    // ACTC: 'AcceptedTechnicalValidation' - Authentication and syntactical and semantical validation are successful.
    // RCVD: 'Received' - Payment initiation has been received by the receiving agent.

    public ScaStatusDTO toPSD2ScaStatus() {
        return switch (this) {
            case NOT_YET_STARTED, SIGNING_SESSION_CREATED -> ScaStatusDTO.RECEIVED;
            case SIGNING_IN_PROGRESS -> ScaStatusDTO.STARTED;
            case FAILED -> ScaStatusDTO.FAILED;
            case COMPLETED -> ScaStatusDTO.FINALISED;
        };
    }


}
