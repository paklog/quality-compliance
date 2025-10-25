package com.paklog.quality.domain.event;


public class DefectDetectedEvent extends DomainEvent {
    private final String inspectionId;
    private final String defectType;
    private final String severity;
    private final String description;

    private DefectDetectedEvent(final String inspectionId, final String defectType, final String severity, final String description) {
        super();
        this.inspectionId = inspectionId;
        this.defectType = defectType;
        this.severity = severity;
        this.description = description;
    }

    @Override
    public String getEventType() {
        return "DefectDetected";
    }

    public final String getInspectionId() { return inspectionId; }
    public final String getDefectType() { return defectType; }
    public final String getSeverity() { return severity; }
    public final String getDescription() { return description; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String inspectionId;
        private String defectType;
        private String severity;
        private String description;

        public Builder inspectionId(final String inspectionId) { this.inspectionId = inspectionId; return this; }
        public Builder defectType(final String defectType) { this.defectType = defectType; return this; }
        public Builder severity(final String severity) { this.severity = severity; return this; }
        public Builder description(final String description) { this.description = description; return this; }

        public DefectDetectedEvent build() {
            return new DefectDetectedEvent(inspectionId, defectType, severity, description);
        
}
}
}
