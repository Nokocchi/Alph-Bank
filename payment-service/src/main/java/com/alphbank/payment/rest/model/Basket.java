package com.alphbank.payment.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Basket {

    private UUID basketId;
    private List<Payment> payments;

}
