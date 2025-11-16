package com.alphbank.payment.rest.validation;

import com.alphbank.payment.rest.error.model.InvalidInputException;

import java.math.BigDecimal;

public @interface ValidPaymentInitiation {

    String amountString = dto.getInstructedAmount().getAmount();
    String currency = dto.getInstructedAmount().getCurrency();

    BigDecimal amount = null;
        try {
        amount = new BigDecimal(amountString);
    }
        catch (NumberFormatException e) {
        throw new InvalidInputException()
    }

        Also check that the creditor name and unstructured data is String?
}
