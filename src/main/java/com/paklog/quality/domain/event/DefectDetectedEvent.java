package com.paklog.quality.domain.event;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
public class DefectDetectedEvent extends DomainEvent {
    private final String inspectionId;
    private final String defectType;
    private final String severity;
    private final String description;

    @Builder
    public DefectDetectedEvent(String inspectionId, String defectType, String severity, String description) {
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
}
