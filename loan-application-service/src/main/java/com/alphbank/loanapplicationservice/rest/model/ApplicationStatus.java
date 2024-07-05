package com.alphbank.loanapplicationservice.rest.model;

public enum ApplicationStatus {

    CREATED,
    SIGNING_STARTED,
    SIGNING_FAILED,
    CREDIT_CHECK_PENDING,
    MANUAL_INSPECTION,
    APPROVED
}
