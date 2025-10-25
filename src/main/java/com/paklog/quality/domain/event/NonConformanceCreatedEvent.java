package com.paklog.quality.domain.event;


public class NonConformanceCreatedEvent extends DomainEvent {
    private final String inspectionId;
    private final String description;
    private final int defectCount;

    private NonConformanceCreatedEvent(final String inspectionId, final String description, final int defectCount) {
        super();
        this.inspectionId = inspectionId;
        this.description = description;
        this.defectCount = defectCount;
    }

    @Override
    public String getEventType() {
        return "NonConformanceCreated";
    }

    public final String getInspectionId() { return inspectionId; }
    public final String getDescription() { return description; }
    public final int getDefectCount() { return defectCount; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String inspectionId;
        private String description;
        private int defectCount;

        public Builder inspectionId(final String inspectionId) { this.inspectionId = inspectionId; return this; }
        public Builder description(final String description) { this.description = description; return this; }
        public Builder defectCount(final int defectCount) { this.defectCount = defectCount; return this; }

        public NonConformanceCreatedEvent build() {
            return new NonConformanceCreatedEvent(inspectionId, description, defectCount);
        
}
}
}
