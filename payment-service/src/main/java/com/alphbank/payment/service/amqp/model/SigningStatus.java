package com.alphbank.payment.service.amqp.model;

public enum SigningStatus {
    CREATED,
    IN_PROGRESS,
    CANCELLED,
    EXPIRED,
    COMPLETED
}
