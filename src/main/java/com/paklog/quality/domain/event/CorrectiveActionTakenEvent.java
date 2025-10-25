package com.paklog.quality.domain.event;

import java.time.Instant;

public class CorrectiveActionTakenEvent extends DomainEvent {
    private final String inspectionId;
    private final String action;
    private final Instant completedAt;

    private CorrectiveActionTakenEvent(final String inspectionId, final String action, final Instant completedAt) {
        super();
        this.inspectionId = inspectionId;
        this.action = action;
        this.completedAt = completedAt;
    }

    @Override
    public String getEventType() {
        return "CorrectiveActionTaken";
    }

    public final String getInspectionId() { return inspectionId; }
    public final String getAction() { return action; }
    public final Instant getCompletedAt() { return completedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String inspectionId;
        private String action;
        private Instant completedAt;

        public Builder inspectionId(final String inspectionId) { this.inspectionId = inspectionId; return this; }
        public Builder action(final String action) { this.action = action; return this; }
        public Builder completedAt(final Instant completedAt) { this.completedAt = completedAt; return this; }

        public CorrectiveActionTakenEvent build() {
            return new CorrectiveActionTakenEvent(inspectionId, action, completedAt);
        
}
}
}
