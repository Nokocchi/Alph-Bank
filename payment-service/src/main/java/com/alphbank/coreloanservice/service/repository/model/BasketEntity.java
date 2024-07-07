package com.alphbank.coreloanservice.service.repository.model;

import com.alphbank.coreloanservice.service.model.BasketSigningStatus;
import lombok.Builder;
import lombok.Data;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Builder
@Data
@Table("basket")
public class BasketEntity {

    @Column("id")
    @Id
    private UUID basketId;

    @Column("customer_id")
    private UUID customerId;

    @With
    @Column("signing_session_id")
    private UUID signingSessionId;

    @With
    @Column("signing_status")
    private String signingStatus = BasketSigningStatus.NOT_YET_STARTED.toString();

    public static BasketEntity from(UUID customerId) {
        return BasketEntity.builder()
                .customerId(customerId)
                .build();
    }
}
