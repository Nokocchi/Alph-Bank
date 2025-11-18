package com.alphbank.payment.service.model;

import org.berlingroup.rest.model.FrequencyCodeDTO;

public enum PeriodicPaymentFrequency {

    DAILY,
    MONTHLY,
    ANNUAL;

    public static PeriodicPaymentFrequency from(FrequencyCodeDTO frequency) {
        return switch (frequency) {
            case DAILY -> DAILY;
            case MONTHLY -> MONTHLY;
            case ANNUAL -> ANNUAL;
            // TODO: I don't think this is handled properly in the reactive stream? Does this actually return 400 bad request?
            //  Maybe just check this in the custom validator
            default -> throw new IllegalArgumentException("Unsupported frequency code: %s".formatted(frequency));
        };
    }

    public FrequencyCodeDTO toPSD2Frequency() {
        return switch (this) {
            case DAILY -> FrequencyCodeDTO.DAILY;
            case MONTHLY -> FrequencyCodeDTO.MONTHLY;
            case ANNUAL -> FrequencyCodeDTO.ANNUAL;
        };
    }
}
