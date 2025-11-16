package com.alphbank.payment.service.model;

import org.berlingroup.rest.model.TransactionStatusDTO;

public enum PaymentTransactionStatus {
    RECEIVED,

    AUTHORIZATION_CREATED,
    AUTHORIZATION_STARTED,
    AUTHORIZATION_FAILED,

    CORE_SCHEDULED_FOR_FUTURE,
    CORE_PAYMENT_FAILED,
    CORE_COMPLETED;

    public TransactionStatusDTO toPSD2TransactionStatus() {
        return switch (this) {
            case RECEIVED -> TransactionStatusDTO.RCVD;
            case AUTHORIZATION_CREATED, AUTHORIZATION_STARTED, AUTHORIZATION_FAILED -> TransactionStatusDTO.PDNG;
            case CORE_SCHEDULED_FOR_FUTURE -> TransactionStatusDTO.ACSP; // Scheduled payments are not supported by PSD2, so this should never happen for PSD2 payments
            case CORE_PAYMENT_FAILED -> TransactionStatusDTO.RJCT;
            case CORE_COMPLETED -> TransactionStatusDTO.ACCC;
        };

        // RCVD: 'Received' - Payment initiation has been received by the receiving agent
        // PDNG: 'Pending' - Payment initiation or individual transaction included in the payment initiation is pending. Further checks and status update will be performed
        // ACSP: 'AcceptedSettlementInProcess' - All preceding checks such as technical validation and customer profile were successful and therefore the payment initiation has been accepted for execution
        // RJCT: 'Rejected' - Payment initiation or individual transaction included in the payment initiation has been rejected
        // ACCC: 'AcceptedSettlementCompleted' - Settlement on the creditor's account has been completed
    }

    public static PaymentTransactionStatus fromCoreTransactionStatus(){

    }
}
