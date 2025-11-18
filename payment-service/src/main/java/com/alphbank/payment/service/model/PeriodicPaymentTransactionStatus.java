package com.alphbank.payment.service.model;

import org.berlingroup.rest.model.TransactionStatusDTO;

public enum PeriodicPaymentTransactionStatus {

    RECEIVED,

    AUTHORIZATION_CREATED,
    AUTHORIZATION_STARTED,
    AUTHORIZATION_FAILED,

    CORE_ACCEPTED;

    public TransactionStatusDTO toPSD2TransactionStatus() {
        return switch (this) {
            case RECEIVED -> TransactionStatusDTO.RCVD;
            case AUTHORIZATION_CREATED, AUTHORIZATION_STARTED, AUTHORIZATION_FAILED -> TransactionStatusDTO.PDNG;
            case CORE_ACCEPTED ->
                // Periodic payments are not real payments. If the core has accepted the periodic payments,
                // we can assume that payments will happen in the future, but we cannot say that settlement is complete.
                    TransactionStatusDTO.ACSP;
        };

        // RCVD: 'Received' - Payment initiation has been received by the receiving agent
        // PDNG: 'Pending' - Payment initiation or individual transaction included in the payment initiation is pending. Further checks and status update will be performed
        // ACSP: 'AcceptedSettlementInProcess' - All preceding checks such as technical validation and customer profile were successful and therefore the payment initiation has been accepted for execution
    }
}
