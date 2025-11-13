package com.alphbank.commons.impl;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;

public class Utils {

    public static BigDecimal getAmount(MonetaryAmount monetaryAmount) {
        return monetaryAmount.getNumber().numberValue(BigDecimal.class);
    }

    public static String getCurrencyCode(MonetaryAmount monetaryAmount) {
        return monetaryAmount.getCurrency().getCurrencyCode();
    }

}
