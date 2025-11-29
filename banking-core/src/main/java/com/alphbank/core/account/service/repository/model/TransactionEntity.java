package com.alphbank.core.account.service.repository.model;

import com.alphbank.core.account.service.model.Transaction;
import com.alphbank.core.account.service.model.TransactionCreatedByType;
import lombok.Builder;
import lombok.Data;
import org.javamoney.moneta.Money;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@Table("transaction_")
public class TransactionEntity {

    @Column("id")
    @Id
    private UUID id;

    @Column("account_id")
    private UUID accountId;

    @Column("currency_code")
    private String currencyCode;

    @Column("monetary_value")
    private BigDecimal amount;

    @Column("new_balance")
    private BigDecimal newBalance;

    @Column("message")
    private String message;

    @Column("execution_date_time")
    private LocalDateTime executionDateTime;

    @Column("created_from_type")
    private TransactionCreatedByType createdFromType;

    @Column("created_from_id")
    private UUID createdFromId;

    public Transaction toModel() {
        return Transaction.builder()
                .id(id)
                .currencyCode(currencyCode)
                .amount(Money.of(amount, currencyCode))
                .newBalance(Money.of(newBalance, currencyCode))
                .message(message)
                .executedDateTime(executionDateTime)
                .build();
    }
}
