package com.paklog.quality.domain.event;

import java.time.Instant;

public abstract class DomainEvent {
    private final String eventId;
    private final Instant occurredAt;

    protected DomainEvent() {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.occurredAt = Instant.now();
    }

    public abstract String getEventType();

    public String getEventId() {
        return eventId;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
