package com.paklog.quality.domain.event;


public class ComplianceViolationEvent extends DomainEvent {
    private final String inspectionId;
    private final String violationType;
    private final String description;

    private ComplianceViolationEvent(final String inspectionId, final String violationType, final String description) {
        super();
        this.inspectionId = inspectionId;
        this.violationType = violationType;
        this.description = description;
    }

    @Override
    public String getEventType() {
        return "ComplianceViolation";
    }

    public final String getInspectionId() { return inspectionId; }
    public final String getViolationType() { return violationType; }
    public final String getDescription() { return description; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String inspectionId;
        private String violationType;
        private String description;

        public Builder inspectionId(final String inspectionId) { this.inspectionId = inspectionId; return this; }
        public Builder violationType(final String violationType) { this.violationType = violationType; return this; }
        public Builder description(final String description) { this.description = description; return this; }

        public ComplianceViolationEvent build() {
            return new ComplianceViolationEvent(inspectionId, violationType, description);
        
}
}
}
