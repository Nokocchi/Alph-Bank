package com.alphbank.payment.service.repository.model;

import com.alphbank.payment.service.model.BasketSigningStatus;
import com.alphbank.payment.service.model.SigningBasket;
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
public class SigningBasketEntity {

    @Column("id")
    @Id
    private UUID id;

    @With
    @Column("signing_session_id")
    private UUID signingSessionId;

    @Builder.Default
    @With
    @Column("signing_status")
    private BasketSigningStatus signingStatus = BasketSigningStatus.NOT_YET_STARTED;

    public static SigningBasketEntity from(SigningBasket basket) {
        SigningBasketEntityBuilder builder = SigningBasketEntity.builder()
                .id(basket.id())
                .signingSessionId(basket.signingSessionId());

        if (basket.signingStatus() != null) {
            builder.signingStatus(basket.signingStatus());
        }

        return builder.build();
    }
}
