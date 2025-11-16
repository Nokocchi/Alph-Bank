package com.alphbank.payment.service.model;

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
}
