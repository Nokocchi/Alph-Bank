package com.alphbank.coreaccountservice.rest.model;

import javax.money.MonetaryAmount;
import java.util.UUID;

public record Account(UUID id, String accountName, MonetaryAmount balance, String iban) {

}
