package com.paklog.quality.domain.event;

import lombok.*;
import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = true)
public class CorrectiveActionTakenEvent extends DomainEvent {
    private final String inspectionId;
    private final String action;
    private final Instant completedAt;

    @Builder
    public CorrectiveActionTakenEvent(String inspectionId, String action, Instant completedAt) {
        super();
        this.inspectionId = inspectionId;
        this.action = action;
        this.completedAt = completedAt;
    }

    @Override
    public String getEventType() {
        return "CorrectiveActionTaken";
    }
}
