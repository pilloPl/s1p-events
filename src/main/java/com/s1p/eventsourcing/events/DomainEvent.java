package com.s1p.eventsourcing.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Date;
import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(name = "card.assigned", value = LimitAssigned.class),
        @JsonSubTypes.Type(name = "card.created", value = CardCreated.class),
        @JsonSubTypes.Type(name = "card.repaid", value = CardRepaid.class),
        @JsonSubTypes.Type(name = "card.withdrawn", value = CardWithdrawn.class),
        @JsonSubTypes.Type(name = "card.cycleclosed", value = CycleClosed.class)
})
public interface DomainEvent {

    UUID aggregateUUID();
    Date timestamp();
}
