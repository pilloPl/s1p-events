package com.s1p.eventsourcing.events;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
public class CardWithdrawn implements DomainEvent {
    private UUID uuid;
    private BigDecimal amount;
    private Date date;
    private String type = "card.withdrawn";


    public CardWithdrawn(UUID uuid, BigDecimal amount, Date date) {
        this.uuid = uuid;
        this.amount = amount;
        this.date = date;
    }

    @Override
    public UUID aggregateUUID() {
        return uuid;
    }

    @Override
    public Date timestamp() {
        return date;
    }
}
